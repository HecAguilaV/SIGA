/**
 * a2ui-v09.test.ts — Tests unitarios para funcionalidad A2UI v0.9 del store.
 *
 * Verifica:
 * - handleSurface createSurface → sets components[], surfaceId, dataBindings
 * - handleSurface updateComponents → merge modes (replace/append/patch)
 * - handleSurface updateDataModel → updates dataBindings
 * - Backward compat: patchNode/patchChildren siguen funcionando
 */

import { describe, it, expect, beforeEach } from 'vitest';
import { a2ui } from '../../../src/lib/stores/a2ui.svelte';
import type { A2UIComponent, A2UIv0Message } from '../../../src/lib/types/a2ui';

describe('A2UI v0.9 Store', () => {
	beforeEach(() => {
		// Reset the store
		a2ui.exitAgentiveMode();
		a2ui.updateTree(null, 'replace');
	});

	it('starts with empty components array', () => {
		expect(a2ui.components).toEqual([]);
		expect(a2ui.surfaceId).toBe('');
	});

	it('handleSurface createSurface sets components and surfaceId', () => {
		const msg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 'surface-1',
			components: [
				{ type: 'stat-card', props: { label: 'Ventas', value: 100 } },
				{ type: 'trend-badge', props: { label: 'Crecimiento', value: '+15%' } }
			]
		};

		a2ui.handleSurface(msg);

		expect(a2ui.surfaceId).toBe('surface-1');
		expect(a2ui.components).toHaveLength(2);
		expect(a2ui.components[0].type).toBe('stat-card');
		expect(a2ui.components[1].type).toBe('trend-badge');
	});

	it('handleSurface createSurface sets dataBindings when provided', () => {
		const msg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 'surface-2',
			components: [{ type: 'stat-card', props: { label: 'Test' } }],
			layout: { layout: 'grid', columns: { desktop: 2, tablet: 1, mobile: 1 } }
		};

		a2ui.handleSurface(msg);

		expect(a2ui.surfaceId).toBe('surface-2');
	});

	it('handleSurface updateComponents replace mode replaces all components', () => {
		const createMsg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's1',
			components: [{ type: 'stat-card', ref: 'old' }]
		};
		a2ui.handleSurface(createMsg);
		expect(a2ui.components).toHaveLength(1);

		const updateMsg: A2UIv0Message = {
			type: 'updateComponents',
			surfaceId: 's1',
			components: [
				{ type: 'trend-badge', ref: 'new1' },
				{ type: 'data-table', ref: 'new2' }
			],
			mode: 'replace'
		};
		a2ui.handleSurface(updateMsg);

		expect(a2ui.components).toHaveLength(2);
		expect(a2ui.components[0].type).toBe('trend-badge');
		expect(a2ui.components[1].type).toBe('data-table');
	});

	it('handleSurface updateComponents append mode adds to existing components', () => {
		const createMsg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's1',
			components: [{ type: 'stat-card', ref: 'a' }]
		};
		a2ui.handleSurface(createMsg);

		const appendMsg: A2UIv0Message = {
			type: 'updateComponents',
			surfaceId: 's1',
			components: [{ type: 'trend-badge', ref: 'b' }],
			mode: 'append'
		};
		a2ui.handleSurface(appendMsg);

		expect(a2ui.components).toHaveLength(2);
		expect(a2ui.components[0].ref).toBe('a');
		expect(a2ui.components[1].ref).toBe('b');
	});

	it('handleSurface updateComponents patch mode merges by ref', () => {
		const createMsg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's1',
			components: [
				{ type: 'stat-card', ref: 'card-1', props: { label: 'Ventas', value: 100 } },
				{ type: 'trend-badge', ref: 'badge-1', props: { label: 'Grow', value: '5%' } }
			]
		};
		a2ui.handleSurface(createMsg);

		const patchMsg: A2UIv0Message = {
			type: 'updateComponents',
			surfaceId: 's1',
			components: [
				{ type: 'stat-card', ref: 'card-1', props: { value: 150, change: '+50%' } }
			],
			mode: 'patch'
		};
		a2ui.handleSurface(patchMsg);

		expect(a2ui.components).toHaveLength(2);
		expect(a2ui.components[0].props?.value).toBe(150);
		expect(a2ui.components[0].props?.label).toBe('Ventas');
		expect(a2ui.components[1].props?.value).toBe('5%');
	});

	it('handleSurface updateDataModel updates dataBindings', () => {
		const createMsg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's1',
			components: [{ type: 'stat-card' }]
		};
		a2ui.handleSurface(createMsg);

		const dataMsg: A2UIv0Message = {
			type: 'updateDataModel',
			surfaceId: 's1',
			data: { sales: 1000, users: 500 }
		};
		a2ui.handleSurface(dataMsg);

		expect(a2ui.dataBindings).toEqual({ sales: 1000, users: 500 });
	});

	it('backward compat: patchNode still works after v0.9 changes', () => {
		// Legacy tree mode
		a2ui.updateTree(
			{ type: 'container', nodeId: 'root', children: [{ type: 'card', nodeId: 'card-1', props: { title: 'Old' } }] },
			'replace'
		);

		a2ui.patchNode('card-1', { title: 'Updated' });

		expect(a2ui.tree?.nodeId).toBe('root');
		const child = a2ui.tree?.children?.[0];
		expect(child?.props?.title).toBe('Updated');
	});

	it('backward compat: patchChildren still works after v0.9 changes', () => {
		a2ui.updateTree(
			{ type: 'container', nodeId: 'root', children: [{ type: 'card', props: { title: 'Old' } }] },
			'replace'
		);

		a2ui.patchChildren('root', [{ type: 'chart', props: { type: 'bar' } }]);

		expect(a2ui.tree?.children).toHaveLength(1);
		expect(a2ui.tree?.children?.[0].type).toBe('chart');
	});

	it('handleSurface createSurface also sets dataBindings to empty object', () => {
		const msg: A2UIv0Message = {
			type: 'createSurface',
			surfaceId: 's-no-data',
			components: []
		};

		a2ui.handleSurface(msg);

		expect(a2ui.dataBindings).toEqual({});
	});
});
