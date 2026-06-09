import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fetchWithAuth } from '../../../src/lib/server/gateway';

// Mock cookies helper
function createMockCookies() {
	const store = new Map<string, { value: string; options: Record<string, unknown> }>();

	return {
		get: vi.fn((name: string) => store.get(name)?.value ?? undefined),
		set: vi.fn((name: string, value: string, options: Record<string, unknown>) => {
			store.set(name, { value, options });
		}),
		delete: vi.fn((name: string) => {
			store.delete(name);
		})
	};
}

// Mock event
function createMockEvent(cookies: ReturnType<typeof createMockCookies>, pathname = '/dashboard') {
	return {
		request: new Request(`http://localhost:5173${pathname}`),
		cookies: cookies as any,
		url: new URL(`http://localhost:5173${pathname}`)
	};
}

describe('fetchWithAuth', () => {
	let mockFetch: ReturnType<typeof vi.fn>;
	let cookies: ReturnType<typeof createMockCookies>;
	let event: ReturnType<typeof createMockEvent>;

	beforeEach(() => {
		cookies = createMockCookies();
		event = createMockEvent(cookies);
		mockFetch = vi.fn();
	});

	it('injects Authorization header from cookie', async () => {
		cookies.get.mockImplementation((name: string) => {
			if (name === 'siga_token') return 'test-jwt-token';
			return undefined;
		});

		mockFetch.mockResolvedValue(
			new Response(JSON.stringify({ data: 'ok' }), { status: 200 })
		);

		const response = await fetchWithAuth(mockFetch, event, '/api/test');

		expect(response.ok).toBe(true);
		expect(mockFetch).toHaveBeenCalledTimes(1);

		const callUrl = mockFetch.mock.calls[0][0];
		const callHeaders = mockFetch.mock.calls[0][1]?.headers;
		expect(callUrl).toContain('/api/test');
		expect(callHeaders?.get('Authorization')).toBe('Bearer test-jwt-token');
	});

	it('returns response directly on 200 without refresh attempt', async () => {
		cookies.get.mockImplementation((name: string) => {
			if (name === 'siga_token') return 'valid-token';
			return undefined;
		});

		mockFetch.mockResolvedValue(
			new Response(JSON.stringify({ items: [], total: 0 }), { status: 200 })
		);

		const response = await fetchWithAuth(mockFetch, event, '/api/data');
		const body = await response.json();

		expect(body).toEqual({ items: [], total: 0 });
		expect(mockFetch).toHaveBeenCalledTimes(1);
	});

	it('attempts refresh on 401 and retries', async () => {
		let callCount = 0;

		cookies.get.mockImplementation((name: string) => {
			if (name === 'siga_token') return 'expired-token';
			if (name === 'siga_refresh') return 'valid-refresh-token';
			return undefined;
		});

		mockFetch.mockImplementation(async (url: string, init?: RequestInit) => {
			callCount++;

			if (url.includes('/api/auth/refresh')) {
				return new Response(
					JSON.stringify({
						accessToken: 'new-access-token',
						refreshToken: 'new-refresh-token'
					}),
					{ status: 200 }
				);
			}

			if (callCount === 1) {
				// First call returns 401
				return new Response('Unauthorized', { status: 401 });
			}

			// Second call (retry) returns 200
			return new Response(JSON.stringify({ data: 'retried-ok' }), { status: 200 });
		});

		const response = await fetchWithAuth(mockFetch, event, '/api/protected');
		const body = await response.json();

		expect(body).toEqual({ data: 'retried-ok' });
		expect(mockFetch).toHaveBeenCalledTimes(3); // 1. request, 2. refresh, 3. retry
	});

	it('redirects to login when refresh fails', async () => {
		cookies.get.mockImplementation((name: string) => {
			if (name === 'siga_token') return 'expired-token';
			if (name === 'siga_refresh') return 'expired-refresh-token';
			return undefined;
		});

		mockFetch.mockImplementation(async (url: string) => {
			if (url.includes('/api/auth/refresh')) {
				return new Response('Refresh failed', { status: 401 });
			}
			return new Response('Unauthorized', { status: 401 });
		});

		await expect(
			fetchWithAuth(mockFetch, event, '/api/protected')
		).rejects.toThrow();
	});

	it('works without token for public endpoints', async () => {
		cookies.get.mockImplementation(() => undefined);

		mockFetch.mockResolvedValue(
			new Response(JSON.stringify({ public: true }), { status: 200 })
		);

		const response = await fetchWithAuth(mockFetch, event, '/api/public');
		const body = await response.json();

		expect(body).toEqual({ public: true });
	});

	it('handles 404 from gateway', async () => {
		cookies.get.mockImplementation((name: string) => {
			if (name === 'siga_token') return 'valid-token';
			return undefined;
		});

		mockFetch.mockResolvedValue(
			new Response('Not Found', { status: 404 })
		);

		const response = await fetchWithAuth(mockFetch, event, '/api/nonexistent');
		expect(response.status).toBe(404);
	});

	it('handles 403 from gateway', async () => {
		cookies.get.mockImplementation((name: string) => {
			if (name === 'siga_token') return 'valid-token';
			return undefined;
		});

		mockFetch.mockResolvedValue(
			new Response('Forbidden', { status: 403 })
		);

		const response = await fetchWithAuth(mockFetch, event, '/api/forbidden');
		expect(response.status).toBe(403);
	});
});
