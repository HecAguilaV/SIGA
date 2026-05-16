/**
 * a2ui.ts — Tipos para el protocolo A2UI de UI generativa.
 *
 * Define los contratos para árboles de nodos A2UI, layout hints,
 * acciones de actualización, y props de contenedor.
 */

/** Nodo individual del árbol A2UI */
export interface A2UINode {
	type: string;
	props?: Record<string, unknown>;
	children?: A2UINode[];
	nodeId?: string; // for update/patch targeting
}

/** Hints de layout para contenedores A2UI */
export interface A2UILayout {
	layout: 'grid' | 'stack' | 'sidebar';
	columns?: { desktop: number; tablet: number; mobile: number };
	gap?: string;
}

/** Props extendidas para contenedores A2UI */
export interface A2UIContainerProps extends A2UILayout {
	title?: string;
	description?: string;
}

/** Acción de actualización del árbol A2UI */
export type A2UIAction = 'replace' | 'append';

/** Modo de operación del dashboard */
export type DashboardMode = 'classic' | 'a2ui';

/** Evento SSE extendido con tipos A2UI */
export interface A2UIEvent {
	type: 'a2ui';
	tree: A2UINode;
	action: A2UIAction;
}

/** Evento SSE de actualización de props */
export interface UpdateEvent {
	type: 'update';
	nodeId: string;
	props: Record<string, unknown>;
}

/** Evento SSE de parche de children */
export interface PatchEvent {
	type: 'patch';
	nodeId: string;
	children: A2UINode[];
}

// ──────────────────────────────────────────────
// A2UI v0.9 Types (additive — backward compat)
// ──────────────────────────────────────────────

/** Componente individual del protocolo A2UI v0.9 */
export interface A2UIComponent {
	type: string;
	props?: Record<string, unknown>;
	children?: A2UIComponent[];
	ref?: string; // stable identifier for targeted updates
}

/** Superficie A2UI v0.9 (conjunto de componentes con layout) */
export interface A2UISurface {
	surfaceId: string;
	components: A2UIComponent[];
	layout?: A2UILayout;
}

/** Mensajes del protocolo A2UI v0.9 */
export type A2UIv0Message =
	| {
			type: 'createSurface';
			surfaceId: string;
			components: A2UIComponent[];
			layout?: A2UILayout;
	  }
	| {
			type: 'updateComponents';
			surfaceId: string;
			components: A2UIComponent[];
			mode: 'replace' | 'append' | 'patch';
	  }
	| {
			type: 'updateDataModel';
			surfaceId: string;
			data: Record<string, unknown>;
	  };

/** Unión de todos los eventos A2UI SSE */
export type A2UIStreamEvent = A2UIEvent | UpdateEvent | PatchEvent;
