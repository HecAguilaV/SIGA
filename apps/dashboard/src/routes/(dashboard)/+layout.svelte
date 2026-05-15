<script lang="ts">
	import '../../app.css';
	import Sidebar from '$lib/components/layout/Sidebar.svelte';
	import Header from '$lib/components/layout/Header.svelte';
	import ContextualAssistant from '$lib/components/a2ui/ContextualAssistant.svelte';
	import A2UIRenderer from '$lib/components/a2ui/A2UIRenderer.svelte';
	import { a2ui } from '$lib/stores/a2ui.svelte';
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
			<!-- Classic mode: render slot content -->
			<div class="classic-slot" class:classic-slot--hidden={a2ui.isAgentive}>
				{@render children()}
			</div>

			<!-- Agentive mode: render A2UIRenderer -->
			{#if a2ui.isAgentive}
				<div class="a2ui-slot" class:a2ui-slot--visible={a2ui.isAgentive}>
					<A2UIRenderer tree={a2ui.tree} />
				</div>
			{/if}
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
		position: relative;
	}

	/* Classic slot — hidden when in agentive mode (keep mounted for state) */
	.classic-slot {
		opacity: 1;
		transition: opacity var(--transition-base);
	}

	.classic-slot--hidden {
		opacity: 0;
		pointer-events: none;
		position: absolute;
		inset: 0;
		overflow: hidden;
		height: 0;
	}

	/* A2UI slot — shown when in agentive mode */
	.a2ui-slot {
		opacity: 0;
		transition: opacity var(--transition-base);
	}

	.a2ui-slot--visible {
		opacity: 1;
	}

	/* Responsive: mobile adjustments */
	@media (max-width: 768px) {
		.app-main {
			padding: var(--spacing-md);
		}
	}
</style>
