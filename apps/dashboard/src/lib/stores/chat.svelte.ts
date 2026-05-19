/**
 * chat.svelte.ts — Store de chat A2UI con streaming SSE.
 *
 * Maneja el estado de la conversación (mensajes, estado de conexión),
 * envío de mensajes al proxy SSE, reconexión con backoff exponencial
 * y cancelación de requests en curso.
 *
 * Utiliza runes ($state) de Svelte 5 para reactividad.
 */

import type { ChatMessage, ChatStatus, SSEEvent, ToolCall, ReconnectConfig } from '$lib/types/chat';
import { a2ui } from '$lib/stores/a2ui.svelte';

/** Genera un ID único para cada mensaje */
let messageIdCounter = 0;
function generateId(): string {
	messageIdCounter++;
	return `msg-${Date.now()}-${messageIdCounter}`;
}

/** Configuración por defecto de reconexión */
const DEFAULT_RECONNECT: ReconnectConfig = {
	maxRetries: 3,
	baseDelayMs: 1000,
	timeoutMs: 60_000
};

class ChatStore {
	/** Historial de mensajes de la conversación */
	messages = $state<ChatMessage[]>([]);

	/** Estado actual de la conexión SSE */
	status = $state<ChatStatus>('idle');

	/** AbortController para cancelar la request en curso */
	abortController = $state<AbortController | null>(null);

	/** Tool calls notificadas por el agente */
	toolCalls = $state<ToolCall[]>([]);

	/** Contador de reintentos de reconexión */
	private retryCount = $state(0);

	/** Último mensaje enviado (para reconexión) */
	private lastMessage: { text: string; context?: string } | null = null;

	/** Timers activos (para limpieza) */
	private timeoutId: ReturnType<typeof setTimeout> | null = null;
	private retryTimer: ReturnType<typeof setTimeout> | null = null;

	/**
	 * send — Envía un mensaje al chat.
	 *
	 * 1. Agrega el mensaje del usuario al historial
	 * 2. Inicia la conexión SSE al proxy
	 * 3. Procesa eventos (chunk → acumula, done → finaliza, error → muestra)
	 * 4. Maneja reconexión automática en caso de error
	 */
	async send(text: string, context?: string, metadata?: { hidden?: boolean }): Promise<void> {
		if (!text.trim() || this.status === 'connecting' || this.status === 'streaming') return;

		this.lastMessage = { text, context };
		this.retryCount = 0;

		// Agregar mensaje del usuario
		const userMsg: ChatMessage = {
			id: generateId(),
			role: 'user',
			content: text,
			timestamp: new Date(),
			metadata
		};
		this.messages = [...this.messages, userMsg];

		// Agregar placeholder para la respuesta del asistente
		const assistantMsg: ChatMessage = {
			id: generateId(),
			role: 'assistant',
			content: '',
			timestamp: new Date(),
			streaming: true
		};
		this.messages = [...this.messages, assistantMsg];

		await this.startStream(assistantMsg.id, context);
	}

	/**
	 * startStream — Inicia la conexión SSE y procesa los eventos.
	 */
	private async startStream(assistantMsgId: string, context?: string): Promise<void> {
		this.status = 'connecting';
		this.toolCalls = [];

		const controller = new AbortController();
		this.abortController = controller;

		try {
			// Construir URL con query params
			const params = new URLSearchParams();
			params.set('message', this.lastMessage?.text ?? '');
			if (context) params.set('context', context);

			const history = this.messages
				.filter((m) => !m.streaming && m.id !== assistantMsgId)
				.map((m) => ({ role: m.role, content: m.content }));
			params.set('history', JSON.stringify(history));

			const response = await fetch(`/chat-handler/stream?${params.toString()}`, {
				signal: controller.signal,
				headers: {
					Accept: 'text/event-stream'
				}
			});

			if (controller.signal.aborted) {
				this.setAssistantError(assistantMsgId, '');
				return;
			}

			if (!response.ok) {
				// Error HTTP — no reintentar, mostrar error directamente
				const errorMsg = `Error del servidor (${response.status})`;
				this.setAssistantError(assistantMsgId, errorMsg);
				return;
			}

			this.status = 'streaming';

			// Leer el stream SSE
			await this.readStream(response, assistantMsgId, controller);
		} catch (err) {
			if (err instanceof DOMException && err.name === 'AbortError') {
				// Cancelación voluntaria — finalizar sin error
				this.finalizeAssistantMessage(assistantMsgId);
				this.status = 'idle';
				return;
			}

			// Intentar reconexión para errores de red/stream (no HTTP)
			await this.handleReconnect(assistantMsgId, context);
		}
	}

