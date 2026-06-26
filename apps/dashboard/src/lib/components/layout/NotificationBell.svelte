<script lang="ts">
	import Bell from 'phosphor-svelte/lib/Bell';
	import { onMount } from 'svelte';

	const DEV_BOT_URL = ''; // empty = use relative /api/dev-bot proxy
	const AUTH_TOKEN = 'siga2026';

	let isOpen = $state(false);

	type Notification = { id: number; source: string; author?: string; content: string; is_read: number; tags?: string; created_at: string };
	let notifications = $state<Notification[]>([]);
	let unreadCount = $state(0);
	let loading = $state(false);

	const API = '/api/dev-bot';

	async function fetchNotifications() {
		loading = true;
		try {
			const res = await fetch(`${API}/notifications?limit=50`);
			const data = await res.json();
			notifications = data.notifications || [];
			unreadCount = data.unread_count || 0;
		} catch (e) {
			console.error('Failed to fetch notifications:', e);
		} finally {
			loading = false;
		}
	}

	async function fetchUnreadCount() {
		try {
			const res = await fetch(`${API}/count`);
			const data = await res.json();
			unreadCount = data.unread_count || 0;
		} catch {}
	}

	async function handleOpen() {
		isOpen = true;
		await fetchNotifications();
		try {
			await fetch(`${API}/read-all`, { method: 'POST' });
			unreadCount = 0;
		} catch {}
	}

	function handleClose() {
		isOpen = false;
	}

	function formatDate(dateStr: string): string {
		try {
			const d = new Date(dateStr + 'Z');
			const now = new Date();
			const diff = now.getTime() - d.getTime();
			const mins = Math.floor(diff / 60000);
			if (mins < 1) return 'ahora';
			if (mins < 60) return `hace ${mins} min`;
			const hours = Math.floor(mins / 60);
			if (hours < 24) return `hace ${hours}h`;
			return d.toLocaleDateString('es-CL', { day: 'numeric', month: 'short' });
		} catch { return dateStr; }
	}

	function sourceIcon(source: string): string {
		if (source === 'discord') return '💬';
		if (source === 'manual') return '📝';
		return '🔔';
	}

	// Poll unread count every 30s
	onMount(() => {
		fetchUnreadCount();
		const interval = setInterval(fetchUnreadCount, 30000);
		return () => clearInterval(interval);
	});

	// Click outside to close
	function onWindowClick(e: MouseEvent) {
		const target = e.target as HTMLElement;
		if (!target.closest('.notification-bell')) {
			isOpen = false;
		}
	}
</script>

<svelte:window onclick={onWindowClick} />

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
				{#if loading}
					<li class="empty-state">Cargando...</li>
				{:else if notifications.length === 0}
					<li class="empty-state">Sin notificaciones 💤</li>
				{:else}
					{#each notifications as item (item.id)}
						<li class="notification-item" class:unread={item.is_read === 0}>
							<div class="notification-header">
								<span class="notification-source">{sourceIcon(item.source)} {item.source}</span>
								{#if item.author}
									<span class="notification-author">{item.author}</span>
								{/if}
							</div>
							<p class="notification-message">{item.content}</p>
							<div class="notification-footer">
								<span class="notification-time">{formatDate(item.created_at)}</span>
								{#if item.tags}
									<span class="notification-tag">{item.tags}</span>
								{/if}
							</div>
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

	.notification-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: var(--spacing-sm);
		margin-bottom: 2px;
	}

	.notification-source {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		font-weight: var(--font-weight-medium);
		text-transform: uppercase;
		letter-spacing: 0.5px;
	}

	.notification-author {
		font-size: var(--font-size-xs);
		color: var(--color-accent);
		font-weight: var(--font-weight-semibold);
	}

	.notification-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 4px;
	}

	.notification-tag {
		font-size: 10px;
		padding: 1px 6px;
		border-radius: var(--radius-full);
		background: var(--color-bg-alt);
		color: var(--color-text-muted);
		font-weight: var(--font-weight-medium);
	}
</style>
