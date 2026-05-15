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

/** Unión de todos los eventos A2UI SSE */
export type A2UIStreamEvent = A2UIEvent | UpdateEvent | PatchEvent;
