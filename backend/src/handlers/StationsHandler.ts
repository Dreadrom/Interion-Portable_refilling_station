/**
 * Stations Handler Lambda Function
 * Handles all station-related endpoints including PTS-2 controller integration
 */

import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { query, queryOne } from '../database/connection';
import { extractToken, verifyToken } from '../utils/jwt';
import {
  errorResponse,
  internalError,
  notFoundError,
  successResponse,
  unauthorizedError,
  validationError,
} from '../utils/response';

interface Station {
  StationID: string;
  StationName: string;
  Location: string;
  Latitude: number;
  Longitude: number;
  Timezone: string;
  Status: 'IDLE' | 'DISPENSING' | 'ALARM' | 'OFFLINE' | 'MAINTENANCE';
  LastHeartbeat: string;
  CreatedAt: string;
  UpdatedAt: string;
  PTSHost?: string;
  PTSPort?: number;
  PTSProtocol?: 'HTTP' | 'HTTPS';
}

interface TankStatus {
  TankID: string;
  StationID: string;
  Product: string;
  LevelLitres: number;
  CapacityLitres: number;
  TemperatureC: number;
  LowLevelAlarm: boolean;
  HighLevelAlarm: boolean;
  Timestamp: string;
}

/**
 * PTS-2 Controller Communication Helper
 */
class PTSController {
  private baseUrl: string;

  constructor(host: string, port: number, protocol: 'HTTP' | 'HTTPS' = 'HTTP') {
    const scheme = protocol === 'HTTPS' ? 'https' : 'http';
    this.baseUrl = `${scheme}://${host}:${port}`;
  }

  /**
   * Send request to PTS-2 controller
   */
  private async sendRequest(payload: any): Promise<any> {
    try {
      console.log('[PTS] Sending request:', JSON.stringify(payload));

      const response = await fetch(this.baseUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(`PTS request failed: ${response.status} ${response.statusText}`);
      }

      const data = await response.json();
      console.log('[PTS] Received response:', JSON.stringify(data));

      // Check for errors in response
      if (data.Packets && data.Packets.some((p: any) => p.Result === 'Fail')) {
        const errorPacket = data.Packets.find((p: any) => p.Result === 'Fail');
        throw new Error(`PTS error: ${errorPacket?.ErrorMessage || 'Unknown error'}`);
      }

      return data;
    } catch (error: any) {
      console.error('[PTS] Request failed:', error);
      throw error;
    }
  }

  /**
   * Get Controller Type (3.1.1.7)
   */
  async getControllerType(): Promise<string> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetControllerType',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    return response.Packets?.[0]?.Data?.Type || 'Unknown';
  }

  /**
   * Get Date/Time (3.1.1.8)
   */
  async getDateTime(): Promise<{ date: string; time: string }> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetDateTime',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    const data = response.Packets?.[0]?.Data;
    return {
      date: data?.Date || '',
      time: data?.Time || '',
    };
  }

  /**
   * Get Product Prices (3.1.1.9)
   */
  async getProductPrices(): Promise<Array<{ product: number; price: number }>> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetProductPrices',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    const prices = response.Packets?.[0]?.Data?.Prices || [];
    return prices.map((p: any) => ({
      product: p.Product,
      price: p.Price,
    }));
  }

  /**
   * Get Tanks (3.1.1.10)
   */
  async getTanks(): Promise<Array<{
    tank: number;
    product: number;
    volume: number;
    tcVolume: number;
    ullage: number;
    height: number;
    water: number;
    temp: number;
  }>> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetTanks',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    const tanks = response.Packets?.[0]?.Data?.Tanks || [];
    return tanks.map((t: any) => ({
      tank: t.Tank,
      product: t.Product,
      volume: t.Volume,
      tcVolume: t.TCVolume,
      ullage: t.Ullage,
      height: t.Height,
      water: t.Water,
      temp: t.Temp,
    }));
  }

  /**
   * Get Totalizers (3.1.1.11)
   */
  async getTotalizers(): Promise<Array<{
    hose: number;
    product: number;
    volume: number;
    amount: number;
  }>> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetTotalizers',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    const totalizers = response.Packets?.[0]?.Data?.Totalizers || [];
    return totalizers.map((t: any) => ({
      hose: t.Hose,
      product: t.Product,
      volume: t.Volume,
      amount: t.Amount,
    }));
  }

  /**
   * Get Deliveries (3.1.1.12)
   */
  async getDeliveries(): Promise<Array<{
    hose: number;
    product: number;
    volume: number;
    amount: number;
    price: number;
  }>> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetDeliveries',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    const deliveries = response.Packets?.[0]?.Data?.Deliveries || [];
    return deliveries.map((d: any) => ({
      hose: d.Hose,
      product: d.Product,
      volume: d.Volume,
      amount: d.Amount,
      price: d.Price,
    }));
  }

  /**
   * Authorize Hose (3.1.1.13)
   */
  async authorizeHose(hose: number, type: 'Volume' | 'Amount', value: number): Promise<void> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'Authorize',
          Data: {
            Hose: hose,
            Type: type,
            Value: value,
          },
        },
      ],
    };

    await this.sendRequest(payload);
  }

  /**
   * Stop Delivery (3.1.1.14)
   */
  async stopDelivery(hose: number): Promise<void> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'Stop',
          Data: {
            Hose: hose,
          },
        },
      ],
    };

    await this.sendRequest(payload);
  }

  /**
   * Emergency Stop (3.1.1.15)
   */
  async emergencyStop(): Promise<void> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'EmergencyStop',
        },
      ],
    };

    await this.sendRequest(payload);
  }

  /**
   * Clear Delivery (3.1.1.16)
   */
  async clearDelivery(hose: number): Promise<void> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'Clear',
          Data: {
            Hose: hose,
          },
        },
      ],
    };

    await this.sendRequest(payload);
  }

  /**
   * Get Alarms (3.1.1.17)
   */
  async getAlarms(): Promise<Array<{
    id: number;
    priority: number;
    active: boolean;
    acknowledged: boolean;
    text: string;
  }>> {
    const payload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetAlarms',
        },
      ],
    };

    const response = await this.sendRequest(payload);
    const alarms = response.Packets?.[0]?.Data?.Alarms || [];
    return alarms.map((a: any) => ({
      id: a.Id,
      priority: a.Priority,
      active: a.Active,
      acknowledged: a.Acknowledged,
      text: a.Text,
    }));
  }
}

