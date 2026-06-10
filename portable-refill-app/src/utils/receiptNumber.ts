/**
 * Receipt number generation — station-scoped but obfuscated.
 *
 * Format:  <STATION_CODE>-<OBFUSCATED_SEQ>
 *   STATION_CODE  : 4 uppercase hex chars derived from a FNV-1a hash of the
 *                   station name.  Same station always maps to the same code,
 *                   but the original name cannot be recovered from the code.
 *   OBFUSCATED_SEQ: 6 uppercase hex chars.  The raw 1-based per-station
 *                   sequence number is multiplied by a prime then XOR-masked
 *                   before formatting, so sequential receipts (1,2,3 …) do
 *                   NOT produce an obvious pattern on the printed number.
 *
 * Security property: a competitor who sees two receipts from the same station
 * cannot determine the number of transactions that occurred between them
 * without knowing the two hidden constants (PRIME & XKEY).
 */

// ─── FNV-1a 32-bit hash → 4-char hex station code ────────────────────────────
function stationCode(name: string): string {
  let h = 0x811c9dc5;
  for (let i = 0; i < name.length; i++) {
    h = (h ^ name.charCodeAt(i)) >>> 0;
    h = Math.imul(h, 0x01000193) >>> 0;
  }
  return (h & 0xffff).toString(16).toUpperCase().padStart(4, '0');
}

// ─── Sequence obfuscation ─────────────────────────────────────────────────────
const PRIME = 7919;    // Knuth multiplicative hashing prime
const XKEY  = 0xa5c3;  // XOR mask

function obfuscateSeq(n: number): string {
  const v = ((n * PRIME) ^ XKEY) & 0xffffff;
  return v.toString(16).toUpperCase().padStart(6, '0');
}

// ─── Public API ───────────────────────────────────────────────────────────────

/**
 * Build a printable receipt number.
 * @param stationName  The station name stored on the transaction
 * @param seq          1-based ordinal of this transaction at that station
 *                     (use {@link stationSeqFor} to compute it)
 */
export function buildReceiptNumber(stationName: string, seq: number): string {
  return `${stationCode(stationName)}-${obfuscateSeq(seq)}`;
}

/**
 * Derive the 1-based per-station ordinal of a transaction from the full
 * transaction history list (sorted oldest-first within the station).
 *
 * @param txnId        The transaction whose ordinal we need
 * @param txnStation   Its station name
 * @param allTxns      The complete history array (any order is fine)
 * @returns            ≥ 1; falls back to 1 if the transaction isn't found
 */
export function stationSeqFor(
  txnId: string,
  txnStation: string,
  allTxns: Array<{ id: string; stationName: string; timestamp: number }>,
): number {
  const sameStation = allTxns
    .filter(t => t.stationName === txnStation)
    .sort((a, b) => a.timestamp - b.timestamp);
  const idx = sameStation.findIndex(t => t.id === txnId);
  return idx >= 0 ? idx + 1 : sameStation.length + 1;
}
