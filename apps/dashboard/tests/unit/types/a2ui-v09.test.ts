/**
 * a2ui-v09.test.ts — Tests unitarios para tipos A2UI v0.9.
 *
 * Verifica:
 * - A2UIComponent con type, props, children, ref
 * - A2UISurface con surfaceId, components, layout
 * - A2UIv0Message createSurface
 * - A2UIv0Message updateComponents
 * - A2UIv0Message updateDataModel
 * - Backward compat: A2UINode, A2UIEvent, UpdateEvent, PatchEvent siguen funcionando
 */

import { describe, it, expect } from 'vitest';
import type {
	A2UIComponent,
	A2UISurface,
	A2UIv0Message,
	A2UINode,
	A2UIEvent,
	UpdateEvent,
	PatchEvent
} from '../../../src/lib/types/a2ui';

describe('A2UI v0.9 Types', () => {
	it('A2UIComponent holds type, props, children, ref', () => {
		const component: A2UIComponent = {
			type: 'stat-card',
			props: { label: 'Ventas', value: '$12.4K', delta: 8.2 },
			children: [],
			ref: 'sc-1'
		};

		expect(component.type).toBe('stat-card');
		expect(component.props?.label).toBe('Ventas');
		expect(component.props?.value).toBe('$12.4K');
		expect(component.props?.delta).toBe(8.2);
		expect(component.children).toEqual([]);
		expect(component.ref).toBe('sc-1');
	});

	it('A2UIComponent allows children array of A2UIComponent', () => {
		const parent: A2UIComponent = {
			type: 'container',
			ref: 'main',
			children: [
				{ type: 'stat-card', ref: 'sc-1', props: { label: 'Ventas' } },
				{ type: 'trend-badge', ref: 'tb-1', props: { text: '+12%', variant: 'success' } }
			]
		};

		expect(parent.children).toHaveLength(2);
		expect(parent.children![0].type).toBe('stat-card');
		expect(parent.children![1].type).toBe('trend-badge');
	});

	it('A2UISurface holds surfaceId, components, and optional layout', () => {
		const surface: A2UISurface = {
			surfaceId: 's1',
			components: [
				{ type: 'stat-card', ref: 'sc-1', props: { label: 'Ventas', value: '$12.4K' } }
			],
			layout: { layout: 'grid', columns: { desktop: 3, tablet: 2, mobile: 1 } }
		};

		expect(surface.surfaceId).toBe('s1');
		expect(surface.components).toHaveLength(1);
		expect(surface.layout?.layout).toBe('grid');
		expect(surface.layout?.columns?.desktop).toBe(3);
	});

	it('A2UISurface allows layout to be omitted', () => {
		const surface: A2UISurface = {
			surfaceId: 's2',
			components: []
		};

		expect(surface.surfaceId).toBe('s2');
		expect(surface.components).toEqual([]);
		expect(surface.layout).toBeUndefined();
	});

	it('A2UIv0Message createSurface has type createSurface + surface fields', () => {
		const msg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's1',
			components: [
				{ type: 'stat-card', ref: 'sc-1', props: { label: 'Ventas', value: '$12.4K' } }
			]
		};

		expect(msg.type).toBe('createSurface');
		if (msg.type === 'createSurface') {
			expect(msg.surfaceId).toBe('s1');
			expect(msg.components).toHaveLength(1);
			expect(msg.components[0].type).toBe('stat-card');
		}
	});

	it('A2UIv0Message createSurface accepts optional layout', () => {
		const msg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's1',
			components: [],
			layout: { layout: 'grid', columns: { desktop: 3, tablet: 2, mobile: 1 } }
		};

		expect(msg.type).toBe('createSurface');
		if (msg.type === 'createSurface') {
			expect(msg.layout?.layout).toBe('grid');
		}
	});

	it('A2UIv0Message updateComponents has type, surfaceId, components, mode', () => {
		const msg: A2UIv0Message = {
			type: 'updateComponents',
			surfaceId: 's1',
			components: [
				{ type: 'stat-card', ref: 'sc-1', props: { value: '$15K' } }
			],
			mode: 'replace'
		};

		expect(msg.type).toBe('updateComponents');
		if (msg.type === 'updateComponents') {
			expect(msg.surfaceId).toBe('s1');
			expect(msg.components).toHaveLength(1);
			expect(msg.mode).toBe('replace');
		}
	});

	it('A2UIv0Message updateComponents supports append and patch modes', () => {
		const appendMsg: A2UIv0Message = {
			type: 'updateComponents',
			surfaceId: 's1',
			components: [{ type: 'trend-badge', ref: 'tb-1' }],
			mode: 'append'
		};
		const patchMsg: A2UIv0Message = {
			type: 'updateComponents',
			surfaceId: 's1',
			components: [{ type: 'stat-card', ref: 'sc-1', props: { value: '$20K' } }],
			mode: 'patch'
		};

		if (appendMsg.type === 'updateComponents') {
			expect(appendMsg.mode).toBe('append');
		}
		if (patchMsg.type === 'updateComponents') {
			expect(patchMsg.mode).toBe('patch');
		}
	});

	it('A2UIv0Message updateDataModel has type, surfaceId, data', () => {
		const msg: A2UIv0Message = {
			type: 'updateDataModel',
			surfaceId: 's1',
			data: { 'chart-1': '/api/sales' }
		};

		expect(msg.type).toBe('updateDataModel');
		if (msg.type === 'updateDataModel') {
			expect(msg.surfaceId).toBe('s1');
			expect(msg.data['chart-1']).toBe('/api/sales');
		}
	});

	it('creates a union of all three message types via A2UIv0Message', () => {
		const messages: A2UIv0Message[] = [
			{ type: 'createSurface', surfaceId: 's1', components: [] },
			{ type: 'updateComponents', surfaceId: 's1', components: [], mode: 'replace' },
			{ type: 'updateDataModel', surfaceId: 's1', data: {} }
		];

		expect(messages).toHaveLength(3);
		expect(messages[0].type).toBe('createSurface');
		expect(messages[1].type).toBe('updateComponents');
		expect(messages[2].type).toBe('updateDataModel');
	});
});

describe('A2UI Backward Compat Types', () => {
	it('A2UINode still works with type, props, children, nodeId', () => {
		const node: A2UINode = {
			type: 'card',
			props: { variant: 'default' },
			children: [{ type: 'button', props: { label: 'Click' } }],
			nodeId: 'card-1'
		};

		expect(node.type).toBe('card');
		expect(node.nodeId).toBe('card-1');
		expect(node.children).toHaveLength(1);
	});

	it('A2UIEvent still works with type, tree, action', () => {
		const event: A2UIEvent = {
			type: 'a2ui',
			tree: { type: 'container', children: [] },
			action: 'replace'
		};

		expect(event.type).toBe('a2ui');
		expect(event.tree.type).toBe('container');
		expect(event.action).toBe('replace');
	});

	it('UpdateEvent still works with type, nodeId, props', () => {
		const event: UpdateEvent = {
			type: 'update',
			nodeId: 'n1',
			props: { title: 'Updated' }
		};

		expect(event.type).toBe('update');
		expect(event.nodeId).toBe('n1');
		expect(event.props.title).toBe('Updated');
	});

	it('PatchEvent still works with type, nodeId, children', () => {
		const event: PatchEvent = {
			type: 'patch',
			nodeId: 'n1',
			children: [{ type: 'card' }]
		};

		expect(event.type).toBe('patch');
		expect(event.nodeId).toBe('n1');
		expect(event.children).toHaveLength(1);
	});
});
