<script lang="ts">
	/**
	 * ContextualAssistant.svelte — Asistente contextual flotante (FAB + chat widget).
	 *
	 * Props:
	 * - mode: 'analyst' | 'operator' — modo de operación del asistente
	 * - currentRoute: string — ruta actual del dashboard para contexto
	 *
	 * Características:
	 * - FAB global con badge de estado online/offline
	 * - Se expande en un widget de chat con historial
	 * - Reconexión automática con backoff (1s → 2s → 4s, máx 3)
	 * - Timeout global de 60s por mensaje
	 * - Envía contexto de ruta actual en el payload
	 * - Tool indicators para llamadas a herramientas
	 */

	import AssistantFab from './AssistantFab.svelte';
	import ChatBubble from './ChatBubble.svelte';
	import ChatInput from './ChatInput.svelte';
	import ToolIndicator from './ToolIndicator.svelte';
	import { chat } from '$lib/stores/chat.svelte';

	interface ToolCallDisplay {
		name: string;
		status: 'running' | 'done' | 'error';
		label?: string;
	}

	let {
		mode = 'operator',
		currentRoute = '/'
	}: {
		mode?: 'analyst' | 'operator';
		currentRoute?: string;
	} = $props();

	let isOpen = $state(false);
	let isOnline = $state(true); // Inicia asumiendo online
	let isConnecting = $state(false);
	let activeToolCalls = $state<ToolCallDisplay[]>([]);
	let chatWidgetEl: HTMLDivElement | undefined = $state();

	// Sincronizar con el store
	$effect(() => {
		activeToolCalls = chat.toolCalls ?? [];
		isConnecting = chat.status === 'connecting';
	});

	function toggleOpen() {
		isOpen = !isOpen;
		if (isOpen) {
			// Scroll al último mensaje al abrir
			requestAnimationFrame(() => {
				if (chatWidgetEl) {
					chatWidgetEl.scrollTop = chatWidgetEl.scrollHeight;
				}
			});
		}
	}

	async function handleSend(text: string) {
		// Construir contexto con modo y ruta actual
		const contextPayload = JSON.stringify({
			mode,
			currentRoute,
			timestamp: Date.now()
		});

		await chat.send(text, contextPayload);

		// Scroll al final después de agregar mensajes
		requestAnimationFrame(() => {
			if (chatWidgetEl) {
				chatWidgetEl.scrollTop = chatWidgetEl.scrollHeight;
			}
		});
	}

	function handleReconnect() {
		chat.reconnect();
	}

	function handleCancel() {
		chat.cancel();
	}

	const statusMessage = $derived.by(() => {
		switch (chat.status) {
			case 'connecting':
				return 'Conectando...';
			case 'streaming':
				return 'El asistente está respondiendo...';
			case 'error':
				return 'Error de conexión';
			default:
				return '';
		}
	});

	// Scroll automático al recibir nuevos mensajes
	$effect(() => {
		if (chat.messages.length > 0 && chatWidgetEl) {
			requestAnimationFrame(() => {
				chatWidgetEl.scrollTop = chatWidgetEl.scrollHeight;
			});
		}
	});
</script>

