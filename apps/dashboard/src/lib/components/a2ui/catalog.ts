/**
 * catalog.ts — Catálogo de componentes A2UI.
 *
 * Mapea strings type → componentes Svelte 5 del design system.
 * Provee lookup seguro con fallback para tipos no registrados.
 */

import type { ComponentType } from 'svelte';

// UI atoms
import Card from '$lib/components/ui/Card.svelte';
import Button from '$lib/components/ui/Button.svelte';
import Input from '$lib/components/ui/Input.svelte';
import Badge from '$lib/components/ui/Badge.svelte';
import Spinner from '$lib/components/ui/Spinner.svelte';
import Skeleton from '$lib/components/ui/Skeleton.svelte';
import Modal from '$lib/components/ui/Modal.svelte';

// Charts
import ChartWrapper from '$lib/components/charts/ChartWrapper.svelte';

// CRUD
import CrudTable from '$lib/components/crud/CrudTable.svelte';
import CrudForm from '$lib/components/crud/CrudForm.svelte';
import SearchBar from '$lib/components/crud/SearchBar.svelte';

// Dashboard
import InsightPanel from '$lib/components/dashboard/InsightPanel.svelte';
import AnomalyList from '$lib/components/dashboard/AnomalyList.svelte';

/**
 * Mapa de tipos A2UI a componentes Svelte 5.
 * Cada entrada mapea un string type (ej: "card", "chart")
 * al componente del design system correspondiente.
 */
export const A2UI_COMPONENT_MAP: Record<string, ComponentType> = {
	card: Card,
	button: Button,
	input: Input,
	badge: Badge,
	spinner: Spinner,
	skeleton: Skeleton,
	modal: Modal,
	chart: ChartWrapper,
	'crud-table': CrudTable,
	'crud-form': CrudForm,
	'search-bar': SearchBar,
	'insight-panel': InsightPanel,
	'anomaly-list': AnomalyList
};

/**
 * Tipos A2UI registrados (para validación).
 */
export const A2UI_TYPES: string[] = Object.keys(A2UI_COMPONENT_MAP);

/**
 * getComponent — Lookup seguro de componente por tipo.
 * Retorna el componente o null si no está registrado.
 */
export function getComponent(type: string): ComponentType | null {
	return A2UI_COMPONENT_MAP[type] ?? null;
}
