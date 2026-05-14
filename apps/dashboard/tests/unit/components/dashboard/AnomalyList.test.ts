import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import AnomalyList from '$lib/components/dashboard/AnomalyList.svelte';

describe('AnomalyList', () => {
	const mockAnomalies = [
		{
			id: 'a1',
			type: 'stock',
			message: 'Ajuste de stock sin justificación en Producto #3',
			severity: 'high' as const,
			timestamp: new Date().toISOString()
		},
		{
			id: 'a2',
			type: 'price',
			message: 'Cambio de precio fuera de horario laboral en 2 productos',
			severity: 'medium' as const,
			timestamp: new Date().toISOString()
		}
	];

	it('renders anomaly messages', () => {
		render(AnomalyList, { props: { anomalies: mockAnomalies } });
		expect(screen.getByText('Ajuste de stock sin justificación en Producto #3')).toBeInTheDocument();
		expect(screen.getByText('Cambio de precio fuera de horario laboral en 2 productos')).toBeInTheDocument();
	});

	it('shows empty state when no anomalies', () => {
		render(AnomalyList, { props: { anomalies: [] } });
		expect(screen.getByText('Sin anomalías recientes')).toBeInTheDocument();
	});

	it('shows custom empty message when provided', () => {
		render(AnomalyList, { props: { anomalies: [], emptyMessage: 'No se detectaron eventos anómalos' } });
		expect(screen.getByText('No se detectaron eventos anómalos')).toBeInTheDocument();
	});

	it('shows panel title when provided', () => {
		render(AnomalyList, { props: { anomalies: mockAnomalies, title: 'Alertas' } });
		expect(screen.getByText('Alertas')).toBeInTheDocument();
	});
});
