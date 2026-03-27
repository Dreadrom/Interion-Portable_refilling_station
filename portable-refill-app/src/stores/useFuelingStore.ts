import { create } from 'zustand';
import {
  Station,
  StationDetail,
  ProductType,
  DispensePresetType,
  Transaction,
  DispenseProgress,
  DispenseStartRequest,
  StopReason,
} from '../types';
import {
  getStationById,
  getStations,
} from '../api/stations';
import {
  startDispense,
  stopDispense,
  getDispenseProgress,
} from '../api/dispense';
import { DEFAULTS } from '../utils/constants';

interface FuelingState {
  // Station selection
  stations: Station[];
  selectedStation: StationDetail | null;
  isLoadingStations: boolean;

  // Product & preset selection
  selectedProduct: ProductType | null;
  presetType: DispensePresetType;
  presetVolume: number | null;  // litres
  presetAmount: number | null;  // MYR

  // Active transaction
  currentTransaction: Transaction | null;
  dispenseProgress: DispenseProgress | null;
  isDispensing: boolean;

  // General
  isLoading: boolean;
  error: string | null;
}

interface FuelingActions {
  loadStations: (params?: { latitude?: number; longitude?: number; radiusKm?: number }) => Promise<void>;
  selectStation: (stationId: string) => Promise<void>;
  selectProduct: (product: ProductType) => void;
  setPreset: (type: DispensePresetType, value: number) => void;

  startFueling: (paymentId: string) => Promise<Transaction>;
  stopFueling: (reason?: StopReason) => Promise<void>;
  refreshProgress: () => Promise<void>;

  reset: () => void;
  clearError: () => void;
}

let progressPollTimer: ReturnType<typeof setTimeout> | null = null;
let activeProgressPollSessionId = 0;
let isProgressPollRequestInFlight = false;
let consecutiveProgressPollErrors = 0;

const FUELING_POLL_MAX_BACKOFF_MS = 15000;
const FUELING_POLL_JITTER_FACTOR = 0.2;
const FUELING_POLL_MAX_ERROR_RETRIES = 10;

function withJitter(baseDelay: number, factor: number): number {
  const jitter = baseDelay * factor * Math.random();
  return Math.round(baseDelay + jitter);
}

function getFuelingRetryDelay(baseIntervalMs: number, consecutiveErrors: number): number {
  const exponent = Math.max(0, consecutiveErrors - 1);
  const backoff = Math.min(baseIntervalMs * 2 ** exponent, FUELING_POLL_MAX_BACKOFF_MS);
  return withJitter(backoff, FUELING_POLL_JITTER_FACTOR);
}

function stopFuelingPollingInternal() {
  if (progressPollTimer) {
    clearTimeout(progressPollTimer);
    progressPollTimer = null;
  }
  activeProgressPollSessionId++;
  isProgressPollRequestInFlight = false;
  consecutiveProgressPollErrors = 0;
}

