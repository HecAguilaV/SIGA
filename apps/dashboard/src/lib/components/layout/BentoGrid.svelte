<script lang="ts">
	import type { Snippet } from 'svelte';

	let {
		children,
		cols = 12,
		gap = 'gutter',
		class: className = ''
	}: {
		children: Snippet;
		cols?: number;
		gap?: 'base' | 'gutter' | 'card-padding';
		class?: string;
	} = $props();
</script>

<div 
	class="bento-grid {className}" 
	style="--grid-cols: {cols}; --grid-gap: var(--spacing-{gap});"
>
	{@render children?.()}
</div>

<style>
	.bento-grid {
		display: grid;
		grid-template-columns: repeat(var(--grid-cols), 1fr);
		gap: var(--grid-gap);
		width: 100%;
	}

	@media (max-width: 1024px) {
		.bento-grid {
			grid-template-columns: 1fr;
		}
	}
</style>
