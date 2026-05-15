<script lang="ts">
	/**
	 * A2UIRenderer.svelte — Renderizador principal de árboles A2UI.
	 *
	 * Props: tree (A2UINode | A2UINode[] | null)
	 *
	 * - Si tree es null: muestra estado vacío
	 * - Si tree es un array: renderiza cada nodo como hijo directo
	 * - Si tree es un solo nodo: renderiza ese nodo (si tiene children, se manejan recursivamente)
	 *
	 * El renderizado real de cada nodo delega en A2UINodeRenderer.
	 */

	import A2UINodeRenderer from './A2UINodeRenderer.svelte';
	import type { A2UINode } from '$lib/types/a2ui';

	let {
		tree = null
	}: {
		tree?: A2UINode | A2UINode[] | null;
	} = $props();
</script>

<div class="a2ui-renderer" data-testid="a2ui-renderer">
	{#if tree === null || tree === undefined}
		<!-- Empty state -->
		<div class="a2ui-empty" role="status">
			<div class="a2ui-empty-icon">📋</div>
			<p class="a2ui-empty-text">No hay contenido disponible</p>
			<p class="a2ui-empty-hint">Activa el modo agéntico para comenzar</p>
		</div>
	{:else if Array.isArray(tree)}
		<!-- Array of nodes: render each one -->
		<div class="a2ui-array">
			{#each tree as node, i}
				<A2UINodeRenderer node={node} level={0} />
			{/each}
		</div>
	{:else}
		<!-- Single node: render it -->
		<A2UINodeRenderer node={tree} level={0} />
	{/if}
</div>

<style>
	.a2ui-renderer {
		width: 100%;
		min-height: 200px;
	}

	.a2ui-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: var(--spacing-2xl, 48px) var(--spacing-lg);
		gap: var(--spacing-sm);
		color: var(--color-text-muted);
		text-align: center;
	}

	.a2ui-empty-icon {
		font-size: 2.5rem;
		margin-bottom: var(--spacing-sm);
		opacity: 0.5;
	}

	.a2ui-empty-text {
		font-size: var(--font-size-lg);
		font-weight: var(--font-weight-medium);
		color: var(--color-text-secondary);
		margin: 0;
	}

	.a2ui-empty-hint {
		font-size: var(--font-size-sm);
		margin: 0;
	}

	.a2ui-array {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}
</style>
