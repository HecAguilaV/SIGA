<script lang="ts">
	/**
	 * ChatBubble.svelte — Burbuja de mensaje individual para el chat A2UI.
	 *
	 * Props:
	 * - role: 'user' | 'assistant' — quién envía el mensaje
	 * - content: string — contenido textual del mensaje
	 * - streaming: boolean — si el asistente está aún generando
	 * - timestamp: Date — momento del mensaje
	 *
	 * Muestra indicador de escritura (puntos animados) cuando
	 * streaming=true y content está vacío.
	 */

	import UserCircle from 'phosphor-svelte/lib/UserCircle';
	import Sparkle from 'phosphor-svelte/lib/Sparkle';

	let {
		role = 'user',
		content = '',
		streaming = false,
		timestamp = new Date(),
		provenance
	}: {
		role?: 'user' | 'assistant';
		content?: string;
		streaming?: boolean;
		timestamp?: Date;
		provenance?: string;
	} = $props();

	const isUser = $derived(role === 'user');
	const isAssistant = $derived(role === 'assistant');
	const showStreamingDots = $derived(isAssistant && streaming && !content);
	const formattedTime = $derived(
		timestamp.toLocaleTimeString('es-AR', {
			hour: '2-digit',
			minute: '2-digit'
		})
	);
</script>

<div
	class="chat-bubble"
	class:user={isUser}
	class:assistant={isAssistant}
	class:streaming
	role="log"
	aria-live={streaming ? 'polite' : 'off'}
>
	<div class="bubble-avatar">
		{#if isUser}
			<div class="avatar user-avatar" aria-hidden="true">
				<UserCircle size={18} weight="fill" />
			</div>
		{:else}
			<div class="avatar assistant-avatar" aria-hidden="true">
				<Sparkle size={18} weight="fill" />
			</div>
		{/if}
	</div>

	<div class="bubble-content">
		<div class="bubble-header">
			<span class="bubble-role">{isUser ? 'Tú' : 'Asistente'}</span>
			{#if provenance}
				<span class="provenance-badge" class:gemini={provenance === 'gemini'} class:fallback={provenance === 'fallback-engine'}>
					{provenance === 'gemini' ? 'Gemini 3' : 'Fallback'}
				</span>
			{/if}
			<span class="bubble-time">{formattedTime}</span>
		</div>

		<div class="bubble-text">
			{#if showStreamingDots}
				<span class="streaming-dots" aria-label="El asistente está escribiendo">
					<span class="dot">.</span>
					<span class="dot">.</span>
					<span class="dot">.</span>
				</span>
			{:else}
				{content}
			{/if}
		</div>
	</div>
</div>

<style>
	.chat-bubble {
		display: flex;
		gap: var(--spacing-sm);
		padding: var(--spacing-sm) var(--spacing-md);
		max-width: 100%;
	}

	.chat-bubble.user {
		flex-direction: row-reverse;
	}

	.bubble-avatar {
		flex-shrink: 0;
	}

	.avatar {
		width: 32px;
		height: 32px;
		border-radius: var(--radius-full);
		display: inline-flex;
		align-items: center;
		justify-content: center;
	}

	.user-avatar {
		background: var(--color-accent-light);
		color: var(--color-accent);
	}

	.assistant-avatar {
		background: var(--color-info-bg);
		color: var(--color-info);
	}

	.bubble-content {
		max-width: 80%;
		display: flex;
		flex-direction: column;
		gap: 4px;
	}

	.user .bubble-content {
		align-items: flex-end;
	}

	.bubble-header {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
	}

	.user .bubble-header {
		flex-direction: row-reverse;
	}

	.bubble-role {
		font-size: var(--font-size-xs);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text-secondary);
	}

	.bubble-time {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
		font-family: var(--font-mono);
	}

	.provenance-badge {
		font-size: 9px;
		font-weight: var(--font-weight-bold);
		text-transform: uppercase;
		padding: 1px 6px;
		border-radius: 4px;
		letter-spacing: 0.05em;
		backdrop-filter: blur(4px);
	}

	.provenance-badge.gemini {
		background: rgba(16, 185, 129, 0.1);
		color: #10b981;
		border: 1px solid rgba(16, 185, 129, 0.2);
	}

	.provenance-badge.fallback {
		background: rgba(245, 158, 11, 0.1);
		color: #f59e0b;
		border: 1px solid rgba(245, 158, 11, 0.2);
	}

	.bubble-text {
		padding: var(--spacing-sm) var(--spacing-md);
		border-radius: var(--radius-lg);
		font-size: var(--font-size-sm);
		line-height: 1.5;
		word-wrap: break-word;
		white-space: pre-wrap;
	}

	.user .bubble-text {
		background: var(--color-accent);
		color: #fff;
		border-bottom-right-radius: var(--radius-sm);
	}

	.assistant .bubble-text {
		background: var(--color-surface);
		color: var(--color-text);
		border: 1px solid var(--color-border-light);
		border-bottom-left-radius: var(--radius-sm);
	}

	/* Streaming dots animation */
	.streaming-dots {
		display: inline-flex;
		align-items: center;
		gap: 2px;
	}

	.dot {
		animation: pulse-dot 1.4s infinite;
		font-size: 1.5rem;
		line-height: 0;
		color: var(--color-text-muted);
		font-weight: var(--font-weight-bold);
	}

	.dot:nth-child(2) {
		animation-delay: 0.2s;
	}

	.dot:nth-child(3) {
		animation-delay: 0.4s;
	}

	@keyframes pulse-dot {
		0%,
		60%,
		100% {
			opacity: 0.2;
		}
		30% {
			opacity: 1;
		}
	}

	.chat-bubble.streaming .bubble-text {
		border-color: var(--color-info);
	}
</style>
