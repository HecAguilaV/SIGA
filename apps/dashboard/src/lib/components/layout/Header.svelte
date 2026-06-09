<script lang="ts">
	import Breadcrumb from './Breadcrumb.svelte';
	import ThemeToggle from './ThemeToggle.svelte';
	import A11yToolbar from './A11yToolbar.svelte';
	import AhorremosTiempoButton from '$lib/components/a2ui/AhorremosTiempoButton.svelte';
	import StoreSelector from './StoreSelector.svelte';
	import NotificationBell from './NotificationBell.svelte';
	import UserProfileMenu from './UserProfileMenu.svelte';
	import MagnifyingGlass from 'phosphor-svelte/lib/MagnifyingGlass';
	import { page } from '$app/stores';

	const currentRoute = $derived($page.url.pathname);

	let searchQuery = $state('');
</script>

<header class="app-header">
	<div class="header-left">
		<Breadcrumb />
		
		<!-- Buscador Universal -->
		<div class="search-bar">
			<MagnifyingGlass size={16} class="search-icon" />
			<input 
				type="text" 
				bind:value={searchQuery}
				placeholder="Buscar productos, SKU, locales..." 
				aria-label="Buscador universal" 
			/>
		</div>
	</div>
	
	<div class="header-right">
		<StoreSelector />
		<A11yToolbar />
		<AhorremosTiempoButton currentRoute={currentRoute} />
		<NotificationBell />
		<ThemeToggle />
		<div class="profile-divider"></div>
		<UserProfileMenu />
	</div>
</header>

<style>
	.app-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 var(--spacing-lg);
		height: 56px;
		background: var(--color-surface-glass);
		backdrop-filter: blur(var(--glass-blur));
		-webkit-backdrop-filter: blur(var(--glass-blur));
		border-bottom: 1px solid var(--color-border);
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
		position: sticky;
		top: 0;
		z-index: 50;
	}

	.header-left {
		display: flex;
		align-items: center;
		gap: var(--spacing-lg);
		flex: 1;
		max-width: 600px;
	}

	.search-bar {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 6px 12px;
		background: var(--color-bg);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		width: 100%;
		max-width: 280px;
		transition: all var(--transition-fast);
	}

	.search-bar:focus-within {
		background: var(--color-surface);
		border-color: var(--color-accent);
		box-shadow: 0 0 0 3px var(--color-accent-light);
	}

	.search-bar :global(.search-icon) {
		color: var(--color-text-muted);
	}

	.search-bar input {
		border: none;
		background: transparent;
		color: var(--color-text);
		font-size: var(--font-size-sm);
		width: 100%;
		outline: none;
	}

	.search-bar input::placeholder {
		color: var(--color-text-muted);
	}

	.header-right {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
	}

	.profile-divider {
		height: 20px;
		width: 1px;
		background: var(--color-border);
		margin: 0 var(--spacing-xs);
	}

	/* Responsive header adjustments */
	@media (max-width: 768px) {
		.app-header {
			padding: 0 var(--spacing-md);
		}

		.search-bar {
			display: none; /* Esconder buscador en mobile o hacerlo colapsable */
		}
	}
</style>
