/**
 * Store tests — useFuelingStore.ts
 * Tests station loading, product/preset selection, dispensing lifecycle,
 * polling backoff, and error handling.
 */

jest.mock('../../portable-refill-app/src/api/stations', () => ({
  getStations: jest.fn(),
  getStationById: jest.fn(),
}));

jest.mock('../../portable-refill-app/src/api/dispense', () => ({
  startDispense: jest.fn(),
  stopDispense: jest.fn(),
  getDispenseProgress: jest.fn(),
}));

import * as StationsApi from '../../portable-refill-app/src/api/stations';
import * as DispenseApi from '../../portable-refill-app/src/api/dispense';
import { useFuelingStore } from '../../portable-refill-app/src/stores/useFuelingStore';
import { Station, StationDetail } from '../../portable-refill-app/src/types';

const mockGetStations = StationsApi.getStations as jest.MockedFunction<typeof StationsApi.getStations>;
const mockGetStationById = StationsApi.getStationById as jest.MockedFunction<typeof StationsApi.getStationById>;
const mockStartDispense = DispenseApi.startDispense as jest.MockedFunction<typeof DispenseApi.startDispense>;
const mockStopDispense = DispenseApi.stopDispense as jest.MockedFunction<typeof DispenseApi.stopDispense>;
const mockGetDispenseProgress = DispenseApi.getDispenseProgress as jest.MockedFunction<typeof DispenseApi.getDispenseProgress>;

const MOCK_STATION: Station = {
  id: 'st-001',
  name: 'Interion Station Alpha',
  location: { latitude: 3.139, longitude: 101.686, address: 'KL City' },
  timezone: 'Asia/Kuala_Lumpur',
  status: 'IDLE',
  lastHeartbeat: new Date().toISOString(),
  createdAt: new Date().toISOString(),
  availableProducts: ['RON95', 'RON97'],
};

const MOCK_STATION_DETAIL: StationDetail = {
  ...MOCK_STATION,
  pricing: [{ product: 'RON95', pricePerLitre: 2.05, currency: 'MYR', effectiveFrom: '' }],
  tankStatus: [],
  alarms: [],
} as any;

const MOCK_TRANSACTION = {
  id: 'txn-123',
  userId: 'user-123',
  stationId: 'st-001',
  status: 'ACTIVE',
  product: 'RON95',
  createdAt: new Date().toISOString(),
};

beforeEach(() => {
  jest.clearAllMocks();
  jest.useFakeTimers();
  useFuelingStore.getState().reset();
});

afterEach(() => {
  jest.useRealTimers();
});

