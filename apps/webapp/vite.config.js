import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export default defineConfig({
  plugins: [sveltekit()],
  resolve: {
    alias: {
      '$docs': path.resolve(__dirname, '../../docs/technical-room')
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/setupTest.js']
  },
  server: {
    port: 5174,
    strictPort: true, // Force 5174 to avoid confusion
    proxy: {
      '/api': {
        target: 'https://siga-backend-production.up.railway.app',
        changeOrigin: true,
        secure: false,
        configure: (proxy, _options) => {
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            proxyReq.removeHeader('Origin');
          });
        }
      }
    }
  }
});