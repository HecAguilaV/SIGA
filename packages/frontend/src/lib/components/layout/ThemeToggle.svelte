<script lang="ts">
	import { theme } from '$lib/stores/theme.svelte';
	import Sun from 'phosphor-svelte/lib/Sun';
	import Moon from 'phosphor-svelte/lib/Moon';

	let current = $derived($theme);

	function handleToggle() {
		theme.toggle();
	}
</script>

<button
	class="theme-toggle"
	onclick={handleToggle}
	aria-label={current === 'light' ? 'Activar modo oscuro' : 'Activar modo claro'}
	type="button"
>
	{#if current === 'light'}
		<Moon size={20} weight="regular" />
	{:else}
		<Sun size={20} weight="regular" />
	{/if}
</button>

<style>
	.theme-toggle {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 36px;
		height: 36px;
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		background: var(--color-surface);
		color: var(--color-text-secondary);
		cursor: pointer;
		transition: background var(--transition-fast), color var(--transition-fast), border-color var(--transition-fast);
	}

	.theme-toggle:hover {
		background: var(--color-surface-hover);
		color: var(--color-accent);
		border-color: var(--color-accent);
	}
</style>
