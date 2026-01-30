/**
 * Database Connection Utility
 * PostgreSQL connection pool for AWS RDS
 */

import { Pool, QueryResult } from 'pg';

let pool: Pool | null = null;

/**
 * Database configuration from environment variables
 */
const dbConfig = {
  host: process.env.DB_HOST,
  port: parseInt(process.env.DB_PORT || '5432'),
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  max: 10, // Maximum number of clients in the pool
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 2000,
};

/**
 * Get database connection pool
 */
export function getPool(): Pool {
  if (!pool) {
    pool = new Pool(dbConfig);
  }
  return pool;
}

/**
 * Execute a query
 */
export async function query<T = any>(
  sql: string,
  params?: any[]
): Promise<T> {
  const client = getPool();
  const result: QueryResult = await client.query(sql, params);
  return result.rows as T;
}

/**
 * Execute a query and return first row
 */
export async function queryOne<T = any>(
  sql: string,
  params?: any[]
): Promise<T | null> {
  const rows = await query<T[]>(sql, params);
  return rows.length > 0 ? rows[0] : null;
}

/**
 * Close database connection pool
 */
export async function closePool(): Promise<void> {
  if (pool) {
    await pool.end();
    pool = null;
  }
}
