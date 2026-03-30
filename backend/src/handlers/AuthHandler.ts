/**
 * Auth Handler Lambda Function
 * Handles all authentication endpoints
 */

import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import bcrypt from 'bcryptjs';
import { v4 as uuidv4 } from 'uuid';
import { query, queryOne } from '../database/connection';
import {
    extractToken,
    generateRefreshToken,
    generateToken,
    getExpiresIn,
    verifyToken,
} from '../utils/jwt';
import {
    errorResponse,
    internalError,
    notFoundError,
    successResponse,
    unauthorizedError,
    validationError,
} from '../utils/response';

interface User {
  userid: string;
  useremail: string;
  userpassword: string;
  username: string;
  userphone: string | null;
  userrole: 'ADMIN' | 'DRIVER';
  createdat: string;
  updatedat: string;
}

/**
 * Main Lambda handler
 */
export async function handler(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  console.log('Event:', JSON.stringify(event, null, 2));

  const path = event.path;
  const method = event.httpMethod;

  try {
    // Route to appropriate handler
    if (path === '/auth/register' && method === 'POST') {
      return await handleRegister(event);
    }

    if (path === '/auth/login' && method === 'POST') {
      return await handleLogin(event);
    }

    if (path === '/auth/me' && method === 'GET') {
      return await handleGetMe(event);
    }

    if (path === '/auth/forgot-password' && method === 'POST') {
      return await handleForgotPassword(event);
    }

    if (path === '/auth/reset-password' && method === 'POST') {
      return await handleResetPassword(event);
    }

    if (path === '/auth/logout' && method === 'POST') {
      return await handleLogout(event);
    }

    if (path === '/auth/send-otp' && method === 'POST') {
      return await handleSendOtp(event);
    }

    if (path === '/auth/verify-otp' && method === 'POST') {
      return await handleVerifyOtp(event);
    }

    if (path === '/user/update' && method === 'POST') {
      return await handleUpdateProfile(event);
    }

    if (path === '/user/change-password' && method === 'POST') {
      return await handleChangePassword(event);
    }

    // Handle OPTIONS for CORS
    if (method === 'OPTIONS') {
      return {
        statusCode: 200,
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
          'Access-Control-Allow-Headers': 'Content-Type, Authorization',
        },
        body: '',
      };
    }

    return errorResponse('NOT_FOUND', 'Endpoint not found', 404);
  } catch (error: any) {
    console.error('Error:', error);
    return internalError(error.message || 'Internal server error');
  }
}

/**
 * Register new user
 */
async function handleRegister(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = JSON.parse(event.body || '{}');
  const { email, password, name, phone } = body;

  // Validation
  if (!email || !password || !name) {
    return validationError('Email, password, and name are required');
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return validationError('Invalid email format', 'email');
  }

  if (password.length < 6) {
    return validationError('Password must be at least 6 characters', 'password');
  }

  // Check if user already exists
  const existingUser = await queryOne<User>(
    'SELECT userid FROM Users WHERE UserEmail = $1',
    [email]
  );

  if (existingUser) {
    return errorResponse('CONFLICT', 'User with this email already exists', 409);
  }

  // Hash password
  const hashedPassword = await bcrypt.hash(password, 10);

  // Create user
  const userId = uuidv4();
  await query(
    `INSERT INTO Users (UserID, UserEmail, UserPassword, UserName, UserPhone, UserRole)
     VALUES ($1, $2, $3, $4, $5, 'DRIVER')`,
    [userId, email, hashedPassword, name, phone || null]
  );

  // Generate tokens
  const tokenPayload = { userId, email, role: 'DRIVER' };
  const token = generateToken(tokenPayload);
  const refreshToken = generateRefreshToken(tokenPayload);

  // Return user data
  return successResponse(
    {
      user: {
        id: userId,
        email,
        name,
        phone: phone || null,
        role: 'DRIVER',
        createdAt: new Date().toISOString(),
      },
      token,
      refreshToken,
      expiresIn: getExpiresIn(),
    },
    201
  );
}

