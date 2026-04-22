<script>
    import { onMount } from "svelte";
    import { api } from "$lib/services/api";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";
    import { toast } from "$lib/stores/toast";

    let users = [];
    let loading = true;

    const columns = [
        { key: "firstName", label: "Name" },
        { key: "email", label: "Email" },
        { key: "role", label: "Role" },
        {
            key: "isActive",
            label: "Status",
            formatter: (val) => (val ? "Active" : "Inactive"),
        },
    ];

    onMount(async () => {
        try {
            const response = await api.get("/api/auth/users");
            // Backend returns a list of User objects directly or wrapped in success
            if (Array.isArray(response)) {
                users = response;
            } else if (response.success) {
                users = response.users || [];
            }
        } catch (error) {
            console.error("Error loading users:", error);
            toast.add("Failed to load users", "error");
        } finally {
            loading = false;
        }
    });

    function handleCreate() {
        goto("/users/new");
    }

    function handleEdit(event) {
        const item = event.detail;
        goto(`/users/${item.id}`);
    }

    async function handleDelete(event) {
        const item = event.detail;
        if (confirm(`Are you sure you want to deactivate user "${item.firstName}"?`)) {
            try {
                // DELETE in users is a soft delete (deactivate)
                const response = await api.delete(`/api/auth/users/${item.id}`);
                if (response.success || response.status === 204) {
                    users = users.map((u) =>
                        u.id === item.id ? { ...u, isActive: false } : u,
                    );
                    toast.add("User deactivated", "success");
                } else {
                    toast.add("Error: " + (response.message || "Unknown error"), "error");
                }
            } catch (e) {
                toast.add("Deactivation error: " + e.message, "error");
            }
        }
    }
</script>

<CrudTable
    title="User Management"
    data={users}
    {columns}
    {loading}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>
