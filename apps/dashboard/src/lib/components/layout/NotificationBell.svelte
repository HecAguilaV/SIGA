<script lang="ts">
	import Bell from 'phosphor-svelte/lib/Bell';

	let isOpen = $state(false);

	let notifications = $state([
		{ id: 1, message: 'Alerta de Stock Crítico: Aceite de Girasol bajo el mínimo', time: 'Hace 5m', read: false },
		{ id: 2, message: 'Ingreso de mercadería: +50 Harina de Trigo', time: 'Hace 1h', read: true },
		{ id: 3, message: 'Ajuste de inventario realizado por Operador Juan', time: 'Hace 3h', read: true }
	]);

	const unreadCount = $derived(notifications.filter(n => !n.read).length);

	function markAllAsRead() {
		notifications = notifications.map(n => ({ ...n, read: true }));
	}
</script>

<div class="notification-bell" role="none">
	<button
		class="bell-btn"
		onclick={() => { isOpen = !isOpen; if (isOpen) markAllAsRead(); }}
		type="button"
		aria-expanded={isOpen}
		aria-label="Ver notificaciones"
	>
		<Bell size={20} weight="regular" />
		{#if unreadCount > 0}
			<span class="badge">{unreadCount}</span>
		{/if}
	</button>

	{#if isOpen}
		<div class="dropdown-menu">
			<div class="dropdown-header">
				<h3>Notificaciones</h3>
			</div>
			<ul class="notification-list">
				{#if notifications.length === 0}
					<li class="empty-state">Sin notificaciones</li>
				{:else}
					{#each notifications as item}
						<li class="notification-item" class:unread={!item.read}>
							<p class="notification-message">{item.message}</p>
							<span class="notification-time">{item.time}</span>
						</li>
					{/each}
				{/if}
			</ul>
		</div>
	{/if}
</div>

<style>
	.notification-bell {
		position: relative;
		display: inline-block;
	}

	.bell-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 36px;
		height: 36px;
		background: transparent;
		border: none;
		border-radius: var(--radius-full);
		color: var(--color-text-secondary);
		cursor: pointer;
		position: relative;
		transition: all var(--transition-fast);
	}

	.bell-btn:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.badge {
		position: absolute;
		top: 4px;
		right: 4px;
		background: var(--color-error);
		color: #fff;
		font-size: 10px;
		font-weight: var(--font-weight-bold);
		padding: 2px 5px;
		border-radius: var(--radius-full);
		line-height: 1;
		min-width: 16px;
		text-align: center;
	}

	.dropdown-menu {
		position: absolute;
		top: calc(100% + 8px);
		right: 0;
		width: 320px;
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		box-shadow: var(--shadow-lg);
		z-index: 200;
		overflow: hidden;
		display: flex;
		flex-direction: column;
	}

	.dropdown-header {
		padding: var(--spacing-md);
		border-bottom: 1px solid var(--color-border-light);
	}

	.dropdown-header h3 {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		margin: 0;
	}

	.notification-list {
		max-height: 300px;
		overflow-y: auto;
		display: flex;
		flex-direction: column;
	}

	.notification-item {
		padding: var(--spacing-md);
		border-bottom: 1px solid var(--color-border-light);
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xs);
		transition: background var(--transition-fast);
	}

	.notification-item:hover {
		background: var(--color-bg-alt);
	}

	.notification-item.unread {
		background: var(--color-accent-light);
	}

	.notification-message {
		font-size: var(--font-size-sm);
		color: var(--color-text);
		margin: 0;
		line-height: 1.4;
	}

	.notification-time {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
	}

	.empty-state {
		padding: var(--spacing-xl);
		text-align: center;
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}
</style>
