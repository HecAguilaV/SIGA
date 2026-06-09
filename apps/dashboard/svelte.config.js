import adapter from '@sveltejs/adapter-node';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	preprocess: vitePreprocess(),

	kit: {
		adapter: adapter(),
		csrf: {
			checkOrigin: false
		},
		alias: {
			'@siga/ui-kit': '../../packages/ui-kit',
			'@siga/ui-kit/*': '../../packages/ui-kit/*',
			'@siga/shared': '../../packages/shared',
			'@siga/shared/*': '../../packages/shared/*'
		}
	}
};

export default config;
