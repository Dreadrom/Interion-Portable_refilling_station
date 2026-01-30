/**
 * Station API
 */

import { Alert } from 'react-native';
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
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: getStations()', JSON.stringify(params || {}));
  
  await new Promise(resolve => setTimeout(resolve, 500));
  
  return [
    {
      id: 'test-station-1',
      name: 'Interion Test Station Alpha',
      location: {
        latitude: 3.139,
        longitude: 101.6869,
        address: '123 Jalan Test, Kuala Lumpur, 50450',
      },
      timezone: 'Asia/Kuala_Lumpur',
      status: 'IDLE',
      lastHeartbeat: new Date().toISOString(),
      availableProducts: ['RON95', 'RON97', 'DIESEL'],
      createdAt: new Date().toISOString(),
    },
    {
      id: 'test-station-2',
      name: 'Interion Station Beta',
      location: {
        latitude: 3.145,
        longitude: 101.693,
        address: '456 Jalan Petaling, Kuala Lumpur, 50100',
      },
      timezone: 'Asia/Kuala_Lumpur',
      status: 'DISPENSING',
      lastHeartbeat: new Date().toISOString(),
      availableProducts: ['RON95', 'DIESEL'],
      createdAt: new Date().toISOString(),
    },
    {
      id: 'test-station-3',
      name: 'Interion Station Gamma',
      location: {
        latitude: 3.133,
        longitude: 101.681,
        address: '789 Jalan Ampang, Kuala Lumpur, 50088',
      },
      timezone: 'Asia/Kuala_Lumpur',
      status: 'IDLE',
      lastHeartbeat: new Date().toISOString(),
      availableProducts: ['RON95', 'RON97', 'DIESEL', 'PREMIUM_DIESEL'],
      createdAt: new Date().toISOString(),
    },
  ];
  
  // TODO: Uncomment when backend is ready
  // return get<Station[]>(API_ENDPOINTS.STATIONS, params);
}

/**
 * Get station detail by ID
 */
