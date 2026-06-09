<script lang="ts">
	import '../../app.css';
	import { theme } from '$lib/stores/theme.svelte';
	import Sun from 'phosphor-svelte/lib/Sun';
	import Moon from 'phosphor-svelte/lib/Moon';

	let { children }: { children: import('svelte').Snippet } = $props();

	function handleToggle() {
		theme.toggle();
	}
</script>

<svelte:head>
	<title>SIGA — Sistema de Gestión</title>
</svelte:head>

<div class="auth-page">
	<button
		class="theme-fab"
		onclick={handleToggle}
		aria-label="Alternar tema"
		type="button"
	>
		{@render ThemeIcon()}
	</button>

	<div class="auth-container">
		{@render children()}
	</div>
</div>

{#snippet ThemeIcon()}
	{#if $theme === 'light'}
		<Moon size={20} weight="regular" />
	{:else}
		<Sun size={20} weight="regular" />
	{/if}
{/snippet}

<style>
	.auth-page {
		min-height: 100vh;
		display: flex;
		align-items: center;
		justify-content: center;
		background: var(--color-bg);
		background-image:
			radial-gradient(ellipse at 20% 50%, var(--color-accent-light) 0%, transparent 50%),
			radial-gradient(ellipse at 80% 50%, var(--color-accent-light) 0%, transparent 50%);
		padding: var(--spacing-md);
	}

	.auth-container {
		width: 100%;
		max-width: 440px;
	}

	.theme-fab {
		position: fixed;
		top: var(--spacing-lg);
		right: var(--spacing-lg);
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 40px;
		height: 40px;
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		background: var(--color-surface-glass);
		backdrop-filter: blur(var(--glass-blur));
		color: var(--color-text-secondary);
		cursor: pointer;
		transition: all var(--transition-fast);
		z-index: 10;
	}

	.theme-fab:hover {
		background: var(--color-surface);
		color: var(--color-accent);
		border-color: var(--color-accent);
	}
</style>
