<script lang="ts">
	let {
		title = undefined,
		description = undefined,
		loading = false,
		empty = false,
		emptyMessage = 'Sin datos disponibles',
		children
	}: {
		title?: string;
		description?: string;
		loading?: boolean;
		empty?: boolean;
		emptyMessage?: string;
		children?: import('svelte').Snippet;
	} = $props();
</script>

<div class="chart-container" data-testid="chart-container">
	{#if title || description}
		<div class="chart-container-header">
			{#if title}
				<h3 class="chart-container-title">{title}</h3>
			{/if}
			{#if description}
				<p class="chart-container-description">{description}</p>
			{/if}
		</div>
	{/if}

	<div class="chart-container-body">
		{#if loading}
			<div class="chart-container-skeleton" role="status" aria-label="Cargando gráfico">
				Cargando gráfico...
			</div>
		{:else if empty}
			<div class="chart-container-empty" role="status">
				{emptyMessage}
			</div>
		{:else if children}
			{@render children()}
		{/if}
	</div>
</div>

<style>
	.chart-container {
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		overflow: hidden;
	}

	.chart-container-header {
		padding: var(--spacing-md) var(--spacing-md) 0;
	}

	.chart-container-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		margin: 0;
	}

	.chart-container-description {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		margin: var(--spacing-xs) 0 0;
	}

	.chart-container-body {
		padding: var(--spacing-md);
	}

	.chart-container-skeleton {
		height: 250px;
		background: var(--color-bg-alt);
		border-radius: var(--radius-md);
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
		animation: pulse 1.5s infinite;
	}

	.chart-container-empty {
		height: 150px;
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}

	@keyframes pulse {
		0%, 100% { opacity: 1; }
		50% { opacity: 0.6; }
	}
</style>