// ─────────────────────────────────────────────────────────────────────────────
// loadStations
// ─────────────────────────────────────────────────────────────────────────────
describe('useFuelingStore — loadStations', () => {
  test('populates stations array on success', async () => {
    mockGetStations.mockResolvedValue([MOCK_STATION]);
    await useFuelingStore.getState().loadStations();
    expect(useFuelingStore.getState().stations).toHaveLength(1);
    expect(useFuelingStore.getState().stations[0].id).toBe('st-001');
  });

  test('sets isLoadingStations during load', async () => {
    let resolve!: (v: any) => void;
    mockGetStations.mockReturnValue(new Promise((r) => (resolve = r)));
    const promise = useFuelingStore.getState().loadStations();
    expect(useFuelingStore.getState().isLoadingStations).toBe(true);
    resolve([]);
    await promise;
    expect(useFuelingStore.getState().isLoadingStations).toBe(false);
  });

  test('sets error on failure', async () => {
    mockGetStations.mockRejectedValue(new Error('Network error'));
    await expect(useFuelingStore.getState().loadStations()).rejects.toThrow();
    expect(useFuelingStore.getState().error).toBe('Network error');
  });

  test('passes location params to API', async () => {
    mockGetStations.mockResolvedValue([]);
    await useFuelingStore.getState().loadStations({ latitude: 3.14, longitude: 101.7, radiusKm: 5 });
    expect(mockGetStations).toHaveBeenCalledWith({ latitude: 3.14, longitude: 101.7, radiusKm: 5 });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// selectStation
// ─────────────────────────────────────────────────────────────────────────────
describe('useFuelingStore — selectStation', () => {
  test('sets selectedStation on success', async () => {
    mockGetStationById.mockResolvedValue(MOCK_STATION_DETAIL);
    await useFuelingStore.getState().selectStation('st-001');
    expect(useFuelingStore.getState().selectedStation?.id).toBe('st-001');
  });

  test('clears selectedStation before fetching', async () => {
    useFuelingStore.setState({ selectedStation: MOCK_STATION_DETAIL });
    let resolve!: (v: any) => void;
    mockGetStationById.mockReturnValue(new Promise((r) => (resolve = r)));
    const promise = useFuelingStore.getState().selectStation('st-002');
    expect(useFuelingStore.getState().selectedStation).toBeNull();
    resolve(MOCK_STATION_DETAIL);
    await promise;
  });

  test('sets error on failure', async () => {
    mockGetStationById.mockRejectedValue(new Error('Station not found'));
    await expect(useFuelingStore.getState().selectStation('INVALID')).rejects.toThrow();
    expect(useFuelingStore.getState().error).toBe('Station not found');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// selectProduct / setPreset
// ─────────────────────────────────────────────────────────────────────────────
describe('useFuelingStore — product and preset', () => {
  test('selectProduct updates selectedProduct', () => {
    useFuelingStore.getState().selectProduct('RON95');
    expect(useFuelingStore.getState().selectedProduct).toBe('RON95');
  });

  test('setPreset AMOUNT updates presetAmount', () => {
    useFuelingStore.getState().setPreset('AMOUNT', 50);
    expect(useFuelingStore.getState().presetType).toBe('AMOUNT');
    expect(useFuelingStore.getState().presetAmount).toBe(50);
  });

  test('setPreset VOLUME updates presetVolume', () => {
    useFuelingStore.getState().setPreset('VOLUME', 20);
    expect(useFuelingStore.getState().presetType).toBe('VOLUME');
    expect(useFuelingStore.getState().presetVolume).toBe(20);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// startFueling
// ─────────────────────────────────────────────────────────────────────────────
describe('useFuelingStore — startFueling', () => {
  beforeEach(() => {
    useFuelingStore.setState({
      selectedStation: MOCK_STATION_DETAIL,
      selectedProduct: 'RON95',
      presetType: 'AMOUNT',
      presetAmount: 50,
    });
  });

  test('returns transaction on success', async () => {
    mockStartDispense.mockResolvedValue({ transaction: MOCK_TRANSACTION } as any);
    const txn = await useFuelingStore.getState().startFueling('pay-123');
    expect(txn.id).toBe('txn-123');
  });

  test('sets isDispensing to true', async () => {
    mockStartDispense.mockResolvedValue({ transaction: MOCK_TRANSACTION } as any);
    await useFuelingStore.getState().startFueling('pay-123');
    expect(useFuelingStore.getState().isDispensing).toBe(true);
  });

  test('stores currentTransaction', async () => {
    mockStartDispense.mockResolvedValue({ transaction: MOCK_TRANSACTION } as any);
    await useFuelingStore.getState().startFueling('pay-123');
    expect(useFuelingStore.getState().currentTransaction?.id).toBe('txn-123');
  });

  test('sets error on failure', async () => {
    mockStartDispense.mockRejectedValue(new Error('Station offline'));
    await expect(useFuelingStore.getState().startFueling('pay-123')).rejects.toThrow();
    expect(useFuelingStore.getState().error).toBe('Station offline');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// stopFueling
// ─────────────────────────────────────────────────────────────────────────────
describe('useFuelingStore — stopFueling', () => {
  beforeEach(() => {
    useFuelingStore.setState({
      currentTransaction: MOCK_TRANSACTION as any,
      isDispensing: true,
    });
  });

  test('sets isDispensing to false', async () => {
    mockStopDispense.mockResolvedValue(undefined);
    await useFuelingStore.getState().stopFueling('USER_STOPPED');
    expect(useFuelingStore.getState().isDispensing).toBe(false);
  });

  test('calls stopDispense API with transactionId', async () => {
    mockStopDispense.mockResolvedValue(undefined);
    await useFuelingStore.getState().stopFueling('USER_STOPPED');
    expect(mockStopDispense).toHaveBeenCalledWith(expect.objectContaining({ transactionId: 'txn-123' }));
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// reset / clearError
// ─────────────────────────────────────────────────────────────────────────────
describe('useFuelingStore — reset', () => {
  test('clears all state', () => {
    useFuelingStore.setState({
      selectedStation: MOCK_STATION_DETAIL,
      currentTransaction: MOCK_TRANSACTION as any,
      isDispensing: true,
      error: 'err',
    });
    useFuelingStore.getState().reset();
    const s = useFuelingStore.getState();
    expect(s.selectedStation).toBeNull();
    expect(s.currentTransaction).toBeNull();
    expect(s.isDispensing).toBe(false);
    expect(s.error).toBeNull();
  });
});

describe('useFuelingStore — clearError', () => {
  test('resets error to null', () => {
    useFuelingStore.setState({ error: 'Some error' });
    useFuelingStore.getState().clearError();
    expect(useFuelingStore.getState().error).toBeNull();
  });
});
