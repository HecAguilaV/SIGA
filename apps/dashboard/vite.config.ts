import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import path from 'path';

export default defineConfig({
	plugins: [sveltekit()],
	resolve: {
		alias: {
			'@siga/ui-kit': path.resolve(__dirname, '../../packages/ui-kit'),
			'@siga/shared': path.resolve(__dirname, '../../packages/shared')
		}
	},
	server: {
		port: 5173,
		strictPort: true,
		proxy: {
			// Agent A2UI (POST /api/agent/a2ui) → direct to Kotlin agent
			'/api/agent': {
				target: 'http://localhost:8000',
				changeOrigin: true
			},
			// Chat SSE (GET /api/chat/stream) → rewrite path to agent's endpoint
			'/api/chat': {
				target: 'http://localhost:8000',
				changeOrigin: true,
				rewrite: (path) => path.replace(/^\/api\/chat/, '/api/agent/chat')
			},
			// Everything else through the gateway
			'/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
				secure: false
			}
		}
	}
});
