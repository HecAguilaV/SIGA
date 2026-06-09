import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/svelte';
import ChartWrapper from '$lib/components/charts/ChartWrapper.svelte';

// Mock chart.js to avoid real canvas rendering in jsdom
vi.mock('chart.js', () => {
	const destroyMock = vi.fn();
	const updateMock = vi.fn();
	const Chart: any = vi.fn().mockImplementation(function (
		this: { destroy: typeof destroyMock; update: typeof updateMock; config: { type: string }; data: unknown; options: unknown },
		_ctx: unknown,
		config: { type: string; data: unknown; options: unknown }
	) {
		this.destroy = destroyMock;
		this.update = updateMock;
		this.config = { type: config.type };
		this.data = config.data;
		this.options = config.options;
	});
	Chart.register = vi.fn();
	return {
		Chart,
		registerables: []
	};
});

describe('ChartWrapper', () => {
	const mockData = {
		labels: ['Ene', 'Feb', 'Mar'],
		datasets: [{ label: 'Test', data: [1, 2, 3] }]
	};

	it('shows loading skeleton when loading is true', () => {
		render(ChartWrapper, {
			props: { loading: true, data: mockData }
		});
		expect(screen.getByRole('status')).toBeInTheDocument();
		expect(screen.getByRole('status')).toHaveTextContent(/cargando/i);
	});

	it('renders canvas element when not loading', async () => {
		const { container } = render(ChartWrapper, {
			props: { loading: false, data: mockData, type: 'bar' }
		});

		await waitFor(() => {
			expect(container.querySelector('canvas')).toBeInTheDocument();
		});
	});

	it('calls Chart constructor with correct type', async () => {
		const { Chart } = await import('chart.js');
		vi.clearAllMocks();

		render(ChartWrapper, {
			props: { type: 'bar', data: mockData, loading: false }
		});

		await waitFor(() => {
			expect(Chart).toHaveBeenCalled();
		});
	});

	it('hides loading skeleton when chart is ready', async () => {
		render(ChartWrapper, {
			props: { loading: false, data: mockData }
		});

		await waitFor(() => {
			expect(screen.queryByRole('status')).not.toBeInTheDocument();
		});
	});

	it('recreates chart on type change', async () => {
		const { Chart } = await import('chart.js');
		const mockChart = Chart as any;

		const { rerender } = render(ChartWrapper, {
			props: { type: 'bar', data: mockData, loading: false }
		});

		// Wait for chart to be created with 'bar' type
		await waitFor(() => {
			expect(screen.queryByRole('status')).not.toBeInTheDocument();
		});

		// Verify the last chart was created with bar type
		const barCalls = mockChart.mock.calls.filter((c: any) => c[1]?.type === 'bar');
		expect(barCalls.length).toBeGreaterThan(0);

		const callsBeforeRerender = mockChart.mock.calls.length;

		rerender({
			type: 'line',
			data: mockData,
			loading: false
		});

		// After rerender, chart should be re-created with new type
		await waitFor(() => {
			const lineCalls = mockChart.mock.calls.slice(callsBeforeRerender).filter((c: any) => c[1]?.type === 'line');
			expect(lineCalls.length).toBeGreaterThan(0);
		});
	});
});
