/**
 * Demo station data — used as fallback when the backend is unavailable (dev/demo mode).
 * Stations are located at highway rest areas (R&R) along the North-South Expressway (Plus E1/E2).
 *
 * Product: AdBlue  |  Price: MYR 10.00 / L
 * Each station has 2 pumps.
 * Tank capacities: 3 000 L | 10 000 L | 25 000 L only.
 * T001 is at ~10% tank level with lowLevelAlarm active.
 */

import { StationDetail, Pump } from '../types';

const NOW = '2026-03-27T08:00:00.000Z';
const PRODUCT = 'AdBlue' as const;

function makePumps(stationId: string, s1: Pump['status'], s2: Pump['status']): Pump[] {
  return [
    { id: `pump-${stationId}-1`, stationId, pumpNumber: 1, status: s1, lastUsed: NOW },
    { id: `pump-${stationId}-2`, stationId, pumpNumber: 2, status: s2, lastUsed: NOW },
  ];
}

export const DEMO_STATIONS: (StationDetail & { distanceKm: number })[] = [
  {
    id: 'demo-station-001',
    name: 'T001 - R&R Ayer Keroh',
    location: {
      latitude: 2.2614,
      longitude: 102.2292,
      address: 'R&R Ayer Keroh, KM 165, Plus Expressway (E2), 75450 Ayer Keroh, Melaka',
    },
    timezone: 'Asia/Kuala_Lumpur',
    status: 'ALARM',
    lastHeartbeat: NOW,
    availableProducts: [PRODUCT],
    distanceKm: 0.8,
    createdAt: '2025-06-01T00:00:00.000Z',
    pumps: makePumps('demo-station-001', 'IDLE', 'IDLE'),
    tankStatus: [
      {
        id: 'tank-001-a',
        stationId: 'demo-station-001',
        product: PRODUCT,
        levelLitres: 1000,
        capacityLitres: 10000,
        temperatureC: 29.4,
        lowLevelAlarm: true,
        highLevelAlarm: false,
        timestamp: NOW,
      },
    ],
    pricing: [
      {
        id: 'price-001-a',
        stationId: 'demo-station-001',
        product: PRODUCT,
        unitPrice: 10.00,
        currency: 'MYR',
        effectiveFrom: '2026-01-01T00:00:00.000Z',
      },
    ],
    config: {
      stationId: 'demo-station-001',
      maxDispenseVolume: 100,
      maxDispenseAmount: 300,
      maintenanceMode: false,
      enabled: true,
      emergencyStopEnabled: true,
      autoStopOnTargetReached: true,
    },
  },
  {
    id: 'demo-station-002',
    name: 'T002 - R&R Seremban',
    location: {
      latitude: 2.7220,
      longitude: 101.9362,
      address: 'R&R Seremban, KM 256, Plus Expressway (E2), 70450 Seremban, Negeri Sembilan',
    },
    timezone: 'Asia/Kuala_Lumpur',
    status: 'IDLE',
    lastHeartbeat: NOW,
    availableProducts: [PRODUCT],
    distanceKm: 2.3,
    createdAt: '2025-06-01T00:00:00.000Z',
    pumps: makePumps('demo-station-002', 'IDLE', 'IDLE'),
    tankStatus: [
      {
        id: 'tank-002-a',
        stationId: 'demo-station-002',
        product: PRODUCT,
        levelLitres: 18000,
        capacityLitres: 25000,
        temperatureC: 28.7,
        lowLevelAlarm: false,
        highLevelAlarm: false,
        timestamp: NOW,
      },
    ],
    pricing: [
      {
        id: 'price-002-a',
        stationId: 'demo-station-002',
        product: PRODUCT,
        unitPrice: 10.00,
        currency: 'MYR',
        effectiveFrom: '2026-01-01T00:00:00.000Z',
      },
    ],
    config: {
      stationId: 'demo-station-002',
      maxDispenseVolume: 100,
      maxDispenseAmount: 300,
      maintenanceMode: false,
      enabled: true,
      emergencyStopEnabled: true,
      autoStopOnTargetReached: true,
    },
  },
  {
    id: 'demo-station-003',
    name: 'T003 - R&R Tapah',
    location: {
      latitude: 4.1958,
      longitude: 101.2689,
      address: 'R&R Tapah, KM 374, Plus Expressway (E1), 35000 Tapah, Perak',
    },
    timezone: 'Asia/Kuala_Lumpur',
    status: 'DISPENSING',
    lastHeartbeat: NOW,
    availableProducts: [PRODUCT],
    distanceKm: 5.7,
    createdAt: '2025-09-15T00:00:00.000Z',
    pumps: makePumps('demo-station-003', 'IN_USE', 'IDLE'),
    tankStatus: [
      {
        id: 'tank-003-a',
        stationId: 'demo-station-003',
        product: PRODUCT,
        levelLitres: 1500,
        capacityLitres: 3000,
        temperatureC: 31.2,
        lowLevelAlarm: false,
        highLevelAlarm: false,
        timestamp: NOW,
      },
    ],
    pricing: [
      {
        id: 'price-003-a',
        stationId: 'demo-station-003',
        product: PRODUCT,
        unitPrice: 10.00,
        currency: 'MYR',
        effectiveFrom: '2026-01-01T00:00:00.000Z',
      },
    ],
    config: {
      stationId: 'demo-station-003',
      maxDispenseVolume: 100,
      maxDispenseAmount: 300,
      maintenanceMode: false,
      enabled: true,
      emergencyStopEnabled: true,
      autoStopOnTargetReached: true,
    },
  },
  {
    id: 'demo-station-004',
    name: 'T004 - R&R Yong Peng',
    location: {
      latitude: 2.0177,
      longitude: 102.9745,
      address: 'R&R Yong Peng, KM 97, Plus Expressway (E2), 83700 Yong Peng, Johor',
    },
    timezone: 'Asia/Kuala_Lumpur',
    status: 'OFFLINE',
    lastHeartbeat: '2026-03-26T22:14:00.000Z',
    availableProducts: [PRODUCT],
    distanceKm: 7.1,
    createdAt: '2025-11-01T00:00:00.000Z',
    pumps: makePumps('demo-station-004', 'OFFLINE', 'OFFLINE'),
    tankStatus: [
      {
        id: 'tank-004-a',
        stationId: 'demo-station-004',
        product: PRODUCT,
        levelLitres: 6000,
        capacityLitres: 10000,
        temperatureC: 29.9,
        lowLevelAlarm: false,
        highLevelAlarm: false,
        timestamp: '2026-03-26T22:14:00.000Z',
      },
    ],
    pricing: [
      {
        id: 'price-004-a',
        stationId: 'demo-station-004',
        product: PRODUCT,
        unitPrice: 10.00,
        currency: 'MYR',
        effectiveFrom: '2026-01-01T00:00:00.000Z',
      },
    ],
    config: {
      stationId: 'demo-station-004',
      maxDispenseVolume: 100,
      maxDispenseAmount: 300,
      maintenanceMode: false,
      enabled: false,
      emergencyStopEnabled: true,
      autoStopOnTargetReached: true,
    },
  },
];

/**
 * Look up a single demo station by ID.
 */
export function getDemoStationById(id: string): (StationDetail & { distanceKm: number }) | undefined {
  return DEMO_STATIONS.find(s => s.id === id);
}

/**
 * Deduct dispensed volume from a demo station's tank.
 * Mutates the shared DEMO_STATIONS entry in place so any screen that reads the
 * array afterwards (e.g. the Nearby Stations list) sees the updated level.
 * Also re-evaluates the lowLevelAlarm threshold (≤ 10% of capacity).
 */
export function deductDemoStationVolume(stationId: string, volumeLitres: number): void {
  const station = DEMO_STATIONS.find(s => s.id === stationId);
  if (!station?.tankStatus?.length) return;
  const tank = station.tankStatus[0];
  tank.levelLitres = Math.max(0, tank.levelLitres - volumeLitres);
  tank.lowLevelAlarm = tank.levelLitres <= tank.capacityLitres * 0.10;
  if (tank.lowLevelAlarm) {
    station.status = 'ALARM';
  }
}
