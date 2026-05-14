<script lang="ts">
	import { invalidate } from '$app/navigation';
	import type { PageData } from './$types';
	import Card from '@siga/ui-kit/Card.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';

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
	const dashboardError = $derived(data.error as string | null);
	const userName = $derived(data.user?.name ?? 'Usuario');
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
									{insight.trend === 'up' ? '↑' : insight.trend === 'down' ? '↓' : '→'} {insight.trendValue ?? ''}
								</Badge>
							{/if}
						</div>
						<p class="kpi-value">{insight.value}</p>
						<p class="kpi-title">{insight.title}</p>
					</div>
				{/snippet}
			</Card>
		{/each}
	</div>

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
				{#if anomalies.length === 0}
					<p class="panel-empty">Sin anomalías recientes</p>
				{:else}
					<div class="anomaly-list">
						{#each anomalies as anomaly (anomaly.id)}
							<div class="anomaly-item">
								<span class="anomaly-severity">
									{anomaly.severity === 'critical' || anomaly.severity === 'high' ? '🔴' : '🟡'}
								</span>
								<p class="anomaly-message">{anomaly.message}</p>
							</div>
						{/each}
					</div>
				{/if}
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
	}

	.stock-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-sm) 0;
		border-bottom: 1px solid var(--color-border-light);
		text-decoration: none;
		transition: background var(--transition-fast);
	}

	.stock-item:hover {
		background: var(--color-surface-hover);
		margin: 0 calc(-1 * var(--spacing-md));
		padding-left: var(--spacing-md);
		padding-right: var(--spacing-md);
	}

	.stock-info {
		display: flex;
		flex-direction: column;
		gap: 2px;
	}

	.stock-name {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		color: var(--color-text);
	}

	.stock-sku {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		font-family: var(--font-mono);
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
	}
</style>
