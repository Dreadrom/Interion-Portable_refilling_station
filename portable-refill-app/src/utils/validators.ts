/**
 * Validation utility functions
 */

/**
 * Validate email format
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate password strength
 * Requires: min 8 chars, at least 1 uppercase, 1 lowercase, 1 number
 */
export function isValidPassword(password: string): boolean {
  if (password.length < 8) return false;
  
  const hasUppercase = /[A-Z]/.test(password);
  const hasLowercase = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  
  return hasUppercase && hasLowercase && hasNumber;
}

/**
 * Get password strength score (0-4)
 */
export function getPasswordStrength(password: string): {
  score: number;
  label: 'Very Weak' | 'Weak' | 'Fair' | 'Good' | 'Strong';
} {
  let score = 0;
  
  if (password.length >= 8) score++;
  if (password.length >= 12) score++;
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score++;
  
  const labels: Array<'Very Weak' | 'Weak' | 'Fair' | 'Good' | 'Strong'> = [
    'Very Weak',
    'Weak',
    'Fair',
    'Good',
    'Strong',
  ];
  
  const normalizedScore = Math.min(score, 4);
  return {
    score: normalizedScore,
    label: labels[normalizedScore],
  };
}

/**
 * Validate phone number (Malaysia format)
 */
export function isValidPhoneNumber(phone: string): boolean {
  // Remove non-digits
  const cleaned = phone.replace(/\D/g, '');
  
  // Malaysia phone: 10-11 digits (with or without country code)
  if (cleaned.length === 10) {
    // Mobile: starts with 01
    return /^01[0-9]{8}$/.test(cleaned);
  }
  
  if (cleaned.length === 11) {
    // With country code: +60 1x-xxx xxxx
    return /^601[0-9]{8}$/.test(cleaned);
  }
  
  return false;
}

/**
 * Validate amount (positive number with max 2 decimals)
 */
export function isValidAmount(amount: string | number): boolean {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  
  if (isNaN(num) || num <= 0) return false;
  
  // Check max 2 decimal places
  const str = num.toString();
  const decimalIndex = str.indexOf('.');
  if (decimalIndex !== -1) {
    const decimals = str.substring(decimalIndex + 1).length;
    if (decimals > 2) return false;
  }
  
  return true;
}

/**
 * Validate volume (positive number)
 */
export function isValidVolume(volume: string | number): boolean {
  const num = typeof volume === 'string' ? parseFloat(volume) : volume;
  return !isNaN(num) && num > 0;
}

/**
 * Validate required field
 */
export function isRequired(value: any): boolean {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  return true;
}

/**
 * Validate minimum length
 */
export function minLength(value: string, length: number): boolean {
  return value.length >= length;
}

/**
 * Validate maximum length
 */
export function maxLength(value: string, length: number): boolean {
  return value.length <= length;
}

/**
 * Validate number range
 */
export function isInRange(value: number, min: number, max: number): boolean {
  return value >= min && value <= max;
}

/**
 * Validate URL format
 */
export function isValidUrl(url: string): boolean {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}

/**
 * Form validation result
 */
export interface ValidationResult {
  isValid: boolean;
  errors: Record<string, string>;
}

/**
 * Validate login form
 */
export function validateLoginForm(data: {
  email: string;
  password: string;
}): ValidationResult {
  const errors: Record<string, string> = {};
  
  if (!isRequired(data.email)) {
    errors.email = 'Email is required';
  } else if (!isValidEmail(data.email)) {
    errors.email = 'Invalid email format';
  }
  
  if (!isRequired(data.password)) {
    errors.password = 'Password is required';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}

/**
 * Validate registration form
 */
export function validateRegistrationForm(data: {
  email: string;
  password: string;
  confirmPassword: string;
  name: string;
  phone?: string;
}): ValidationResult {
  const errors: Record<string, string> = {};
  
  if (!isRequired(data.name)) {
    errors.name = 'Name is required';
  } else if (!minLength(data.name, 2)) {
    errors.name = 'Name must be at least 2 characters';
  }
  
  if (!isRequired(data.email)) {
    errors.email = 'Email is required';
  } else if (!isValidEmail(data.email)) {
    errors.email = 'Invalid email format';
  }
  
  if (!isRequired(data.password)) {
    errors.password = 'Password is required';
  } else if (!isValidPassword(data.password)) {
    errors.password = 'Password must be at least 8 characters with uppercase, lowercase, and number';
  }
  
  if (data.password !== data.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }
  
  if (data.phone && !isValidPhoneNumber(data.phone)) {
    errors.phone = 'Invalid phone number';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}

/**
 * Validate dispense request form
 */
export function validateDispenseForm(data: {
  presetType: 'VOLUME' | 'AMOUNT';
  presetVolume?: string;
  presetAmount?: string;
}): ValidationResult {
  const errors: Record<string, string> = {};
  
  if (data.presetType === 'VOLUME') {
    if (!data.presetVolume || !isValidVolume(data.presetVolume)) {
      errors.presetVolume = 'Invalid volume';
    }
  } else {
    if (!data.presetAmount || !isValidAmount(data.presetAmount)) {
      errors.presetAmount = 'Invalid amount';
    }
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}