<div class="contextual-assistant">
	<!-- FAB trigger -->
	<AssistantFab
		isOnline={isOnline}
		isOpen={isOpen}
		onclick={toggleOpen}
		unread={0}
	/>

	<!-- Chat widget (expandido) -->
	{#if isOpen}
		<div class="chat-widget" role="dialog" aria-label="Asistente SIGA" aria-modal="false">
			<!-- Header -->
			<div class="widget-header">
				<div class="header-info">
					<span class="header-title">Asistente SIGA</span>
					<span class="header-mode">{mode === 'analyst' ? 'Analista' : 'Operador'}</span>
				</div>
				<div class="header-actions">
					{#if chat.status === 'streaming' || chat.status === 'connecting'}
						<button class="header-btn" onclick={handleCancel} aria-label="Cancelar" type="button">
							<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
								<rect x="6" y="6" width="12" height="12" rx="2" ry="2"></rect>
							</svg>
						</button>
					{/if}
					<button class="header-btn" onclick={toggleOpen} aria-label="Cerrar" type="button">
						<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
							<line x1="18" y1="6" x2="6" y2="18"></line>
							<line x1="6" y1="6" x2="18" y2="18"></line>
						</svg>
					</button>
				</div>
			</div>

			<!-- Messages area -->
			<div class="widget-messages" bind:this={chatWidgetEl}>
				{#if chat.messages.length === 0}
					<div class="empty-state">
						<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="var(--color-text-muted)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
							<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
						</svg>
						<p class="empty-title">Asistente {mode === 'analyst' ? 'Analista' : 'Operador'}</p>
						<p class="empty-desc">
							{mode === 'analyst'
								? 'Consulta datos del negocio: stock, ventas, tendencias.'
								: 'Ejecuta acciones: ajustar stock, crear productos, etc.'}
						</p>
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
				{/if}

				<!-- Tool indicators -->
				{#if activeToolCalls.length > 0}
					<div class="tool-indicators">
						{#each activeToolCalls as tool (tool.name)}
							<ToolIndicator name={tool.name} status={tool.status} label={tool.label ?? tool.name} />
						{/each}
					</div>
				{/if}

				<!-- Status message -->
				{#if statusMessage}
					<div class="status-message" class:error={chat.status === 'error'} role="status">
						{statusMessage}
						{#if chat.status === 'error'}
							<button class="reconnect-btn" onclick={handleReconnect} type="button">
								Reconectar
							</button>
						{/if}
					</div>
				{/if}
			</div>

			<!-- Input area -->
			<ChatInput
				disabled={chat.status === 'connecting' || chat.status === 'streaming'}
				onsend={handleSend}
				placeholder={mode === 'analyst' ? 'Consulta datos...' : 'Ej: agregá 10 unidades a Harina 000'}
			/>
		</div>
	{/if}
</div>

<style>
	.contextual-assistant {
		position: fixed;
		bottom: 24px;
		right: 24px;
		z-index: 900;
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: var(--spacing-sm);
	}

	.chat-widget {
		position: absolute;
		bottom: 64px;
		right: 0;
		width: 360px;
		height: 520px;
		max-height: calc(100vh - 120px);
		background: var(--color-bg);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		box-shadow: var(--shadow-lg);
		display: flex;
		flex-direction: column;
		overflow: hidden;
		animation: slide-up 0.2s ease-out;
	}

	@keyframes slide-up {
		from {
			opacity: 0;
			transform: translateY(12px);
		}
		to {
			opacity: 1;
			transform: translateY(0);
		}
	}

	.widget-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-md);
		border-bottom: 1px solid var(--color-border);
		background: var(--color-surface);
		flex-shrink: 0;
	}

	.header-info {
		display: flex;
		flex-direction: column;
		gap: 2px;
	}

	.header-title {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	.header-mode {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		text-transform: capitalize;
	}

	.header-actions {
		display: flex;
		gap: var(--spacing-xs);
	}

	.header-btn {
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
		transition: all var(--transition-fast);
	}

	.header-btn:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.widget-messages {
		flex: 1;
		overflow-y: auto;
		padding: var(--spacing-sm) 0;
	}

	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: var(--spacing-xl);
		text-align: center;
		gap: var(--spacing-sm);
		height: 100%;
	}

	.empty-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	.empty-desc {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		line-height: 1.5;
		max-width: 260px;
	}

	.tool-indicators {
		display: flex;
		flex-wrap: wrap;
		gap: var(--spacing-xs);
		padding: var(--spacing-xs) var(--spacing-md);
	}

	.status-message {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: var(--spacing-sm);
		padding: var(--spacing-sm) var(--spacing-md);
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		text-align: center;
	}

	.status-message.error {
		color: var(--color-error);
	}

	.reconnect-btn {
		padding: 3px 10px;
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
</style>
