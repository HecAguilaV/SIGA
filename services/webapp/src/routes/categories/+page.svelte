<script>
    import { onMount, onDestroy } from "svelte";
    import { businessStore } from "$lib/stores/businessStore";
    import { api } from "$lib/services/api";
    import { toast } from "$lib/stores/toast";
    import { authStore } from "$lib/stores/authStore";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";
    import ProductAssignmentModal from "$lib/components/ProductAssignmentModal.svelte";

    let categories = [];
    let loading = true;
    let canCreate = false;
    let canEdit = false;
    let canDelete = false;

    // Permissions check
    $: {
        const user = $authStore.user;
        if (user) {
            const hasAccess = user.role === "ADMINISTRATOR" || user.role === "OPERATOR";
            canCreate = hasAccess;
            canEdit = hasAccess;
            canDelete = hasAccess;
        }
    }

    // Subscribe to store for categories
    const unsubscribe = businessStore.subscribe((store) => {
        categories = store.categories || [];
        if (!store.loading) loading = false;
    });

    const columns = [
        { key: "name", label: "Name" },
        { key: "description", label: "Description" },
        {
            key: "isActive",
            label: "Status",
            formatter: (val) => (val !== false ? "Active" : "Inactive"),
        },
        {
            key: "extra_actions",
            label: "Products",
            formatter: (val, row) =>
                `<button class="button is-small is-info is-light" onclick="document.dispatchEvent(new CustomEvent('assign-products', { detail: ${row.id} }))">🔗 Assign</button>`,
        },
    ];

    onMount(async () => {
        try {
            if ($businessStore.categories.length === 0) {
                await businessStore.loadData();
            } else {
                loading = false;
                businessStore.loadData(); // Background refresh
            }
        } catch (error) {
            console.error("Error loading categories:", error);
        }

        // Listener for "Assign (Hack)" button
        const handleAssignEvent = (e) => {
            const categoryId = e.detail;
            const category = categories.find((c) => c.id === categoryId);
            if (category) handleAssignProducts(category);
        };

        document.addEventListener("assign-products", handleAssignEvent);

        return () => {
            document.removeEventListener("assign-products", handleAssignEvent);
        };
    });

    onDestroy(() => {
        unsubscribe();
    });

    function handleCreate() {
        goto("/categories/new");
    }

    function handleEdit(event) {
        const item = event.detail;
        goto(`/categories/${item.id}`);
    }

    async function handleDelete(event) {
        const item = event.detail;
        if (confirm(`Are you sure you want to delete category "${item.name}"?`)) {
            try {
                const response = await api.delete(`/api/categories/${item.id}`);
                if (response.success) {
                    // Update local store or refresh
                    await businessStore.loadData();
                    toast.add("Category deleted", "success");
                } else {
                    toast.add("Error: " + response.message, "error");
                }
            } catch (e) {
                toast.add("Delete error: " + e.message, "error");
            }
        }
    }

    let modalOpen = false;
    let selectedCategory = null;

    function handleAssignProducts(category) {
        selectedCategory = category;
        modalOpen = true;
    }

    function handleModalClose() {
        modalOpen = false;
        selectedCategory = null;
    }
</script>

<CrudTable
    title="Category Management"
    data={categories}
    {columns}
    {loading}
    {canCreate}
    {canEdit}
    {canDelete}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>

{#if modalOpen && selectedCategory}
    <ProductAssignmentModal
        category={selectedCategory}
        isOpen={modalOpen}
        on:close={handleModalClose}
        on:save={async () => {
            handleModalClose();
            toast.add("Products assigned successfully", "success");
            await businessStore.loadData(); // Refresh to reflect changes
        }}
    />
{/if}
