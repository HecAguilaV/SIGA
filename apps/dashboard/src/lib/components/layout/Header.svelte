<script lang="ts">
	import Breadcrumb from './Breadcrumb.svelte';
	import ThemeToggle from './ThemeToggle.svelte';
	import A11yToolbar from './A11yToolbar.svelte';
</script>

<header class="app-header">
	<div class="header-left">
		<Breadcrumb />
	</div>
	<div class="header-right">
		<A11yToolbar />
		<ThemeToggle />
	</div>
</header>

<style>
	.app-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 var(--spacing-lg);
		height: 56px;
		background: var(--color-surface-glass);
		backdrop-filter: blur(var(--glass-blur));
		-webkit-backdrop-filter: blur(var(--glass-blur));
		border-bottom: 1px solid var(--color-border);
		position: sticky;
		top: 0;
		z-index: 50;
	}

	.header-left {
		display: flex;
		align-items: center;
		gap: var(--spacing-md);
	}

	.header-right {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
	}
</style>