/**
 * Login user
 */
async function handleLogin(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = JSON.parse(event.body || '{}');
  const { email, password } = body;

  // Validation
  if (!email || !password) {
    return validationError('Email and password are required');
  }

  // Find user
  const user = await queryOne<User>(
    'SELECT * FROM Users WHERE UserEmail = $1',
    [email]
  );

  if (!user) {
    return unauthorizedError('Invalid email or password');
  }

  // Verify password
  const isValidPassword = await bcrypt.compare(password, user.userpassword);

  if (!isValidPassword) {
    return unauthorizedError('Invalid email or password');
  }

  // Generate tokens
  const tokenPayload = {
    userId: user.userid,
    email: user.useremail,
    role: user.userrole,
  };
  const token = generateToken(tokenPayload);
  const refreshToken = generateRefreshToken(tokenPayload);

  // Return user data
  return successResponse({
    user: {
      id: user.userid,
      email: user.useremail,
      name: user.username,
      phone: user.userphone,
      role: user.userrole,
      createdAt: user.createdat,
      updatedAt: user.updatedat,
    },
    token,
    refreshToken,
    expiresIn: getExpiresIn(),
  });
}

/**
 * Get current user profile
 */
async function handleGetMe(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // Extract and verify token
  const token = extractToken(event.headers.Authorization || event.headers.authorization);

  if (!token) {
    return unauthorizedError('No token provided');
  }

  let payload;
  try {
    payload = verifyToken(token);
  } catch (error) {
    return unauthorizedError('Invalid or expired token');
  }

  // Get user from database
  const user = await queryOne<User>(
    'SELECT * FROM Users WHERE UserID = $1',
    [payload.userId]
  );

  if (!user) {
    return notFoundError('User not found');
  }

  return successResponse({
    id: user.userid,
    email: user.useremail,
    name: user.username,
    phone: user.userphone,
    role: user.userrole,
    createdAt: user.createdat,
    updatedAt: user.updatedat,
  });
}

/**
 * Forgot password - Generate reset token
 */
async function handleForgotPassword(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = JSON.parse(event.body || '{}');
  const { email } = body;

  if (!email) {
    return validationError('Email is required');
  }

  // Find user
  const user = await queryOne<User>(
    'SELECT UserID FROM Users WHERE UserEmail = $1',
    [email]
  );

  // Always return success to prevent email enumeration
  if (!user) {
    return successResponse({
      message: 'If the email exists, a password reset link has been sent',
    });
  }

  // Generate reset token
  const resetToken = uuidv4();
  const expiresAt = new Date(Date.now() + 3600000); // 1 hour

  await query(
    `INSERT INTO PasswordResetTokens (TokenID, UserID, Token, ExpiresAt)
     VALUES ($1, $2, $3, $4)`,
    [uuidv4(), user.userid, resetToken, expiresAt]
  );

  // TODO: Send email with reset token
  // For now, just return the token (in production, send via email)
  console.log('Reset token:', resetToken);

  return successResponse({
    message: 'If the email exists, a password reset link has been sent',
    // Remove this in production:
    resetToken: resetToken,
  });
}

/**
 * Reset password with token
 */
