<script lang="ts">
	import Badge from '@siga/ui-kit/Badge.svelte';

	interface Insight {
		id: string;
		title: string;
		description: string;
		type: 'positive' | 'info' | 'warning' | 'danger';
		context?: string;
	}

	let {
		insights = [],
		title = 'Hallazgos Analíticos',
		emptyMessage = 'No hay datos'
	}: {
		insights: Insight[];
		title?: string;
		emptyMessage?: string;
	} = $props();

	const badgeVariant = $derived((type: string) => {
		switch (type) {
			case 'positive': return 'success' as const;
			case 'warning': return 'warning' as const;
			case 'danger': return 'danger' as const;
			default: return 'info' as const;
		}
	});
</script>

<div class="insight-panel">
	{#if title}
		<h3 class="insight-panel-title">{title}</h3>
	{/if}

	{#if insights.length === 0}
		<p class="insight-empty" role="status">{emptyMessage}</p>
	{:else}
		<div class="insight-list">
			{#each insights as insight, i (insight.id ?? i)}
				<div class="insight-item">
					<div class="insight-header">
						<Badge variant={badgeVariant(insight.type)}>
							{insight.type === 'positive' ? '↑' : insight.type === 'warning' ? '⚠' : insight.type === 'danger' ? '🔴' : 'ℹ'} {insight.title}
						</Badge>
					</div>
					<p class="insight-description">{insight.description}</p>
					{#if insight.context}
						<p class="insight-context">{insight.context}</p>
					{/if}
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
	.insight-panel {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.insight-panel-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		margin: 0;
	}

	.insight-empty {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		text-align: center;
		padding: var(--spacing-lg);
	}

	.insight-list {
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
</style>
