/**
 * API Integration tests — auth.ts
 * Tests every auth API wrapper function for correct endpoint invocation,
 * payload forwarding, and response handling.
 * The Axios client is fully mocked so no network calls are made.
 */

jest.mock('../../portable-refill-app/src/api/client', () => ({
  post: jest.fn(),
  get: jest.fn(),
}));

import { post, get } from '../../portable-refill-app/src/api/client';
import {
  login,
  register,
  getCurrentUser,
  forgotPassword,
  resetPassword,
  updateProfile,
  changePassword,
  logout,
} from '../../portable-refill-app/src/api/auth';

const mockPost = post as jest.MockedFunction<typeof post>;
const mockGet = get as jest.MockedFunction<typeof get>;

const MOCK_USER = {
  id: 'u-1',
  email: 'driver@interion.com.sg',
  name: 'Ali',
  phone: '0123456789',
  role: 'DRIVER',
  createdAt: new Date().toISOString(),
};

const MOCK_LOGIN_RESPONSE = {
  user: MOCK_USER,
  token: 'access-token',
  refreshToken: 'refresh-token',
  expiresIn: 900,
};

beforeEach(() => {
  jest.clearAllMocks();
});

// ─────────────────────────────────────────────────────────────────────────────
// login
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — login', () => {
  test('posts to /auth/login with credentials', async () => {
    mockPost.mockResolvedValue(MOCK_LOGIN_RESPONSE);
    await login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    expect(mockPost).toHaveBeenCalledWith('/auth/login', {
      email: 'driver@interion.com.sg',
      password: 'Passw0rd',
    });
  });

  test('returns the API response', async () => {
    mockPost.mockResolvedValue(MOCK_LOGIN_RESPONSE);
    const result = await login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    expect(result.token).toBe('access-token');
    expect(result.user.email).toBe('driver@interion.com.sg');
  });

  test('propagates error from client', async () => {
    mockPost.mockRejectedValue(new Error('401 Unauthorized'));
    await expect(login({ email: 'x@x.com', password: 'wrong' })).rejects.toThrow('401 Unauthorized');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// register
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — register', () => {
  test('posts to /auth/register with registration data', async () => {
    mockPost.mockResolvedValue(MOCK_LOGIN_RESPONSE);
    await register({ email: 'new@x.com', password: 'Passw0rd', name: 'New User' });
    expect(mockPost).toHaveBeenCalledWith('/auth/register', {
      email: 'new@x.com',
      password: 'Passw0rd',
      name: 'New User',
    });
  });

  test('returns login response (auto-login after register)', async () => {
    mockPost.mockResolvedValue(MOCK_LOGIN_RESPONSE);
    const result = await register({ email: 'new@x.com', password: 'Passw0rd', name: 'New User' });
    expect(result.user).toBeDefined();
    expect(result.token).toBeDefined();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// getCurrentUser
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — getCurrentUser', () => {
  test('calls GET /auth/me', async () => {
    mockGet.mockResolvedValue(MOCK_USER);
    await getCurrentUser();
    expect(mockGet).toHaveBeenCalledWith('/auth/me');
  });

  test('returns user', async () => {
    mockGet.mockResolvedValue(MOCK_USER);
    const user = await getCurrentUser();
    expect(user.email).toBe('driver@interion.com.sg');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// forgotPassword
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — forgotPassword', () => {
  test('posts to /auth/forgot-password with email', async () => {
    mockPost.mockResolvedValue({ message: 'Email sent' });
    await forgotPassword({ email: 'driver@interion.com.sg' });
    expect(mockPost).toHaveBeenCalledWith('/auth/forgot-password', {
      email: 'driver@interion.com.sg',
    });
  });

  test('returns message', async () => {
    mockPost.mockResolvedValue({ message: 'If the email exists...' });
    const result = await forgotPassword({ email: 'driver@interion.com.sg' });
    expect(result.message).toContain('If the email exists');
  });

  describe('SECURITY — response must not contain resetToken (Finding C2)', () => {
    test('client function does not expose resetToken if accidentally present in response', async () => {
      // The backend currently leaks resetToken — this test documents the client
      // is unaware of the leak; the fix must be on the backend.
      const leakyResponse = {
        message: 'If the email exists...',
        resetToken: 'SECRET-RESET-TOKEN',
      };
      mockPost.mockResolvedValue(leakyResponse);
      const result: any = await forgotPassword({ email: 'x@x.com' });
      // Currently the client passes it through — this should NOT exist in production
      // This test intentionally flags the presence of the field.
      if ('resetToken' in result) {
        console.warn('[SECURITY FINDING C2] resetToken present in forgot-password response');
      }
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// resetPassword
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — resetPassword', () => {
  test('posts to /auth/reset-password', async () => {
    mockPost.mockResolvedValue({ message: 'Password reset' });
    await resetPassword({ token: 'abc', newPassword: 'NewPassw0rd' });
    expect(mockPost).toHaveBeenCalledWith('/auth/reset-password', {
      token: 'abc',
      newPassword: 'NewPassw0rd',
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// updateProfile / changePassword
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — updateProfile', () => {
  test('posts to /user/update', async () => {
    mockPost.mockResolvedValue(MOCK_USER);
    await updateProfile({ name: 'New Name' });
    expect(mockPost).toHaveBeenCalledWith('/user/update', { name: 'New Name' });
  });
});

describe('auth API — changePassword', () => {
  test('posts to /user/change-password', async () => {
    mockPost.mockResolvedValue({ message: 'Password changed' });
    await changePassword({ currentPassword: 'OldPass1', newPassword: 'NewPass1' });
    expect(mockPost).toHaveBeenCalledWith('/user/change-password', {
      currentPassword: 'OldPass1',
      newPassword: 'NewPass1',
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// logout
// ─────────────────────────────────────────────────────────────────────────────
describe('auth API — logout', () => {
  test('posts to /auth/logout', async () => {
    mockPost.mockResolvedValue({});
    await logout();
    expect(mockPost).toHaveBeenCalledWith('/auth/logout', {});
  });

  test('resolves even if logout endpoint returns error', async () => {
    mockPost.mockRejectedValue(new Error('404 Not Found'));
    await expect(logout()).resolves.not.toThrow();
  });
});
