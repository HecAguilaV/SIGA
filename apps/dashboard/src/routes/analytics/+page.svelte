<script lang="ts">
	import { invalidate } from '$app/navigation';
	import type { PageData } from './$types';
	import Card from '@siga/ui-kit/Card.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';
	import Skeleton from '@siga/ui-kit/Skeleton.svelte';
	import ChartContainer from '$lib/components/charts/ChartContainer.svelte';
	import ChartWrapper from '$lib/components/charts/ChartWrapper.svelte';
	import TrendUp from 'phosphor-svelte/lib/TrendUp';
	import Warning from 'phosphor-svelte/lib/Warning';
	import WarningCircle from 'phosphor-svelte/lib/WarningCircle';
	import Info from 'phosphor-svelte/lib/Info';

	let { data }: { data: PageData } = $props();

	// Polling cada 60s
	$effect(() => {
		const pollInterval = setInterval(() => {
			invalidate('analytics:insights');
		}, 60000);
		return () => clearInterval(pollInterval);
	});

	const analytics = $derived(data.analytics);
	const analyticsError = $derived(data.error as string | null);

	// Chart-ready data
	const trendData = $derived({
		labels: analytics?.trends?.map((t: { date: string }) => t.date) ?? [],
		datasets: [
			{
				label: 'Valor',
				data: analytics?.trends?.map((t: { value: number }) => t.value) ?? [],
				borderColor: '#4f46e5',
				backgroundColor: 'rgba(79, 70, 229, 0.1)',
				fill: true,
				tension: 0.4
			}
		]
	});

	const trendOptions = $derived({
		plugins: {
			legend: { display: false }
		},
		scales: {
			x: {
				grid: { display: false }
			},
			y: {
				beginAtZero: true,
				grid: { color: 'rgba(0,0,0,0.05)' }
			}
		}
	});

	// Pie chart data for product distribution
	const pieData = $derived({
		labels: ['Harinas', 'Lácteos', 'Bebidas', 'Granos', 'Otros'],
		datasets: [
			{
				data: [35, 25, 20, 15, 5],
				backgroundColor: ['#4f46e5', '#06b6d4', '#f59e0b', '#10b981', '#8b5cf6']
			}
		]
	});

	const hasData = $derived(analytics && analytics.trends?.length > 0);

	const insightVariant = $derived((type: string) => {
		switch (type) {
			case 'positive': return 'success' as const;
			case 'warning': return 'warning' as const;
			case 'danger': return 'danger' as const;
			default: return 'info' as const;
		}
	});
</script>

<svelte:head>
	<title>SIGA — Analíticas</title>
</svelte:head>

