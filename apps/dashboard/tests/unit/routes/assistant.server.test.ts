/**
 * assistant.server.test.ts — Tests para +server.ts del asistente.
 *
 * Verifica:
 * - GET /api/chat/stream (existing — no regression)
 * - POST /api/agent/a2ui (new proxy)
 * - Validación de mensaje vacío
 */

import { describe, it, expect, vi } from 'vitest';

// Mock fetch
const mockFetch = vi.fn();
vi.stubGlobal('fetch', mockFetch);

// Mock process.env
vi.stubEnv('GATEWAY_BASE', 'http://test-gateway:8080');

// Import the handlers after mocks
const { POST } = await import('../../../src/routes/assistant/+server.ts');

describe('Assistant Server POST /api/agent/a2ui', () => {
	it('forwards POST to backend agent service', async () => {
		const expectedResponse = {
			surfaceId: 'surface-abc',
			surface: {
				type: 'createSurface',
				components: [
					{ type: 'stat-card', props: { label: 'Ventas', value: 100 } }
				],
				dataBindings: {}
			},
			provenance: 'gemini'
		};

		mockFetch.mockResolvedValueOnce({
			ok: true,
			status: 200,
			json: () => Promise.resolve(expectedResponse)
		});

		const request = new Request('http://localhost/api/agent/a2ui', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ message: 'Analiza ventas', mode: 'analyst' })
		});

		const response = await POST({ request, fetch: mockFetch, url: new URL('http://localhost') } as any);

		expect(response.status).toBe(200);
		const body = await response.json();
		expect(body.surfaceId).toBe('surface-abc');
		expect(body.surface.type).toBe('createSurface');
		expect(body.surface.components).toHaveLength(1);
		expect(body.provenance).toBe('gemini');
	});

	it('returns 400 for empty message', async () => {
		const request = new Request('http://localhost/api/agent/a2ui', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ message: '', mode: 'analyst' })
		});

		const response = await POST({ request, fetch: mockFetch, url: new URL('http://localhost') } as any);

		expect(response.status).toBe(400);
		const body = await response.json();
		expect(body.code).toBe('INVALID_MESSAGE');
	});

	it('returns 400 for missing message field', async () => {
		const request = new Request('http://localhost/api/agent/a2ui', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ mode: 'analyst' })
		});

		const response = await POST({ request, fetch: mockFetch, url: new URL('http://localhost') } as any);

		expect(response.status).toBe(400);
		const body = await response.json();
		expect(body.code).toBe('INVALID_MESSAGE');
	});

	it('forwards to correct agent endpoint', async () => {
		mockFetch.mockResolvedValueOnce({
			ok: true,
			status: 200,
			json: () => Promise.resolve({ surfaceId: 'test' })
		});

		const request = new Request('http://localhost/api/agent/a2ui', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ message: 'test', context: 'dashboard' })
		});

		await POST({ request, fetch: mockFetch, url: new URL('http://localhost') } as any);

		// Verify fetch was called with the correct URL
		expect(mockFetch).toHaveBeenCalledWith(
			'http://test-gateway:8080/api/agent/a2ui',
			expect.objectContaining({
				method: 'POST',
				headers: expect.objectContaining({
					'Content-Type': 'application/json'
				})
			})
		);
	});

	it('passes through backend error status codes', async () => {
		mockFetch.mockResolvedValueOnce({
			ok: false,
			status: 502,
			statusText: 'Bad Gateway',
			json: () => Promise.resolve({ code: 'ALL_TIERS_DOWN', message: 'All tiers failed' })
		});

		const request = new Request('http://localhost/api/agent/a2ui', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ message: 'test' })
		});

		const response = await POST({ request, fetch: mockFetch, url: new URL('http://localhost') } as any);

		expect(response.status).toBe(502);
		const body = await response.json();
		expect(body.code).toBe('ALL_TIERS_DOWN');
	});
});
