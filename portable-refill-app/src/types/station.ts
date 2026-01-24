/**
 * Station-related types
 */

export type StationStatus = 
  | 'IDLE'           // Ready to dispense
  | 'DISPENSING'     // Currently dispensing
  | 'ALARM'          // Has active alarms
  | 'OFFLINE'        // Not connected
  | 'MAINTENANCE';   // Maintenance mode

export type ProductType = 
  | 'RON95' 
  | 'RON97' 
  | 'DIESEL' 
  | 'PREMIUM_DIESEL';

export interface Station {
  id: string;
  name: string;
  location: {
    latitude: number;
    longitude: number;
    address?: string;
  };
  timezone: string;
  status: StationStatus;
  lastHeartbeat: string; // ISO timestamp
  availableProducts: ProductType[];
  createdAt: string;
  updatedAt?: string;
}

export interface TankStatus {
  id: string;
  stationId: string;
  product: ProductType;
  levelLitres: number;
  capacityLitres: number;
  temperatureC: number;
  lowLevelAlarm: boolean;
  highLevelAlarm: boolean;
  timestamp: string; // ISO timestamp
}

export interface StationConfig {
  stationId: string;
  maxDispenseVolume: number; // litres
  maxDispenseAmount: number; // currency units
  maintenanceMode: boolean;
  enabled: boolean;
  emergencyStopEnabled: boolean;
  autoStopOnTargetReached: boolean;
}

export interface Pricing {
  id: string;
  stationId: string;
  product: ProductType;
  unitPrice: number; // price per litre
  currency: string;
  effectiveFrom: string;
  effectiveTo?: string;
}

export interface StationDetail extends Station {
  tankStatus: TankStatus[];
  pricing: Pricing[];
  config?: StationConfig;
  distanceKm?: number; // Distance from user if location provided
}

// Request types
export interface GetStationsRequest {
  latitude?: number;
  longitude?: number;
  radiusKm?: number;
  status?: StationStatus[];
}
