/**
 * TrendBadge.test.ts — Tests unitarios para TrendBadge.svelte.
 *
 * Verifica:
 * - Renderizado de label y value
 * - Trend direction indicators (up, down, stable)
 * - Color variants
 * - Default props behavior
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import TrendBadge from '../../../../src/lib/components/a2ui/TrendBadge.svelte';

describe('TrendBadge', () => {
	it('renders label and value', () => {
		render(TrendBadge, {
			props: { label: 'Crecimiento', value: '+15%' }
		});

		expect(screen.getByText('Crecimiento')).toBeInTheDocument();
		expect(screen.getByText('+15%')).toBeInTheDocument();
	});

	it('renders with numeric value', () => {
		render(TrendBadge, {
			props: { label: 'Usuarios', value: 128 }
		});

		expect(screen.getByText('Usuarios')).toBeInTheDocument();
		expect(screen.getByText('128')).toBeInTheDocument();
	});

	it('renders up trend with default color', () => {
		render(TrendBadge, {
			props: { label: 'Ganancia', value: '+20%', trend: 'up' }
		});

		const badge = screen.getByTestId('trend-badge');
		expect(badge.className).toContain('trend-up');
	});

	it('renders down trend', () => {
		render(TrendBadge, {
			props: { label: 'Perdida', value: '-5%', trend: 'down' }
		});

		const badge = screen.getByTestId('trend-badge');
		expect(badge.className).toContain('trend-down');
	});

	it('renders stable trend', () => {
		render(TrendBadge, {
			props: { label: 'Estable', value: '0%', trend: 'stable' }
		});

		const badge = screen.getByTestId('trend-badge');
		expect(badge.className).toContain('trend-stable');
	});

	it('defaults to stable trend when not provided', () => {
		render(TrendBadge, {
			props: { label: 'Default', value: 'N/A' }
		});

		const badge = screen.getByTestId('trend-badge');
		expect(badge.className).toContain('trend-stable');
	});

	it('applies custom color prop', () => {
		render(TrendBadge, {
			props: { label: 'Custom', value: '42', color: 'violet' }
		});

		const badge = screen.getByTestId('trend-badge');
		expect(badge.className).toContain('color-violet');
	});
});
