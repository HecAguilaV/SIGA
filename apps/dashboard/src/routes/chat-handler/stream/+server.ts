import { error } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';
import type { RequestHandler } from './$types';

const AGENT_BASE = env.AGENT_BASE || 'http://localhost:8000';
const AGENT_TIMEOUT_MS = 60_000;

export const GET: RequestHandler = async ({ url, fetch }) => {
	const message = url.searchParams.get('message');
	const context = url.searchParams.get('context') || '';
	const history = url.searchParams.get('history') || '[]';

	if (!message || message.trim().length === 0) {
		console.warn('[SSE Proxy] Intento de conexión sin mensaje');
		throw error(400, 'El parámetro "message" es requerido');
	}

	const agentUrl = new URL(`${AGENT_BASE}/api/agent/chat/stream`);
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
			const parts = buffer.split('\n\n');
			buffer = parts.pop() ?? '';

			for (const part of parts) {
				const trimmed = part.trim();
				if (!trimmed) continue;
				const sseEvent = transformAgentEvent(trimmed);
				if (sseEvent) {
					await writer.write(encoder.encode(sseEvent + '\n\n'));
				}
			}
		}
	} catch (err) {
		if (err instanceof DOMException && err.name === 'AbortError') {
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

function transformAgentEvent(line: string): string | null {
	const trimmed = line.trim();
	if (!trimmed) return null;

	// Si ya es un evento SSE (empieza con event: o data:), lo dejamos pasar tal cual
	if (trimmed.startsWith('event:') || trimmed.startsWith('data:')) {
		return trimmed;
	}

	// Si no, intentamos parsear como JSON por si es un objeto crudo
	try {
		const parsed = JSON.parse(trimmed);
		if (parsed.type && ['chunk', 'done', 'error', 'tool', 'a2ui', 'update', 'patch'].includes(parsed.type)) {
			return `data: ${JSON.stringify(parsed)}`;
		}
		return `data: ${JSON.stringify({ type: 'chunk', content: trimmed, done: false })}`;
	} catch {
		// Es texto plano, lo envolvemos en un chunk
		return `data: ${JSON.stringify({ type: 'chunk', content: trimmed, done: false })}`;
	}
}
