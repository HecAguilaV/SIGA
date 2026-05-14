<script lang="ts">
	import { page } from '$app/stores';
	import { user } from '$lib/stores/auth.svelte';
	import { fly } from 'svelte/transition';

	// Phosphor icons
	import Gauge from 'phosphor-svelte/lib/Gauge';
	import Package from 'phosphor-svelte/lib/Package';
	import Storefront from 'phosphor-svelte/lib/Storefront';
	import Tag from 'phosphor-svelte/lib/Tag';
	import Users from 'phosphor-svelte/lib/Users';
	import ChartBar from 'phosphor-svelte/lib/ChartBar';
	import ChatCircle from 'phosphor-svelte/lib/ChatCircle';
	import CreditCard from 'phosphor-svelte/lib/CreditCard';
	import SignOut from 'phosphor-svelte/lib/SignOut';
	import CaretLeft from 'phosphor-svelte/lib/CaretLeft';
	import CaretRight from 'phosphor-svelte/lib/CaretRight';
	import UserCircle from 'phosphor-svelte/lib/UserCircle';

	interface NavItem {
		label: string;
		href: string;
		icon: typeof Gauge;
		roles: string[];
	}

	const allNavItems: NavItem[] = [
		{ label: 'Dashboard', href: '/', icon: Gauge, roles: ['ADMINISTRATOR', 'OPERATOR', 'CASHIER'] },
		{ label: 'Productos', href: '/products', icon: Package, roles: ['ADMINISTRATOR', 'OPERATOR'] },
		{ label: 'Locales', href: '/stores', icon: Storefront, roles: ['ADMINISTRATOR', 'OPERATOR'] },
		{ label: 'Categorías', href: '/categories', icon: Tag, roles: ['ADMINISTRATOR', 'OPERATOR'] },
		{ label: 'Usuarios', href: '/users', icon: Users, roles: ['ADMINISTRATOR'] },
		{ label: 'Analíticas', href: '/analytics', icon: ChartBar, roles: ['ADMINISTRATOR', 'OPERATOR'] },
		{ label: 'Asistente', href: '/assistant', icon: ChatCircle, roles: ['ADMINISTRATOR', 'OPERATOR', 'CASHIER'] },
		{ label: 'POS', href: '/pos', icon: CreditCard, roles: ['CASHIER'] }
	];

	let collapsed = $state(false);

	const currentPath = $derived($page.url.pathname);
	const currentUser = $derived($user);
	const userRole = $derived(currentUser?.rol ?? '');
	const userName = $derived(currentUser?.name ?? 'Usuario');
	const userEmail = $derived(currentUser?.email ?? '');

	const visibleItems = $derived(
		allNavItems.filter((item) => item.roles.length === 0 || item.roles.includes(userRole))
	);

	function isActive(href: string): boolean {
		if (href === '/') return currentPath === '/';
		return currentPath.startsWith(href);
	}

	function toggleCollapse() {
		collapsed = !collapsed;
	}
</script>

