/**
 * chat.ts — Tipos para el sistema de chat A2UI con streaming SSE.
 *
 * Define los contratos para mensajes del chat, estados de conexión,
 * eventos SSE del agente, y llamadas a herramientas.
 */

import type { A2UINode, A2UIv0Message } from './a2ui';

/** Mensaje individual en la conversación */
export interface ChatMessage {
	id: string;
	role: 'user' | 'assistant';
	content: string;
	timestamp: Date;
	streaming?: boolean;
	provenance?: 'gemini' | 'fallback-engine' | string;
	metadata?: {
		hidden?: boolean;
		[key: string]: unknown;
	};
}

/** Posibles estados del store de chat */
export type ChatStatus = 'idle' | 'connecting' | 'streaming' | 'error';

/** Evento SSE recibido desde el proxy del agente */
export interface SSEEvent {
	type: 'chunk' | 'done' | 'error' | 'tool' | 'a2ui' | 'update' | 'patch';
	content?: string;
	done?: boolean;
	code?: string;
	message?: string;
	name?: string;
	status?: 'running' | 'done' | 'error';
	// A2UI event payloads
	tree?: A2UINode;
	action?: 'replace' | 'append';
	surface?: A2UIv0Message;
	nodeId?: string;
	props?: Record<string, unknown>;
	children?: A2UINode[];
}

/** Llamada a herramienta notificada por el agente */
export interface ToolCall {
	name: string;
	status: 'running' | 'done' | 'error';
	label?: string;
}

/** Configuración de reconexión */
export interface ReconnectConfig {
	maxRetries: number;
	baseDelayMs: number;
	timeoutMs: number;
}
