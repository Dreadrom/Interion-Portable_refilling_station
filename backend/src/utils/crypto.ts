/**
 * Cryptographic utilities for payment gateway integration
 */

import crypto from 'crypto';

/**
 * Generate MD5 hash
 */
export function md5(input: string): string {
  return crypto.createHash('md5').update(input).digest('hex');
}

/**
 * Generate Fiuu vcode for payment request
 * @param amount Payment amount (e.g., "10.00")
 * @param merchantId Fiuu merchant ID
 * @param orderId Unique order/payment ID
 * @param verifyKey Fiuu verify key
 * @param currency Optional currency code (for extended vcode)
 * @returns MD5 hash vcode
 */
export function generateFiuuVcode(
  amount: string,
  merchantId: string,
  orderId: string,
  verifyKey: string,
  currency?: string
): string {
  const baseString = `${amount}${merchantId}${orderId}${verifyKey}`;
  const vcodeString = currency ? `${baseString}${currency}` : baseString;
  return md5(vcodeString);
}

/**
 * Verify Fiuu skey from payment response
 * @param tranId Fiuu transaction ID
 * @param orderId Your order/payment ID
 * @param status Payment status (00, 11, 22)
 * @param domain Fiuu domain or merchant ID
 * @param amount Payment amount
 * @param currency Currency code
 * @param paydate Payment date from Fiuu
 * @param appcode App code from Fiuu
 * @param secretKey Fiuu secret key
 * @param receivedSkey The skey from Fiuu response
 * @returns true if skey is valid
 */
export function verifyFiuuSkey(
  tranId: string,
  orderId: string,
  status: string,
  domain: string,
  amount: string,
  currency: string,
  paydate: string,
  appcode: string,
  secretKey: string,
  receivedSkey: string
): boolean {
  const preSkey = md5(`${tranId}${orderId}${status}${domain}${amount}${currency}`);
  const calculatedSkey = md5(`${paydate}${domain}${preSkey}${appcode}${secretKey}`);
  return calculatedSkey === receivedSkey;
}
