import { defineConfig } from 'vitest/config';
import { sveltekit } from '@sveltejs/kit/vite';
import path from 'path';

export default defineConfig({
	plugins: [sveltekit()],
	resolve: {
		conditions: ['browser'],
		alias: {
			'@siga/ui-kit': path.resolve(__dirname, '../../packages/ui-kit'),
			'@siga/shared': path.resolve(__dirname, '../../packages/shared')
		}
	},
	test: {
		environment: 'jsdom',
		globals: true,
		include: ['tests/**/*.test.ts', 'src/**/*.test.ts'],
		setupFiles: ['./tests/setup.ts'],
		coverage: {
			provider: 'v8',
			reporter: ['text', 'html', 'lcov'],
			thresholds: {
				statements: 70,
				branches: 70,
				functions: 70,
				lines: 70
			}
		}
	}
});
