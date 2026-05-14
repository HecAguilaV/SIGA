/**
 * ContextualAssistant.test.ts — Tests unitarios para ContextualAssistant.svelte.
 *
 * Verifica:
 * - Renderiza el componente en modo analyst y operator
 * - Muestra FAB
 * - Abre/cierra el widget de chat
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import ContextualAssistant from '../../../../src/lib/components/a2ui/ContextualAssistant.svelte';

// Mock de $app/stores
vi.mock('$app/stores', () => ({
	page: {
		subscribe: (fn: (val: unknown) => void) => {
			fn({ url: { pathname: '/products' } });
			return () => {};
		}
	}
}));

// Mock de fetch para el store de chat
globalThis.fetch = vi.fn().mockResolvedValue({
	ok: true,
	body: new ReadableStream({
		start(controller) {
			const encoder = new TextEncoder();
			controller.enqueue(encoder.encode('data: {"type":"done","content":"OK","done":true}\n\n'));
			controller.close();
		}
	}),
	headers: new Headers({ 'Content-Type': 'text/event-stream' })
});

describe('ContextualAssistant', () => {
	beforeEach(() => {
		vi.clearAllMocks();
	});

	it('renders the FAB button', () => {
		const { container } = render(ContextualAssistant, {
			props: {
				mode: 'operator',
				currentRoute: '/'
			}
		});

		const fab = container.querySelector('.assistant-fab');
		expect(fab).toBeTruthy();
	});

	it('renders in analyst mode', () => {
		const { container } = render(ContextualAssistant, {
			props: {
				mode: 'analyst',
				currentRoute: '/analytics'
			}
		});

		const fab = container.querySelector('.assistant-fab');
		expect(fab).toBeTruthy();
		expect(fab?.getAttribute('aria-label')).toContain('Abrir');
	});

	it('renders with correct accessibility label', () => {
		const { container } = render(ContextualAssistant, {
			props: {
				mode: 'operator',
				currentRoute: '/'
			}
		});

		const fab = container.querySelector('.assistant-fab');
		expect(fab?.getAttribute('aria-label')).toBe('Abrir asistente');
		expect(fab?.getAttribute('aria-expanded')).toBe('false');
	});

	it('chat widget is hidden by default', () => {
		const { container } = render(ContextualAssistant, {
			props: {
				mode: 'operator',
				currentRoute: '/'
			}
		});

		const widget = container.querySelector('.chat-widget');
		expect(widget).toBeFalsy();
	});

	it('shows status badge on FAB', () => {
		const { container } = render(ContextualAssistant, {
			props: {
				mode: 'operator',
				currentRoute: '/'
			}
		});

		const badge = container.querySelector('.status-badge');
		expect(badge).toBeTruthy();
	});
});
