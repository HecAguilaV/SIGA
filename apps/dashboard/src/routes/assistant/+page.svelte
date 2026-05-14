<script lang="ts">
	/**
	 * AssistantPage.svelte — Página completa del asistente A2UI.
	 *
	 * Ruta: /assistant
	 * Muestra el chat en pantalla completa (no flotante).
	 * Ideal para conversaciones largas o cuando se necesita
	 * más espacio que el widget flotante.
	 */

	import { page } from '$app/stores';
	import ChatBubble from '$lib/components/a2ui/ChatBubble.svelte';
	import ChatInput from '$lib/components/a2ui/ChatInput.svelte';
	import ToolIndicator from '$lib/components/a2ui/ToolIndicator.svelte';
	import { chat } from '$lib/stores/chat.svelte';

	import Sparkle from 'phosphor-svelte/lib/Sparkle';
	import Trash from 'phosphor-svelte/lib/Trash';

	let messagesContainer: HTMLDivElement | undefined = $state();

	const currentRoute = $derived($page.url.pathname);

	function handleSend(text: string) {
		const contextPayload = JSON.stringify({
			mode: 'operator',
			currentRoute,
			timestamp: Date.now()
		});
		chat.send(text, contextPayload);
	}

	function handleClear() {
		chat.clear();
	}

	function handleReconnect() {
		chat.reconnect();
	}

	function handleCancel() {
		chat.cancel();
	}

	// Auto-scroll al recibir nuevos mensajes
	$effect(() => {
		if (chat.messages.length > 0 && messagesContainer) {
			requestAnimationFrame(() => {
				messagesContainer.scrollTop = messagesContainer.scrollHeight;
			});
		}
	});

	const isBusy = $derived(chat.status === 'connecting' || chat.status === 'streaming');
</script>

<svelte:head>
	<title>SIGA — Asistente</title>
</svelte:head>