/**
 * Main Lambda handler
 */
export async function handler(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  console.log('Event:', JSON.stringify(event, null, 2));

  const path = event.path;
  const method = event.httpMethod;

  try {
    // Public endpoints (no auth required)
    if (path === '/stations' && method === 'GET') {
      return await handleGetStations(event);
    }

    if (path.match(/^\/stations\/[^/]+$/) && method === 'GET') {
      const id = path.split('/')[2];
      return await handleGetStationById(id);
    }

    if (path.match(/^\/stations\/[^/]+\/tank$/) && method === 'GET') {
      const id = path.split('/')[2];
      return await handleGetStationTank(id);
    }

    if (path.match(/^\/stations\/[^/]+\/status$/) && method === 'GET') {
      const id = path.split('/')[2];
      return await handleGetStationStatus(id);
    }

    // Protected endpoints (require auth)
    const token = extractToken(event);
    if (!token) {
      return unauthorizedError('Missing authorization token');
    }

    const decoded = verifyToken(token);
    if (!decoded) {
      return unauthorizedError('Invalid or expired token');
    }

    // Handle OPTIONS for CORS
    if (method === 'OPTIONS') {
      return {
        statusCode: 200,
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
          'Access-Control-Allow-Headers': 'Content-Type, Authorization',
        },
        body: '',
      };
    }

    return errorResponse('NOT_FOUND', 'Endpoint not found', 404);
  } catch (error: any) {
    console.error('Error:', error);
    return internalError(error.message || 'Internal server error');
  }
}

/**
 * Get list of stations
 */
async function handleGetStations(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  try {
    const params = event.queryStringParameters || {};
    const { latitude, longitude, radiusKm, status } = params;

    let sql = `
      SELECT 
        StationID as id,
        StationName as name,
        Location as address,
        Latitude as latitude,
        Longitude as longitude,
        Timezone as timezone,
        Status as status,
        LastHeartbeat as lastHeartbeat,
        CreatedAt as createdAt,
        UpdatedAt as updatedAt
      FROM Stations
      WHERE 1=1
    `;

    const sqlParams: any[] = [];

    // Filter by status if provided
    if (status) {
      const statuses = status.split(',');
      const placeholders = statuses.map(() => '?').join(',');
      sql += ` AND Status IN (${placeholders})`;
      sqlParams.push(...statuses);
    }

    // Calculate distance if location provided
    if (latitude && longitude) {
      const lat = parseFloat(latitude);
      const lng = parseFloat(longitude);
      const radius = radiusKm ? parseFloat(radiusKm) : 10;

      sql = `
        SELECT *,
          (6371 * acos(
            cos(radians(?)) * cos(radians(Latitude)) *
            cos(radians(Longitude) - radians(?)) +
            sin(radians(?)) * sin(radians(Latitude))
          )) AS distance
        FROM (${sql}) AS stations
        WHERE distance <= ?
        ORDER BY distance
      `;
      sqlParams.push(lat, lng, lat, radius);
    }

    const stations = await query(sql, sqlParams);

    // Get available products for each station
    const stationsWithProducts = await Promise.all(
      stations.map(async (station: any) => {
        const products = await query(
          `
          SELECT DISTINCT Product as product
          FROM StationPricing
          WHERE StationID = ? AND (EffectiveTo IS NULL OR EffectiveTo > NOW())
          ORDER BY product
        `,
          [station.id]
        );

        return {
          ...station,
          availableProducts: products.map((p: any) => p.product),
        };
      })
    );

    return successResponse(stationsWithProducts);
  } catch (error: any) {
    console.error('Error getting stations:', error);
    return internalError(error.message);
  }
}

