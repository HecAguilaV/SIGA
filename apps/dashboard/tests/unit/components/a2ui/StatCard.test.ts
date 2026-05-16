/**
 * StatCard.test.ts — Tests unitarios para StatCard.svelte.
 *
 * Verifica:
 * - Renderizado de label y value
 * - Trend indicators (up, down, neutral)
 * - Change text display
 * - Icon rendering
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import StatCard from '../../../../src/lib/components/a2ui/StatCard.svelte';

describe('StatCard', () => {
	it('renders label and value', () => {
		render(StatCard, {
			props: { label: 'Ventas', value: '$12,450' }
		});

		expect(screen.getByText('Ventas')).toBeInTheDocument();
		expect(screen.getByText('$12,450')).toBeInTheDocument();
	});

	it('renders with numeric value', () => {
		render(StatCard, {
			props: { label: 'Pedidos', value: 42 }
		});

		expect(screen.getByText('Pedidos')).toBeInTheDocument();
		expect(screen.getByText('42')).toBeInTheDocument();
	});

	it('shows up trend indicator', () => {
		render(StatCard, {
			props: { label: 'Ingresos', value: '$8k', trend: 'up', change: '+12.5%' }
		});

		expect(screen.getByText('+12.5%')).toBeInTheDocument();
		// The trend-up indicator should be rendered — we check for a specific class or test id
		const card = screen.getByTestId('stat-card');
		expect(card.className).toContain('trend-up');
	});

	it('shows down trend indicator', () => {
		render(StatCard, {
			props: { label: 'Devoluciones', value: 3, trend: 'down', change: '-8.3%' }
		});

		expect(screen.getByText('-8.3%')).toBeInTheDocument();
		const card = screen.getByTestId('stat-card');
		expect(card.className).toContain('trend-down');
	});

	it('shows neutral trend indicator', () => {
		render(StatCard, {
			props: { label: 'Stock', value: 156, trend: 'neutral', change: '0%' }
		});

		expect(screen.getByText('0%')).toBeInTheDocument();
		const card = screen.getByTestId('stat-card');
		expect(card.className).toContain('trend-neutral');
	});

	it('does not render change when not provided', () => {
		render(StatCard, {
			props: { label: 'Ventas', value: '$1k' }
		});

		expect(screen.getByText('Ventas')).toBeInTheDocument();
		expect(screen.getByText('$1k')).toBeInTheDocument();
		// No "+12.5%" or anything like that
		expect(screen.queryByText(/^[+-]/)).toBeNull();
	});

	it('renders icon when provided', () => {
		render(StatCard, {
			props: { label: 'Usuarios', value: '1,234', icon: 'users' }
		});

		// The icon should be rendered — expecting a data-testid on the icon element
		expect(screen.getByTestId('stat-card-icon')).toBeInTheDocument();
		expect(screen.getByTestId('stat-card-icon')).toHaveTextContent('users');
	});

	it('does not render icon element when not provided', () => {
		render(StatCard, {
			props: { label: 'Ventas', value: '$5k' }
		});

		expect(screen.queryByTestId('stat-card-icon')).toBeNull();
	});
});
