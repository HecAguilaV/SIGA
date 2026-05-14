import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import ChartContainer from '$lib/components/charts/ChartContainer.svelte';

describe('ChartContainer', () => {
	it('renders title when provided', () => {
		render(ChartContainer, {
			props: { title: 'Ventas Mensuales' }
		});
		expect(screen.getByText('Ventas Mensuales')).toBeInTheDocument();
	});

	it('renders description when provided', () => {
		render(ChartContainer, {
			props: { description: 'Evolución de ventas en los últimos 30 días' }
		});
		expect(screen.getByText('Evolución de ventas en los últimos 30 días')).toBeInTheDocument();
	});

	it('shows skeleton loading state', () => {
		render(ChartContainer, {
			props: { loading: true, title: 'Test Chart' }
		});
		expect(screen.getByRole('status')).toBeInTheDocument();
		expect(screen.getByRole('status')).toHaveTextContent(/cargando/i);
	});

	it('shows empty state message when empty is true', () => {
		render(ChartContainer, {
			props: { empty: true, emptyMessage: 'No hay datos disponibles' }
		});
		expect(screen.getByText('No hay datos disponibles')).toBeInTheDocument();
	});

	it('shows default empty message when no custom message provided', () => {
		render(ChartContainer, {
			props: { empty: true }
		});
		expect(screen.getByText(/sin datos/i)).toBeInTheDocument();
	});

	it('renders children slot when not empty and not loading', () => {
		render(ChartContainer, {
			props: { title: 'Test Chart' },
			// Test that content area renders without loading/empty overlay
		});
		// The chart container should have a content area
		const container = screen.getByTestId('chart-container');
		expect(container).toBeInTheDocument();
	});
});
