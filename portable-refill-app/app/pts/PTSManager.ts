import { Device } from './Device';

export class PTSManager {
  private device: Device;

  constructor() {
    this.device = new Device();
  }

  async open(): Promise<void> {
    console.log('PTSManager: open');
    await this.device.open();
    console.log('[PTSManager] open() SUCCESS');
  }

  async close(): Promise<void> {
    await this.device.close();
  }

   async sendRequest(payload: any): Promise<any> {
    if (!this.device) {
      throw new Error('Device not initialized');
    }

    try {
      console.log('[PTSManager] Sending request:', payload);

      // Assuming your Device class has a method like `send` that returns the raw response
      const rawResponse = await this.device.send(JSON.stringify(payload));

      // Parse and return JSON response
      const jsonResponse = JSON.parse(rawResponse);
      console.log('[PTSManager] Received response:', jsonResponse);

      return jsonResponse;
    } catch (err) {
      console.error('[PTSManager] sendRequest failed', err);
      throw err;
    }
  }
}