<div class="analytics-page">
	<div class="analytics-header">
		<h1>Analíticas</h1>
		<p class="analytics-subtitle">Información detallada y tendencias del negocio</p>
	</div>

	{#if analyticsError && !hasData}
		<div class="error-banner" role="alert">
			{analyticsError}
		</div>
	{/if}

	<!-- Summary Card -->
	{#if analytics?.summary}
		<Card variant="glass" padding="md">
			{#snippet children()}
				<div class="summary-content">
					<p class="summary-text">{analytics.summary}</p>
				</div>
			{/snippet}
		</Card>
	{/if}

	<!-- Charts Grid -->
	<div class="charts-grid">
		<ChartContainer title="Tendencia de 7 Días" description="Evolución diaria de indicadores">
			{#snippet children()}
				{@render chartContent()}
			{/snippet}
		</ChartContainer>

		<ChartContainer title="Distribución por Categoría" description="Composición del inventario">
			{#snippet children()}
				<div style="height: 300px">
					{#if hasData}
						<ChartWrapper type="doughnut" data={pieData} options={{ plugins: { legend: { position: 'bottom' } } }} />
					{:else}
						<div class="chart-empty-state" role="status">Sin datos disponibles</div>
					{/if}
				</div>
			{/snippet}
		</ChartContainer>
	</div>

	{#snippet chartContent()}
		<div style="height: 300px">
			{#if hasData}
				<ChartWrapper type="line" data={trendData} options={trendOptions} />
			{:else}
				<div class="chart-empty-state" role="status">Sin datos disponibles</div>
			{/if}
		</div>
	{/snippet}

	<!-- Insights Panel -->
	{#if analytics?.insights?.length}
		<Card variant="default" padding="md">
			{#snippet header()}
				<h2 class="panel-title">Hallazgos Analíticos</h2>
			{/snippet}
			{#snippet children()}
				<div class="insights-list">
					{#each analytics.insights as insight (insight.id)}
						<div class="insight-item">
							<div class="insight-header">
								<Badge variant={insightVariant(insight.type)}>
									{#if insight.type === 'positive'}
										<TrendUp size={14} weight="bold" aria-label="positive insight" />
									{:else if insight.type === 'warning'}
										<Warning size={14} weight="bold" aria-label="warning insight" />
									{:else if insight.type === 'danger'}
										<WarningCircle size={16} weight="bold" aria-label="danger insight" />
									{:else}
										<Info size={14} weight="bold" aria-label="info insight" />
									{/if}
									{insight.title}
								</Badge>
							</div>
							<p class="insight-description">{insight.description}</p>
							{#if insight.context}
								<p class="insight-context">{insight.context}</p>
							{/if}
						</div>
					{/each}
				</div>
			{/snippet}
		</Card>
	{/if}

	<!-- Anomalies Section -->
	{#if analytics?.anomalies?.length}
		<Card variant="default" padding="md">
			{#snippet header()}
				<h2 class="panel-title">Anomalías Detectadas</h2>
			{/snippet}
			{#snippet children()}
				<div class="anomaly-list">
					{#each analytics.anomalies as anomaly (anomaly.id)}
						<div class="anomaly-item">
							<span
								class="anomaly-severity"
								aria-label={`Severidad: ${anomaly.severity}`}
								style="color: {anomaly.severity === 'critical' || anomaly.severity === 'high' ? 'var(--color-error)' : 'var(--color-warning)'}"
							>
								<WarningCircle size={16} weight="bold" aria-hidden="true" />
							</span>
							<p class="anomaly-message">{anomaly.message}</p>
						</div>
					{/each}
				</div>
			{/snippet}
		</Card>
	{/if}

	{#if analyticsError && hasData}
		<div class="info-banner" role="status">
			{analyticsError}
		</div>
	{/if}
</div>

<style>
	.analytics-page {
		max-width: 1200px;
		margin: 0 auto;
	}

	.analytics-header {
		margin-bottom: var(--spacing-lg);
	}

	.analytics-header h1 {
		font-size: var(--font-size-2xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
	}

	.analytics-subtitle {
		color: var(--color-text-secondary);
		font-size: var(--font-size-sm);
		margin-top: var(--spacing-xs);
	}

	.error-banner {
		padding: var(--spacing-sm) var(--spacing-md);
		background: var(--color-error-bg);
		color: var(--color-error-text);
		border-radius: var(--radius-md);
		margin-bottom: var(--spacing-lg);
		font-size: var(--font-size-sm);
	}

	.info-banner {
		padding: var(--spacing-sm) var(--spacing-md);
		background: var(--color-bg-alt);
		color: var(--color-text-muted);
		border-radius: var(--radius-md);
		margin-top: var(--spacing-lg);
		font-size: var(--font-size-sm);
	}

	.summary-content {
		padding: var(--spacing-sm) 0;
	}

	.summary-text {
		font-size: var(--font-size-base);
		color: var(--color-text);
		line-height: 1.6;
	}

	.charts-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: var(--spacing-md);
		margin-top: var(--spacing-lg);
	}

	@media (max-width: 768px) {
		.charts-grid {
			grid-template-columns: 1fr;
		}
	}

	.chart-empty-state {
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}

	.panel-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	.insights-list {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.insight-item {
		padding: var(--spacing-sm) 0;
		border-bottom: 1px solid var(--color-border-light);
	}

	.insight-item:last-child {
		border-bottom: none;
	}

	.insight-header {
		margin-bottom: var(--spacing-xs);
	}

	.insight-description {
		font-size: var(--font-size-sm);
		color: var(--color-text);
		margin: 0;
	}

	.insight-context {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		margin: var(--spacing-xs) 0 0;
		font-style: italic;
	}

	.anomaly-list {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
	}

	.anomaly-item {
		display: flex;
		align-items: flex-start;
		gap: var(--spacing-sm);
		padding: var(--spacing-sm);
		border-radius: var(--radius-md);
		background: var(--color-bg-alt);
	}

	.anomaly-severity {
		font-size: 1rem;
		flex-shrink: 0;
	}

	.anomaly-message {
		font-size: var(--font-size-sm);
		color: var(--color-text);
		line-height: 1.4;
		margin: 0;
	}
</style>
