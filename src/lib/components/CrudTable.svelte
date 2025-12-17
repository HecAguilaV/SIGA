<script>
    import { createEventDispatcher } from "svelte";
    import { PencilSimple, Trash, Plus } from "phosphor-svelte";

    export let title = "Items";
    export let data = [];
    export let columns = [];
    export let loading = false;
    export let canCreate = true;
    export let canEdit = true;
    export let canDelete = true;

    const dispatch = createEventDispatcher();

    // Sorting Logic
    let sortColumn = "";
    let sortDirection = 1; // 1: asc, -1: desc

    function handleSort(column) {
        if (sortColumn === column.key) {
            sortDirection *= -1;
        } else {
            sortColumn = column.key;
            sortDirection = 1;
        }
    }

    $: sortedData = [...data].sort((a, b) => {
        if (!sortColumn) return 0;

        const valA = a[sortColumn];
        const valB = b[sortColumn];

        // Safe Compare
        const strA =
            valA === null || valA === undefined
                ? ""
                : String(valA).toLowerCase();
        const strB =
            valB === null || valB === undefined
                ? ""
                : String(valB).toLowerCase();

        if (strA < strB) return -1 * sortDirection;
        if (strA > strB) return 1 * sortDirection;
        return 0;
    });
</script>

<div class="box">
    <div class="level">
        <div class="level-left">
            <h1 class="title is-4">{title}</h1>
        </div>
        <div class="level-right">
            {#if canCreate}
                <button
                    class="button is-primary"
                    on:click={() => dispatch("create")}
                >
                    <span class="icon">
                        <Plus />
                    </span>
                    <span>Nuevo</span>
                </button>
            {/if}
        </div>
    </div>

    {#if loading}
        <progress class="progress is-small is-primary" max="100">15%</progress>
    {:else if data.length === 0}
        <div class="notification is-info is-light">
            No hay registros para mostrar.
        </div>
    {:else}
        <div class="table-container">
            <table class="table is-fullwidth is-hoverable is-striped">
                <thead>
                    <tr>
                        {#each columns as col}
                            <th
                                class="is-clickable"
                                on:click={() => handleSort(col)}
                                style="cursor: pointer; user-select: none;"
                            >
                                {col.label}
                                {#if sortColumn === col.key}
                                    <span
                                        >{sortDirection === 1 ? "▲" : "▼"}</span
                                    >
                                {/if}
                            </th>
                        {/each}
                        <th style="width: 100px;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {#each sortedData as item}
                        <tr>
                            {#each columns as col}
                                <td>
                                    {#if col.formatter}
                                        {@html col.formatter(
                                            item[col.key],
                                            item,
                                        )}
                                    {:else}
                                        {item[col.key]}
                                    {/if}
                                </td>
                            {/each}
                            <td>
                                <div class="buttons are-small">
                                    {#if canEdit}
                                        <button
                                            class="button is-info is-light"
                                            title="Editar"
                                            on:click={() =>
                                                dispatch("edit", item)}
                                        >
                                            <span class="icon">
                                                <PencilSimple />
                                            </span>
                                        </button>
                                    {/if}
                                    {#if canDelete}
                                        <button
                                            class="button is-danger is-light"
                                            title="Eliminar"
                                            on:click={() =>
                                                dispatch("delete", item)}
                                        >
                                            <span class="icon">
                                                <Trash />
                                            </span>
                                        </button>
                                    {/if}
                                </div>
                            </td>
                        </tr>
                    {/each}
                </tbody>
            </table>
        </div>
    {/if}
</div>

<style>
    .table-container {
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }
</style>