<aside class="sidebar" class:collapsed>
	<div class="sidebar-header">
		<div class="logo-area">
			<span class="logo-icon">S</span>
			{#if !collapsed}
				<span class="logo-text" transition:fly={{ x: -8, duration: 150 }}>SIGA</span>
			{/if}
		</div>
		<button
			class="collapse-btn"
			onclick={toggleCollapse}
			aria-label={collapsed ? 'Expandir menú' : 'Colapsar menú'}
			type="button"
		>
			{#if collapsed}
				<CaretRight size={16} weight="bold" />
			{:else}
				<CaretLeft size={16} weight="bold" />
			{/if}
		</button>
	</div>

	<nav class="sidebar-nav" aria-label="Navegación principal">
		<ul>
			{#each visibleItems as item}
				<li>
					<a
						href={item.href}
						class="nav-item"
						class:active={isActive(item.href)}
						aria-current={isActive(item.href) ? 'page' : undefined}
					>
						<span class="nav-icon">
							{#if item.icon === Gauge}
								<Gauge size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === Package}
								<Package size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === Storefront}
								<Storefront size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === Tag}
								<Tag size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === Users}
								<Users size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === ChartBar}
								<ChartBar size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === ChatCircle}
								<ChatCircle size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{:else if item.icon === CreditCard}
								<CreditCard size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
							{/if}
						</span>
						{#if !collapsed}
							<span class="nav-label">{item.label}</span>
						{/if}
					</a>
				</li>
			{/each}
		</ul>
	</nav>

	<div class="sidebar-footer">
		<div class="user-menu">
			<div class="user-avatar">
				<UserCircle size={24} weight="fill" />
			</div>
			{#if !collapsed}
				<div class="user-info">
					<span class="user-name">{userName}</span>
					<span class="user-role">{userRole}</span>
				</div>
			{/if}
			<form action="/logout" method="POST" class="logout-form">
				<button type="submit" class="logout-btn" aria-label="Cerrar sesión">
					<SignOut size={20} weight="regular" />
				</button>
			</form>
		</div>
	</div>
</aside>

<style>
	.sidebar {
		width: 240px;
		min-width: 240px;
		height: 100vh;
		background: var(--color-surface);
		border-right: 1px solid var(--color-border);
		display: flex;
		flex-direction: column;
		transition: width var(--transition-base), min-width var(--transition-base);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.sidebar.collapsed {
		width: 64px;
		min-width: 64px;
	}

	.sidebar-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-md);
		border-bottom: 1px solid var(--color-border-light);
		height: 56px;
	}

	.logo-area {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		overflow: hidden;
	}

	.logo-icon {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 32px;
		height: 32px;
		background: var(--color-accent);
		color: #fff;
		border-radius: var(--radius-md);
		font-weight: var(--font-weight-bold);
		font-size: var(--font-size-lg);
		flex-shrink: 0;
	}

	.logo-text {
		font-size: var(--font-size-lg);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
		white-space: nowrap;
	}

	.collapse-btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 28px;
		height: 28px;
		border: none;
		background: transparent;
		color: var(--color-text-muted);
		border-radius: var(--radius-sm);
		cursor: pointer;
		flex-shrink: 0;
		transition: background var(--transition-fast), color var(--transition-fast);
	}

	.collapse-btn:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.sidebar-nav {
		flex: 1;
		padding: var(--spacing-sm);
		overflow-y: auto;
	}

	.sidebar-nav ul {
		display: flex;
		flex-direction: column;
		gap: 2px;
	}

	.nav-item {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 10px 12px;
		border-radius: var(--radius-md);
		color: var(--color-text-secondary);
		text-decoration: none;
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
		white-space: nowrap;
		overflow: hidden;
	}

	.nav-item:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.nav-item.active {
		background: var(--color-accent-light);
		color: var(--color-accent);
		font-weight: var(--font-weight-semibold);
	}

	.nav-icon {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 20px;
		height: 20px;
		flex-shrink: 0;
	}

	.nav-label {
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.sidebar-footer {
		border-top: 1px solid var(--color-border-light);
		padding: var(--spacing-sm);
	}

	.user-menu {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 8px;
		border-radius: var(--radius-md);
		transition: background var(--transition-fast);
	}

	.user-menu:hover {
		background: var(--color-bg-alt);
	}

	.user-avatar {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
		color: var(--color-accent);
	}

	.user-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		min-width: 0;
	}

	.user-name {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.user-role {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.logout-form {
		flex-shrink: 0;
	}

	.logout-btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 32px;
		height: 32px;
		border: none;
		background: transparent;
		color: var(--color-text-muted);
		border-radius: var(--radius-sm);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.logout-btn:hover {
		background: var(--color-error-bg);
		color: var(--color-error);
	}
</style>
