/**
 * Station API
 */

import { get, post } from './client';
import {
  Station,
  StationDetail,
  TankStatus,
  GetStationsRequest,
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';

export async function getStations(params?: GetStationsRequest): Promise<Station[]> {
  return get<Station[]>(API_ENDPOINTS.STATIONS, params);
}

export async function getStationById(id: string): Promise<StationDetail> {
  return get<StationDetail>(API_ENDPOINTS.STATION_DETAIL(id));
}

export async function getStationTank(id: string): Promise<TankStatus[]> {
  return get<TankStatus[]>(API_ENDPOINTS.STATION_TANK(id));
}

export async function getStationStatus(id: string): Promise<{
  stationId: string;
  status: Station['status'];
  lastHeartbeat: string;
  currentTransaction?: string;
}> {
  return get(API_ENDPOINTS.STATION_STATUS(id));
}

/**
 * Report a low-tank alarm to the backend.
 * The backend forwards this to the owner via SNS/SES so they can
 * dispatch a tanker to refill the station.
 */
export async function reportLowTankAlarm(stationId: string, payload: {
  tankId: string;
  product: string;
  levelLitres: number;
  capacityLitres: number;
  percentFull: number;
}): Promise<void> {
  return post(API_ENDPOINTS.STATION_ALARM(stationId), {
    code: 'TANK_LOW',
    severity: 'WARNING',
    message: `${payload.product} tank at ${payload.percentFull.toFixed(0)}% capacity (${payload.levelLitres} L remaining). Dispatch tanker for refill.`,
    details: payload,
    triggeredAt: new Date().toISOString(),
  });
}
