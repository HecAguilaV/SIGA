<script>
    import { onMount, onDestroy } from "svelte";
    import { businessStore } from "$lib/stores/businessStore";
    import { api } from "$lib/services/api";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";
    import { toast } from "$lib/stores/toast";

    let stores = [];
    let loading = true;

    // Subscribe to businessStore for stores data
    const unsubscribe = businessStore.subscribe((store) => {
        stores = store.stores || [];
        if (!store.loading) loading = false;
    });

    const columns = [
        { key: "name", label: "Name" },
        { key: "address", label: "Address" },
        { key: "city", label: "City" },
        {
            key: "isActive",
            label: "Status",
            formatter: (val) => (val ? "Active" : "Inactive"),
        },
    ];

    onMount(async () => {
        try {
            if ($businessStore.stores.length === 0) {
                await businessStore.loadData();
            } else {
                loading = false;
                businessStore.loadData(); // Background refresh
            }
        } catch (error) {
            console.error("Error loading stores:", error);
        }
    });

    onDestroy(() => {
        unsubscribe();
    });

    function handleCreate() {
        goto("/stores/new");
    }

    function handleEdit(event) {
        const item = event.detail;
        goto(`/stores/${item.id}`);
    }

    async function handleDelete(event) {
        const item = event.detail;
        if (confirm(`Are you sure you want to delete store "${item.name}"?`)) {
            try {
                const response = await api.delete(`/api/stores/${item.id}`);
                if (response.success) {
                    await businessStore.loadData();
                    toast.add("Store deleted", "success");
                } else {
                    toast.add("Error: " + response.message, "error");
                }
            } catch (e) {
                toast.add("Delete error: " + e.message, "error");
            }
        }
    }
</script>

<CrudTable
    title="Store Management"
    data={stores}
    {columns}
    {loading}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>
