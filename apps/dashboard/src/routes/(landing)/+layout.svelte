<script lang="ts">
	import '../../app.css';
	import { onMount } from 'svelte';

	let { children }: { children: import('svelte').Snippet } = $props();

	onMount(() => {
		document.documentElement.setAttribute('data-theme', 'light');
	});
</script>

{@render children()}
