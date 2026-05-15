/**
 * a2ui.stream.test.ts — Tests de integración para SSE A2UI.
 *
 * Verifica que el store de chat procese correctamente eventos SSE
 * con payloads A2UI y actualice el store A2UI en consecuencia.
 */

import { describe, it, expect, beforeEach, vi } from 'vitest';
import { chat } from '../../../src/lib/stores/chat.svelte';
import { a2ui } from '../../../src/lib/stores/a2ui.svelte';

function setupMockStream(events: string[]): void {
	const encoder = new TextEncoder();
	const stream = new ReadableStream({
		start(controller) {
			for (const event of events) {
				controller.enqueue(encoder.encode(`data: ${event}\n\n`));
			}
			controller.close();
		}
	});

	globalThis.fetch = vi.fn().mockResolvedValue({
		ok: true,
		body: stream,
		headers: new Headers({ 'Content-Type': 'text/event-stream' })
	});
}

describe('A2UI SSE Stream Integration', () => {
	beforeEach(() => {
		chat.clear();
		a2ui.exitAgentiveMode();
		a2ui.updateTree(null, 'replace');
		a2ui.updateLayout({ layout: 'grid', columns: { desktop: 3, tablet: 2, mobile: 1 }, gap: 'md' });
		vi.clearAllMocks();
	});

	it('SSE a2ui event replaces tree via chat store', async () => {
		setupMockStream([
			JSON.stringify({
				type: 'a2ui',
				tree: {
					type: 'container',
					props: { layout: 'grid' },
					children: [
						{ type: 'insight-panel', props: { title: 'KPIs', variant: 'primary' } },
						{ type: 'chart', props: { type: 'bar' } },
						{ type: 'anomaly-list', props: { title: 'Alertas' } }
					]
				},
				action: 'replace'
			})
		]);

		await chat.send('Show me the dashboard');

		// Tree should be set
		expect(a2ui.tree).not.toBeNull();
		expect(a2ui.tree?.type).toBe('container');
		expect(a2ui.tree?.children).toHaveLength(3);
		expect(a2ui.tree?.children?.[0].type).toBe('insight-panel');
		expect(a2ui.tree?.children?.[1].type).toBe('chart');
		expect(a2ui.tree?.children?.[2].type).toBe('anomaly-list');
	});

	it('SSE a2ui event with append adds to existing tree', async () => {
		// Set initial tree
		a2ui.updateTree({
			type: 'container',
			children: [{ type: 'card', props: { title: 'Welcome' } }]
		}, 'replace');

		setupMockStream([
			JSON.stringify({
				type: 'a2ui',
				tree: {
					type: 'container',
					children: [{ type: 'chart', props: { type: 'line' } }]
				},
				action: 'append'
			})
		]);

		await chat.send('Add chart');

		expect(a2ui.tree?.children).toHaveLength(2);
		expect(a2ui.tree?.children?.[0].type).toBe('card');
		expect(a2ui.tree?.children?.[1].type).toBe('chart');
	});

	it('SSE update event patches props of a node', async () => {
		a2ui.updateTree({
			type: 'container',
			children: [
				{ type: 'chart', nodeId: 'chart-1', props: { title: 'Ventas', type: 'bar', loading: true } }
			]
		}, 'replace');

		setupMockStream([
			JSON.stringify({
				type: 'update',
				nodeId: 'chart-1',
				props: { title: 'Ventas Actualizado', loading: false }
			})
		]);

		await chat.send('Update chart');

		const chartNode = a2ui.tree?.children?.[0];
		expect(chartNode?.props?.title).toBe('Ventas Actualizado');
		expect(chartNode?.props?.loading).toBe(false);
		// Original props preserved
		expect(chartNode?.props?.type).toBe('bar');
	});

	it('SSE patch event replaces children of a node', async () => {
		a2ui.updateTree({
			type: 'container',
			nodeId: 'main-container',
			children: [
				{ type: 'insight-panel', props: { title: 'Old Insights' } },
				{ type: 'card', props: { title: 'Old Card' } }
			]
		}, 'replace');

		setupMockStream([
			JSON.stringify({
				type: 'patch',
				nodeId: 'main-container',
				children: [
					{ type: 'chart', props: { type: 'pie' } },
					{ type: 'anomaly-list', props: { title: 'New Anomalies' } }
				]
			})
		]);

		await chat.send('Replace content');

		expect(a2ui.tree?.children).toHaveLength(2);
		expect(a2ui.tree?.children?.[0].type).toBe('chart');
		expect(a2ui.tree?.children?.[1].type).toBe('anomaly-list');
	});

	it('SSE mixed events: a2ui + update + patch in sequence', async () => {
		// First, send an a2ui event
		setupMockStream([
			JSON.stringify({
				type: 'a2ui',
				tree: {
					type: 'container',
					nodeId: 'main',
					children: [
						{ type: 'card', nodeId: 'card-1', props: { title: 'Initial' } }
					]
				},
				action: 'replace'
			}),
			JSON.stringify({
				type: 'update',
				nodeId: 'card-1',
				props: { title: 'Updated' }
			}),
			JSON.stringify({
				type: 'patch',
				nodeId: 'main',
				children: [
					{ type: 'card', props: { title: 'Updated' } },
					{ type: 'chart', props: { type: 'line' } }
				]
			})
		]);

		await chat.send('Build dashboard');

		// After all 3 events:
		expect(a2ui.tree?.children).toHaveLength(2);
		expect(a2ui.tree?.children?.[0].type).toBe('card');
		expect(a2ui.tree?.children?.[1].type).toBe('chart');
	});

	it('existing text events still work alongside A2UI events', async () => {
		setupMockStream([
			JSON.stringify({ type: 'chunk', content: 'Procesando', done: false }),
			JSON.stringify({ type: 'chunk', content: ' dashboard', done: false }),
			JSON.stringify({ type: 'a2ui', tree: { type: 'card' }, action: 'replace' }),
			JSON.stringify({ type: 'done', content: 'Dashboard listo', done: true })
		]);

		await chat.send('Show dashboard');

		// Text content should be accumulated
		const assistantMsg = chat.messages[1];
		expect(assistantMsg.content).toBe('Procesando dashboard');

		// A2UI tree should be set
		expect(a2ui.tree).not.toBeNull();
		expect(a2ui.tree?.type).toBe('card');

		// Status should be idle
		expect(chat.status).toBe('idle');
	});
});
