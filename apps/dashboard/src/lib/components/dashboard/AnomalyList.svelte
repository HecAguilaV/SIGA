<script lang="ts">
	interface Anomaly {
		id: string;
		type: string;
		message: string;
		severity: 'low' | 'medium' | 'high' | 'critical';
		timestamp?: string;
	}

	let {
		anomalies = [],
		title = 'Anomalías Detectadas',
		emptyMessage = 'Sin anomalías recientes'
	}: {
		anomalies: Anomaly[];
		title?: string;
		emptyMessage?: string;
	} = $props();
</script>

<div class="anomaly-panel">
	{#if title}
		<h3 class="anomaly-panel-title">{title}</h3>
	{/if}

	{#if anomalies.length === 0}
		<p class="anomaly-empty" role="status">{emptyMessage}</p>
	{:else}
		<div class="anomaly-list">
			{#each anomalies as anomaly (anomaly.id)}
				<div class="anomaly-item">
					<span class="anomaly-severity" aria-label={`Severidad: ${anomaly.severity}`}>
						{anomaly.severity === 'critical' || anomaly.severity === 'high' ? '🔴' : '🟡'}
					</span>
					<p class="anomaly-message">{anomaly.message}</p>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
	.anomaly-panel {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.anomaly-panel-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		margin: 0;
	}

	.anomaly-empty {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		text-align: center;
		padding: var(--spacing-lg);
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
