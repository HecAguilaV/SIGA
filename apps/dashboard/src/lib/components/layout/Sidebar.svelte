<script lang="ts">
	import { page } from '$app/stores';
	import { user, userPermissions } from '$lib/stores/auth.svelte';
	import { ui } from '$lib/stores/ui.svelte';
	import { hasPermission, ADMIN_ROLES } from '$lib/auth/permissions';
	import { fly } from 'svelte/transition';

	// Phosphor icons
	import Gauge from 'phosphor-svelte/lib/Gauge';
	import Package from 'phosphor-svelte/lib/Package';
	import Storefront from 'phosphor-svelte/lib/Storefront';
	import CreditCard from 'phosphor-svelte/lib/CreditCard';
	import ChartBar from 'phosphor-svelte/lib/ChartBar';
	import Gear from 'phosphor-svelte/lib/Gear';
	import SignOut from 'phosphor-svelte/lib/SignOut';
	import CaretLeft from 'phosphor-svelte/lib/CaretLeft';
	import CaretRight from 'phosphor-svelte/lib/CaretRight';

	interface NavItem {
		label: string;
		href: string;
		icon: any;
		permission?: string;
	}

	const allNavItems: NavItem[] = [
		{ label: 'Inicio', href: '/dashboard', icon: Gauge },
		{ label: 'Análisis', href: '/analytics/predictive', icon: ChartBar, permission: 'analytics:view' },
		{ label: 'Inventario', href: '/products', icon: Package, permission: 'inventory:view' },
		{ label: 'Locales', href: '/stores', icon: Storefront, permission: 'inventory:view' },
		{ label: 'POS', href: '/pos', icon: CreditCard, permission: 'pos:view' },
		{ label: 'Reportes', href: '/analytics', icon: ChartBar, permission: 'analytics:view' },
		{ label: 'Configuración', href: '/users', icon: Gear, permission: 'admin:view' }
	];

	let collapsed = $state(false);

	const currentPath = $derived($page.url.pathname);
	const currentUserPermissions = $derived($userPermissions);

	const currentUser = $derived($user);
	const visibleItems = $derived(
		currentUser?.rol && ADMIN_ROLES.includes(currentUser.rol)
			? allNavItems
			: allNavItems.filter((item) => !item.permission || hasPermission(currentUserPermissions, item.permission))
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
		<a href="/dashboard" class="logo-area" class:logo-area-collapsed={collapsed} aria-label="Ir al inicio">
			{#if collapsed}
				<img src="/S.png" alt="S" class="logo-icon-img" transition:fly={{ x: 8, duration: 150 }} />
			{:else}
				<img src="/Logo_SIGA.png" alt="SIGA" class="logo-full-img" transition:fly={{ x: -8, duration: 150 }} />
			{/if}
		</a>
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

	<!-- Botón Acción Principal: Nuevo Movimiento -->
	<div class="sidebar-action">
		{#if !collapsed}
			<button class="action-btn" onclick={() => ui.openNewMovement()} type="button">
				<span class="action-icon-plus">+</span>
				<span class="action-text">Nuevo Movimiento</span>
			</button>
		{:else}
			<button class="action-btn collapsed-action" onclick={() => ui.openNewMovement()} type="button" aria-label="Nuevo Movimiento">
				<span class="action-icon-plus">+</span>
			</button>
		{/if}
	</div>

	<nav class="sidebar-nav" aria-label="Navegación principal">
		<ul>
			{#each visibleItems as item}
				{@const Icon = item.icon}
				<li>
					<a
						href={item.href}
						class="nav-item"
						class:active={isActive(item.href)}
						aria-current={isActive(item.href) ? 'page' : undefined}
					>
						<span class="nav-icon">
							<Icon size={20} weight={isActive(item.href) ? 'fill' : 'regular'} />
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
		<form action="/logout" method="POST" class="logout-form">
			<button type="submit" class="logout-btn" class:collapsed-logout={collapsed}>
				<SignOut size={20} weight="regular" />
				{#if !collapsed}
					<span class="logout-text">Cerrar Sesión</span>
				{/if}
			</button>
		</form>
	</div>
</aside>

<style>
	.sidebar {
		width: var(--w-sidebar-width);
		min-width: var(--w-sidebar-width);
		height: 100vh;
		background: var(--color-surface);
		box-shadow: 2px 0 8px rgba(15, 23, 42, 0.04);
		border-right: 1px solid var(--color-border-light);
		display: flex;
		flex-direction: column;
		transition: width var(--transition-base), min-width var(--transition-base);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.sidebar.collapsed {
		width: var(--w-sidebar-collapsed-width);
		min-width: var(--w-sidebar-collapsed-width);
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
		height: 36px;
		overflow: hidden;
	}

	.logo-area-collapsed {
		justify-content: center;
		width: 100%;
	}

	.logo-icon-img {
		width: 28px;
		height: 28px;
		object-fit: contain;
	}

	.logo-full-img {
		height: 28px;
		object-fit: contain;
		max-width: 120px;
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

	.sidebar-action {
		padding: var(--spacing-md);
		display: flex;
		justify-content: center;
	}

	.action-btn {
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: var(--spacing-sm);
		padding: 10px 16px;
		background: var(--color-primary);
		color: #fff;
		border: none;
		border-radius: var(--radius-md);
		font-weight: var(--font-weight-semibold);
		font-size: var(--font-size-sm);
		cursor: pointer;
		transition: background var(--transition-fast), transform var(--transition-fast);
	}

	.action-btn:hover {
		background: var(--color-primary-hover);
		transform: translateY(-1px);
	}

	.action-btn:active {
		transform: translateY(0);
	}

	.action-icon-plus {
		font-size: var(--font-size-lg);
		line-height: 1;
		font-weight: var(--font-weight-bold);
	}

	.collapsed-action {
		width: 40px;
		height: 40px;
		padding: 0;
		border-radius: 50%;
	}

	.sidebar-nav {
		flex: 1;
		padding: var(--spacing-sm);
		overflow-y: auto;
	}

	.sidebar-nav ul {
		display: flex;
		flex-direction: column;
		gap: 4px;
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
		position: relative;
	}

	.nav-item:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.nav-item.active {
		background: var(--color-primary-light);
		color: var(--color-primary);
		font-weight: var(--font-weight-semibold);
	}

	.nav-item.active::before {
		content: '';
		position: absolute;
		left: 0;
		top: 20%;
		height: 60%;
		width: 4px;
		background-color: var(--color-primary);
		border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
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

	.logout-form {
		width: 100%;
	}

	.logout-btn {
		width: 100%;
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 10px 12px;
		border: none;
		background: transparent;
		color: var(--color-text-muted);
		border-radius: var(--radius-md);
		cursor: pointer;
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
	}

	.logout-btn:hover {
		background: var(--color-error-bg);
		color: var(--color-error);
	}

	.collapsed-logout {
		justify-content: center;
		padding: 10px 0;
	}

	.logout-text {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
</style>