/**
 * Get station by ID
 */
async function handleGetStationById(id: string): Promise<APIGatewayProxyResult> {
  try {
    const station = await queryOne(
      `
      SELECT 
        StationID as id,
        StationName as name,
        Location as address,
        Latitude as latitude,
        Longitude as longitude,
        Timezone as timezone,
        Status as status,
        LastHeartbeat as lastHeartbeat,
        CreatedAt as createdAt,
        UpdatedAt as updatedAt,
        PTSHost,
        PTSPort,
        PTSProtocol
      FROM Stations
      WHERE StationID = ?
    `,
      [id]
    );

    if (!station) {
      return notFoundError('Station not found');
    }

    // Get tank status from PTS-2 controller if available
    let tankStatus: any[] = [];
    if (station.PTSHost && station.PTSPort) {
      try {
        const pts = new PTSController(
          station.PTSHost,
          station.PTSPort,
          station.PTSProtocol || 'HTTP'
        );
        const tanks = await pts.getTanks();

        tankStatus = tanks.map((tank) => ({
          id: `${id}-tank-${tank.tank}`,
          stationId: id,
          product: getProductName(tank.product),
          levelLitres: tank.volume,
          capacityLitres: tank.volume + tank.ullage,
          temperatureC: tank.temp,
          lowLevelAlarm: tank.volume < 1000, // Example threshold
          highLevelAlarm: tank.ullage < 500, // Example threshold
          timestamp: new Date().toISOString(),
        }));
      } catch (error) {
        console.error('Error getting tank status from PTS:', error);
        // Fall back to database if PTS fails
      }
    }

    // If PTS failed or not configured, get from database
    if (tankStatus.length === 0) {
      tankStatus = await query(
        `
        SELECT 
          TankID as id,
          StationID as stationId,
          Product as product,
          LevelLitres as levelLitres,
          CapacityLitres as capacityLitres,
          TemperatureC as temperatureC,
          LowLevelAlarm as lowLevelAlarm,
          HighLevelAlarm as highLevelAlarm,
          Timestamp as timestamp
        FROM TankStatus
        WHERE StationID = ?
        ORDER BY Product
      `,
        [id]
      );
    }

    // Get pricing
    const pricing = await query(
      `
      SELECT 
        PricingID as id,
        StationID as stationId,
        Product as product,
        UnitPrice as unitPrice,
        Currency as currency,
        EffectiveFrom as effectiveFrom,
        EffectiveTo as effectiveTo
      FROM StationPricing
      WHERE StationID = ? AND (EffectiveTo IS NULL OR EffectiveTo > NOW())
      ORDER BY Product
    `,
      [id]
    );

    // Get config
    const config = await queryOne(
      `
      SELECT 
        StationID as stationId,
        MaxDispenseVolume as maxDispenseVolume,
        MaxDispenseAmount as maxDispenseAmount,
        MaintenanceMode as maintenanceMode,
        Enabled as enabled,
        EmergencyStopEnabled as emergencyStopEnabled,
        AutoStopOnTargetReached as autoStopOnTargetReached
      FROM StationConfig
      WHERE StationID = ?
    `,
      [id]
    );

    const stationDetail = {
      ...station,
      tankStatus,
      pricing,
      config: config || undefined,
      availableProducts: pricing.map((p: any) => p.product),
    };

    return successResponse(stationDetail);
  } catch (error: any) {
    console.error('Error getting station detail:', error);
    return internalError(error.message);
  }
}

/**
 * Get station tank status
 */
