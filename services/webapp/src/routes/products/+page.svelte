<script>
    import { onMount, onDestroy } from "svelte";
    import { businessStore } from "$lib/stores/businessStore";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";
    import { api } from "$lib/services/api";

    let products = [];
    let loading = true;
    let stockData = [];

    // Subscribe to stock AND products data from store (Single Source of Truth)
    const unsubscribe = businessStore.subscribe((store) => {
        stockData = store.stock || [];
        if (store.products && store.products.length > 0) {
            products = store.products;
            loading = false;
        }
    });

    // Helper to get total stock for a product
    const getStockTotal = (prodId) => {
        return stockData
            .filter((s) => s.productId === prodId)
            .reduce((acc, curr) => acc + curr.quantity, 0);
    };

    const columns = [
        { key: "name", label: "Name" },
        {
            key: "total_stock",
            label: "Total Stock",
            formatter: (_, row) => {
                const total = getStockTotal(row.id);
                let colorClass = "is-success";
                let icon = "";

                if (total <= 5) {
                    colorClass = "is-danger";
                    icon = "🔥";
                } else if (total <= 10) {
                    colorClass = "is-warning";
                    icon = "⚠️";
                }

                return `<span class="tag ${colorClass} is-light">
                            ${icon} <b>${total} u.</b>
                        </span>`;
            },
        },
        { key: "barcode", label: "SKU / Barcode" },
        {
            key: "unitPrice",
            label: "Price",
            formatter: (val) => {
                if (!val) return "-";
                const num = Number(val);
                return `$ ${num.toLocaleString("es-CL", { minimumFractionDigits: 0, maximumFractionDigits: 0 })}`;
            },
        },
        {
            key: "isActive",
            label: "Status",
            formatter: (val) => (val ? "Active" : "Inactive"),
        },
    ];

    onMount(async () => {
        try {
            if ($businessStore.products.length === 0) {
                await businessStore.loadData();
            } else {
                loading = false;
                businessStore.loadData(); // Background refresh
            }
        } catch (error) {
            console.error("Error loading data:", error);
            loading = false;
        }
    });

    onDestroy(() => {
        unsubscribe();
    });

    function handleCreate() {
        goto("/products/new");
    }

    function handleEdit(event) {
        const item = event.detail;
        goto(`/products/${item.id}`);
    }

    async function handleDelete(event) {
        const item = event.detail;
        if (confirm(`Are you sure you want to delete product "${item.name}"?`)) {
            try {
                const response = await api.delete(`/api/products/${item.id}`);
                if (response.success) {
                    products = products.filter((p) => p.id !== item.id);
                } else {
                    alert("Error: " + response.message);
                }
            } catch (e) {
                alert("Delete error: " + e.message);
            }
        }
    }
</script>

<CrudTable
    title="Product Management"
    data={products}
    {columns}
    {loading}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>
