<script lang="ts">
	/**
	 * TrendBadge.svelte — Badge con indicador de tendencia.
	 *
	 * Props:
	 * - label: string — etiqueta descriptiva
	 * - value: string | number — valor a mostrar
	 * - trend?: 'up' | 'down' | 'stable' — dirección de tendencia
	 * - color?: string — color personalizado (se agrega como clase)
	 */

	type Trend = 'up' | 'down' | 'stable';

	let {
		label,
		value,
		trend = 'stable',
		color
	}: {
		label: string;
		value: string | number;
		trend?: Trend;
		color?: string;
	} = $props();
</script>

<span
	class="trend-badge trend-{trend}{color ? ` color-${color}` : ''}"
	data-testid="trend-badge"
>
	<span class="trend-badge-label">{label}</span>
	<span class="trend-badge-value">{value}</span>
</span>

<style>
	.trend-badge {
		display: inline-flex;
		align-items: center;
		gap: var(--spacing-xs);
		padding: 2px 10px;
		font-size: var(--font-size-xs);
		font-weight: var(--font-weight-semibold);
		border-radius: var(--radius-full);
		line-height: 1.4;
		white-space: nowrap;
		letter-spacing: 0.03em;
	}

	.trend-badge-label {
		opacity: 0.8;
	}

	.trend-badge-value {
		font-weight: var(--font-weight-bold);
	}

	.trend-up {
		background: var(--color-success-bg);
		color: var(--color-success-text);
	}

	.trend-down {
		background: var(--color-error-bg);
		color: var(--color-error-text);
	}

	.trend-stable {
		background: var(--color-info-bg);
		color: var(--color-info-text);
	}

	.color-violet {
		background: var(--color-violet-bg, #ede9fe);
		color: var(--color-violet-text, #6d28d9);
	}
</style>
