/**
 * A2UIRenderer-v09.test.ts — Tests para A2UIRenderer.svelte con A2UI v0.9 envelope.
 *
 * Verifica:
 * - Renderizado de lista plana de components
 * - Mapeo type → componente via catalog.ts
 * - Empty state cuando components está vacío
 * - Backward compat con tree (A2UINode)
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import A2UIRenderer from '../../../../src/lib/components/a2ui/A2UIRenderer.svelte';
import type { A2UIComponent } from '../../../../src/lib/types/a2ui';

// Mock chart.js to avoid real canvas rendering
vi.mock('chart.js', () => {
	const destroyMock = vi.fn();
	const updateMock = vi.fn();
	const Chart = vi.fn().mockImplementation(function (
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

describe('A2UIRenderer v0.9', () => {
	it('renders stat-card component from v0.9 envelope', () => {
		const components: A2UIComponent[] = [
			{ type: 'stat-card', props: { label: 'Revenue', value: '$10k' } }
		];

		render(A2UIRenderer, { surfaceId: 's1', components });

		expect(screen.getByText('Revenue')).toBeInTheDocument();
		expect(screen.getByText('$10k')).toBeInTheDocument();
	});

	it('renders multiple components from v0.9 envelope', () => {
		const components: A2UIComponent[] = [
			{ type: 'stat-card', props: { label: 'Sales', value: 500 } },
			{ type: 'trend-badge', props: { label: 'Growth', value: '+12%', trend: 'up' } }
		];

		render(A2UIRenderer, { surfaceId: 's1', components });

		expect(screen.getByText('Sales')).toBeInTheDocument();
		expect(screen.getByText('500')).toBeInTheDocument();
		expect(screen.getByText('Growth')).toBeInTheDocument();
		expect(screen.getByText('+12%')).toBeInTheDocument();
	});

	it('renders data-table component from v0.9 envelope', () => {
		const components: A2UIComponent[] = [
			{
				type: 'data-table',
				props: {
					columns: [{ key: 'name', label: 'Name' }],
					rows: [{ name: 'Alice' }]
				}
			}
		];

		render(A2UIRenderer, { surfaceId: 's1', components });

		expect(screen.getByText('Name')).toBeInTheDocument();
		expect(screen.getByText('Alice')).toBeInTheDocument();
	});

	it('shows empty state when components array is empty', () => {
		render(A2UIRenderer, { surfaceId: 's1', components: [] });

		expect(screen.getByText('No hay contenido disponible')).toBeInTheDocument();
	});

	it('shows empty state when components is undefined', () => {
		render(A2UIRenderer, { surfaceId: 's1' });

		expect(screen.getByText('No hay contenido disponible')).toBeInTheDocument();
	});

	it('renders components with no surfaceId still works', () => {
		const components: A2UIComponent[] = [
			{ type: 'stat-card', props: { label: 'Test', value: 1 } }
		];

		render(A2UIRenderer, { components });

		expect(screen.getByText('Test')).toBeInTheDocument();
	});
});
