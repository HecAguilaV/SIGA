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

	it('renders a Phosphor SVG inside each anomaly-severity host', () => {
		const { container } = render(AnomalyList, { props: { anomalies: mockAnomalies } });
		// mockAnomalies covers both ternary branches: high (red) and medium (yellow)
		const severitySvgs = container.querySelectorAll('.anomaly-severity svg');
		expect(severitySvgs.length).toBe(mockAnomalies.length);
	});

	it('keeps severity aria-label and marks the Phosphor SVG decorative', () => {
		const { container } = render(AnomalyList, { props: { anomalies: mockAnomalies } });
		const hosts = container.querySelectorAll('.anomaly-severity');
		expect(hosts[0]?.getAttribute('aria-label')).toBe('Severidad: high');
		expect(hosts[1]?.getAttribute('aria-label')).toBe('Severidad: medium');
		// Host label is the accessible name; the icon itself must be hidden from AT
		expect(hosts[0]?.querySelector('svg')?.getAttribute('aria-hidden')).toBe('true');
		expect(hosts[1]?.querySelector('svg')?.getAttribute('aria-hidden')).toBe('true');
	});
});
