<script lang="ts" generics="T extends Record<string, unknown>">
	import Skeleton from '@siga/ui-kit/Skeleton.svelte';
	import Spinner from '@siga/ui-kit/Spinner.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';
	import PencilSimple from 'phosphor-svelte/lib/PencilSimple';
	import TrashSimple from 'phosphor-svelte/lib/TrashSimple';
	import CaretLeft from 'phosphor-svelte/lib/CaretLeft';
	import CaretRight from 'phosphor-svelte/lib/CaretRight';
	import ArrowsDownUp from 'phosphor-svelte/lib/ArrowsDownUp';

	export interface ColumnDef<T> {
		key: string;
		label: string;
		sortable?: boolean;
		render?: (item: T) => string;
		class?: string;
	}

	export interface ActionDef {
		label: string;
		variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
		icon?: string;
		onClick: (item: T) => void;
	}

	let {
		columns = [] as ColumnDef<T>[],
		data = [] as T[],
		total = 0,
		page = 1,
		pageSize = 20,
		actions,
		loading = false,
		onPageChange,
		children,
		loadingChildren
	}: {
		columns: ColumnDef<T>[];
		data: T[];
		total: number;
		page: number;
		pageSize?: number;
		actions?: ActionDef[];
		loading?: boolean;
		onPageChange?: (p: number) => void;
		children?: import('svelte').Snippet;
		loadingChildren?: import('svelte').Snippet;
	} = $props();

	const totalPages = $derived(Math.max(1, Math.ceil(total / pageSize)));
	const showingFrom = $derived(total === 0 ? 0 : (page - 1) * pageSize + 1);
	const showingTo = $derived(Math.min(page * pageSize, total));

	function getCellValue(item: T, col: ColumnDef<T>): string {
		if (col.render) return col.render(item);
		const val = item[col.key];
		return val != null ? String(val) : '';
	}

	function prevPage() {
		if (page > 1) onPageChange?.(page - 1);
	}

	function nextPage() {
		if (page < totalPages) onPageChange?.(page + 1);
	}
</script>

<div class="crud-table-wrapper">
	<table class="crud-table" role="grid">
		<thead>
			<tr>
				{#each columns as col}
					<th class="table-th {col.class || ''}" class:sortable={col.sortable}>
						<span class="th-content">
							{col.label}
							{#if col.sortable}
								<ArrowsDownUp size={12} weight="regular" class="sort-icon" />
							{/if}
						</span>
					</th>
				{/each}
				{#if actions && actions.length > 0}
					<th class="table-th table-th-actions">
						<span class="sr-only">Acciones</span>
					</th>
				{/if}
			</tr>
		</thead>
		<tbody>
			{#if loading}
				<tr>
					<td colspan={columns.length + (actions && actions.length > 0 ? 1 : 0)}>
						{#if loadingChildren}
							{@render loadingChildren()}
						{:else}
							{#each Array(5) as _}
								<Skeleton variant="table-row" />
							{/each}
						{/if}
					</td>
				</tr>
			{:else if data.length === 0}
				<tr>
					<td colspan={columns.length + (actions && actions.length > 0 ? 1 : 0)}>
						<div class="empty-state">
							{#if children}
								{@render children()}
							{:else}
								<p class="empty-text">Sin datos disponibles</p>
							{/if}
						</div>
					</td>
				</tr>
			{:else}
				{#each data as item, i (JSON.stringify(item))}
					<tr class="table-row" class:even={i % 2 === 0}>
						{#each columns as col}
							<td class="table-td {col.class || ''}">
								{getCellValue(item, col)}
							</td>
						{/each}
						{#if actions && actions.length > 0}
							<td class="table-td table-td-actions">
								<div class="actions-group">
									{#each actions as action}
										<button
											class="action-btn action-{action.variant || 'ghost'}"
											onclick={() => action.onClick(item)}
											aria-label={action.label}
											type="button"
										>
											{#if action.label === 'Editar' || action.label === 'edit'}
												<PencilSimple size={16} />
											{:else if action.label === 'Eliminar' || action.label === 'delete'}
												<TrashSimple size={16} />
											{:else}
												{action.label}
											{/if}
										</button>
									{/each}
								</div>
							</td>
						{/if}
					</tr>
				{/each}
			{/if}
		</tbody>
	</table>

	{#if total > 0}
		<div class="table-footer">
			<span class="pagination-info">
				Mostrando {showingFrom}-{showingTo} de {total}
			</span>
			<div class="pagination-controls">
				<button
					class="page-btn"
					onclick={prevPage}
					disabled={page <= 1}
					aria-label="Página anterior"
					type="button"
				>
					<CaretLeft size={14} weight="bold" />
				</button>
				<span class="page-indicator">
					{page} / {totalPages}
				</span>
				<button
					class="page-btn"
					onclick={nextPage}
					disabled={page >= totalPages}
					aria-label="Página siguiente"
					type="button"
				>
					<CaretRight size={14} weight="bold" />
				</button>
			</div>
		</div>
	{/if}
</div>

<style>
	.crud-table-wrapper {
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		overflow: hidden;
	}

	.crud-table {
		width: 100%;
		border-collapse: collapse;
	}

	.table-th {
		text-align: left;
		padding: 12px 16px;
		font-size: var(--font-size-xs);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text-muted);
		text-transform: uppercase;
		letter-spacing: 0.05em;
		background: var(--color-bg-alt);
		border-bottom: 1px solid var(--color-border);
		white-space: nowrap;
	}

	.th-content {
		display: inline-flex;
		align-items: center;
		gap: 4px;
	}

	.sort-icon {
		color: var(--color-text-muted);
		opacity: 0.5;
	}

	.sortable {
		cursor: pointer;
	}

	.table-th-actions {
		width: 80px;
		text-align: right;
	}

	.table-row {
		transition: background var(--transition-fast);
	}

	.table-row:hover {
		background: var(--color-surface-hover);
	}

	.table-row.even {
		background: var(--color-bg-alt);
	}

	.table-row.even:hover {
		background: var(--color-surface-hover);
	}

	.table-td {
		padding: 12px 16px;
		font-size: var(--font-size-sm);
		color: var(--color-text);
		border-bottom: 1px solid var(--color-border-light);
		vertical-align: middle;
	}

	.table-td-actions {
		text-align: right;
	}

	.actions-group {
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 4px;
	}

	.action-btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 32px;
		height: 32px;
		border: none;
		background: transparent;
		color: var(--color-text-muted);
		border-radius: var(--radius-sm);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.action-btn:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.action-danger:hover {
		background: var(--color-error-bg);
		color: var(--color-error);
	}

	.empty-state {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: var(--spacing-2xl);
	}

	.empty-text {
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}

	.table-footer {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 12px 16px;
		background: var(--color-bg-alt);
		border-top: 1px solid var(--color-border);
	}

	.pagination-info {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
	}

	.pagination-controls {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
	}

	.page-btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 28px;
		height: 28px;
		border: 1px solid var(--color-border);
		background: var(--color-surface);
		color: var(--color-text-secondary);
		border-radius: var(--radius-sm);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.page-btn:hover:not(:disabled) {
		border-color: var(--color-accent);
		color: var(--color-accent);
	}

	.page-btn:disabled {
		opacity: 0.3;
		cursor: not-allowed;
	}

	.page-indicator {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		color: var(--color-text-secondary);
		min-width: 60px;
		text-align: center;
	}
</style>
