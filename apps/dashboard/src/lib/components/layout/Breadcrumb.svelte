<script lang="ts">
	import { page } from '$app/stores';
	import CaretRight from 'phosphor-svelte/lib/CaretRight';

	interface BreadcrumbItem {
		label: string;
		href: string;
	}

	const segments = $derived.by(() => {
		const path = $page.url.pathname;
		if (path === '/') return [{ label: 'Dashboard', href: '/' }];

		const parts = path.split('/').filter(Boolean);
		const items: BreadcrumbItem[] = [{ label: 'Dashboard', href: '/' }];
		let currentHref = '';

		for (const part of parts) {
			if (part === '(dashboard)') continue;
			currentHref += `/${part}`;
			const label = part
				.replace(/\[id\]/g, 'Detalle')
				.replace(/-/g, ' ')
				.replace(/\b\w/g, (c) => c.toUpperCase());
			items.push({ label, href: currentHref });
		}

		return items;
	});
</script>

<nav aria-label="Breadcrumb" class="breadcrumb">
	<ol>
		{#each segments as segment, i (segment.href)}
			<li class="breadcrumb-item">
				{#if i < segments.length - 1}
					<a href={segment.href} class="breadcrumb-link">{segment.label}</a>
					<CaretRight size={12} weight="bold" class="breadcrumb-sep" />
				{:else}
					<span class="breadcrumb-current" aria-current="page">{segment.label}</span>
				{/if}
			</li>
		{/each}
	</ol>
</nav>

<style>
	.breadcrumb {
		display: flex;
		align-items: center;
	}

	.breadcrumb ol {
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
		flex-wrap: wrap;
	}

	.breadcrumb-item {
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
	}

	.breadcrumb-link {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		text-decoration: none;
		transition: color var(--transition-fast);
	}

	.breadcrumb-link:hover {
		color: var(--color-accent);
	}

	.breadcrumb-sep {
		color: var(--color-text-muted);
		flex-shrink: 0;
	}

	.breadcrumb-current {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}
</style>
