import { create } from 'zustand';
import { Payment, CreatePaymentRequest, PaymentMethod } from '../types';
import { createPayment, getPayment } from '../api/payments';
import { DEFAULTS } from '../utils/constants';

interface PaymentState {
  currentPayment: Payment | null;
  isLoading: boolean;
  error: string | null;
}

interface PaymentActions {
  initiate: (request: CreatePaymentRequest) => Promise<Payment>;
  pollStatus: (onSuccess?: (payment: Payment) => void, onFailure?: (payment: Payment) => void) => void;
  stopPolling: () => void;
  cancel: () => void;
  reset: () => void;
  clearError: () => void;
}

let pollTimer: ReturnType<typeof setTimeout> | null = null;
let pollAttempts = 0;
let activePollSessionId = 0;
let isPollRequestInFlight = false;
let consecutivePollErrors = 0;

const PAYMENT_POLL_MAX_BACKOFF_MS = 30000;
const PAYMENT_POLL_JITTER_FACTOR = 0.2;

function withJitter(baseDelay: number, factor: number): number {
  const jitter = baseDelay * factor * Math.random();
  return Math.round(baseDelay + jitter);
}

function getPaymentRetryDelay(baseIntervalMs: number, consecutiveErrors: number): number {
  const exponent = Math.max(0, consecutiveErrors - 1);
  const backoff = Math.min(baseIntervalMs * 2 ** exponent, PAYMENT_POLL_MAX_BACKOFF_MS);
  return withJitter(backoff, PAYMENT_POLL_JITTER_FACTOR);
}

function stopPaymentPollingInternal() {
  if (pollTimer) {
    clearTimeout(pollTimer);
    pollTimer = null;
  }
  activePollSessionId++;
  pollAttempts = 0;
  consecutivePollErrors = 0;
  isPollRequestInFlight = false;
}

export const usePaymentStore = create<PaymentState & PaymentActions>((set, get) => ({
  currentPayment: null,
  isLoading: false,
  error: null,

  initiate: async (request: CreatePaymentRequest) => {
    set({ isLoading: true, error: null });
    try {
      const { payment } = await createPayment(request);
      set({ currentPayment: payment, isLoading: false });
      return payment;
    } catch (err: any) {
      set({ isLoading: false, error: err.message || 'Failed to create payment' });
      throw err;
    }
  },

  pollStatus: (onSuccess, onFailure) => {
    const { currentPayment } = get();
    if (!currentPayment) return;

    stopPaymentPollingInternal();
    const sessionId = activePollSessionId;

    const scheduleNext = (delayMs: number) => {
      if (sessionId !== activePollSessionId) return;
      pollTimer = setTimeout(check, delayMs);
    };

    const check = async () => {
      if (sessionId !== activePollSessionId) return;

      if (isPollRequestInFlight) {
        scheduleNext(DEFAULTS.PAYMENT_POLL_INTERVAL);
        return;
      }

      isPollRequestInFlight = true;
      pollAttempts++;
      try {
        const latestPaymentId = get().currentPayment?.id;
        if (!latestPaymentId) {
          stopPaymentPollingInternal();
          return;
        }

        const { payment } = await getPayment(latestPaymentId);
        if (sessionId !== activePollSessionId) return;

        set({ currentPayment: payment });
        consecutivePollErrors = 0;

        if (payment.status === 'SUCCESS') {
          stopPaymentPollingInternal();
          onSuccess?.(payment);
          return;
        }

        if (payment.status === 'FAILED' || payment.status === 'CANCELLED' || payment.status === 'EXPIRED') {
          stopPaymentPollingInternal();
          onFailure?.(payment);
          return;
        }

        if (pollAttempts >= DEFAULTS.PAYMENT_POLL_MAX_ATTEMPTS) {
          // Timeout — treat as expired
          const expired = { ...payment, status: 'EXPIRED' as const };
          set({ currentPayment: expired });
          stopPaymentPollingInternal();
          onFailure?.(expired);
          return;
        }

        scheduleNext(DEFAULTS.PAYMENT_POLL_INTERVAL);
      } catch {
        if (sessionId !== activePollSessionId) return;

        consecutivePollErrors++;
        // Retry transient failures with exponential backoff and jitter.
        if (pollAttempts < DEFAULTS.PAYMENT_POLL_MAX_ATTEMPTS) {
          const retryDelay = getPaymentRetryDelay(
            DEFAULTS.PAYMENT_POLL_INTERVAL,
            consecutivePollErrors
          );
          scheduleNext(retryDelay);
        }
      } finally {
        isPollRequestInFlight = false;
      }
    };

    scheduleNext(DEFAULTS.PAYMENT_POLL_INTERVAL);
  },

  stopPolling: () => {
    stopPaymentPollingInternal();
  },

  cancel: () => {
    stopPaymentPollingInternal();
    set((state) => ({
      currentPayment: state.currentPayment
        ? { ...state.currentPayment, status: 'CANCELLED' }
        : null,
    }));
  },

  reset: () => {
    stopPaymentPollingInternal();
    set({ currentPayment: null, isLoading: false, error: null });
  },

  clearError: () => set({ error: null }),
}));
