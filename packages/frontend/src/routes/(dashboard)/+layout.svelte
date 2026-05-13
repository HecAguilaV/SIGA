<script lang="ts">
	import '../../app.css';
	import ThemeToggle from '$lib/components/layout/ThemeToggle.svelte';
	import { page } from '$app/stores';

	let { children }: { children?: import('svelte').Snippet } = $props();
</script>

<svelte:head>
	<title>SIGA — Dashboard</title>
</svelte:head>

<div class="app-shell">
	<!-- Placeholder header -->
	<header class="app-header">
		<div class="header-left">
			<span class="app-logo">SIGA</span>
		</div>
		<div class="header-right">
			<ThemeToggle />
			<form action="/logout" method="POST">
				<button type="submit" class="logout-btn" aria-label="Cerrar sesión">
					Cerrar sesión
				</button>
			</form>
		</div>
	</header>

	<main id="main-content" class="app-main">
		{@render children()}
	</main>
</div>

<style>
	.app-shell {
		min-height: 100vh;
		display: flex;
		flex-direction: column;
	}

	.app-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-md) var(--spacing-lg);
		background: var(--color-surface-glass);
		backdrop-filter: blur(var(--glass-blur));
		border-bottom: 1px solid var(--color-border);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.header-left {
		display: flex;
		align-items: center;
		gap: var(--spacing-md);
	}

	.app-logo {
		font-size: var(--font-size-xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-accent);
	}

	.header-right {
		display: flex;
		align-items: center;
		gap: var(--spacing-md);
	}

	.logout-btn {
		padding: 8px 16px;
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		background: var(--color-surface);
		color: var(--color-text-secondary);
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.logout-btn:hover {
		background: var(--color-error-bg);
		color: var(--color-error);
		border-color: var(--color-error);
	}

	.app-main {
		flex: 1;
		padding: var(--spacing-lg);
	}
</style>
