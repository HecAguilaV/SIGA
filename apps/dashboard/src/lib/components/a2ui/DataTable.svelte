<script lang="ts">
	/**
	 * DataTable.svelte — Tabla de datos con soporte de paginación opcional.
	 *
	 * Props:
	 * - columns: Array<{key: string, label: string}> — definición de columnas
	 * - rows: Array<Record<string, unknown>> — datos a mostrar
	 * - pageSize?: number — número de filas por página (opcional)
	 */

	let {
		columns,
		rows,
		pageSize
	}: {
		columns: Array<{ key: string; label: string }>;
		rows: Array<Record<string, unknown>> | undefined;
		pageSize?: number;
	} = $props();

	const displayRows = $derived(
		rows && pageSize ? rows.slice(0, pageSize) : rows ?? []
	);

	const totalRows = $derived(rows?.length ?? 0);

	const isEmpty = $derived(!rows || rows.length === 0);
</script>

<div class="data-table-wrapper" data-testid="data-table">
	{#if isEmpty}
		<div class="data-table-empty" role="status">
			<p>Sin datos disponibles</p>
		</div>
	{:else}
		<table class="data-table">
			<thead>
				<tr>
					{#each columns as col}
						<th class="data-table-th">{col.label}</th>
					{/each}
				</tr>
			</thead>
			<tbody>
				{#each displayRows as row}
					<tr class="data-table-tr">
						{#each columns as col}
							<td class="data-table-td">{row[col.key] ?? ''}</td>
						{/each}
					</tr>
				{/each}
			</tbody>
		</table>
		{#if pageSize && totalRows > pageSize}
			<div class="data-table-footer">
				<span class="data-table-info">Mostrando {Math.min(pageSize, totalRows)} de {totalRows}</span>
			</div>
		{/if}
	{/if}
</div>

<style>
	.data-table-wrapper {
		width: 100%;
		overflow-x: auto;
	}

	.data-table {
		width: 100%;
		border-collapse: collapse;
		font-size: var(--font-size-sm);
	}

	.data-table-th {
		text-align: left;
		padding: var(--spacing-sm) var(--spacing-md);
		background: var(--color-bg-alt);
		color: var(--color-text-secondary);
		font-weight: var(--font-weight-semibold);
		text-transform: uppercase;
		letter-spacing: 0.03em;
		font-size: var(--font-size-xs);
		border-bottom: 2px solid var(--color-border);
	}

	.data-table-td {
		padding: var(--spacing-sm) var(--spacing-md);
		border-bottom: 1px solid var(--color-border-light);
		color: var(--color-text-primary);
	}

	.data-table-tr:last-child .data-table-td {
		border-bottom: none;
	}

	.data-table-tr:hover .data-table-td {
		background: var(--color-bg-hover, rgba(0,0,0,0.02));
	}

	.data-table-empty {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: var(--spacing-2xl);
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}

	.data-table-footer {
		display: flex;
		justify-content: flex-end;
		padding: var(--spacing-sm) var(--spacing-md);
		border-top: 1px solid var(--color-border-light);
	}

	.data-table-info {
		font-size: var(--font-size-xs);
		color: var(--color-text-muted);
	}
</style>
