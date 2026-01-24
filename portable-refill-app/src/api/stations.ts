/**
 * Station API
 */

import { get } from './client';
import { 
  Station, 
  StationDetail, 
  TankStatus, 
  GetStationsRequest 
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';

/**
 * Get list of stations (optionally filtered by location)
 */
export async function getStations(params?: GetStationsRequest): Promise<Station[]> {
  return get<Station[]>(API_ENDPOINTS.STATIONS, params);
}

/**
 * Get station detail by ID
 */
export async function getStationById(id: string): Promise<StationDetail> {
  return get<StationDetail>(API_ENDPOINTS.STATION_DETAIL(id));
}

/**
 * Get station tank status
 */
export async function getStationTank(id: string): Promise<TankStatus[]> {
  return get<TankStatus[]>(API_ENDPOINTS.STATION_TANK(id));
}

/**
 * Get station status (real-time)
 */
export async function getStationStatus(id: string): Promise<{
  stationId: string;
  status: Station['status'];
  lastHeartbeat: string;
  currentTransaction?: string;
}> {
  return get(API_ENDPOINTS.STATION_STATUS(id));
}
