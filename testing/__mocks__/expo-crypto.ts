// Mock for expo-crypto — uses Node's built-in crypto for accurate MD5 in tests
import crypto from 'crypto';

export enum CryptoDigestAlgorithm {
  MD5 = 'MD5',
  SHA1 = 'SHA-1',
  SHA256 = 'SHA-256',
  SHA384 = 'SHA-384',
  SHA512 = 'SHA-512',
}

export async function digestStringAsync(
  algorithm: CryptoDigestAlgorithm | string,
  data: string
): Promise<string> {
  const algo = algorithm === 'MD5' ? 'md5'
    : algorithm === 'SHA-1' ? 'sha1'
    : algorithm === 'SHA-256' ? 'sha256'
    : algorithm === 'SHA-384' ? 'sha384'
    : 'sha512';
  return crypto.createHash(algo).update(data).digest('hex');
}
