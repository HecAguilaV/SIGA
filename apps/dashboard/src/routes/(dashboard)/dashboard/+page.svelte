<script lang="ts">
	import { invalidate } from '$app/navigation';
	import type { PageData } from './$types';
	import Card from '@siga/ui-kit/Card.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';
	import ChartWrapper from '$lib/components/charts/ChartWrapper.svelte';
	import InsightPanel from '$lib/components/dashboard/InsightPanel.svelte';
	import AnomalyList from '$lib/components/dashboard/AnomalyList.svelte';
	import MicroTrend from '$lib/components/dashboard/MicroTrend.svelte';

	let { data }: { data: PageData } = $props();

	// Polling cada 60s
	$effect(() => {
		const pollInterval = setInterval(() => {
			invalidate('dashboard:insights');
		}, 60000);
		return () => clearInterval(pollInterval);
	});

	const insights = $derived((data.insights ?? []) as any[]);
	const lowStock = $derived((data.lowStock ?? []) as any[]);
	const anomalies = $derived((data.anomalies ?? []) as any[]);
	const trends = $derived((data.trends ?? []) as any[]);
	const dashboardError = $derived(data.error as string | null);
	const userName = $derived(data.user?.name ?? 'Usuario');

	// Chart-ready trend data
	const trendData = $derived({
		labels: trends.map((t: { date: string }) => t.date) ?? [],
		datasets: [
			{
				label: 'Valor',
				data: trends.map((t: { value: number }) => t.value) ?? [],
				borderColor: 'var(--color-accent)',
				backgroundColor: 'rgba(249, 115, 22, 0.08)',
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
			x: { grid: { display: false } },
			y: {
				beginAtZero: true,
				grid: { color: 'rgba(0,0,0,0.03)' }
			}
		}
	});

	// Derive insights para InsightPanel desde data de anomalías
	const insightFindings = $derived(
		anomalies.length > 0
			? [
					{
						id: 'stock-insight',
						title: lowStock.length > 0 ? 'Stock Bajo Detectado' : 'Stock Normal',
						description:
							lowStock.length > 0
								? `${lowStock.length} producto${lowStock.length !== 1 ? 's' : ''} con stock por debajo del mínimo`
								: 'Todos los productos tienen stock suficiente',
						type: lowStock.length > 0 ? ('warning' as const) : ('positive' as const),
						context: lowStock.length > 0 ? 'Revisar pedidos de reposición' : 'Sin acciones requeridas'
					}
				]
			: []
	);

	// Generador de micro-tendencias históricas para los KPI cards
	function getMicroTrendData(id: string): number[] {
		switch (id) {
			case '1': return [38, 39, 40, 40, 41, 42, 42];
			case '2': return [3, 3, 3, 3, 3, 3, 3];
			case '3': return [12, 10, 8, 7, 5, 4, 3];
			case '4': return [12000, 13000, 12500, 14000, 15000, 16000, 16400];
			default: return [10, 12, 11, 14, 13, 16, 15];
		}
	}
</script>

<svelte:head>
	<title>SIGA — Dashboard</title>
</svelte:head>

<div class="dashboard">
	<div class="dashboard-greeting">
		<h1>Bienvenido, {userName}</h1>
		<p class="dashboard-subtitle">Panel principal — SIGA v2</p>
	</div>

	{#if dashboardError}
		<div class="error-banner" role="alert">
			{dashboardError} — mostrando datos locales
		</div>
	{/if}

	<!-- KPI Grid -->
	<div class="kpi-grid">
		{#each insights as insight (insight.id)}
			<Card variant="glass" padding="lg">
				{#snippet children()}
					<div class="kpi-card">
						<div class="kpi-header">
							<span class="kpi-icon">
								{insight.variant === 'primary' ? '📊' : insight.variant === 'info' ? '🏪' : insight.variant === 'danger' ? '⚠️' : insight.variant === 'success' ? '💰' : '📈'}
							</span>
							{#if insight.trend}
								<Badge variant={insight.trend === 'up' ? 'success' : insight.trend === 'down' ? 'danger' : 'info'}>
									{insight.trend === 'up' ? '↑' : insight.trend === 'down' ? '↓' : '→'} {insight.trendValue ?? ''}%
								</Badge>
							{/if}
						</div>
						<div class="kpi-body">
							<p class="kpi-value">{insight.value}</p>
							<MicroTrend 
								data={getMicroTrendData(insight.id)} 
								color={insight.trend === 'up' ? 'var(--color-success)' : insight.trend === 'down' ? 'var(--color-error)' : 'var(--color-text-muted)'} 
								width={64} 
								height={22} 
							/>
						</div>
						<p class="kpi-title">{insight.title}</p>
					</div>
				{/snippet}
			</Card>
		{/each}
	</div>

	<!-- Trend Chart (7 días) -->
	{#if trends.length > 0}
		<div class="trend-section">
			<Card variant="default" padding="md">
				{#snippet header()}
					<h2 class="panel-title">Tendencia de 7 Días</h2>
				{/snippet}
				{#snippet children()}
					<div class="trend-chart-container">
						<ChartWrapper type="line" data={trendData} options={trendOptions} />
					</div>
				{/snippet}
			</Card>
		</div>
	{/if}

	<!-- Insight Panel -->
	{#if insightFindings.length > 0}
		<div class="insight-section">
			<Card variant="default" padding="md">
				{#snippet header()}
					<h2 class="panel-title">Hallazgos</h2>
				{/snippet}
				{#snippet children()}
					<InsightPanel insights={insightFindings} title="" />
				{/snippet}
			</Card>
		</div>
	{/if}

	<div class="dashboard-grid">
		<!-- Low Stock Panel -->
		<Card variant="default" padding="md">
			{#snippet header()}
				<h2 class="panel-title">Productos con Stock Bajo</h2>
			{/snippet}
			{#snippet children()}
				{#if lowStock.length === 0}
					<p class="panel-empty">Sin productos con stock crítico</p>
				{:else}
					<div class="stock-list">
						{#each lowStock as item (item.id)}
							<a href="/products/{item.id}" class="stock-item">
								<div class="stock-info">
									<span class="stock-name">{item.name}</span>
									<span class="stock-sku">{item.sku}</span>
								</div>
								<div class="stock-health">
									<Badge variant={item.stock === 0 ? 'danger' : 'warning'}>
										{item.stock === 0 ? 'Crítico' : 'Bajo'}
									</Badge>
								</div>
								<div class="stock-numbers">
									<span class="stock-current">{item.stock}</span>
									<span class="stock-sep">/</span>
									<span class="stock-min">{item.stockMin}</span>
								</div>
							</a>
						{/each}
					</div>
				{/if}
			{/snippet}
		</Card>

		<!-- Anomalies Panel -->
		<Card variant="default" padding="md">
			{#snippet header()}
				<h2 class="panel-title">Anomalías Detectadas</h2>
			{/snippet}
			{#snippet children()}
				<AnomalyList anomalies={anomalies} title="" />
			{/snippet}
		</Card>
	</div>
</div>

<style>
	.dashboard {
		max-width: 1200px;
		margin: 0 auto;
	}

	.dashboard-greeting {
		margin-bottom: var(--spacing-lg);
	}

	.dashboard-greeting h1 {
		font-size: var(--font-size-2xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
	}

	.dashboard-subtitle {
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

	.kpi-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
		gap: var(--spacing-md);
		margin-bottom: var(--spacing-lg);
	}

	.kpi-card {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
	}

	.kpi-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.kpi-icon {
		font-size: 1.5rem;
	}

	.kpi-body {
		display: flex;
		align-items: flex-end;
		justify-content: space-between;
		gap: var(--spacing-sm);
	}

	.kpi-value {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
		line-height: 1;
	}

	.kpi-title {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
	}

	.trend-section,
	.insight-section {
		margin-bottom: var(--spacing-lg);
	}

	.trend-chart-container {
		height: 300px;
		width: 100%;
	}

	.dashboard-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: var(--spacing-md);
	}

	@media (max-width: 768px) {
		.dashboard-grid {
			grid-template-columns: 1fr;
		}
	}

	.panel-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	.panel-empty {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		text-align: center;
		padding: var(--spacing-lg);
	}

	.stock-list {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xs);
	}

	.stock-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-md) var(--spacing-sm);
		border-bottom: 1px solid var(--color-border-light);
		text-decoration: none;
		transition: background var(--transition-fast), border-radius var(--transition-fast);
		border-radius: var(--radius-md);
	}

	.stock-item:hover {
		background: var(--color-surface-hover);
	}

	.stock-info {
		display: flex;
		flex-direction: column;
		gap: 2px;
		flex: 1;
	}

	.stock-name {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	.stock-sku {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		font-family: var(--font-mono);
	}

	.stock-health {
		margin-right: var(--spacing-lg);
	}

	.stock-numbers {
		display: flex;
		align-items: center;
		gap: 4px;
		font-family: var(--font-mono);
		font-size: var(--font-size-sm);
	}

	.stock-current {
		color: var(--color-error);
		font-weight: var(--font-weight-bold);
	}

	.stock-sep {
		color: var(--color-text-muted);
	}

	.stock-min {
		color: var(--color-text-muted);
	}
</style>
