/**
 * A2UIRenderer.test.ts — Tests unitarios para A2UIRenderer.svelte.
 *
 * Verifica:
 * - Renderizado de árbol simple (1 card)
 * - Renderizado de árbol anidado (container > card + chart)
 * - Empty state (tree = null)
 * - Unknown type → muestra fallback
 * - Layout hints → aplica clases CSS correctas
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/svelte';
import A2UIRenderer from '../../../../src/lib/components/a2ui/A2UIRenderer.svelte';
import type { A2UINode } from '../../../../src/lib/types/a2ui';
import { a2ui } from '../../../../src/lib/stores/a2ui.svelte';

// Mock chart.js to avoid real canvas rendering
vi.mock('chart.js', () => {
	const destroyMock = vi.fn();
	const updateMock = vi.fn();
	const Chart: any = vi.fn().mockImplementation(function (
		this: { destroy: typeof destroyMock; update: typeof updateMock; config: { type: string } },
		_ctx: unknown,
		config: { type: string }
	) {
		this.destroy = destroyMock;
		this.update = updateMock;
		this.config = { type: config.type };
	});
	Chart.register = vi.fn();
	return { Chart, registerables: [] };
});

// Reset store between tests
function resetStore(): void {
	a2ui.exitAgentiveMode();
	a2ui.updateTree(null, 'replace');
	a2ui.updateLayout({ layout: 'grid', columns: { desktop: 3, tablet: 2, mobile: 1 }, gap: 'md' });
}

describe('A2UIRenderer', () => {
	beforeEach(() => {
		resetStore();
	});

	it('renders a simple card from A2UINode tree', () => {
		const tree: A2UINode = {
			type: 'card',
			props: { variant: 'default', padding: 'md' }
		};

		render(A2UIRenderer, { tree });

		// Card should be rendered (card component renders a div with role="region")
		const card = document.querySelector('.card');
		expect(card).not.toBeNull();
		expect(card?.classList.contains('card-default')).toBe(true);
	});

	it('renders nested tree (container > card + chart)', async () => {
		const tree: A2UINode = {
			type: 'container',
			props: { layout: 'grid' },
			children: [
				{ type: 'card', props: { variant: 'glass' } },
				{ type: 'chart', props: { type: 'bar' } }
			]
		};

		render(A2UIRenderer, { tree });

		// Container should render with grid class
		const container = document.querySelector('.a2ui-container');
		expect(container).not.toBeNull();

		// Both children should render
		const cards = document.querySelectorAll('.card');
		expect(cards.length).toBe(1);

		// ChartWrapper loads chart.js asynchronously — wait for it
		await waitFor(() => {
			const chartElement = document.querySelector('.chart-wrapper');
			expect(chartElement).not.toBeNull();
		});
	});

	it('shows empty state when tree is null', () => {
		render(A2UIRenderer, { tree: null });

		// Should show empty state message
		expect(screen.getByText('No hay contenido disponible')).toBeInTheDocument();
	});

	it('renders a Phosphor SVG inside the empty-state icon', () => {
		const { container } = render(A2UIRenderer, { tree: null });
		const svg = container.querySelector('.a2ui-empty-icon svg');
		expect(svg).not.toBeNull();
		// Prove a real icon rendered, not an empty <svg> shell (Phosphor uses <path>)
		expect(svg?.querySelectorAll('path').length).toBeGreaterThan(0);
	});

	it('shows fallback for unknown component type', () => {
		const tree: A2UINode = {
			type: 'unknown-component-type',
			props: {}
		};

		render(A2UIRenderer, { tree });

		// Should show fallback message
		expect(screen.getByText('Componente no disponible')).toBeInTheDocument();
	});

	it('renders array of trees', () => {
		const trees: A2UINode[] = [
			{ type: 'card', props: { variant: 'default' } },
			{ type: 'badge', props: { variant: 'info' } },
			{ type: 'spinner', props: { size: 'md' } }
		];

		render(A2UIRenderer, { tree: trees });

		const cards = document.querySelectorAll('.card');
		expect(cards.length).toBe(1);

		const badges = document.querySelectorAll('.badge');
		expect(badges.length).toBe(1);

		const spinners = document.querySelectorAll('.spinner');
		expect(spinners.length).toBe(1);
	});

	it('applies grid layout from props', () => {
		const tree: A2UINode = {
			type: 'container',
			props: {
				layout: 'grid',
				columns: { desktop: 3, tablet: 2, mobile: 1 }
			},
			children: [
				{ type: 'card', props: { variant: 'default' } },
				{ type: 'card', props: { variant: 'glass' } },
				{ type: 'card', props: { variant: 'default' } }
			]
		};

		render(A2UIRenderer, { tree });

		const container = document.querySelector('.a2ui-grid');
		expect(container).not.toBeNull();
	});

	it('applies stack layout from props', () => {
		const tree: A2UINode = {
			type: 'container',
			props: {
				layout: 'stack',
				gap: 'lg'
			},
			children: [
				{ type: 'card', props: { variant: 'default' } }
			]
		};

		render(A2UIRenderer, { tree });

		const container = document.querySelector('.a2ui-stack');
		expect(container).not.toBeNull();
	});

	it('renders button with correct props', () => {
		const tree: A2UINode = {
			type: 'button',
			props: { variant: 'primary', size: 'md' }
		};

		render(A2UIRenderer, { tree });

		const button = screen.getByRole('button');
		expect(button).toBeInTheDocument();
		expect(button).toHaveAttribute('class', expect.stringContaining('btn-primary'));
	});

	it('renders input with label', () => {
		const tree: A2UINode = {
			type: 'input',
			props: { label: 'Nombre', placeholder: 'Ingrese nombre' }
		};

		render(A2UIRenderer, { tree });

		expect(screen.getByText('Nombre')).toBeInTheDocument();
	});

	it('renders skeleton for loading states', () => {
		const tree: A2UINode = {
			type: 'skeleton',
			props: { variant: 'card' }
		};

		render(A2UIRenderer, { tree });

		const skeleton = document.querySelector('.skeleton-card');
		expect(skeleton).not.toBeNull();
	});

	it('renders spinner component', () => {
		const tree: A2UINode = {
			type: 'spinner',
			props: { size: 'lg' }
		};

		render(A2UIRenderer, { tree });

		const spinner = document.querySelector('.spinner');
		expect(spinner).not.toBeNull();
	});
});
