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
	 * - Se expande en un widget de chat arrastrable (Draggable)
	 * - Se abre automáticamente al entrar en modo agéntico
	 */

	import AssistantFab from './AssistantFab.svelte';
	import ChatBubble from './ChatBubble.svelte';
	import ChatInput from './ChatInput.svelte';
	import ToolIndicator from './ToolIndicator.svelte';
	import { chat } from '$lib/stores/chat.svelte';
	import { a2ui } from '$lib/stores/a2ui.svelte';

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
	let isOnline = $state(true);
	let isConnecting = $state(false);
	let activeToolCalls = $state<ToolCallDisplay[]>([]);
	let chatWidgetEl: HTMLDivElement | undefined = $state();

	// Sincronizar con el store de chat y a2ui
	$effect(() => {
		// Ocultar motores internos de la lista de herramientas técnicas
		activeToolCalls = (chat.toolCalls ?? []).filter(
			(t) => !['gemini', 'fallback-engine'].includes(t.name)
		);
		isConnecting = chat.status === 'connecting';
	});

	// Abrir automáticamente si se activa el modo agéntico
	$effect(() => {
		if (a2ui.isAgentive && !isOpen) {
			isOpen = true;
		}
	});

	function toggleOpen() {
		isOpen = !isOpen;
		if (isOpen) {
			requestAnimationFrame(() => {
				if (chatWidgetEl) {
					chatWidgetEl.scrollTop = chatWidgetEl.scrollHeight;
				}
			});
		}
	}

	async function handleSend(text: string) {
		const contextPayload = JSON.stringify({
			mode,
			currentRoute,
			timestamp: Date.now()
		});

		await chat.send(text, contextPayload);

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
			case 'connecting': return 'Conectando...';
			case 'streaming': return 'El asistente está respondiendo...';
			case 'error': return 'Error de conexión';
			default: return '';
		}
	});

	$effect(() => {
		// Scroll to bottom when messages or UI components change
		if (chatWidgetEl && (chat.messages.length > 0 || a2ui.components?.length > 0 || a2ui.tree)) {
			requestAnimationFrame(() => {
				chatWidgetEl.scrollTop = chatWidgetEl.scrollHeight;
			});
		}
	});

	// Svelte Action para arrastrar la ventana
	function draggable(node: HTMLElement) {
		let x = 0;
		let y = 0;

		function handleMousedown(event: MouseEvent) {
			// Solo permitir arrastrar desde el header
			if (!(event.target as HTMLElement).closest('.widget-header')) return;
			// Ignorar clics en los botones del header
			if ((event.target as HTMLElement).closest('.header-btn')) return;

			// Convertir a fixed si no lo es, para evitar conflictos de offset
			node.style.position = 'fixed';
			
			const rect = node.getBoundingClientRect();
			x = event.clientX - rect.left;
			y = event.clientY - rect.top;

			window.addEventListener('mousemove', handleMousemove);
			window.addEventListener('mouseup', handleMouseup);
		}

		function handleMousemove(event: MouseEvent) {
			let newLeft = event.clientX - x;
			let newTop = event.clientY - y;

			// Límites para no perder la ventana
			const maxLeft = window.innerWidth - node.offsetWidth;
			const maxTop = window.innerHeight - node.offsetHeight;
			
			newLeft = Math.max(0, Math.min(newLeft, maxLeft));
			newTop = Math.max(0, Math.min(newTop, maxTop));

			node.style.left = `${newLeft}px`;
			node.style.top = `${newTop}px`;
			node.style.bottom = 'auto'; // Sobrescribir el default
			node.style.right = 'auto';  // Sobrescribir el default
		}

		function handleMouseup() {
			window.removeEventListener('mousemove', handleMousemove);
			window.removeEventListener('mouseup', handleMouseup);
		}

		node.addEventListener('mousedown', handleMousedown);

		return {
			destroy() {
				node.removeEventListener('mousedown', handleMousedown);
			}
		};
	}
</script>

<div class="contextual-assistant">
	<AssistantFab
		isOnline={isOnline}
		isOpen={isOpen}
		onclick={toggleOpen}
		unread={0}
	/>
</div>

<!-- Chat widget (Fuera de contextual-assistant para que sea position: fixed global) -->
{#if isOpen}
	<div class="chat-widget" role="dialog" aria-label="Asistente SIGA" aria-modal="false" use:draggable>
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
				{#each chat.messages.filter(m => !m.metadata?.hidden) as msg (msg.id)}
					<ChatBubble
						role={msg.role}
						content={msg.content}
						streaming={msg.streaming ?? false}
						timestamp={msg.timestamp}
						provenance={msg.provenance}
					/>
				{/each}
			{/if}

			{#if activeToolCalls.length > 0}
				<div class="tool-indicators">
					{#each activeToolCalls as tool (tool.name)}
						<ToolIndicator name={tool.name} status={tool.status} label={tool.label ?? tool.name} />
					{/each}
				</div>
			{/if}

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

		<ChatInput
			disabled={chat.status === 'connecting' || chat.status === 'streaming'}
			onsend={handleSend}
			placeholder={mode === 'analyst' ? 'Consulta datos...' : 'Ej: agregá 10 unidades a Harina 000'}
		/>
	</div>
{/if}

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
		position: fixed;
		bottom: 90px;
		right: 24px;
		width: 380px;
		height: 520px;
		min-width: 300px;
		min-height: 350px;
		max-width: 90vw;
		max-height: calc(100vh - 120px);
		background: var(--color-bg);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		box-shadow: var(--shadow-xl);
		display: flex;
		flex-direction: column;
		overflow: hidden;
		resize: both;
		animation: slide-up 0.2s ease-out;
		z-index: 950;
		transition: opacity var(--transition-base), transform var(--transition-base), border-color var(--transition-base);
	}

	@media (max-width: 768px) {
		.chat-widget {
			bottom: 0;
			right: 0;
			width: 100%;
			height: 80vh;
			max-height: 90vh;
			border-radius: var(--radius-lg) var(--radius-lg) 0 0;
			border-bottom: none;
			box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
		}
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
		cursor: grab;
		user-select: none;
	}

	.widget-header:active {
		cursor: grabbing;
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
