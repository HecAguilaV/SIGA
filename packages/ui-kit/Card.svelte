<script lang="ts">
	type Variant = 'default' | 'glass';
	type Padding = 'sm' | 'md' | 'lg';

	let {
		variant = 'default',
		padding = 'md',
		header,
		children,
		footer,
		...rest
	}: {
		variant?: Variant;
		padding?: Padding;
		header?: import('svelte').Snippet;
		children?: import('svelte').Snippet;
		footer?: import('svelte').Snippet;
	} & Record<string, unknown> = $props();
</script>

<div class="card card-{variant} card-pad-{padding}" role="region" {...rest}>
	{#if header}
		<div class="card-header">
			{@render header()}
		</div>
	{/if}
	{#if children}
		<div class="card-content">
			{@render children()}
		</div>
	{/if}
	{#if footer}
		<div class="card-footer">
			{@render footer()}
		</div>
	{/if}
</div>

<style>
	.card {
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		box-shadow: var(--shadow-sm);
		transition: box-shadow var(--transition-base);
	}

	.card-default:hover {
		box-shadow: var(--shadow-md);
	}

	.card-glass {
		background: var(--glass-bg);
		backdrop-filter: blur(var(--glass-blur));
		-webkit-backdrop-filter: blur(var(--glass-blur));
		border: var(--glass-border);
		box-shadow: var(--glass-shadow);
	}

	.card-pad-sm > :global(.card-header),
	.card-pad-sm > :global(.card-content),
	.card-pad-sm > :global(.card-footer) {
		padding: var(--spacing-sm);
	}

	.card-pad-md > :global(.card-header),
	.card-pad-md > :global(.card-content),
	.card-pad-md > :global(.card-footer) {
		padding: var(--spacing-md);
	}

	.card-pad-lg > :global(.card-header),
	.card-pad-lg > :global(.card-content),
	.card-pad-lg > :global(.card-footer) {
		padding: var(--spacing-lg);
	}

	.card-header {
		border-bottom: 1px solid var(--color-border-light);
	}

	.card-footer {
		border-top: 1px solid var(--color-border-light);
	}
</style>
