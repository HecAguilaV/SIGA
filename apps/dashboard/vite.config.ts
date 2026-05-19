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
			// Redirigir llamadas a la API clásica al Gateway
			'/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
				secure: false
			}
		}
	}
});
