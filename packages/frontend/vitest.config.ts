import { defineConfig } from 'vitest/config';
import { sveltekit } from '@sveltejs/kit/vite';

export default defineConfig({
	plugins: [sveltekit()],
	resolve: {
		conditions: ['browser']
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