async function handleGetStationTank(id: string): Promise<APIGatewayProxyResult> {
  try {
    const station = await queryOne(
      'SELECT PTSHost, PTSPort, PTSProtocol FROM Stations WHERE StationID = ?',
      [id]
    );

    if (!station) {
      return notFoundError('Station not found');
    }

    let tankStatus: any[] = [];

    // Try to get from PTS-2 controller first
    if (station.PTSHost && station.PTSPort) {
      try {
        const pts = new PTSController(
          station.PTSHost,
          station.PTSPort,
          station.PTSProtocol || 'HTTP'
        );
        const tanks = await pts.getTanks();

        tankStatus = tanks.map((tank) => ({
          id: `${id}-tank-${tank.tank}`,
          stationId: id,
          product: getProductName(tank.product),
          levelLitres: tank.volume,
          capacityLitres: tank.volume + tank.ullage,
          temperatureC: tank.temp,
          lowLevelAlarm: tank.volume < 1000,
          highLevelAlarm: tank.ullage < 500,
          timestamp: new Date().toISOString(),
        }));

        return successResponse(tankStatus);
      } catch (error) {
        console.error('Error getting tank status from PTS:', error);
        // Fall through to database query
      }
    }

    // Fall back to database
    tankStatus = await query(
      `
      SELECT 
        TankID as id,
        StationID as stationId,
        Product as product,
        LevelLitres as levelLitres,
        CapacityLitres as capacityLitres,
        TemperatureC as temperatureC,
        LowLevelAlarm as lowLevelAlarm,
        HighLevelAlarm as highLevelAlarm,
        Timestamp as timestamp
      FROM TankStatus
      WHERE StationID = ?
      ORDER BY Product
    `,
      [id]
    );

    return successResponse(tankStatus);
  } catch (error: any) {
    console.error('Error getting tank status:', error);
    return internalError(error.message);
  }
}

/**
 * Get station status
 */
async function handleGetStationStatus(id: string): Promise<APIGatewayProxyResult> {
  try {
    const station = await queryOne(
      `
      SELECT 
        StationID,
        Status,
        LastHeartbeat,
        PTSHost,
        PTSPort,
        PTSProtocol
      FROM Stations
      WHERE StationID = ?
    `,
      [id]
    );

    if (!station) {
      return notFoundError('Station not found');
    }

    // Check for active transaction
    const activeTransaction = await queryOne(
      `
      SELECT TransactionID
      FROM Transactions
      WHERE StationID = ? AND Status = 'IN_PROGRESS'
      ORDER BY CreatedAt DESC
      LIMIT 1
    `,
      [id]
    );

    // Try to get real-time status from PTS-2 controller
    let ptsStatus = null;
    if (station.PTSHost && station.PTSPort) {
      try {
        const pts = new PTSController(
          station.PTSHost,
          station.PTSPort,
          station.PTSProtocol || 'HTTP'
        );

        // Get current deliveries to check if dispensing
        const deliveries = await pts.getDeliveries();
        const hasActiveDelivery = deliveries.some((d) => d.volume > 0);

        // Get alarms
        const alarms = await pts.getAlarms();
        const hasActiveAlarm = alarms.some((a) => a.active && !a.acknowledged);

        // Determine status
        let status = station.Status;
        if (hasActiveAlarm) {
          status = 'ALARM';
        } else if (hasActiveDelivery) {
          status = 'DISPENSING';
        } else {
          status = 'IDLE';
        }

        ptsStatus = {
          stationId: id,
          status,
          lastHeartbeat: new Date().toISOString(),
          currentTransaction: activeTransaction?.TransactionID || undefined,
          ptsConnected: true,
          alarms: alarms.filter((a) => a.active),
          deliveries,
        };
      } catch (error) {
        console.error('Error getting PTS status:', error);
      }
    }

    // Return PTS status if available, otherwise database status
    if (ptsStatus) {
      return successResponse(ptsStatus);
    }

    return successResponse({
      stationId: id,
      status: station.Status,
      lastHeartbeat: station.LastHeartbeat,
      currentTransaction: activeTransaction?.TransactionID || undefined,
      ptsConnected: false,
    });
  } catch (error: any) {
    console.error('Error getting station status:', error);
    return internalError(error.message);
  }
}

/**
 * Helper function to map product number to name
 */
function getProductName(productNumber: number): string {
  const productMap: Record<number, string> = {
    1: 'RON95',
    2: 'RON97',
    3: 'DIESEL',
    4: 'PREMIUM_DIESEL',
  };
  return productMap[productNumber] || 'UNKNOWN';
}