<div class="assistant-page">
	<!-- Header -->
	<header class="page-header">
		<div class="header-left">
			<Sparkle size={24} weight="fill" aria-hidden="true" />
			<div class="header-text">
				<h1>Asistente SIGA</h1>
				<p class="header-status">
					{chat.status === 'idle' ? 'Listo para ayudarte' :
						chat.status === 'connecting' ? 'Conectando...' :
						chat.status === 'streaming' ? 'El asistente está respondiendo...' :
						'Error de conexión'}
				</p>
			</div>
		</div>
		<div class="header-actions">
			{#if isBusy}
				<button class="action-btn" onclick={handleCancel} aria-label="Cancelar" type="button">
					Cancelar
				</button>
			{/if}
			<button class="action-btn secondary" onclick={handleClear} aria-label="Limpiar conversación" type="button">
				<Trash size={16} weight="regular" aria-hidden="true" />
				Limpiar
			</button>
		</div>
	</header>

	<!-- Messages -->
	<div class="messages-area" bind:this={messagesContainer}>
		{#if chat.messages.length === 0}
			<div class="empty-state">
				<Sparkle size={48} weight="thin" aria-hidden="true" />
				<h2>Asistente SIGA</h2>
				<p>Preguntame sobre tu negocio o pedime que ejecute acciones.</p>
				<div class="suggestions">
					<button class="suggestion-chip" onclick={() => handleSend('¿Cuántos productos tienen stock bajo?')} type="button">
						¿Cuántos productos tienen stock bajo?
					</button>
					<button class="suggestion-chip" onclick={() => handleSend('Mostrame el resumen del dashboard')} type="button">
						Mostrame el resumen del dashboard
					</button>
					<button class="suggestion-chip" onclick={() => handleSend('Agregá 10 unidades al producto con SKU más vendido')} type="button">
						Agregá 10 unidades al más vendido
					</button>
				</div>
			</div>
		{:else}
			{#each chat.messages as msg (msg.id)}
				<ChatBubble
					role={msg.role}
					content={msg.content}
					streaming={msg.streaming ?? false}
					timestamp={msg.timestamp}
				/>
			{/each}

			<!-- Tool indicators -->
			{#if chat.toolCalls && chat.toolCalls.length > 0}
				<div class="tool-indicators">
					{#each chat.toolCalls as tool (tool.name)}
						<ToolIndicator name={tool.name} status={tool.status} label={tool.label ?? tool.name} />
					{/each}
				</div>
			{/if}

			<!-- Error state with reconnect -->
			{#if chat.status === 'error'}
				<div class="error-bar">
					<span>Conexión perdida.</span>
					<button class="reconnect-btn" onclick={handleReconnect} type="button">
						Reconectar
					</button>
				</div>
			{/if}
		{/if}
	</div>

	<!-- Input -->
	<div class="input-container">
		<ChatInput
			disabled={isBusy}
			onsend={handleSend}
			placeholder="Escribí un mensaje..."
		/>
	</div>
</div>

<style>
	.assistant-page {
		display: flex;
		flex-direction: column;
		height: calc(100vh - 120px);
		max-width: 800px;
		margin: 0 auto;
	}

	.page-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-md) var(--spacing-lg);
		border-bottom: 1px solid var(--color-border);
		background: var(--color-surface);
		border-radius: var(--radius-lg) var(--radius-lg) 0 0;
		flex-shrink: 0;
	}

	.header-left {
		display: flex;
		align-items: center;
		gap: var(--spacing-md);
		color: var(--color-accent);
	}

	.header-text h1 {
		font-size: var(--font-size-lg);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		margin: 0;
	}

	.header-status {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		margin: 0;
	}

	.header-actions {
		display: flex;
		gap: var(--spacing-sm);
	}

	.action-btn {
		display: inline-flex;
		align-items: center;
		gap: var(--spacing-xs);
		padding: 6px 14px;
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		background: var(--color-surface);
		color: var(--color-text);
		font-size: var(--font-size-sm);
		cursor: pointer;
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
	}

	.action-btn:hover {
		background: var(--color-bg-alt);
		border-color: var(--color-accent);
	}

	.action-btn.secondary {
		color: var(--color-text-secondary);
	}

	.messages-area {
		flex: 1;
		overflow-y: auto;
		padding: var(--spacing-md) 0;
		background: var(--color-bg);
	}

	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: var(--spacing-2xl);
		text-align: center;
		gap: var(--spacing-md);
		height: 100%;
		color: var(--color-text-muted);
	}

	.empty-state h2 {
		font-size: var(--font-size-xl);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
		margin: 0;
	}

	.empty-state p {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		max-width: 360px;
	}

	.suggestions {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
		margin-top: var(--spacing-md);
	}

	.suggestion-chip {
		padding: var(--spacing-sm) var(--spacing-md);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-full);
		background: var(--color-surface);
		color: var(--color-text-secondary);
		font-size: var(--font-size-sm);
		cursor: pointer;
		transition: all var(--transition-fast);
		white-space: nowrap;
	}

	.suggestion-chip:hover {
		background: var(--color-accent-light);
		border-color: var(--color-accent);
		color: var(--color-accent);
	}

	.tool-indicators {
		display: flex;
		flex-wrap: wrap;
		gap: var(--spacing-xs);
		padding: var(--spacing-xs) var(--spacing-lg);
	}

	.error-bar {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: var(--spacing-sm);
		padding: var(--spacing-sm) var(--spacing-md);
		font-size: var(--font-size-sm);
		color: var(--color-error);
		background: var(--color-error-bg);
		margin: var(--spacing-sm) var(--spacing-lg);
		border-radius: var(--radius-md);
	}

	.reconnect-btn {
		padding: 4px 12px;
		border: 1px solid var(--color-error);
		border-radius: var(--radius-full);
		background: transparent;
		color: var(--color-error);
		font-size: var(--font-size-xs);
		cursor: pointer;
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
	}

	.reconnect-btn:hover {
		background: var(--color-error);
		color: #fff;
	}

	.input-container {
		flex-shrink: 0;
		border-top: 1px solid var(--color-border);
		background: var(--color-surface);
		border-radius: 0 0 var(--radius-lg) var(--radius-lg);
	}
</style>
