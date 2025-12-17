import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [sveltekit()],
  server: {
    port: 1574,
    strictPort: true, // Force 1574 to avoid confusion
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