export async function getStationById(id: string): Promise<StationDetail> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: getStationById()', `Station ID: ${id}`);
  
  await new Promise(resolve => setTimeout(resolve, 800));
  
  // Mock data for different stations
  const stationData: Record<string, any> = {
    'test-station-1': {
      id: id,
      name: 'Interion Test Station Alpha',
      location: {
        latitude: 3.139,
        longitude: 101.6869,
        address: '123 Jalan Test, Kuala Lumpur, 50450',
      },
      status: 'IDLE',
      availableProducts: ['RON95', 'RON97', 'DIESEL'],
      tankStatus: [
        {
          id: `${id}-tank-1`,
          stationId: id,
          product: 'RON95',
          levelLitres: 3500,
          capacityLitres: 5000,
          temperatureC: 28.5,
          lowLevelAlarm: false,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
        {
          id: `${id}-tank-2`,
          stationId: id,
          product: 'RON97',
          levelLitres: 800,
          capacityLitres: 5000,
          temperatureC: 29.1,
          lowLevelAlarm: true,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
        {
          id: `${id}-tank-3`,
          stationId: id,
          product: 'DIESEL',
          levelLitres: 4200,
          capacityLitres: 5000,
          temperatureC: 27.8,
          lowLevelAlarm: false,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
      ],
    },
    'test-station-2': {
      id: id,
      name: 'Interion Station Beta',
      location: {
        latitude: 3.145,
        longitude: 101.693,
        address: '456 Jalan Petaling, Kuala Lumpur, 50100',
      },
      status: 'DISPENSING',
      availableProducts: ['RON95', 'DIESEL'],
      tankStatus: [
        {
          id: `${id}-tank-1`,
          stationId: id,
          product: 'RON95',
          levelLitres: 4500,
          capacityLitres: 5000,
          temperatureC: 29.2,
          lowLevelAlarm: false,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
        {
          id: `${id}-tank-2`,
          stationId: id,
          product: 'DIESEL',
          levelLitres: 50,
          capacityLitres: 5000,
          temperatureC: 28.0,
          lowLevelAlarm: true,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
      ],
    },
    'test-station-3': {
      id: id,
      name: 'Interion Station Gamma',
      location: {
        latitude: 3.133,
        longitude: 101.681,
        address: '789 Jalan Ampang, Kuala Lumpur, 50088',
      },
      status: 'IDLE',
      availableProducts: ['RON95', 'RON97', 'DIESEL', 'PREMIUM_DIESEL'],
      tankStatus: [
        {
          id: `${id}-tank-1`,
          stationId: id,
          product: 'RON95',
          levelLitres: 2800,
          capacityLitres: 5000,
          temperatureC: 28.8,
          lowLevelAlarm: false,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
        {
          id: `${id}-tank-2`,
          stationId: id,
          product: 'RON97',
          levelLitres: 3900,
          capacityLitres: 5000,
          temperatureC: 29.0,
          lowLevelAlarm: false,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
        {
          id: `${id}-tank-3`,
          stationId: id,
          product: 'DIESEL',
          levelLitres: 4800,
          capacityLitres: 5000,
          temperatureC: 27.5,
          lowLevelAlarm: false,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
        {
          id: `${id}-tank-4`,
          stationId: id,
          product: 'PREMIUM_DIESEL',
          levelLitres: 950,
          capacityLitres: 3000,
          temperatureC: 27.8,
          lowLevelAlarm: true,
          highLevelAlarm: false,
          timestamp: new Date().toISOString(),
        },
      ],
    },
  };

  const station = stationData[id] || stationData['test-station-1'];
  
  return {
    ...station,
    timezone: 'Asia/Kuala_Lumpur',
    lastHeartbeat: new Date().toISOString(),
    createdAt: new Date().toISOString(),
    pricing: [
      {
        id: `${id}-price-1`,
        stationId: id,
        product: 'RON95',
        unitPrice: 2.05,
        currency: 'MYR',
        effectiveFrom: new Date().toISOString(),
      },
      {
        id: `${id}-price-2`,
        stationId: id,
        product: 'RON97',
        unitPrice: 2.35,
        currency: 'MYR',
        effectiveFrom: new Date().toISOString(),
      },
      {
        id: `${id}-price-3`,
        stationId: id,
        product: 'DIESEL',
        unitPrice: 2.15,
        currency: 'MYR',
        effectiveFrom: new Date().toISOString(),
      },
    ],
    config: {
      stationId: id,
      maxDispenseVolume: 100,
      maxDispenseAmount: 500,
      maintenanceMode: false,
      enabled: true,
      emergencyStopEnabled: false,
      autoStopOnTargetReached: true,
    },
  };
  
  // TODO: Uncomment when backend is ready
  // return get<StationDetail>(API_ENDPOINTS.STATION_DETAIL(id));
}

/**
 * Get station tank status
 */
export async function getStationTank(id: string): Promise<TankStatus[]> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: getStationTank()', `Station ID: ${id}`);
  
  await new Promise(resolve => setTimeout(resolve, 500));
  
  return [
    {
      id: `${id}-tank-1`,
      stationId: id,
      product: 'RON95',
      levelLitres: 3500,
      capacityLitres: 5000,
      temperatureC: 28.5,
      lowLevelAlarm: false,
      highLevelAlarm: false,
      timestamp: new Date().toISOString(),
    },
    {
      id: `${id}-tank-2`,
      stationId: id,
      product: 'RON97',
      levelLitres: 800,
      capacityLitres: 5000,
      temperatureC: 29.1,
      lowLevelAlarm: true,
      highLevelAlarm: false,
      timestamp: new Date().toISOString(),
    },
  ];
  
  // TODO: Uncomment when backend is ready
  // return get<TankStatus[]>(API_ENDPOINTS.STATION_TANK(id));
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
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: getStationStatus()', `Station ID: ${id}`);
  
  await new Promise(resolve => setTimeout(resolve, 500));
  
  return {
    stationId: id,
    status: 'IDLE',
    lastHeartbeat: new Date().toISOString(),
  };
  
  // TODO: Uncomment when backend is ready
  // return get(API_ENDPOINTS.STATION_STATUS(id));
}

