import { describe, it, expect, vi, beforeEach } from 'vitest';
import { login, logout, setSessionCookies, clearSessionCookies } from '../../../src/lib/server/auth.server';

function createMockCookies() {
	const store = new Map<string, { value: string; options: Record<string, unknown> }>();

	return {
		get: vi.fn((name: string) => store.get(name)?.value ?? undefined),
		set: vi.fn((name: string, value: string, options: Record<string, unknown>) => {
			store.set(name, { value, options });
		}),
		delete: vi.fn((name: string, _opts?: Record<string, unknown>) => {
			store.delete(name);
		})
	};
}

function createMockEvent(cookies: ReturnType<typeof createMockCookies>) {
	const url = new URL('http://localhost:5173/login');
	return {
		request: new Request('http://localhost:5173/login'),
		cookies,
		url,
		fetch: vi.fn()
	} as any;
}

describe('Auth Flow Integration', () => {
	let mockFetch: ReturnType<typeof vi.fn>;
	let cookies: ReturnType<typeof createMockCookies>;

	beforeEach(() => {
		cookies = createMockCookies();
		mockFetch = vi.fn();
	});

	describe('login()', () => {
		it('returns data on successful customer login', async () => {
			const loginResponse = {
				accessToken: 'jwt-customer-token',
				refreshToken: 'refresh-customer-token',
				principalType: 'customer',
				user: {
					id: 'cust-1',
					email: 'cliente@demo.com',
					name: 'Cliente Demo',
					principalType: 'customer',
					tenantId: 'tenant-1'
				}
			};

			mockFetch.mockResolvedValue(
				new Response(JSON.stringify(loginResponse), { status: 200 })
			);

			const result = await login(mockFetch, 'cliente@demo.com', 'demo1234');

			expect(result.data).toBeDefined();
			expect(result.data?.accessToken).toBe('jwt-customer-token');
			expect(result.data?.user.principalType).toBe('customer');
			expect(result.error).toBeUndefined();
		});

		it('returns data on successful user login', async () => {
			const loginResponse = {
				accessToken: 'jwt-user-token',
				refreshToken: 'refresh-user-token',
				principalType: 'user',
				user: {
					id: 'user-1',
					email: 'admin@siga.com',
					name: 'Admin SIGA',
					principalType: 'user',
					rol: 'admin',
					tenantId: 'tenant-1'
				}
			};

			mockFetch.mockResolvedValue(
				new Response(JSON.stringify(loginResponse), { status: 200 })
			);

			const result = await login(mockFetch, 'admin@siga.com', 'admin1234');

			expect(result.data).toBeDefined();
			expect(result.data?.user.rol).toBe('admin');
			expect(result.error).toBeUndefined();
		});

		it('returns error on invalid credentials', async () => {
			mockFetch.mockResolvedValue(
				new Response('Unauthorized', { status: 401 })
			);

			const result = await login(mockFetch, 'wrong@email.com', 'wrongpass');

			expect(result.data).toBeUndefined();
			expect(result.error).toBe('Credenciales inválidas');
		});

		it('returns error on gateway failure', async () => {
			mockFetch.mockRejectedValue(new Error('Network error'));

			const result = await login(mockFetch, 'test@test.com', 'password');

			expect(result.data).toBeUndefined();
			expect(result.error).toBeDefined();
		});
	});

	describe('setSessionCookies()', () => {
		const event = createMockEvent(createMockCookies());

		it('sets siga_token cookie with correct options', () => {
			setSessionCookies(event, 'test-access-token', 'test-refresh-token');

			expect(event.cookies.set).toHaveBeenCalledWith(
				'siga_token',
				'test-access-token',
				expect.objectContaining({
					path: '/',
					httpOnly: true,
					secure: true,
					sameSite: 'lax'
				})
			);
		});

		it('sets siga_refresh cookie with correct options', () => {
			setSessionCookies(event, 'test-access-token', 'test-refresh-token');

			expect(event.cookies.set).toHaveBeenCalledWith(
				'siga_refresh',
				'test-refresh-token',
				expect.objectContaining({
					path: '/api/auth/refresh',
					httpOnly: true,
					secure: true,
					sameSite: 'strict'
				})
			);
		});
	});

	describe('clearSessionCookies()', () => {
		it('deletes both cookies', () => {
			const event = createMockEvent(createMockCookies());
			clearSessionCookies(event);

			expect(event.cookies.delete).toHaveBeenCalledWith('siga_token', { path: '/' });
			expect(event.cookies.delete).toHaveBeenCalledWith('siga_refresh', { path: '/api/auth/refresh' });
		});
	});

	describe('logout()', () => {
		it('calls gateway logout with token', async () => {
			const event = createMockEvent(createMockCookies());
			event.cookies.get.mockReturnValue('valid-token');

			mockFetch.mockResolvedValue(new Response(null, { status: 200 }));

			await logout(mockFetch, event);

			expect(mockFetch).toHaveBeenCalledWith(
				expect.stringContaining('/api/auth/logout'),
				expect.objectContaining({
					method: 'POST',
					headers: expect.objectContaining({
						Authorization: 'Bearer valid-token'
					})
				})
			);
		});

		it('handles gateway error gracefully', async () => {
			const event = createMockEvent(createMockCookies());
			event.cookies.get.mockReturnValue('valid-token');

			mockFetch.mockRejectedValue(new Error('Network error'));

			// Should not throw
			await expect(logout(mockFetch, event)).resolves.toBeUndefined();
		});
	});
});
