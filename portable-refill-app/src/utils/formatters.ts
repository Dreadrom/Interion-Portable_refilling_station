/**
 * Utility functions for formatting data
 */

/**
 * Format currency amount
 */
export function formatCurrency(
  amount: number,
  currency: string = 'MYR',
  locale: string = 'en-MY'
): string {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
}

/**
 * Format volume with unit
 */
export function formatVolume(litres: number, decimals: number = 2): string {
  return `${litres.toFixed(decimals)} L`;
}

/**
 * Format flow rate
 */
export function formatFlowRate(litresPerMinute: number, decimals: number = 1): string {
  return `${litresPerMinute.toFixed(decimals)} L/min`;
}

/**
 * Format date and time
 */
export function formatDateTime(
  isoString: string,
  locale: string = 'en-MY',
  options?: Intl.DateTimeFormatOptions
): string {
  const date = new Date(isoString);
  const defaultOptions: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    ...options,
  };
  return date.toLocaleString(locale, defaultOptions);
}

/**
 * Format date only
 */
export function formatDate(
  isoString: string,
  locale: string = 'en-MY'
): string {
  const date = new Date(isoString);
  return date.toLocaleDateString(locale, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

/**
 * Format time only
 */
export function formatTime(
  isoString: string,
  locale: string = 'en-MY'
): string {
  const date = new Date(isoString);
  return date.toLocaleTimeString(locale, {
    hour: '2-digit',
    minute: '2-digit',
  });
}

/**
 * Format relative time (e.g., "2 hours ago")
 */
export function formatRelativeTime(isoString: string): string {
  const date = new Date(isoString);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24);

  if (diffSec < 60) return 'just now';
  if (diffMin < 60) return `${diffMin}m ago`;
  if (diffHour < 24) return `${diffHour}h ago`;
  if (diffDay < 7) return `${diffDay}d ago`;
  return formatDate(isoString);
}

/**
 * Format duration in seconds to readable string
 */
export function formatDuration(seconds: number): string {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;

  if (hours > 0) {
    return `${hours}h ${minutes}m ${secs}s`;
  }
  if (minutes > 0) {
    return `${minutes}m ${secs}s`;
  }
  return `${secs}s`;
}

/**
 * Format distance
 */
export function formatDistance(km: number): string {
  if (km < 1) {
    return `${Math.round(km * 1000)}m`;
  }
  return `${km.toFixed(1)} km`;
}

/**
 * Format percentage
 */
export function formatPercentage(value: number, decimals: number = 0): string {
  return `${value.toFixed(decimals)}%`;
}

/**
 * Format tank level percentage
 */
export function formatTankLevel(currentLitres: number, capacityLitres: number): string {
  const percentage = (currentLitres / capacityLitres) * 100;
  return `${percentage.toFixed(0)}% (${formatVolume(currentLitres)})`;
}

/**
 * Truncate text with ellipsis
 */
export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength - 3) + '...';
}

/**
 * Capitalize first letter
 */
export function capitalizeFirst(text: string): string {
  if (!text) return '';
  return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
}

/**
 * Format product name
 */
export function formatProductName(product: string): string {
  // RON95 -> RON 95, PREMIUM_DIESEL -> Premium Diesel
  return product
    .replace(/_/g, ' ')
    .replace(/([A-Z])(\d)/g, '$1 $2')
    .split(' ')
    .map(word => capitalizeFirst(word))
    .join(' ');
}

/**
 * Format phone number (Malaysia format)
 */
export function formatPhoneNumber(phone: string): string {
  // Remove non-digits
  const cleaned = phone.replace(/\D/g, '');
  
  // Format: +60 12-345 6789
  if (cleaned.startsWith('60') && cleaned.length === 11) {
    return `+${cleaned.slice(0, 2)} ${cleaned.slice(2, 4)}-${cleaned.slice(4, 7)} ${cleaned.slice(7)}`;
  }
  
  // Format: 012-345 6789
  if (cleaned.length === 10) {
    return `${cleaned.slice(0, 3)}-${cleaned.slice(3, 6)} ${cleaned.slice(6)}`;
  }
  
  return phone;
}
