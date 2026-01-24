/**
 * Dispense API
 */

import { post, get } from './client';
import { 
  DispenseStartRequest,
  DispenseStartResponse,
  DispenseStopRequest,
  DispenseStopResponse,
  DispenseProgressResponse,
  DispenseProgress,
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';

/**
 * Start dispensing
 */
export async function startDispense(data: DispenseStartRequest): Promise<DispenseStartResponse> {
  return post<DispenseStartResponse>(API_ENDPOINTS.DISPENSE_START, data);
}

/**
 * Stop dispensing
 */
export async function stopDispense(data: DispenseStopRequest): Promise<DispenseStopResponse> {
  return post<DispenseStopResponse>(API_ENDPOINTS.DISPENSE_STOP, data);
}

/**
 * Get dispense progress
 */
export async function getDispenseProgress(transactionId: string): Promise<DispenseProgressResponse> {
  return get<DispenseProgressResponse>(API_ENDPOINTS.DISPENSE_PROGRESS(transactionId));
}

/**
 * Poll dispense progress until completed
 */
export async function pollDispenseProgress(
  transactionId: string,
  options: {
    interval?: number;
    onUpdate?: (progress: DispenseProgress) => void;
  } = {}
): Promise<DispenseProgress> {
  const { interval = 1000, onUpdate } = options;
  
  return new Promise((resolve, reject) => {
    const checkProgress = async () => {
      try {
        const { progress } = await getDispenseProgress(transactionId);
        
        // Call update callback
        if (onUpdate) {
          onUpdate(progress);
        }
        
        // Check if dispensing is complete
        if (progress.status === 'COMPLETED' || progress.status === 'STOPPED') {
          resolve(progress);
          return;
        }
        
        if (progress.status === 'FAILED' || progress.status === 'CANCELLED') {
          reject(new Error(`Dispense ${progress.status.toLowerCase()}`));
          return;
        }
        
        // Continue polling if still in progress
        if (progress.status === 'IN_PROGRESS' || progress.status === 'PENDING') {
          setTimeout(checkProgress, interval);
        }
      } catch (error) {
        reject(error);
      }
    };
    
    checkProgress();
  });
}
