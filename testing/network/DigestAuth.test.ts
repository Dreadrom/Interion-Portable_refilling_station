/**
 * Network tests — DigestAuth.ts
 * Tests Digest Authentication header construction for correctness,
 * edge cases, and security properties.
 */

// expo-crypto MD5 must be mocked since we are in a Node/Jest environment
jest.mock('expo-crypto', () => ({
  CryptoDigestAlgorithm: { MD5: 'MD5' },
  digestStringAsync: jest.fn((_algo: string, value: string) => {
    // Pure-JS MD5 stub using Node's built-in crypto
    const crypto = require('crypto');
    return Promise.resolve(crypto.createHash('md5').update(value).digest('hex'));
  }),
}));

import { buildDigestAuth } from '../../portable-refill-app/src/network/DigestAuth';

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
function parseDigestHeader(header: string): Record<string, string> {
  const params: Record<string, string> = {};
  header
    .replace(/^Digest\s+/, '')
    .split(/,\s*/)
    .forEach((part) => {
      const match = part.match(/(\w+)=["']?([^"',]*)["']?/);
      if (match) params[match[1]] = match[2];
    });
  return params;
}

const CHALLENGE = 'realm="TestRealm", nonce="abc123nonce", qop="auth"';
const METHOD = 'GET';
const URI = '/pts/api/status';
const USERNAME = 'admin';
const PASSWORD = 'S3cureP@ss';

// ─────────────────────────────────────────────────────────────────────────────
// Structural correctness
// ─────────────────────────────────────────────────────────────────────────────
describe('buildDigestAuth — structure', () => {
  test('header starts with "Digest "', async () => {
    const header = await buildDigestAuth(METHOD, URI, USERNAME, PASSWORD, CHALLENGE);
    expect(header).toMatch(/^Digest /);
  });

  test('contains required Digest fields', async () => {
    const header = await buildDigestAuth(METHOD, URI, USERNAME, PASSWORD, CHALLENGE);
    const params = parseDigestHeader(header);
    expect(params.username).toBe(USERNAME);
    expect(params.realm).toBe('TestRealm');
    expect(params.nonce).toBe('abc123nonce');
    expect(params.uri).toBe(URI);
    expect(params.response).toBeDefined();
    expect(params.response.length).toBe(32); // MD5 hex = 32 chars
    expect(params.nc).toBe('00000001');
    expect(params.cnonce).toBeDefined();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// Correctness of response hash
// ─────────────────────────────────────────────────────────────────────────────
describe('buildDigestAuth — hash correctness', () => {
  test('response hash changes when username changes', async () => {
    const h1 = await buildDigestAuth(METHOD, URI, 'user1', PASSWORD, CHALLENGE);
    const h2 = await buildDigestAuth(METHOD, URI, 'user2', PASSWORD, CHALLENGE);
    const p1 = parseDigestHeader(h1);
    const p2 = parseDigestHeader(h2);
    expect(p1.response).not.toBe(p2.response);
  });

  test('response hash changes when password changes', async () => {
    const h1 = await buildDigestAuth(METHOD, URI, USERNAME, 'pass1', CHALLENGE);
    const h2 = await buildDigestAuth(METHOD, URI, USERNAME, 'pass2', CHALLENGE);
    expect(parseDigestHeader(h1).response).not.toBe(parseDigestHeader(h2).response);
  });

  test('response hash changes when method changes (GET vs POST)', async () => {
    const h1 = await buildDigestAuth('GET', URI, USERNAME, PASSWORD, CHALLENGE);
    const h2 = await buildDigestAuth('POST', URI, USERNAME, PASSWORD, CHALLENGE);
    expect(parseDigestHeader(h1).response).not.toBe(parseDigestHeader(h2).response);
  });

  test('response hash changes when URI changes', async () => {
    const h1 = await buildDigestAuth(METHOD, '/endpoint-a', USERNAME, PASSWORD, CHALLENGE);
    const h2 = await buildDigestAuth(METHOD, '/endpoint-b', USERNAME, PASSWORD, CHALLENGE);
    expect(parseDigestHeader(h1).response).not.toBe(parseDigestHeader(h2).response);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// cnonce uniqueness (replay protection)
// ─────────────────────────────────────────────────────────────────────────────
describe('buildDigestAuth — replay protection', () => {
  test('cnonce is different on each call', async () => {
    const cnonces = new Set<string>();
    for (let i = 0; i < 10; i++) {
      const header = await buildDigestAuth(METHOD, URI, USERNAME, PASSWORD, CHALLENGE);
      cnonces.add(parseDigestHeader(header).cnonce);
    }
    // With Math.random()-based cnonce, expect at least some variability over 10 calls
    expect(cnonces.size).toBeGreaterThan(1);
  });

  describe('SECURITY — cnonce uses Math.random (not crypto-secure)', () => {
    test('cnonce is not cryptographically random (documents the gap)', async () => {
      // Math.random().toString(36) is used for cnonce — not cryptographically secure.
      // This documents the finding; a fix would use crypto.randomBytes().
      const header = await buildDigestAuth(METHOD, URI, USERNAME, PASSWORD, CHALLENGE);
      const { cnonce } = parseDigestHeader(header);
      expect(cnonce).toBeDefined();
      expect(cnonce.length).toBeGreaterThan(0);
      // If this test is seen, note that cnonce should use: crypto.randomBytes(8).toString('hex')
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// Challenge parsing edge cases
// ─────────────────────────────────────────────────────────────────────────────
describe('buildDigestAuth — challenge parsing', () => {
  test('handles challenge without qop (defaults to "auth")', async () => {
    const challengeNoQop = 'realm="TestRealm", nonce="nonce99"';
    const header = await buildDigestAuth(METHOD, URI, USERNAME, PASSWORD, challengeNoQop);
    expect(header).toMatch(/^Digest /);
    const params = parseDigestHeader(header);
    expect(params.response).toBeDefined();
  });

  test('handles challenge with quoted nonce', async () => {
    const challenge = 'realm="MyRealm", nonce="quotednonce", qop="auth"';
    const header = await buildDigestAuth(METHOD, URI, USERNAME, PASSWORD, challenge);
    expect(parseDigestHeader(header).nonce).toBe('quotednonce');
  });
});