async function handleResetPassword(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = JSON.parse(event.body || '{}');
  const { token, newPassword } = body;

  if (!token || !newPassword) {
    return validationError('Token and new password are required');
  }

  if (newPassword.length < 6) {
    return validationError('Password must be at least 6 characters');
  }

  // Find valid reset token
  const resetToken = await queryOne<any>(
    `SELECT * FROM PasswordResetTokens
     WHERE Token = $1 AND Used = FALSE AND ExpiresAt > CURRENT_TIMESTAMP`,
    [token]
  );

  if (!resetToken) {
    return errorResponse('INVALID_TOKEN', 'Invalid or expired reset token', 400);
  }

  // Hash new password
  const hashedPassword = await bcrypt.hash(newPassword, 10);

  // Update user password
  await query(
    'UPDATE Users SET UserPassword = $1 WHERE UserID = $2',
    [hashedPassword, resetToken.userid]
  );

  // Mark token as used
  await query(
    'UPDATE PasswordResetTokens SET Used = TRUE WHERE TokenID = $1',
    [resetToken.tokenid]
  );

  return successResponse({
    message: 'Password successfully reset',
  });
}

/**
 * Update user profile
 */
async function handleUpdateProfile(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // Extract and verify token
  const token = extractToken(event.headers.Authorization || event.headers.authorization);

  if (!token) {
    return unauthorizedError('No token provided');
  }

  let payload;
  try {
    payload = verifyToken(token);
  } catch (error) {
    return unauthorizedError('Invalid or expired token');
  }

  const body = JSON.parse(event.body || '{}');
  const { name, phone, email } = body;

  // Update user
  const updates: string[] = [];
  const params: any[] = [];
  let paramCount = 1;

  if (name) {
    updates.push(`UserName = $${paramCount++}`);
    params.push(name);
  }

  if (phone !== undefined) {
    updates.push(`UserPhone = $${paramCount++}`);
    params.push(phone || null);
  }

  if (email) {
    // Check if email is already taken by another user
    const existingUser = await queryOne<User>(
      'SELECT userid FROM Users WHERE UserEmail = $1 AND UserID != $2',
      [email, payload.userId]
    );
    if (existingUser) {
      return errorResponse('CONFLICT', 'Email address is already in use', 409);
    }
    updates.push(`UserEmail = $${paramCount++}`);
    params.push(email);
  }

  if (updates.length === 0) {
    return validationError('No fields to update');
  }

  params.push(payload.userId);

  await query(
    `UPDATE Users SET ${updates.join(', ')} WHERE UserID = $${paramCount}`,
    params
  );

  // Get updated user
  const user = await queryOne<User>(
    'SELECT * FROM Users WHERE UserID = $1',
    [payload.userId]
  );

  return successResponse({
    id: user!.userid,
    email: user!.useremail,
    name: user!.username,
    phone: user!.userphone,
    role: user!.userrole,
    createdAt: user!.createdat,
    updatedAt: user!.updatedat,
  });
}

/**
 * Change password
 */
async function handleChangePassword(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // Extract and verify token
  const token = extractToken(event.headers.Authorization || event.headers.authorization);

  if (!token) {
    return unauthorizedError('No token provided');
  }

  let payload;
  try {
    payload = verifyToken(token);
  } catch (error) {
    return unauthorizedError('Invalid or expired token');
  }

  const body = JSON.parse(event.body || '{}');
  const { currentPassword, newPassword } = body;

  if (!currentPassword || !newPassword) {
    return validationError('Current password and new password are required');
  }

  if (newPassword.length < 6) {
    return validationError('New password must be at least 6 characters');
  }

  // Get user
  const user = await queryOne<User>(
    'SELECT * FROM Users WHERE UserID = $1',
    [payload.userId]
  );

  if (!user) {
    return notFoundError('User not found');
  }

  // Verify current password
  const isValidPassword = await bcrypt.compare(currentPassword, user.userpassword);

  if (!isValidPassword) {
    return unauthorizedError('Current password is incorrect');
  }

  // Hash new password
  const hashedPassword = await bcrypt.hash(newPassword, 10);

  // Update password
  await query(
    'UPDATE Users SET UserPassword = $1 WHERE UserID = $2',
    [hashedPassword, user.userid]
  );

  return successResponse({
    message: 'Password successfully changed',
  });
}

/**
 * Logout (optional - mainly client-side token removal)
 */