	/**
	 * readStream — Lee el ReadableStream de la respuesta SSE
	 * y procesa cada evento.
	 */
	private async readStream(response: Response, assistantMsgId: string, controller?: AbortController): Promise<void> {
		const reader = response.body?.getReader();
		if (!reader) {
			throw new Error('No se pudo leer el stream de respuesta');
		}

		const decoder = new TextDecoder();
		let buffer = '';

		try {
			while (true) {
				// Verificar si cancelaron mientras esperábamos
				if (controller?.signal.aborted) {
					this.finalizeAssistantMessage(assistantMsgId);
					break;
				}

				const { done, value } = await reader.read();

				if (done) {
					// Procesar resto del buffer
					if (buffer.trim()) {
						this.processSSEBuffer(buffer, assistantMsgId);
					}
					// Si no hay contenido y no recibimos done event, marcar como completo
					this.finalizeAssistantMessage(assistantMsgId);
					break;
				}

				buffer += decoder.decode(value, { stream: true });

				// Dividir por doble newline (delimitador SSE)
				const parts = buffer.split('\n\n');
				buffer = parts.pop() ?? '';

				for (const part of parts) {
					this.processSSEBuffer(part, assistantMsgId);
				}
			}
		} finally {
			reader.releaseLock();
			this.status = 'idle';
			this.abortController = null;
		}
	}

	/**
	 * processSSEBuffer — Procesa un bloque SSE (una o más líneas "data: ...").
	 */
	private processSSEBuffer(block: string, assistantMsgId: string): void {
		const lines = block.split('\n');
		for (const line of lines) {
			const trimmed = line.trim();

			// Ignorar líneas vacías o que no sean data:
			if (!trimmed || !trimmed.startsWith('data:')) continue;

			// Extraer el JSON después de "data: "
			const jsonStr = trimmed.slice(5).trim();
			if (!jsonStr) continue;

			try {
				const event: SSEEvent = JSON.parse(jsonStr);
				this.handleSSEEvent(event, assistantMsgId);
			} catch {
				// JSON inválido — ignorar
			}
		}
	}

	/**
	 * handleSSEEvent — Procesa un evento SSE individual.
	 */
	private handleSSEEvent(event: SSEEvent, assistantMsgId: string): void {
		switch (event.type) {
			case 'chunk': {
				if (event.content) {
					this.appendToAssistantMessage(assistantMsgId, event.content);
				}
				break;
			}
			case 'done': {
				// done event puede contener el texto completo; chunks ya lo construyeron
				// Solo reemplazar si no hay contenido acumulado (ej: si done llega sin chunks previos)
				const currentMsg = this.messages.find((m) => m.id === assistantMsgId);
				if (event.content && (!currentMsg || !currentMsg.content)) {
					this.appendToAssistantMessage(assistantMsgId, event.content);
				}
				this.finalizeAssistantMessage(assistantMsgId);
				break;
			}
			case 'error': {
				this.setAssistantError(
					assistantMsgId,
					event.message ?? 'Ocurrió un error al procesar tu consulta'
				);
				break;
			}
			case 'tool': {
				if (event.name && event.status) {
					const toolCall: ToolCall = {
						name: event.name,
						status: event.status as 'running' | 'done' | 'error',
						label: event.message ?? event.name
					};
					this.toolCalls = [
						...this.toolCalls.filter((t) => t.name !== event.name),
						toolCall
					];

					// Si el nombre es gemini o fallback-engine, setear procedencia
					if (['gemini', 'fallback-engine'].includes(event.name)) {
						this.messages = this.messages.map((m) =>
							m.id === assistantMsgId ? { ...m, provenance: event.name } : m
						);
					}
				}
				break;
			}
			case 'a2ui': {
				if (event.surface) {
					a2ui.handleSurface(event.surface);
					a2ui.enterAgentiveMode({});
				} else if (event.tree && event.action) {
					a2ui.updateTree(event.tree, event.action);
					a2ui.enterAgentiveMode({});
				}
				break;
			}
			case 'update': {
				if (event.nodeId && event.props) {
					a2ui.patchNode(event.nodeId, event.props);
				}
				break;
			}
			case 'patch': {
				if (event.nodeId && event.children) {
					a2ui.patchChildren(event.nodeId, event.children);
				}
				break;
			}
		}
	}

