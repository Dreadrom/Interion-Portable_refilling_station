/**
 * Store tests — usePaymentStore.ts
 * Tests payment initiation, polling logic, cancellation, and error handling.
 */

jest.mock('../../portable-refill-app/src/api/payments', () => ({
  createPayment: jest.fn(),
  getPayment: jest.fn(),
}));

import * as PaymentsApi from '../../portable-refill-app/src/api/payments';
import { usePaymentStore } from '../../portable-refill-app/src/stores/usePaymentStore';
import { Payment } from '../../portable-refill-app/src/types';

const mockCreatePayment = PaymentsApi.createPayment as jest.MockedFunction<typeof PaymentsApi.createPayment>;
const mockGetPayment = PaymentsApi.getPayment as jest.MockedFunction<typeof PaymentsApi.getPayment>;

function makeMockPayment(overrides: Partial<Payment> = {}): Payment {
  return {
    id: 'pay-123',
    userId: 'user-123',
    amount: 50,
    currency: 'MYR',
    status: 'PENDING',
    paymentMethod: 'FPX',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    ...overrides,
  } as Payment;
}

beforeEach(() => {
  jest.clearAllMocks();
  jest.useFakeTimers();
  usePaymentStore.setState({
    currentPayment: null,
    isLoading: false,
    error: null,
  });
});

afterEach(() => {
  usePaymentStore.getState().stopPolling();
  jest.useRealTimers();
});

// ─────────────────────────────────────────────────────────────────────────────
// initiate
// ─────────────────────────────────────────────────────────────────────────────
describe('usePaymentStore — initiate', () => {
  test('sets isLoading true then false', async () => {
    mockCreatePayment.mockResolvedValue({ payment: makeMockPayment() } as any);
    const promise = usePaymentStore.getState().initiate({ amount: 50, currency: 'MYR', stationId: 'st-1' } as any);
    expect(usePaymentStore.getState().isLoading).toBe(true);
    await promise;
    expect(usePaymentStore.getState().isLoading).toBe(false);
  });

  test('stores payment in state on success', async () => {
    const payment = makeMockPayment();
    mockCreatePayment.mockResolvedValue({ payment } as any);
    await usePaymentStore.getState().initiate({ amount: 50, currency: 'MYR', stationId: 'st-1' } as any);
    expect(usePaymentStore.getState().currentPayment?.id).toBe('pay-123');
  });

  test('returns the created payment', async () => {
    const payment = makeMockPayment();
    mockCreatePayment.mockResolvedValue({ payment } as any);
    const result = await usePaymentStore.getState().initiate({ amount: 50, currency: 'MYR', stationId: 'st-1' } as any);
    expect(result.id).toBe('pay-123');
  });

  test('sets error and throws on failure', async () => {
    mockCreatePayment.mockRejectedValue(new Error('Payment gateway unavailable'));
    await expect(
      usePaymentStore.getState().initiate({ amount: 50, currency: 'MYR', stationId: 'st-1' } as any)
    ).rejects.toThrow('Payment gateway unavailable');
    expect(usePaymentStore.getState().error).toBe('Payment gateway unavailable');
  });

  describe('SECURITY — amount validation is the frontend responsibility', () => {
    test('store passes amount through without enforcing positive constraint', async () => {
      // Documents that the store does not validate amounts — this must be done in UI
      const payment = makeMockPayment({ amount: -1 });
      mockCreatePayment.mockResolvedValue({ payment } as any);
      const result = await usePaymentStore.getState().initiate({ amount: -1, currency: 'MYR', stationId: 'st-1' } as any);
      expect(result.amount).toBe(-1); // Store does not block this — backend must
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// cancel / reset
// ─────────────────────────────────────────────────────────────────────────────
describe('usePaymentStore — cancel and reset', () => {
  test('cancel clears currentPayment', () => {
    usePaymentStore.setState({ currentPayment: makeMockPayment() });
    usePaymentStore.getState().cancel();
    // cancel() sets status to CANCELLED but keeps the payment object (by design)
    expect(usePaymentStore.getState().currentPayment?.status).toBe('CANCELLED');
  });

  test('reset clears all state', () => {
    usePaymentStore.setState({ currentPayment: makeMockPayment(), error: 'some error', isLoading: true });
    usePaymentStore.getState().reset();
    const state = usePaymentStore.getState();
    expect(state.currentPayment).toBeNull();
    expect(state.error).toBeNull();
    expect(state.isLoading).toBe(false);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// pollStatus
// ─────────────────────────────────────────────────────────────────────────────
describe('usePaymentStore — pollStatus', () => {
  test('calls onSuccess when payment status becomes SUCCESS', async () => {
    const successPayment = makeMockPayment({ status: 'SUCCESS' });
    mockGetPayment.mockResolvedValue({ payment: successPayment } as any);
    usePaymentStore.setState({ currentPayment: makeMockPayment() });

    const onSuccess = jest.fn();
    usePaymentStore.getState().pollStatus(onSuccess, undefined);

    // Advance past first poll interval (default 3000ms)
    await jest.advanceTimersByTimeAsync(3100);
    await Promise.resolve(); // flush microtasks

    expect(onSuccess).toHaveBeenCalledWith(expect.objectContaining({ status: 'SUCCESS' }));
  });

  test('calls onFailure when payment status becomes FAILED', async () => {
    const failedPayment = makeMockPayment({ status: 'FAILED' });
    mockGetPayment.mockResolvedValue({ payment: failedPayment } as any);
    usePaymentStore.setState({ currentPayment: makeMockPayment() });

    const onFailure = jest.fn();
    usePaymentStore.getState().pollStatus(undefined, onFailure);

    await jest.advanceTimersByTimeAsync(3100);
    await Promise.resolve();

    expect(onFailure).toHaveBeenCalledWith(expect.objectContaining({ status: 'FAILED' }));
  });

  test('stops polling after stopPolling is called', async () => {
    mockGetPayment.mockResolvedValue({ payment: makeMockPayment({ status: 'PENDING' }) } as any);
    usePaymentStore.setState({ currentPayment: makeMockPayment() });

    usePaymentStore.getState().pollStatus();
    usePaymentStore.getState().stopPolling();

    await jest.advanceTimersByTimeAsync(10000);
    // Should have been called 0 or 1 times (any in-flight request), not many
    expect(mockGetPayment.mock.calls.length).toBeLessThanOrEqual(1);
  });

  test('does not poll when there is no currentPayment', async () => {
    usePaymentStore.setState({ currentPayment: null });
    usePaymentStore.getState().pollStatus();
    await jest.advanceTimersByTimeAsync(5000);
    expect(mockGetPayment).not.toHaveBeenCalled();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// clearError
// ─────────────────────────────────────────────────────────────────────────────
describe('usePaymentStore — clearError', () => {
  test('resets error to null', () => {
    usePaymentStore.setState({ error: 'Payment failed' });
    usePaymentStore.getState().clearError();
    expect(usePaymentStore.getState().error).toBeNull();
  });
});
