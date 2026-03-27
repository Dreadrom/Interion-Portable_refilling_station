import * as Crypto from 'expo-crypto';

async function md5(value: string): Promise<string> {
  return Crypto.digestStringAsync(
    Crypto.CryptoDigestAlgorithm.MD5,
    value
  );
}

export async function buildDigestAuth(
  method: string,
  uri: string,
  username: string,
  password: string,
  challenge: string
): Promise<string> {
  const params: Record<string, string> = {};

  challenge.replace(/(\w+)=["]?([^",]+)["]?/g, (_, k, v) => {
    params[k] = v;
    return '';
  });

  const realm = params.realm;
  const nonce = params.nonce;
  const qop = params.qop ?? 'auth';
  const nc = '00000001';
  const cnonce = Math.random().toString(36).substring(2, 10);

  const ha1 = await md5(`${username}:${realm}:${password}`);
  const ha2 = await md5(`${method}:${uri}`);
  const response = await md5(
    `${ha1}:${nonce}:${nc}:${cnonce}:${qop}:${ha2}`
  );

  return `Digest username="${username}", realm="${realm}", nonce="${nonce}", uri="${uri}", qop=${qop}, nc=${nc}, cnonce="${cnonce}", response="${response}"`;
}
