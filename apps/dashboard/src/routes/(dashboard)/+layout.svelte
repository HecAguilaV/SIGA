<script lang="ts">
	import '../../app.css';
	import Sidebar from '$lib/components/layout/Sidebar.svelte';
	import Header from '$lib/components/layout/Header.svelte';
	import ContextualAssistant from '$lib/components/a2ui/ContextualAssistant.svelte';
	import { page } from '$app/stores';
	import type { LayoutData } from './$types';

	let { children, data }: {
		children?: import('svelte').Snippet;
		data: LayoutData;
	} = $props();

	const currentRoute = $derived($page.url.pathname);
</script>

<svelte:head>
	<title>SIGA — Dashboard</title>
</svelte:head>

<div class="app-shell">
	<Sidebar />
	<div class="app-content">
		<Header />
		<main id="main-content" class="app-main">
			{@render children()}
		</main>
	</div>
</div>

{#if data.user}
	<ContextualAssistant
		mode="operator"
		currentRoute={currentRoute}
	/>
{/if}

<style>
	.app-shell {
		min-height: 100vh;
		display: flex;
	}

	.app-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		min-width: 0;
	}

	.app-main {
		flex: 1;
		padding: var(--spacing-lg);
		overflow-y: auto;
	}
</style>
