<script lang="ts">
	type Variant = 'info' | 'warning' | 'danger' | 'success';

	let {
		variant = 'info',
		children
	}: {
		variant?: Variant;
		children?: import('svelte').Snippet;
	} = $props();
</script>

<span class="badge badge-{variant}" role="status">
	{#if children}
		{@render children()}
	{/if}
</span>

<style>
	.badge {
		display: inline-flex;
		align-items: center;
		padding: 2px 10px;
		font-size: var(--font-size-xs);
		font-weight: var(--font-weight-semibold);
		border-radius: var(--radius-full);
		line-height: 1.4;
		white-space: nowrap;
		text-transform: uppercase;
		letter-spacing: 0.03em;
	}

	.badge-info {
		background: var(--color-info-bg);
		color: var(--color-info-text);
	}

	.badge-warning {
		background: var(--color-warning-bg);
		color: var(--color-warning-text);
	}

	.badge-danger {
		background: var(--color-error-bg);
		color: var(--color-error-text);
	}

	.badge-success {
		background: var(--color-success-bg);
		color: var(--color-success-text);
	}
</style>
