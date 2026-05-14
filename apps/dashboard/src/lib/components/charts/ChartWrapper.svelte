<script lang="ts">
	import { onMount } from 'svelte';

	type ChartType = 'bar' | 'line' | 'pie' | 'doughnut';

	let {
		type = 'bar',
		data = undefined,
		options = {},
		loading = false,
		height = 300
	}: {
		type?: ChartType;
		data?: any;
		options?: any;
		loading?: boolean;
		height?: number;
	} = $props();

	let canvasEl: HTMLCanvasElement;
	let chartInstance: any = null;
	let chartJsState = $state<'loading' | 'ready' | 'error'>('loading');
	let ChartJSClass: any = null;
	let prevType = $state(type);

	onMount(async () => {
		try {
			const mod = await import('chart.js');
			ChartJSClass = mod.Chart;
			ChartJSClass.register(...mod.registerables);
			chartJsState = 'ready';
		} catch {
			chartJsState = 'error';
		}
	});

	// Create or update chart when dependencies change
	$effect(() => {
		if (chartJsState !== 'ready' || loading || !canvasEl || !data) return;

		if (chartInstance) {
			// Type change requires full recreate
			if (prevType !== type) {
				chartInstance.destroy();
				chartInstance = null;
				prevType = type;
			} else {
				chartInstance.data = data;
				chartInstance.options = { responsive: true, maintainAspectRatio: false, ...options };
				chartInstance.update();
				return;
			}
		}

		if (!chartInstance) {
			chartInstance = new ChartJSClass(canvasEl, {
				type,
				data,
				options: { responsive: true, maintainAspectRatio: false, ...options }
			});
		}
	});

	// Cleanup on destroy
	$effect(() => {
		return () => {
			if (chartInstance) {
				chartInstance.destroy();
				chartInstance = null;
			}
		};
	});
</script>

{#if loading || chartJsState === 'loading'}
	<div class="chart-skeleton" style="height: {height}px" role="status" aria-label="Cargando gráfico">
		Cargando gráfico...
	</div>
{:else if chartJsState === 'error'}
	<div class="chart-error" style="height: {height}px" role="alert">
		Error al cargar el gráfico
	</div>
{:else}
	<div class="chart-wrapper" style="height: {height}px">
		<canvas bind:this={canvasEl}></canvas>
	</div>
{/if}

<style>
	.chart-skeleton {
		background: var(--color-bg-alt);
		border-radius: var(--radius-lg);
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
		animation: pulse 1.5s infinite;
	}

	.chart-error {
		background: var(--color-error-bg);
		border-radius: var(--radius-lg);
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--color-error-text);
		font-size: var(--font-size-sm);
	}

	.chart-wrapper {
		position: relative;
		width: 100%;
	}

	.chart-wrapper canvas {
		width: 100% !important;
		height: 100% !important;
	}

	@keyframes pulse {
		0%, 100% { opacity: 1; }
		50% { opacity: 0.6; }
	}
</style>
