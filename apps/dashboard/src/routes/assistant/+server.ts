/**
 * +server.ts — SSE proxy endpoint para A2UI chat.
 *
 * GET /api/chat/stream?message=&context=&history=
 *
 * Conecta con siga-agent vía Gateway, pipea el ReadableStream de respuesta
 * y transforma los eventos SSE (chunk, done, error, tool) hacia el cliente.
 * Timeout de 60s por request.
 */

import { error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const GATEWAY_BASE = process.env.GATEWAY_BASE || 'http://localhost:8080';
const AGENT_TIMEOUT_MS = 60_000;

export const GET: RequestHandler = async ({ url, fetch }) => {
	const message = url.searchParams.get('message');
	const context = url.searchParams.get('context') ?? '';
	const history = url.searchParams.get('history') ?? '[]';

	if (!message || message.trim().length === 0) {
		throw error(400, 'El parámetro "message" es requerido');
	}

	// 1. Conectar con siga-agent vía Gateway
	const agentUrl = new URL(`${GATEWAY_BASE}/api/agent/chat/stream`);
	agentUrl.searchParams.set('message', message);
	agentUrl.searchParams.set('context', context);
	agentUrl.searchParams.set('history', history);

	const controller = new AbortController();
	const timeoutId = setTimeout(() => controller.abort(), AGENT_TIMEOUT_MS);

	let agentRes: Response;
	try {
		agentRes = await fetch(agentUrl.toString(), {
			signal: controller.signal,
			headers: {
				Accept: 'text/event-stream'
			}
		});
	} catch (err) {
		clearTimeout(timeoutId);
		if (err instanceof DOMException && err.name === 'AbortError') {
			throw error(504, 'El agente no respondió a tiempo');
		}
		throw error(502, 'Error al conectar con el agente');
	}

	if (!agentRes.ok) {
		clearTimeout(timeoutId);
		throw error(agentRes.status, `Error del agente: ${agentRes.statusText}`);
	}

	if (!agentRes.body) {
		clearTimeout(timeoutId);
		throw error(502, 'El agente no devolvió un cuerpo de respuesta');
	}

	// 2. Pipe de eventos: ReadableStream → transform → cliente
	const { readable, writable } = new TransformStream();
	const writer = writable.getWriter();
	const encoder = new TextEncoder();
	const decoder = new TextDecoder();

	pipeAgentStream(agentRes.body, writer, encoder, decoder, controller, timeoutId);

	return new Response(readable, {
		headers: {
			'Content-Type': 'text/event-stream',
			'Cache-Control': 'no-cache',
			Connection: 'keep-alive',
			'X-Accel-Buffering': 'no'
		}
	});
};

/**
 * pipeAgentStream — Lee el body del agente (ReadableStream<Uint8Array>),
 * divide en líneas SSE, transforma cada evento al formato estándar SSE
 * y lo escribe en el writable del TransformStream hacia el cliente.
 */
async function pipeAgentStream(
	body: ReadableStream<Uint8Array>,
	writer: WritableStreamDefaultWriter<Uint8Array>,
	encoder: TextEncoder,
	decoder: TextDecoder,
	controller: AbortController,
	timeoutId: ReturnType<typeof setTimeout>
): Promise<void> {
	const reader = body.getReader();
	let buffer = '';

	try {
		while (true) {
			const { done, value } = await reader.read();

			if (done) {
				// Procesar resto del buffer
				if (buffer.trim()) {
					for (const line of buffer.split('\n')) {
						const trimmed = line.trim();
						if (trimmed) {
							await writer.write(encoder.encode(trimmed + '\n\n'));
						}
					}
				}
				break;
			}

			buffer += decoder.decode(value, { stream: true });

			// Dividir por doble newline (delimitador SSE)
			const parts = buffer.split('\n\n');
			// El último elemento puede estar incompleto
			buffer = parts.pop() ?? '';

			for (const part of parts) {
				const trimmed = part.trim();
				if (!trimmed) continue;

				// Transformar el evento del agente al formato SSE estándar
				const sseEvent = transformAgentEvent(trimmed);
				if (sseEvent) {
					await writer.write(encoder.encode(sseEvent + '\n\n'));
				}
			}
		}
	} catch (err) {
		if (err instanceof DOMException && err.name === 'AbortError') {
			// Timeout — enviar evento de error al cliente
			const errorEvent = `data: ${JSON.stringify({
				type: 'error',
				code: 'TIMEOUT',
				message: 'El agente no respondió a tiempo. Intenta de nuevo.'
			})}`;
			await writer.write(encoder.encode(errorEvent + '\n\n'));
		} else {
			const errorEvent = `data: ${JSON.stringify({
				type: 'error',
				code: 'STREAM_ERROR',
				message: 'Error al leer la respuesta del agente.'
			})}`;
			await writer.write(encoder.encode(errorEvent + '\n\n'));
		}
	} finally {
		clearTimeout(timeoutId);
		reader.releaseLock();
		await writer.close();
	}
}

/**
 * transformAgentEvent — Toma una línea JSON del agente y la formatea
 * como un evento SSE estándar (`data: {...}`).
 *
 * El agente emite eventos con formato:
 *   {"type":"chunk","content":"texto","done":false}
 *   {"type":"done","content":"texto","done":true}
 *   {"type":"error","code":"...","message":"..."}
 *   {"type":"tool","name":"...","status":"running"|"done"|"error"}
 *
 * Retorna la línea SSE formateada, o null si no se pudo parsear.
 */
function transformAgentEvent(line: string): string | null {
	// Si ya viene con prefijo "data:", pasarlo directamente
	if (line.startsWith('data:')) {
		return line;
	}

	try {
		const parsed = JSON.parse(line);
		// Validar que sea un evento reconocido
		if (parsed.type && ['chunk', 'done', 'error', 'tool'].includes(parsed.type)) {
			return `data: ${JSON.stringify(parsed)}`;
		}
		// Evento desconocido — pasarlo como chunk genérico
		return `data: ${JSON.stringify({ type: 'chunk', content: line, done: false })}`;
	} catch {
		// No es JSON — tratarlo como chunk de texto
		return `data: ${JSON.stringify({ type: 'chunk', content: line, done: false })}`;
	}
}
