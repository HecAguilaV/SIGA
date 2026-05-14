import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import InsightPanel from '$lib/components/dashboard/InsightPanel.svelte';

describe('InsightPanel', () => {
	const mockInsights = [
		{
			id: 'i1',
			title: 'Pico de Ventas',
			description: 'Las ventas alcanzaron su punto máximo los fines de semana',
			type: 'positive' as const,
			context: '30% por encima del promedio semanal'
		},
		{
			id: 'i2',
			title: 'Stock Bajo',
			description: '3 productos requieren reposición urgente',
			type: 'warning' as const,
			context: 'Revisar próximos pedidos'
		}
	];

	it('renders insight items with titles', () => {
		render(InsightPanel, { props: { insights: mockInsights } });
		expect(screen.getByText(/Pico de Ventas/)).toBeInTheDocument();
		expect(screen.getByText(/Stock Bajo/)).toBeInTheDocument();
	});

	it('renders insight descriptions', () => {
		render(InsightPanel, { props: { insights: mockInsights } });
		expect(screen.getByText('Las ventas alcanzaron su punto máximo los fines de semana')).toBeInTheDocument();
		expect(screen.getByText('3 productos requieren reposición urgente')).toBeInTheDocument();
	});

	it('renders context text when provided', () => {
		render(InsightPanel, { props: { insights: mockInsights } });
		expect(screen.getByText('30% por encima del promedio semanal')).toBeInTheDocument();
		expect(screen.getByText('Revisar próximos pedidos')).toBeInTheDocument();
	});

	it('shows empty state when no insights', () => {
		render(InsightPanel, { props: { insights: [] } });
		expect(screen.getByText('No hay datos')).toBeInTheDocument();
	});

	it('shows custom empty message when provided', () => {
		render(InsightPanel, { props: { insights: [], emptyMessage: 'Sin hallazgos para mostrar' } });
		expect(screen.getByText('Sin hallazgos para mostrar')).toBeInTheDocument();
	});

	it('shows panel title when provided', () => {
		render(InsightPanel, { props: { insights: mockInsights, title: 'Hallazgos Analíticos' } });
		expect(screen.getByText('Hallazgos Analíticos')).toBeInTheDocument();
	});
});
