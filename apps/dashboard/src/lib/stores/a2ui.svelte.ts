/**
 * a2ui.svelte.ts — Store de estado para el modo A2UI (UI generativa).
 *
 * Maneja:
 * - Dual-mode: classic ↔ a2ui
 * - Árbol A2UI reactivo (replace/append/update/patch)
 * - Layout hints responsive
 * - Nodo seleccionado para operaciones dirigidas
 */

import type { A2UINode, A2UILayout, DashboardMode, A2UIComponent, A2UIv0Message } from '$lib/types/a2ui';

class A2UIStore {
	/** Modo de operación: classic (navegación fija) o a2ui (UI generativa) */
	mode = $state<DashboardMode>('classic');

	/** Árbol A2UI actual (null cuando no hay UI generativa activa) */
	tree = $state<A2UINode | null>(null);

	/** ID del nodo actualmente seleccionado (para update/patch targeting) */
	selectedNodeId = $state<string | null>(null);

	/** Hints de layout para el contenedor A2UI */
	layout = $state<A2UILayout>({
		layout: 'grid',
		columns: { desktop: 3, tablet: 2, mobile: 1 },
		gap: 'md'
	});

	/** Contexto de ruta activado al entrar en modo agentivo */
	private routeContext = $state<string>('');

	// ──────────────────────────────────────────────
	// A2UI v0.9 State (additive — backward compat)
	// ──────────────────────────────────────────────

	/** Lista plana de componentes A2UI v0.9 (reemplaza tree para surfaces) */
	components = $state<A2UIComponent[]>([]);

	/** ID de la superficie activa */
	surfaceId = $state<string>('');

	/** Data bindings resueltos para componentes con binding */
	dataBindings = $state<Record<string, unknown>>({});

	/**
	 * enterAgentiveMode — Activa el modo agentivo A2UI.
	 * Almacena el contexto de ruta actual para enviarlo al agente.
	 */
	enterAgentiveMode(context: { route?: string; data?: unknown }): void {
		this.mode = 'a2ui';
		if (context.route) {
			this.routeContext = context.route;
		}
	}

	/**
	 * exitAgentiveMode — Vuelve al modo clásico.
	 */
	exitAgentiveMode(): void {
		this.mode = 'classic';
		this.routeContext = '';
	}

	/**
	 * updateTree — Actualiza el árbol A2UI.
	 * - 'replace': reemplaza el árbol completo (o null para limpiar)
	 * - 'append': agrega los children del nuevo nodo al árbol existente
	 */
	updateTree(newTree: A2UINode | null, action: 'replace' | 'append'): void {
		if (action === 'replace') {
			this.tree = newTree;
			return;
		}

		// append: agregar children al árbol existente
		if (action === 'append' && newTree && newTree.children) {
			if (!this.tree) {
				this.tree = newTree;
				return;
			}
			this.tree = {
				...this.tree,
				children: [...(this.tree.children ?? []), ...newTree.children]
			};
		}
	}

	/**
	 * patchNode — Actualiza props de un nodo específico por nodeId.
	 * Busca recursivamente en el árbol y hace merge de props.
	 */
	patchNode(nodeId: string, props: Record<string, unknown>): void {
		if (!this.tree) return;
		this.tree = this.patchNodeRecursive(this.tree, nodeId, props);
	}

	/**
	 * patchNodeRecursive — Helper recursivo para patchNode.
	 */
	private patchNodeRecursive(
		node: A2UINode,
		nodeId: string,
		props: Record<string, unknown>
	): A2UINode {
		if (node.nodeId === nodeId) {
			return {
				...node,
				props: { ...node.props, ...props }
			};
		}
		if (node.children) {
			return {
				...node,
				children: node.children.map((child) =>
					this.patchNodeRecursive(child, nodeId, props)
				)
			};
		}
		return node;
	}

	/**
	 * patchChildren — Reemplaza los children de un nodo específico por nodeId.
	 */
	patchChildren(nodeId: string, children: A2UINode[]): void {
		if (!this.tree) return;
		this.tree = this.patchChildrenRecursive(this.tree, nodeId, children);
	}

	/**
	 * patchChildrenRecursive — Helper recursivo para patchChildren.
	 */
	private patchChildrenRecursive(
		node: A2UINode,
		nodeId: string,
		children: A2UINode[]
	): A2UINode {
		if (node.nodeId === nodeId) {
			return { ...node, children };
		}
		if (node.children) {
			return {
				...node,
				children: node.children.map((child) =>
					this.patchChildrenRecursive(child, nodeId, children)
				)
			};
		}
		return node;
	}

	// ──────────────────────────────────────────────
	// A2UI v0.9 Surface Handling
	// ──────────────────────────────────────────────

	/**
	 * handleSurface — Procesa un mensaje A2UI v0.9 y actualiza el estado.
	 *
	 * - createSurface → reemplaza components[], surfaceId, dataBindings
	 * - updateComponents → merge según mode (replace/append/patch por ref)
	 * - updateDataModel → merge en dataBindings
	 */
	handleSurface(msg: A2UIv0Message): void {
		switch (msg.type) {
			case 'createSurface': {
				this.surfaceId = msg.surfaceId;
				this.components = [...msg.components];
				this.dataBindings = {};
				break;
			}
			case 'updateComponents': {
				if (msg.mode === 'replace') {
					this.components = [...msg.components];
				} else if (msg.mode === 'append') {
					this.components = [...this.components, ...msg.components];
				} else if (msg.mode === 'patch') {
					// Merge props by ref — update existing or append new
					for (const incoming of msg.components) {
						const idx = this.components.findIndex((c) => c.ref === incoming.ref);
						if (idx >= 0) {
							this.components[idx] = {
								...this.components[idx],
								...incoming,
								props: {
									...(this.components[idx].props ?? {}),
									...(incoming.props ?? {})
								}
							};
						} else {
							this.components = [...this.components, incoming];
						}
					}
				}
				break;
			}
			case 'updateDataModel': {
				this.dataBindings = { ...this.dataBindings, ...msg.data };
				break;
			}
		}
	}

	/**
	 * updateLayout — Actualiza parcialmente los hints de layout.
	 */
	updateLayout(newLayout: Partial<A2UILayout>): void {
		this.layout = { ...this.layout, ...newLayout };
	}

	/** Derived: true cuando el modo activo es 'a2ui' */
	get isAgentive(): boolean {
		return this.mode === 'a2ui';
	}
}

/** Instancia singleton del store A2UI */
export const a2ui = new A2UIStore();