async function handleLogout(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // In a more complex system, you might invalidate the token here
  // For now, just return success
  return successResponse({
    message: 'Successfully logged out',
  });
}

/**
 * Send OTP to a phone number for phone-based login
 */
async function handleSendOtp(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = JSON.parse(event.body || '{}');
  const { phone } = body;

  if (!phone) {
    return validationError('Phone number is required');
  }

  // Normalise: accept 01x or +601x formats
  const normalised = phone.startsWith('+60') ? phone : `+60${phone.replace(/^0/, '')}`;

  // Generate a 6-digit OTP
  const otp = Math.floor(100000 + Math.random() * 900000).toString();
  const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutes

  // Invalidate any existing unused OTPs for this phone
  await query(
    `UPDATE PhoneOTPs SET Used = TRUE WHERE Phone = $1 AND Used = FALSE`,
    [normalised]
  );

  // Store new OTP
  await query(
    `INSERT INTO PhoneOTPs (OTPId, Phone, OTPCode, ExpiresAt) VALUES ($1, $2, $3, $4)`,
    [uuidv4(), normalised, otp, expiresAt.toISOString()]
  );

  // TODO: Integrate real SMS gateway (e.g. Twilio, AWS SNS) here
  // For now, log OTP and return success (demo mode)
  console.log(`[OTP] Phone: ${normalised}, Code: ${otp}, Expires: ${expiresAt.toISOString()}`);

  return successResponse({ message: 'OTP sent successfully', demo: otp });
}

/**
 * Verify OTP and log in or register the user
 */
async function handleVerifyOtp(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = JSON.parse(event.body || '{}');
  const { phone, otp, name } = body;

  if (!phone || !otp) {
    return validationError('Phone and OTP are required');
  }

  const normalised = phone.startsWith('+60') ? phone : `+60${phone.replace(/^0/, '')}`;

  // Find the most recent valid, unused OTP for this phone
  const record = await queryOne<{ otpid: string; otpcode: string; expiresat: string }>(
    `SELECT OTPId, OTPCode, ExpiresAt FROM PhoneOTPs
     WHERE Phone = $1 AND Used = FALSE AND ExpiresAt > NOW()
     ORDER BY CreatedAt DESC LIMIT 1`,
    [normalised]
  );

  if (!record || record.otpcode !== otp) {
    return unauthorizedError('Invalid or expired OTP');
  }

  // Mark OTP as used
  await query(`UPDATE PhoneOTPs SET Used = TRUE WHERE OTPId = $1`, [record.otpid]);

  // Check if a user with this phone already exists
  let user = await queryOne<User>(
    `SELECT * FROM Users WHERE UserPhone = $1`,
    [normalised]
  );

  let isNewUser = false;

  if (!user) {
    isNewUser = true;
    // Create a new user. Use synthetic email to satisfy UNIQUE constraint.
    const syntheticEmail = `phone_${normalised.replace('+', '')}@interion.local`;
    const userId = uuidv4();
    const randomPassword = await bcrypt.hash(uuidv4(), 10);
    const displayName = name || `Driver ${normalised.slice(-4)}`;

    await query(
      `INSERT INTO Users (UserID, UserEmail, UserPassword, UserName, UserPhone, UserRole)
       VALUES ($1, $2, $3, $4, $5, 'DRIVER')`,
      [userId, syntheticEmail, randomPassword, displayName, normalised]
    );

    user = await queryOne<User>(`SELECT * FROM Users WHERE UserID = $1`, [userId]);
  }

  if (!user) {
    return internalError('Failed to create or retrieve user');
  }

  const token = generateToken({
    userId: user.userid,
    email: user.useremail,
    role: user.userrole,
  });

  return successResponse({
    token,
    expiresIn: getExpiresIn(),
    isNewUser,
    user: {
      id: user.userid,
      email: user.useremail,
      name: user.username,
      phone: user.userphone,
      role: user.userrole,
    },
  });
}
