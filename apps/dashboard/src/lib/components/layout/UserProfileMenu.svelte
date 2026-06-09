<script lang="ts">
	import { user } from '$lib/stores/auth.svelte';
	import UserCircle from 'phosphor-svelte/lib/UserCircle';
	import CaretDown from 'phosphor-svelte/lib/CaretDown';
	import SignOut from 'phosphor-svelte/lib/SignOut';

	let isOpen = $state(false);

	const currentUser = $derived($user);
	const userRole = $derived(currentUser?.rol ?? '');
	const userName = $derived(currentUser?.name ?? 'Usuario');
	const userEmail = $derived(currentUser?.email ?? '');
</script>

<div class="user-profile-menu" role="none">
	<button
		class="profile-btn"
		onclick={() => isOpen = !isOpen}
		type="button"
		aria-expanded={isOpen}
		aria-label="Menú de usuario"
	>
		<div class="avatar">
			<UserCircle size={24} weight="fill" />
		</div>
		<div class="user-details">
			<span class="user-name">{userName}</span>
			<span class="user-role">{userRole}</span>
		</div>
		<CaretDown size={12} weight="bold" class="arrow-icon {isOpen ? 'arrow-icon--open' : ''}" />
	</button>

	{#if isOpen}
		<div class="dropdown-menu">
			<div class="user-info-section">
				<p class="name">{userName}</p>
				<p class="email">{userEmail}</p>
			</div>
			<div class="menu-divider"></div>
			<form action="/logout" method="POST" class="logout-form">
				<button type="submit" class="logout-btn">
					<SignOut size={18} weight="regular" />
					<span>Cerrar Sesión</span>
				</button>
			</form>
		</div>
	{/if}
</div>

<style>
	.user-profile-menu {
		position: relative;
		display: inline-block;
	}

	.profile-btn {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 4px 8px;
		background: transparent;
		border: none;
		border-radius: var(--radius-md);
		cursor: pointer;
		transition: background var(--transition-fast);
	}

	.profile-btn:hover {
		background: var(--color-bg-alt);
	}

	.avatar {
		color: var(--color-accent);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.user-details {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		line-height: 1.2;
		max-width: 120px;
		overflow: hidden;
		text-align: left;
	}

	.user-name {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;
		width: 100%;
	}

	.user-role {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;
		width: 100%;
	}

	.arrow-icon {
		color: var(--color-text-muted);
		transition: transform var(--transition-fast);
	}

	.arrow-icon--open {
		transform: rotate(180deg);
	}

	.dropdown-menu {
		position: absolute;
		top: calc(100% + 8px);
		right: 0;
		width: 200px;
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		box-shadow: var(--shadow-lg);
		z-index: 200;
		padding: 4px;
		display: flex;
		flex-direction: column;
	}

	.user-info-section {
		padding: var(--spacing-md);
	}

	.user-info-section .name {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
		margin: 0 0 2px 0;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.user-info-section .email {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		margin: 0;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.menu-divider {
		height: 1px;
		background: var(--color-border-light);
		margin: 4px 0;
	}

	.logout-form {
		width: 100%;
	}

	.logout-btn {
		width: 100%;
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 8px 12px;
		background: transparent;
		border: none;
		border-radius: var(--radius-md);
		color: var(--color-text-secondary);
		cursor: pointer;
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
		text-align: left;
	}

	.logout-btn:hover {
		background: var(--color-error-bg);
		color: var(--color-error);
	}
</style>
