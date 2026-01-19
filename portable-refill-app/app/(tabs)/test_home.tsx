import { PTSManager } from '../pts/PTSManager';

export class Connect {
  private ptsManager: PTSManager;

  constructor() {
    this.ptsManager = new PTSManager();
  }
  
  async onConnectClicked(): Promise<void> {
    try {
      await this.ptsManager.open();
      console.log('PTS connected');
    } catch (err) {
      console.error('PTS connection failed', err);
    }
  }

  async getControllerType(): Promise<any> {
    if (!this.ptsManager) {
      throw new Error('PTSManager not initialized');
    }

    // Make sure the connection is open
    await this.ptsManager.open();

    // Send GetControllerType request
    const requestPayload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetControllerType',
        },
      ],
    };

    try {
      const response = await this.ptsManager.sendRequest(requestPayload);
      return response; // full JSON response
    } catch (err) {
      console.error('Failed to get controller type', err);
      throw err;
    }
  }

   async getDateTimePressed(): Promise<any> {
    if (!this.ptsManager) {
      throw new Error('PTSManager not initialized');
    }

    // Make sure the connection is open
    await this.ptsManager.open();

    // Send GetControllerType request
    const requestPayload = {
      Protocol: 'jsonPTS',
      Packets: [
        {
          Id: 1,
          Type: 'GetDateTime',
        },
      ],
    };

    try {
      const response = await this.ptsManager.sendRequest(requestPayload);
      return response; // full JSON response
    } catch (err) {
      console.error('Failed to get date time', err);
      throw err;
    }
  }
}
