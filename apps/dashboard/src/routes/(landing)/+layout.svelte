<script lang="ts">
	import '../../app.css';
	import { theme } from '$lib/stores/theme.svelte';
	import { onMount } from 'svelte';

	let { children }: { children: import('svelte').Snippet } = $props();

	onMount(() => {
		theme.init();
	});
</script>

{@render children()}
