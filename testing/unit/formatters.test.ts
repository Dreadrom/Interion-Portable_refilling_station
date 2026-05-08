/**
 * Unit tests — formatters.ts
 * Tests all formatting utility functions including edge cases,
 * locale correctness, and boundary values.
 */

import {
  formatCurrency,
  formatVolume,
  formatFlowRate,
  formatDateTime,
  formatDate,
  formatTime,
  formatRelativeTime,
  formatDuration,
  formatDistance,
  formatPercentage,
} from '../../portable-refill-app/src/utils/formatters';

// ─────────────────────────────────────────────────────────────────────────────
// formatCurrency
// ─────────────────────────────────────────────────────────────────────────────
describe('formatCurrency', () => {
  test('formats MYR with 2 decimal places', () => {
    const result = formatCurrency(100);
    expect(result).toMatch(/100\.00/);
  });

  test('formats zero', () => {
    const result = formatCurrency(0);
    expect(result).toMatch(/0\.00/);
  });

  test('formats large amount', () => {
    const result = formatCurrency(1234567.89);
    expect(result).toMatch(/1[,.]?234[,.]?567/);
  });

  test('formats negative (refund scenario)', () => {
    const result = formatCurrency(-5.50);
    expect(result).toMatch(/5\.50/);
  });

  test('respects custom currency', () => {
    const result = formatCurrency(10, 'USD', 'en-US');
    expect(result).toMatch(/\$10\.00|\$\s*10\.00/);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatVolume
// ─────────────────────────────────────────────────────────────────────────────
describe('formatVolume', () => {
  test('formats with default 2 decimals', () => {
    expect(formatVolume(10.5)).toBe('10.50 L');
  });

  test('formats zero', () => {
    expect(formatVolume(0)).toBe('0.00 L');
  });

  test('formats with custom decimal places', () => {
    expect(formatVolume(10.5, 1)).toBe('10.5 L');
    expect(formatVolume(10.5, 0)).toBe('11 L');
  });

  test('formats large volume', () => {
    expect(formatVolume(10000)).toBe('10000.00 L');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatFlowRate
// ─────────────────────────────────────────────────────────────────────────────
describe('formatFlowRate', () => {
  test('formats with default 1 decimal', () => {
    expect(formatFlowRate(25.678)).toBe('25.7 L/min');
  });

  test('formats zero', () => {
    expect(formatFlowRate(0)).toBe('0.0 L/min');
  });

  test('formats with custom decimals', () => {
    expect(formatFlowRate(10.123, 2)).toBe('10.12 L/min');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatDateTime, formatDate, formatTime
// ─────────────────────────────────────────────────────────────────────────────
describe('formatDateTime', () => {
  const iso = '2026-05-08T10:30:00.000Z';

  test('returns a non-empty string from a valid ISO date', () => {
    const result = formatDateTime(iso);
    expect(typeof result).toBe('string');
    expect(result.length).toBeGreaterThan(0);
  });

  test('includes year 2026', () => {
    const result = formatDateTime(iso);
    expect(result).toMatch(/2026/);
  });

  test('handles invalid ISO string gracefully (returns "Invalid Date" or similar)', () => {
    const result = formatDateTime('not-a-date');
    expect(typeof result).toBe('string');
  });
});

describe('formatDate', () => {
  test('returns date string containing year', () => {
    const result = formatDate('2026-05-08T10:30:00.000Z');
    expect(result).toMatch(/2026/);
  });
});

describe('formatTime', () => {
  test('returns a time string', () => {
    const result = formatTime('2026-05-08T10:30:00.000Z');
    expect(typeof result).toBe('string');
    expect(result.length).toBeGreaterThan(0);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatRelativeTime
// ─────────────────────────────────────────────────────────────────────────────
describe('formatRelativeTime', () => {
  function isoSecondsAgo(secs: number): string {
    return new Date(Date.now() - secs * 1000).toISOString();
  }
  function isoMinutesAgo(mins: number): string {
    return isoSecondsAgo(mins * 60);
  }
  function isoHoursAgo(hours: number): string {
    return isoMinutesAgo(hours * 60);
  }
  function isoDaysAgo(days: number): string {
    return isoHoursAgo(days * 24);
  }

  test('returns "just now" for less than 60 seconds ago', () => {
    expect(formatRelativeTime(isoSecondsAgo(30))).toBe('just now');
  });

  test('returns minutes ago for < 60 mins', () => {
    const result = formatRelativeTime(isoMinutesAgo(5));
    expect(result).toMatch(/\dm ago/);
  });

  test('returns hours ago for < 24 hours', () => {
    const result = formatRelativeTime(isoHoursAgo(3));
    expect(result).toMatch(/\dh ago/);
  });

  test('returns days ago for < 7 days', () => {
    const result = formatRelativeTime(isoDaysAgo(3));
    expect(result).toMatch(/\dd ago/);
  });

  test('returns formatted date for >= 7 days', () => {
    const result = formatRelativeTime(isoDaysAgo(10));
    expect(result).toMatch(/2026|\d{4}/);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatDuration
// ─────────────────────────────────────────────────────────────────────────────
describe('formatDuration', () => {
  test('formats seconds only', () => {
    expect(formatDuration(45)).toBe('45s');
  });

  test('formats minutes and seconds', () => {
    expect(formatDuration(125)).toBe('2m 5s');
  });

  test('formats hours, minutes and seconds', () => {
    expect(formatDuration(3661)).toBe('1h 1m 1s');
  });

  test('formats zero', () => {
    expect(formatDuration(0)).toBe('0s');
  });

  test('formats exactly one hour', () => {
    expect(formatDuration(3600)).toBe('1h 0m 0s');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatDistance
// ─────────────────────────────────────────────────────────────────────────────
describe('formatDistance', () => {
  test('formats less than 1km in metres', () => {
    expect(formatDistance(0.5)).toBe('500m');
  });

  test('formats exactly 1km', () => {
    expect(formatDistance(1)).toBe('1.0 km');
  });

  test('formats more than 1km', () => {
    expect(formatDistance(12.34)).toBe('12.3 km');
  });

  test('formats 0km', () => {
    expect(formatDistance(0)).toBe('0m');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// formatPercentage
// ─────────────────────────────────────────────────────────────────────────────
describe('formatPercentage', () => {
  test('formats integer percentage', () => {
    expect(formatPercentage(75)).toBe('75%');
  });

  test('formats with one decimal', () => {
    expect(formatPercentage(75.5, 1)).toBe('75.5%');
  });

  test('formats 0%', () => {
    expect(formatPercentage(0)).toBe('0%');
  });

  test('formats 100%', () => {
    expect(formatPercentage(100)).toBe('100%');
  });
});
