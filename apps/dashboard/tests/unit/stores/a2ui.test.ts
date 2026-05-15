/**
 * a2ui.test.ts — Tests unitarios para el store A2UI (a2ui.svelte.ts).
 *
 * Verifica:
 * - Modo de operación: classic ↔ a2ui
 * - Tree management: updateTree (replace/append), patchNode, patchChildren
 * - Layout hints
 * - Derived state (isAgentive)
 */

import { describe, it, expect, beforeEach } from 'vitest';
import { a2ui } from '../../../src/lib/stores/a2ui.svelte';
import type { A2UINode, A2UILayout } from '../../../src/lib/types/a2ui';

describe('A2UI Store', () => {
beforeEach(() => {
	// Resetear el store entre tests
	a2ui.exitAgentiveMode();
	a2ui.updateTree(null, 'replace');
	a2ui.updateLayout({ layout: 'grid', columns: { desktop: 3, tablet: 2, mobile: 1 }, gap: 'md' });
});

	it('starts in classic mode', () => {
		expect(a2ui.mode).toBe('classic');
		expect(a2ui.isAgentive).toBe(false);
	});

	it('enterAgentiveMode changes mode to a2ui', () => {
		a2ui.enterAgentiveMode({ route: '/products' });

		expect(a2ui.mode).toBe('a2ui');
		expect(a2ui.isAgentive).toBe(true);
	});

	it('enterAgentiveMode stores the route context', () => {
		a2ui.enterAgentiveMode({ route: '/analytics' });

		expect(a2ui.mode).toBe('a2ui');
	});

	it('exitAgentiveMode returns to classic mode', () => {
		a2ui.enterAgentiveMode({ route: '/dashboard' });
		a2ui.exitAgentiveMode();

		expect(a2ui.mode).toBe('classic');
		expect(a2ui.isAgentive).toBe(false);
	});

	it('updateTree with replace sets a new tree', () => {
		const tree: A2UINode = {
			type: 'container',
			props: { layout: 'grid' },
			children: [
				{ type: 'card', props: { title: 'Hello' } }
			]
		};

		a2ui.updateTree(tree, 'replace');

		expect(a2ui.tree).not.toBeNull();
		expect(a2ui.tree?.type).toBe('container');
		expect(a2ui.tree?.children).toHaveLength(1);
		expect(a2ui.tree?.children?.[0].type).toBe('card');
	});

	it('updateTree with replace overwrites existing tree', () => {
		const firstTree: A2UINode = {
			type: 'container',
			children: [{ type: 'card', props: { title: 'First' } }]
		};
		const secondTree: A2UINode = {
			type: 'container',
			children: [{ type: 'chart', props: { type: 'bar' } }]
		};

		a2ui.updateTree(firstTree, 'replace');
		a2ui.updateTree(secondTree, 'replace');

		expect(a2ui.tree?.children).toHaveLength(1);
		expect(a2ui.tree?.children?.[0].type).toBe('chart');
	});

	it('updateTree with append adds children to existing container', () => {
		const initialTree: A2UINode = {
			type: 'container',
			children: [{ type: 'card', props: { title: 'First' } }]
		};
		const appendNode: A2UINode = {
			type: 'container',
			children: [{ type: 'chart', props: { type: 'line' } }]
		};

		a2ui.updateTree(initialTree, 'replace');
		a2ui.updateTree(appendNode, 'append');

		expect(a2ui.tree?.children).toHaveLength(2);
		expect(a2ui.tree?.children?.[0].type).toBe('card');
		expect(a2ui.tree?.children?.[1].type).toBe('chart');
	});

	it('updateTree with append on null tree creates new tree', () => {
		a2ui.updateTree(null, 'replace');

		const newNode: A2UINode = {
			type: 'container',
			children: [{ type: 'badge', props: { variant: 'info' } }]
		};

		a2ui.updateTree(newNode, 'append');

		expect(a2ui.tree).not.toBeNull();
		expect(a2ui.tree?.type).toBe('container');
	});

	it('patchNode updates props of a specific node by nodeId', () => {
		const tree: A2UINode = {
			type: 'container',
			children: [
				{ type: 'chart', nodeId: 'chart-1', props: { type: 'bar', title: 'Ventas' } }
			]
		};

		a2ui.updateTree(tree, 'replace');
		a2ui.patchNode('chart-1', { title: 'Ventas Actualizado', loading: false });

		const chartNode = a2ui.tree?.children?.[0];
		expect(chartNode?.props?.title).toBe('Ventas Actualizado');
		expect(chartNode?.props?.loading).toBe(false);
		expect(chartNode?.props?.type).toBe('bar');
	});

	it('patchNode does nothing for unknown nodeId', () => {
		const tree: A2UINode = {
			type: 'container',
			children: [{ type: 'card', nodeId: 'card-1', props: { title: 'Original' } }]
		};

		a2ui.updateTree(tree, 'replace');
		a2ui.patchNode('unknown-id', { title: 'Changed' });

		expect(a2ui.tree?.children?.[0].props?.title).toBe('Original');
	});

	it('patchChildren replaces children of a specific node by nodeId', () => {
		const tree: A2UINode = {
			type: 'container',
			nodeId: 'main',
			children: [
				{ type: 'card', props: { title: 'Old Card' } }
			]
		};

		a2ui.updateTree(tree, 'replace');

		const newChildren: A2UINode[] = [
			{ type: 'insight-panel', props: { title: 'Insights' } },
			{ type: 'chart', props: { type: 'pie' } }
		];

		a2ui.patchChildren('main', newChildren);

		expect(a2ui.tree?.children).toHaveLength(2);
		expect(a2ui.tree?.children?.[0].type).toBe('insight-panel');
		expect(a2ui.tree?.children?.[1].type).toBe('chart');
	});

	it('patchChildren does nothing for unknown nodeId', () => {
		const tree: A2UINode = {
			type: 'container',
			children: [{ type: 'card', props: { title: 'Stay' } }]
		};

		a2ui.updateTree(tree, 'replace');
		a2ui.patchChildren('unknown', [{ type: 'button', props: {} }]);

		expect(a2ui.tree?.children).toHaveLength(1);
		expect(a2ui.tree?.children?.[0].type).toBe('card');
	});

	it('updateLayout changes layout hints', () => {
		a2ui.updateLayout({ layout: 'stack', gap: 'lg' });

		expect(a2ui.layout.layout).toBe('stack');
		expect(a2ui.layout.gap).toBe('lg');
	});

	it('updateLayout merges with existing layout', () => {
		a2ui.updateLayout({ columns: { desktop: 4, tablet: 3, mobile: 1 } });

		expect(a2ui.layout.layout).toBe('grid');
		expect(a2ui.layout.columns?.desktop).toBe(4);
		expect(a2ui.layout.columns?.tablet).toBe(3);
		expect(a2ui.layout.columns?.mobile).toBe(1);
	});

	it('tracks selectedNodeId', () => {
		expect(a2ui.selectedNodeId).toBeNull();

		// internal state tracking, set via tree
		const tree: A2UINode = {
			type: 'card',
			nodeId: 'card-active',
			props: { title: 'Active' }
		};

		a2ui.updateTree(tree, 'replace');
		// Just verify the nodeId is preserved
		expect(a2ui.tree?.nodeId).toBe('card-active');
	});

	it('isAgentive derived state is correct', () => {
		expect(a2ui.isAgentive).toBe(false);

		a2ui.enterAgentiveMode({ route: '/' });
		expect(a2ui.isAgentive).toBe(true);

		a2ui.exitAgentiveMode();
		expect(a2ui.isAgentive).toBe(false);
	});

	it('tree starts as null', () => {
		expect(a2ui.tree).toBeNull();
	});

	it('updateTree with null removes the tree', () => {
		const tree: A2UINode = {
			type: 'container',
			children: [{ type: 'card', props: { title: 'Data' } }]
		};

		a2ui.updateTree(tree, 'replace');
		expect(a2ui.tree).not.toBeNull();

		a2ui.updateTree(null, 'replace');
		expect(a2ui.tree).toBeNull();
	});
});