export const useFuelingStore = create<FuelingState & FuelingActions>((set, get) => ({
  stations: [],
  selectedStation: null,
  isLoadingStations: false,
  selectedProduct: null,
  presetType: 'AMOUNT',
  presetVolume: null,
  presetAmount: null,
  currentTransaction: null,
  dispenseProgress: null,
  isDispensing: false,
  isLoading: false,
  error: null,

  loadStations: async (params) => {
    set({ isLoadingStations: true, error: null });
    try {
      const stations = await getStations(params);
      set({ stations, isLoadingStations: false });
    } catch (err: any) {
      set({ isLoadingStations: false, error: err.message || 'Failed to load stations' });
      throw err;
    }
  },

  selectStation: async (stationId: string) => {
    set({ isLoading: true, error: null, selectedStation: null });
    try {
      const station = await getStationById(stationId);
      set({ selectedStation: station, isLoading: false });
    } catch (err: any) {
      set({ isLoading: false, error: err.message || 'Failed to load station details' });
      throw err;
    }
  },

  selectProduct: (product: ProductType) => {
    set({ selectedProduct: product });
  },

  setPreset: (type: DispensePresetType, value: number) => {
    if (type === 'VOLUME') {
      set({ presetType: type, presetVolume: value, presetAmount: null });
    } else {
      set({ presetType: type, presetAmount: value, presetVolume: null });
    }
  },

  startFueling: async (paymentId: string) => {
    const { selectedStation, selectedProduct, presetType, presetVolume, presetAmount } = get();
    if (!selectedStation || !selectedProduct) {
      throw new Error('No station or product selected');
    }

    stopFuelingPollingInternal();
    set({ isLoading: true, error: null });
    try {
      const request: DispenseStartRequest = {
        stationId: selectedStation.id,
        paymentId,
        product: selectedProduct,
        nozzle: 1,
        presetType,
        ...(presetType === 'VOLUME' ? { presetVolumeLitres: presetVolume! } : { presetAmount: presetAmount! }),
      };
      const response = await startDispense(request);
      set({
        currentTransaction: response.transaction,
        isDispensing: true,
        isLoading: false,
      });

      const sessionId = activeProgressPollSessionId;
      const transactionId = response.transaction.id;

      const scheduleNext = (delayMs: number) => {
        if (sessionId !== activeProgressPollSessionId) return;
        progressPollTimer = setTimeout(poll, delayMs);
      };

      // Start polling progress
      const poll = async () => {
        if (sessionId !== activeProgressPollSessionId) return;

        if (isProgressPollRequestInFlight) {
          scheduleNext(DEFAULTS.FUELING_POLL_INTERVAL);
          return;
        }

        isProgressPollRequestInFlight = true;
        try {
          const { progress } = await getDispenseProgress(transactionId);
          if (sessionId !== activeProgressPollSessionId) return;

          set({ dispenseProgress: progress });
          consecutiveProgressPollErrors = 0;

          if (progress.status === 'IN_PROGRESS') {
            scheduleNext(DEFAULTS.FUELING_POLL_INTERVAL);
          } else {
            set({ isDispensing: false });
            stopFuelingPollingInternal();
          }
        } catch {
          if (sessionId !== activeProgressPollSessionId) return;

          consecutiveProgressPollErrors++;
          if (consecutiveProgressPollErrors >= FUELING_POLL_MAX_ERROR_RETRIES) {
            stopFuelingPollingInternal();
            set({
              isDispensing: false,
              error: 'Lost connection while monitoring fueling progress. Please refresh status.',
            });
            return;
          }

          const retryDelay = getFuelingRetryDelay(
            DEFAULTS.FUELING_POLL_INTERVAL,
            consecutiveProgressPollErrors
          );
          scheduleNext(retryDelay);
        } finally {
          isProgressPollRequestInFlight = false;
        }
      };

      scheduleNext(DEFAULTS.FUELING_POLL_INTERVAL);

      return response.transaction;
    } catch (err: any) {
      set({ isLoading: false, error: err.message || 'Failed to start dispensing' });
      throw err;
    }
  },

  stopFueling: async (reason?: StopReason) => {
    const { currentTransaction } = get();
    if (!currentTransaction) return;

    stopFuelingPollingInternal();

    set({ isLoading: true, error: null });
    try {
      await stopDispense({ transactionId: currentTransaction.id, reason });
      set({ isDispensing: false, isLoading: false });
    } catch (err: any) {
      set({ isLoading: false, error: err.message || 'Failed to stop dispensing' });
      throw err;
    }
  },

  refreshProgress: async () => {
    const { currentTransaction } = get();
    if (!currentTransaction) return;
    try {
      const { progress } = await getDispenseProgress(currentTransaction.id);
      set({ dispenseProgress: progress });
      if (progress.status !== 'IN_PROGRESS') {
        set({ isDispensing: false });
      }
    } catch {
      // ignore transient errors
    }
  },

  reset: () => {
    stopFuelingPollingInternal();
    set({
      selectedStation: null,
      selectedProduct: null,
      presetType: 'AMOUNT',
      presetVolume: null,
      presetAmount: null,
      currentTransaction: null,
      dispenseProgress: null,
      isDispensing: false,
      isLoading: false,
      error: null,
    });
  },

  clearError: () => set({ error: null }),
}));
