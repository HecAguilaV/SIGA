<script>
    import { page } from "$app/stores";
    import { uiStore } from "$lib/stores/uiStore";
    import {
        House,
        Package,
        ChartBar,
        Sparkle,
        SignOut,
        User,
        Storefront,
        Users,
        Tag,
        CaretLeft,
    } from "phosphor-svelte";
    import { authStore } from "$lib/stores/authStore";
    import { authService } from "$lib/services/auth";

    const menuItems = [
        { label: "Dashboard", href: "/", icon: House }, // Público / Básico
        {
            label: "Análisis",
            href: "/analisis",
            icon: ChartBar,
            requiredPermission: "REPORTES_VER",
        }, // ANALISIS_IA? o REPORTES_VER
        {
            label: "Productos",
            href: "/productos",
            icon: Package,
            requiredPermission: "PRODUCTOS_VER",
        },
        {
            label: "Categorías",
            href: "/categorias",
            icon: Tag,
            requiredPermission: "CATEGORIAS_ACTUALIZAR",
        }, // Asumiendo view is implicitly update for now, or just show if can update
        {
            label: "Locales",
            href: "/locales",
            icon: Storefront,
            requiredPermission: "LOCALES_ACTUALIZAR",
        },
        {
            label: "Usuarios",
            href: "/usuarios",
            icon: Users,
            requiredPermission: "USUARIOS_CREAR",
        }, // O USUARIOS_PERMISOS
    ];

    $: user = $authStore.user || {
        nombre: "Usuario",
        apellido: "",
        rol: "Invitado",
        permisos: [],
    };

    $: filteredItems = menuItems.filter((item) => {
        if (!item.requiredPermission) return true; // Siempre visible si no requiere permiso
        if (user.rol === "ADMINISTRADOR") return true; // Admin ve todo
        return (user.permisos || []).includes(item.requiredPermission);
    });

    const handleLogout = () => {
        authService.logout();
        window.location.href = "/login";
    };

    // Si estamos en móvil, cerramos el sidebar al navegar
    $: if ($uiStore.isMobile && $page.url.pathname) {
        uiStore.closeSidebar();
    }
</script>

<aside class="sidebar" class:closed={!$uiStore.isSidebarOpen}>
    <div class="sidebar-header">
        <div class="logo-container">
            <img src="/S.png" alt="SIGA" class="logo-img" />
            <div class="logo-text-col">
                <span class="logo-text">SIGA</span>
                <span class="logo-subtitle">Inventario</span>
            </div>
        </div>
        <!-- Botón para colapsar el menú -->
        <button
            class="toggle-icon-btn"
            on:click={uiStore.closeSidebar}
            title="Ocultar menú"
        >
            <CaretLeft size={20} weight="bold" />
        </button>
    </div>

    <nav class="sidebar-nav">
        {#each filteredItems as item}
            <a
                href={item.href}
                class="nav-item"
                class:active={$page.url.pathname === item.href}
            >
                <svelte:component
                    this={item.icon}
                    size={24}
                    weight={$page.url.pathname === item.href
                        ? "fill"
                        : "regular"}
                />
                <span class="nav-label">{item.label}</span>
            </a>
        {/each}
    </nav>

    <div class="sidebar-footer">
        <div class="user-profile">
            <div class="user-avatar">
                <User size={20} />
            </div>
            <div class="user-info">
                <span class="user-name">{user.nombre} {user.apellido}</span>
                <span class="user-role">{user.rol}</span>
            </div>
        </div>
        <button
            class="logout-btn"
            on:click={handleLogout}
            title="Cerrar Sesión"
        >
            <SignOut size={24} />
        </button>
    </div>
</aside>

<style>
    .sidebar {
        width: 260px;
        height: 100vh;
        background-color: var(--color-primario);
        color: #ffffff;
        display: flex;
        flex-direction: column;
        position: fixed;
        left: 0;
        top: 0;
        z-index: 3000;
        box-shadow: 4px 0 24px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    .sidebar.closed {
        transform: translateX(-100%);
    }

    .sidebar-header {
        padding: 1.5rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .logo-container {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .logo-img {
        height: 32px;
        width: auto;
    }

    .logo-text-col {
        display: flex;
        flex-direction: column;
        line-height: 1;
    }

    .logo-text {
        font-size: 1.25rem;
        font-weight: 800;
        letter-spacing: 1px;
        color: #ffffff;
    }

    .logo-subtitle {
        font-size: 0.95rem;
        font-weight: 500;
        color: rgba(255, 255, 255, 0.85);
        letter-spacing: 0.5px;
    }

    .toggle-icon-btn {
        background: rgba(255, 255, 255, 0.1);
        border: none;
        color: rgba(255, 255, 255, 0.8);
        cursor: pointer;
        padding: 6px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s;
    }

    .toggle-icon-btn:hover {
        background: rgba(255, 255, 255, 0.2);
        color: #fff;
    }

    .sidebar-nav {
        flex: 1;
        padding: 1.5rem 1rem;
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .nav-item {
        display: flex;
        align-items: center;
        gap: 0.875rem;
        padding: 0.875rem 1rem;
        border-radius: 8px;
        color: rgba(255, 255, 255, 0.7);
        text-decoration: none;
        font-weight: 500;
        transition: all 0.2s ease;
    }

    .nav-item:hover {
        background-color: rgba(255, 255, 255, 0.08);
        color: #ffffff;
        transform: translateX(4px);
    }

    .nav-item.active {
        background-color: rgba(255, 255, 255, 0.15);
        color: #ffffff;
        font-weight: 600;
        box-shadow: inset 4px 0 0 var(--color-secundario);
    }

    .nav-label {
        font-size: 0.95rem;
    }

    .sidebar-footer {
        padding: 1rem;
        background-color: rgba(0, 0, 0, 0.2);
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 0.5rem;
    }

    .user-profile {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        overflow: hidden;
    }

    .user-avatar {
        width: 36px;
        height: 36px;
        background-color: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #ffffff;
    }

    .user-info {
        display: flex;
        flex-direction: column;
        min-width: 0;
    }

    .user-name {
        font-size: 0.85rem;
        font-weight: 600;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 140px;
    }

    .user-role {
        font-size: 0.75rem;
        color: rgba(255, 255, 255, 0.6);
    }

    .logout-btn {
        background: none;
        border: none;
        color: rgba(255, 255, 255, 0.6);
        cursor: pointer;
        padding: 0.5rem;
        border-radius: 6px;
        transition: all 0.2s;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .logout-btn:hover {
        color: #ff6b6b;
        background-color: rgba(255, 255, 255, 0.05);
    }
</style>
