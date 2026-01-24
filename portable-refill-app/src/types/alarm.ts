/**
 * Alarm-related types
 */

export type AlarmSeverity = 
  | 'CRITICAL'  // Immediate action required
  | 'WARNING'   // Attention needed
  | 'INFO';     // Informational

export type AlarmCode = 
  | 'TANK_LOW'           // Tank level below threshold
  | 'TANK_HIGH'          // Tank overfill risk
  | 'TANK_EMPTY'         // Tank empty
  | 'TEMP_HIGH'          // Temperature too high
  | 'TEMP_LOW'           // Temperature too low
  | 'LEAK_DETECTED'      // Leak sensor triggered
  | 'PRESSURE_HIGH'      // High pressure
  | 'PRESSURE_LOW'       // Low pressure
  | 'FLOW_ANOMALY'       // Unexpected flow rate
  | 'EMERGENCY_STOP'     // Emergency stop activated
  | 'POWER_FAILURE'      // Power loss/backup
  | 'COMM_FAILURE'       // Communication lost
  | 'SENSOR_FAULT'       // Sensor malfunction
  | 'PUMP_FAULT'         // Pump malfunction
  | 'VALVE_FAULT'        // Valve malfunction
  | 'DISPENSE_TIMEOUT'   // Dispense took too long
  | 'UNAUTHORIZED_ACCESS'// Security alarm
  | 'MAINTENANCE_DUE'    // Scheduled maintenance
  | 'OTHER';             // Other alarm

export interface Alarm {
  id: string;
  stationId: string;
  code: AlarmCode;
  severity: AlarmSeverity;
  message: string;
  active: boolean;
  
  // Context
  details?: Record<string, any>;
  
  // Timestamps
  triggeredAt: string;
  clearedAt?: string;
  acknowledgedAt?: string;
  acknowledgedBy?: string; // User ID
  
  // Actions taken
  actions?: string[];
  notes?: string;
}

export interface GetAlarmsRequest {
  stationId?: string;
  active?: boolean;
  severity?: AlarmSeverity[];
  startDate?: string;
  endDate?: string;
  limit?: number;
  offset?: number;
}

export interface GetAlarmsResponse {
  alarms: Alarm[];
  total: number;
  limit: number;
  offset: number;
}

export interface AcknowledgeAlarmRequest {
  alarmId: string;
  notes?: string;
}

export interface ClearAlarmRequest {
  alarmId: string;
  notes?: string;
}
