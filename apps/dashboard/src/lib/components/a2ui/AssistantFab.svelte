<script lang="ts">
	/**
	 * AssistantFab.svelte — Botón flotante de acción (FAB) para el asistente A2UI.
	 *
	 * Props:
	 * - isOnline: boolean — estado de conexión del agente
	 * - isOpen: boolean — si el chat widget está abierto
	 * - onclick: () => void — manejador de click
	 * - unread: number — contador de mensajes no leídos (opcional)
	 */

	import ChatCircle from 'phosphor-svelte/lib/ChatCircle';
	import X from 'phosphor-svelte/lib/X';

	let {
		isOnline = false,
		isOpen = false,
		onclick = () => {},
		unread = 0
	}: {
		isOnline?: boolean;
		isOpen?: boolean;
		onclick?: () => void;
		unread?: number;
	} = $props();
</script>

<button
	class="assistant-fab"
	class:open={isOpen}
	onclick={onclick}
	aria-label={isOpen ? 'Cerrar asistente' : 'Abrir asistente'}
	aria-expanded={isOpen}
	type="button"
>
	<span class="fab-icon" aria-hidden="true">
		{#if isOpen}
			<X size={22} weight="bold" />
		{:else}
			<ChatCircle size={22} weight="fill" />
		{/if}
	</span>

	<!-- Online/offline badge -->
	<span class="status-badge" class:online={isOnline} class:offline={!isOnline} aria-label={isOnline ? 'Conectado' : 'Desconectado'}></span>

	{#if unread > 0 && !isOpen}
		<span class="unread-badge" aria-label={`${unread} mensajes no leídos`}>
			{unread > 9 ? '9+' : unread}
		</span>
	{/if}
</button>

<style>
	.assistant-fab {
		position: relative;
		width: 56px;
		height: 56px;
		border: none;
		border-radius: var(--radius-full);
		background: var(--color-accent);
		color: #fff;
		cursor: pointer;
		display: inline-flex;
		align-items: center;
		justify-content: center;
		box-shadow: var(--shadow-lg);
		transition: all var(--transition-base);
		z-index: 1000;
	}

	.assistant-fab:hover {
		background: var(--color-accent-hover);
		transform: scale(1.05);
		box-shadow: var(--shadow-glow);
	}

	.assistant-fab:active {
		transform: scale(0.95);
	}

	.assistant-fab.open {
		background: var(--color-error);
	}

	.assistant-fab.open:hover {
		background: #dc2626;
	}

	.fab-icon {
		display: inline-flex;
		align-items: center;
		justify-content: center;
	}

	/* Status badge */
	.status-badge {
		position: absolute;
		top: 2px;
		right: 2px;
		width: 12px;
		height: 12px;
		border-radius: var(--radius-full);
		border: 2px solid var(--color-surface);
	}

	.status-badge.online {
		background: var(--color-success);
	}

	.status-badge.offline {
		background: var(--color-text-muted);
	}

	/* Unread badge */
	.unread-badge {
		position: absolute;
		top: -4px;
		right: -4px;
		min-width: 20px;
		height: 20px;
		padding: 0 5px;
		border-radius: var(--radius-full);
		background: var(--color-error);
		color: #fff;
		font-size: 0.625rem;
		font-weight: var(--font-weight-bold);
		display: inline-flex;
		align-items: center;
		justify-content: center;
		line-height: 1;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
	}
</style>
