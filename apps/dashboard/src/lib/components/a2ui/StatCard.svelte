<script lang="ts">
	/**
	 * StatCard.svelte — Tarjeta de estadística con indicador de tendencia.
	 *
	 * Props:
	 * - label: string — etiqueta descriptiva
	 * - value: string | number — valor a mostrar
	 * - trend?: 'up' | 'down' | 'neutral' — dirección de tendencia
	 * - change?: string — texto del cambio (ej: "+12.5%")
	 * - icon?: string — nombre del icono a mostrar
	 */

	type Trend = 'up' | 'down' | 'neutral';

	let {
		label,
		value,
		trend = 'neutral',
		change,
		icon
	}: {
		label: string;
		value: string | number;
		trend?: Trend;
		change?: string;
		icon?: string;
	} = $props();
</script>

<div class="stat-card trend-{trend}" data-testid="stat-card">
	<div class="stat-card-header">
		{#if icon}
			<span class="stat-card-icon" data-testid="stat-card-icon">{icon}</span>
		{/if}
		<span class="stat-card-label">{label}</span>
	</div>
	<div class="stat-card-value">{value}</div>
	{#if change !== undefined}
		<div class="stat-card-change">
			<span class="stat-card-change-text">{change}</span>
		</div>
	{/if}
</div>

<style>
	.stat-card {
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		padding: var(--spacing-md);
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xs);
		transition: box-shadow var(--transition-base);
	}

	.stat-card:hover {
		box-shadow: var(--shadow-md);
	}

	.stat-card-header {
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
	}

	.stat-card-icon {
		font-size: var(--font-size-lg);
		opacity: 0.7;
	}

	.stat-card-label {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		font-weight: var(--font-weight-medium);
		text-transform: uppercase;
		letter-spacing: 0.03em;
	}

	.stat-card-value {
		font-size: var(--font-size-2xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text-primary);
		line-height: 1.2;
	}

	.stat-card-change {
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
	}

	.stat-card-change-text {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
	}

	.trend-up .stat-card-change-text {
		color: var(--color-success-text);
	}

	.trend-down .stat-card-change-text {
		color: var(--color-error-text);
	}

	.trend-neutral .stat-card-change-text {
		color: var(--color-text-muted);
	}
</style>
