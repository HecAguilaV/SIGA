/**
 * chat-a2ui-events.test.ts — Tests para los eventos SSE A2UI extendidos.
 *
 * Verifica que el store de chat maneje correctamente
 * los nuevos tipos de eventos: a2ui, update, patch.
 */

import { describe, it, expect, beforeEach, vi } from 'vitest';
import { chat } from '../../../src/lib/stores/chat.svelte';
import { a2ui } from '../../../src/lib/stores/a2ui.svelte';

// Mock global fetch
beforeEach(() => {
	chat.clear();
	a2ui.exitAgentiveMode();
	a2ui.updateTree(null, 'replace');
	a2ui.updateLayout({ layout: 'grid', columns: { desktop: 3, tablet: 2, mobile: 1 }, gap: 'md' });
	vi.clearAllMocks();
});

function createMockStream(events: string[]): ReadableStream {
	const encoder = new TextEncoder();
	return new ReadableStream({
		start(controller) {
			for (const event of events) {
				controller.enqueue(encoder.encode(`data: ${event}\n\n`));
			}
			controller.close();
		}
	});
}

describe('Chat Store A2UI Events', () => {
	beforeEach(() => {
		globalThis.fetch = vi.fn().mockResolvedValue({
			ok: true,
			body: createMockStream([
				JSON.stringify({ type: 'a2ui', tree: { type: 'container', children: [{ type: 'card', props: { title: 'Hello' } }] }, action: 'replace' })
			]),
			headers: new Headers({ 'Content-Type': 'text/event-stream' })
		});
	});

	it('handles a2ui event and updates the A2UI store tree', async () => {
		await chat.send('Show dashboard');

		// The a2ui event should have updated the tree
		expect(a2ui.tree).not.toBeNull();
		expect(a2ui.tree?.type).toBe('container');
		expect(a2ui.tree?.children).toHaveLength(1);
		expect(a2ui.tree?.children?.[0].type).toBe('card');
	});

	it('handles a2ui event with append action', async () => {
		// First set an initial tree
		a2ui.updateTree({
			type: 'container',
			children: [{ type: 'badge', props: { variant: 'info' } }]
		}, 'replace');

		globalThis.fetch = vi.fn().mockResolvedValue({
			ok: true,
			body: createMockStream([
				JSON.stringify({ type: 'a2ui', tree: { type: 'container', children: [{ type: 'chart', props: { type: 'line' } }] }, action: 'append' })
			]),
			headers: new Headers({ 'Content-Type': 'text/event-stream' })
		});

		await chat.send('Add chart');

		// Should now have both badge and chart
		expect(a2ui.tree?.children).toHaveLength(2);
		expect(a2ui.tree?.children?.[0].type).toBe('badge');
		expect(a2ui.tree?.children?.[1].type).toBe('chart');
	});

	it('handles update event and patches node props', async () => {
		// First set an initial tree with a nodeId
		a2ui.updateTree({
			type: 'container',
			children: [{ type: 'chart', nodeId: 'chart-1', props: { title: 'Ventas', type: 'bar' } }]
		}, 'replace');

		globalThis.fetch = vi.fn().mockResolvedValue({
			ok: true,
			body: createMockStream([
				JSON.stringify({ type: 'update', nodeId: 'chart-1', props: { title: 'Ventas Actualizado' } })
			]),
			headers: new Headers({ 'Content-Type': 'text/event-stream' })
		});

		await chat.send('Update chart');

		const chartNode = a2ui.tree?.children?.[0];
		expect(chartNode?.props?.title).toBe('Ventas Actualizado');
		// Original props preserved
		expect(chartNode?.props?.type).toBe('bar');
	});

	it('handles patch event and replaces children', async () => {
		a2ui.updateTree({
			type: 'container',
			nodeId: 'main',
			children: [{ type: 'card', props: { title: 'Old' } }]
		}, 'replace');

		globalThis.fetch = vi.fn().mockResolvedValue({
			ok: true,
			body: createMockStream([
				JSON.stringify({
					type: 'patch',
					nodeId: 'main',
					children: [{ type: 'insight-panel', props: { title: 'New Insights' } }]
				})
			]),
			headers: new Headers({ 'Content-Type': 'text/event-stream' })
		});

		await chat.send('Replace content');

		expect(a2ui.tree?.children).toHaveLength(1);
		expect(a2ui.tree?.children?.[0].type).toBe('insight-panel');
	});
});
