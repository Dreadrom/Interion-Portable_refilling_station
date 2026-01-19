import { HttpClientManager } from '../network/HttpClientManager';

export class Device {
  private clientManager: HttpClientManager;

  constructor() {
    this.clientManager = new HttpClientManager();
  }

  async open(): Promise<void> {
    console.log('Device: open');
    await this.clientManager.open();
    console.log('[Device] open() SUCCESS');
  }

  async close(): Promise<void> {
    await this.clientManager.close();
  }

  async send(payload: string): Promise<string> {
    try {
      console.log('[Device] Sending payload:', payload);

      // Send the request using HttpClientManager
      // Assuming HttpClientManager exposes a method for sending arbitrary JSON requests
      const response = await this.clientManager.sendJson(payload);

      // Return the response as a string
      return JSON.stringify(response);
    } catch (err) {
      console.error('[Device] send() failed', err);
      throw err;
    }
  }
}
