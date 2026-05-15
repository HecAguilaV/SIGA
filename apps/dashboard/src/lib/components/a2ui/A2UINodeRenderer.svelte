<script lang="ts">
	/**
	 * A2UINodeRenderer.svelte — Renderiza un nodo individual del árbol A2UI.
	 *
	 * Mapea el type del nodo al componente del catálogo y pasa sus props.
	 * Renderiza children recursivamente para tipos container.
	 * Muestra fallback para tipos no registrados.
	 *
	 * NOTA: Los children de A2UI se renderizan recursivamente como nodos hermanos
	 * para tipos container. Para componentes del catálogo, la prop 'children'
	 * se filtra de las props porque Svelte 5 trata 'children' como snippet.
	 */

	import { getComponent } from './catalog';
	import type { A2UINode } from '$lib/types/a2ui';

	// Self-import for recursive rendering (Svelte 5 pattern)
	import A2UINodeRenderer from './A2UINodeRenderer.svelte';

	type LayoutType = 'grid' | 'stack' | 'sidebar';

	let {
		node,
		level = 0
	}: {
		node: A2UINode;
		level?: number;
	} = $props();

	/**
	 * Obtiene el componente del catálogo según el type del nodo.
	 */
	function resolveComponent(type: string) {
		return getComponent(type);
	}

	/**
	 * Determina si el nodo es un contenedor (usa layout hints).
	 */
	function isContainer(type: string): boolean {
		return type === 'container';
	}

	/**
	 * Construye clases CSS para contenedores según layout hints.
	 */
	function containerClasses(props: Record<string, unknown> | undefined): string {
		const layout = (props?.layout as LayoutType) ?? 'grid';
		const gap = (props?.gap as string) ?? 'md';
		return `a2ui-container a2ui-${layout} a2ui-gap-${gap}`;
	}

	/**
	 * Extrae children de un nodo, si existen.
	 */
	function hasChildren(n: A2UINode): boolean {
		return Array.isArray(n.children) && n.children.length > 0;
	}

	/**
	 * Filtra props para evitar conflictos con snippets de Svelte 5.
	 * En Svelte 5, 'children' es una prop reservada para snippets.
	 * Los A2UI children se manejan recursivamente, no como snippets.
	 */
	function safeProps(props: Record<string, unknown> | undefined): Record<string, unknown> {
		if (!props) return {};
		const { children: _children, ...rest } = props;
		return rest;
	}
</script>

{#if isContainer(node.type)}
	<!-- Container: render as div with layout classes -->
	<div class={containerClasses(node.props)} role="region" data-level={level}>
		{#if hasChildren(node)}
			{#each node.children as child}
				<div class="a2ui-cell">
					<A2UINodeRenderer node={child} level={level + 1} />
				</div>
			{/each}
		{/if}
	</div>
{:else}
	{@const Component = resolveComponent(node.type)}
	{#if Component}
		{@const componentProps = safeProps(node.props)}
		<Component {...componentProps} />
	{:else}
		<!-- Fallback for unknown component type -->
		<div class="a2ui-fallback" role="alert">
			<span class="a2ui-fallback-text">Componente no disponible</span>
		</div>
	{/if}
{/if}

<style>
	.a2ui-container {
		width: 100%;
	}

	.a2ui-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
		gap: var(--spacing-md);
	}

	.a2ui-stack {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.a2ui-sidebar {
		display: grid;
		grid-template-columns: 1fr 350px;
		gap: var(--spacing-md);
	}

	.a2ui-gap-sm {
		gap: var(--spacing-sm);
	}

	.a2ui-gap-md {
		gap: var(--spacing-md);
	}

	.a2ui-gap-lg {
		gap: var(--spacing-lg);
	}

	.a2ui-cell {
		min-width: 0;
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

	/* Responsive breakpoints */
	@media (max-width: 768px) {
		.a2ui-grid {
			grid-template-columns: 1fr;
		}

		.a2ui-sidebar {
			grid-template-columns: 1fr;
		}
	}

	@media (min-width: 769px) and (max-width: 1024px) {
		.a2ui-grid {
			grid-template-columns: repeat(2, 1fr);
		}
	}
</style>
