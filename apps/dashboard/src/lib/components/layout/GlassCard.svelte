<script lang="ts">
	import type { Snippet } from 'svelte';

	let {
		children,
		padding = 'card-padding',
		class: className = '',
		span = 1,
		variant = 'surface'
	}: {
		children: Snippet;
		padding?: 'none' | 'base' | 'card-padding';
		class?: string;
		span?: number | string;
		variant?: 'surface' | 'glass' | 'primary';
	} = $props();

	const gridSpan = $derived(typeof span === 'number' ? `span ${span}` : span);
</script>

<div 
	class="glass-card-item {variant} {className}"
	style="grid-column: {gridSpan}; padding: {padding === 'none' ? '0' : `var(--spacing-${padding})`};"
>
	{@render children?.()}
</div>

<style>
	.glass-card-item {
		border-radius: var(--radius-xl);
		border: 1px solid var(--color-outline-variant);
		overflow: hidden;
		transition: transform var(--transition-fast), box-shadow var(--transition-fast);
	}

	.surface {
		background-color: var(--color-surface-container-lowest);
		box-shadow: var(--shadow-sm);
	}

	.glass {
		background: var(--color-surface-glass);
		backdrop-filter: blur(8px);
		border-color: var(--color-surface-glass-border);
	}

	.primary {
		background-color: var(--color-primary-container);
		color: var(--color-on-primary-container);
		border: none;
	}

	.glass-card-item:hover {
		box-shadow: var(--shadow-md);
	}
</style>
