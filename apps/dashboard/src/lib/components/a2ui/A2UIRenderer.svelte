<script lang="ts">
	/**
	 * A2UIRenderer.svelte — Renderizador de superficies A2UI.
	 *
	 * Soporta dos modos:
	 * 1. v0.9: surfaceId + components[] — renderiza lista plana via catalog.ts
	 * 2. Legacy: tree (A2UINode) — renderizado recursivo con A2UINodeRenderer
	 *
	 * Props:
	 * - surfaceId?: string — ID de la superficie v0.9
	 * - components?: A2UIComponent[] — componentes planos v0.9
	 * - tree?: A2UINode | A2UINode[] | null — árbol legacy
	 */

	import A2UINodeRenderer from './A2UINodeRenderer.svelte';
	import { getComponent } from './catalog';
	import type { A2UINode } from '$lib/types/a2ui';
	import type { A2UIComponent } from '$lib/types/a2ui';

	let {
		surfaceId = '',
		components = undefined,
		tree = null,
		layout = undefined
	}: {
		surfaceId?: string;
		components?: A2UIComponent[] | undefined;
		tree?: A2UINode | A2UINode[] | null;
		layout?: import('$lib/types/a2ui').A2UILayout;
	} = $props();

	/**
	 * Determina si estamos en modo v0.9 (components[] presente y no vacío).
	 */
	function hasV0Envelope(): boolean {
		return Array.isArray(components) && components.length > 0;
	}

	/**
	 * Resuelve un componente del catálogo por type.
	 */
	function resolveComponent(type: string) {
		return getComponent(type);
	}

	/**
	 * Filtra props reservadas de Svelte 5 (children).
	 */
	function safeProps(props: Record<string, unknown> | undefined): Record<string, unknown> {
		if (!props) return {};
		const { children: _children, ...rest } = props;
		return rest;
	}

	/**
	 * Determina si hay contenido para mostrar.
	 */
	function hasContent(): boolean {
		return hasV0Envelope() || (tree !== null && tree !== undefined);
	}
</script>

<div class="a2ui-renderer" data-testid="a2ui-renderer">
	{#if !hasContent()}
		<!-- Empty state -->
		<div class="a2ui-empty" role="status">
			<div class="a2ui-empty-icon">📋</div>
			<p class="a2ui-empty-text">No hay contenido disponible</p>
			<p class="a2ui-empty-hint">Activa el modo agéntico para comenzar</p>
		</div>
	{:else if hasV0Envelope()}
		<!-- A2UI v0.9: render flat list of components via catalog -->
		<div 
			class="a2ui-components" 
			style="--a2ui-cols-desktop: {layout?.columns?.desktop ?? 3}; --a2ui-cols-tablet: {layout?.columns?.tablet ?? 2}; --a2ui-gap: var(--spacing-{layout?.gap ?? 'lg'});"
		>
			{#each components as comp, i (i)}
				{@const Component = resolveComponent(comp.type)}
				{#if Component}
					{@const compProps = safeProps(comp.props)}
					<Component {...compProps} />
				{:else}
					<div class="a2ui-fallback" role="alert">
						<span class="a2ui-fallback-text">Componente no disponible</span>
					</div>
				{/if}
			{/each}
		</div>
	{:else if Array.isArray(tree)}
		<!-- Legacy: Array of nodes — render each one -->
		<div class="a2ui-array">
			{#each tree as node, i}
				<A2UINodeRenderer {node} level={0} />
			{/each}
		</div>
	{:else if tree}
		<!-- Legacy: Single node — render it -->
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

	.a2ui-components {
		display: grid;
		grid-template-columns: repeat(var(--a2ui-cols-desktop, 3), 1fr);
		gap: var(--a2ui-gap, var(--spacing-lg));
		padding: var(--spacing-md) 0;
		align-items: start;
	}

	@media (max-width: 1024px) {
		.a2ui-components {
			grid-template-columns: repeat(var(--a2ui-cols-tablet, 2), 1fr);
		}
	}

	@media (max-width: 768px) {
		.a2ui-components {
			grid-template-columns: 1fr;
			gap: var(--spacing-md);
		}
	}

	.a2ui-array {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-lg);
	}

	.a2ui-fallback {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: var(--spacing-sm);
		padding: var(--spacing-lg);
		background: var(--color-bg-alt);
		border: 1px dashed var(--color-border);
		border-radius: var(--radius-md);
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}
</style>