	/**
	 * appendToAssistantMessage — Agrega contenido al mensaje del asistente.
	 */
	private appendToAssistantMessage(id: string, content: string): void {
		this.messages = this.messages.map((m) =>
			m.id === id ? { ...m, content: m.content + content } : m
		);
	}

	/**
	 * finalizeAssistantMessage — Marca el mensaje del asistente como completo.
	 */
	private finalizeAssistantMessage(id: string): void {
		this.messages = this.messages.map((m) =>
			m.id === id ? { ...m, streaming: false } : m
		);
		this.status = 'idle';
	}

	/**
	 * setAssistantError — Marca el mensaje del asistente con un error.
	 */
	private setAssistantError(id: string, errorMsg: string): void {
		this.messages = this.messages.map((m) =>
			m.id === id
				? {
						...m,
						content: m.content || errorMsg || 'Error al conectar con el asistente',
						streaming: false
					}
				: m
		);
		this.status = 'error';
	}

	/**
	 * handleReconnect — Reconexión con backoff exponencial.
	 * Backoff: 1s → 2s → 4s (máx 3 reintentos)
	 */
	private async handleReconnect(assistantMsgId: string, context?: string): Promise<void> {
		if (this.retryCount >= DEFAULT_RECONNECT.maxRetries) {
			this.setAssistantError(
				assistantMsgId,
				'Conexión perdida. Intenta de nuevo.'
			);
			return;
		}

		this.retryCount++;
		const delay = DEFAULT_RECONNECT.baseDelayMs * Math.pow(2, this.retryCount - 1);

		this.status = 'connecting';

		return new Promise((resolve) => {
			this.retryTimer = setTimeout(async () => {
				try {
					await this.startStream(assistantMsgId, context);
				} catch {
					// Si falla otra vez, handleReconnect se llama recursivamente
				}
				resolve();
			}, delay);
		});
	}

	/**
	 * reconnect — Intenta reconectar manualmente.
	 * Usa el último mensaje enviado.
	 */
	async reconnect(): Promise<void> {
		if (!this.lastMessage || this.status === 'connecting' || this.status === 'streaming') return;

		this.retryCount = 0;

		const assistantMsg: ChatMessage = {
			id: generateId(),
			role: 'assistant',
			content: '',
			timestamp: new Date(),
			streaming: true
		};
		this.messages = [...this.messages, assistantMsg];

		await this.startStream(assistantMsg.id, this.lastMessage.context);
	}

	/**
	 * cancel — Cancela la request en curso.
	 */
	cancel(): void {
		if (this.abortController) {
			this.abortController.abort();
			this.abortController = null;
		}
		if (this.timeoutId) {
			clearTimeout(this.timeoutId);
			this.timeoutId = null;
		}
		if (this.retryTimer) {
			clearTimeout(this.retryTimer);
			this.retryTimer = null;
		}
		this.status = 'idle';
	}

	/**
	 * clear — Limpia el historial de mensajes.
	 */
	clear(): void {
		this.cancel();
		this.messages = [];
		this.toolCalls = [];
		this.lastMessage = null;
		this.retryCount = 0;
		this.status = 'idle';
	}

	/**
	 * cleanup — Limpia todos los recursos (timers, abort).
	 * Útil para llamar desde onDestroy del componente.
	 */
	cleanup(): void {
		this.cancel();
	}
}

/** Instancia singleton del store de chat */
export const chat = new ChatStore();
