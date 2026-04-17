<script>
    import { page } from "$app/stores";
    import { uiStore } from "$lib/stores/uiStore";
    import { fly, fade } from "svelte/transition";
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
        { label: "Dashboard", href: "/", icon: House },
        { label: "Análisis", href: "/analisis", icon: ChartBar, requiredPermission: "REPORTES_VER" },
        { label: "Productos", href: "/productos", icon: Package, requiredPermission: "PRODUCTOS_VER" },
        { label: "Categorías", href: "/categorias", icon: Tag, requiredPermission: "CATEGORIAS_ACTUALIZAR" },
        { label: "Locales", href: "/locales", icon: Storefront, requiredPermission: "LOCALES_ACTUALIZAR" },
        { label: "Usuarios", href: "/usuarios", icon: Users, requiredPermission: "USUARIOS_CREAR" },
    ];

    $: user = $authStore.user || {
        nombre: "Usuario",
        apellido: "",
        rol: "Invitado",
        permisos: [],
    };

    $: filteredItems = menuItems.filter((item) => {
        if (!item.requiredPermission) return true;
        if (user.rol === "ADMINISTRADOR") return true;
        return (user.permisos || []).includes(item.requiredPermission);
    });

    const handleLogout = () => {
        authService.logout();
        window.location.href = "/login";
    };

    $: if ($uiStore.isMobile && $page.url.pathname) {
        uiStore.closeSidebar();
    }
</script>

<aside class="sidebar glass-card" class:closed={!$uiStore.isSidebarOpen} in:fly={{ x: -260, duration: 400 }}>
    <div class="sidebar-header">
        <div class="logo-container">
            <div class="logo-box">
                <img src="/S.png" alt="SIGA" />
            </div>
            <div class="logo-text-group">
                <span class="logo-title">SIGA</span>
                <span class="logo-tag">MNGMNT</span>
            </div>
        </div>
        <button class="icon-btn-subtle" on:click={uiStore.closeSidebar}>
            <CaretLeft size={18} weight="bold" />
        </button>
    </div>

    <nav class="sidebar-nav">
        <p class="nav-section-title">Navegación principal</p>
        {#each filteredItems as item}
            <a href={item.href} class="nav-link" class:active={$page.url.pathname === item.href}>
                <div class="icon-wrapper">
                    <svelte:component this={item.icon} size={20} weight={$page.url.pathname === item.href ? "fill" : "regular"} />
                </div>
                <span class="nav-label">{item.label}</span>
            </a>
        {/each}
    </nav>

    <div class="sidebar-footer">
        <div class="user-pill">
            <div class="user-info-min">
                <div class="avatar-small">
                    <User size={14} weight="bold" />
                </div>
                <div class="user-meta">
                    <span class="u-name">{user.nombre}</span>
                    <span class="u-role">{user.rol}</span>
                </div>
            </div>
            <button class="logout-btn-min" on:click={handleLogout} title="Cerrar Sesión">
                <SignOut size={18} />
            </button>
        </div>
    </div>
</aside>

<style>
    .sidebar {
        width: 260px;
        height: 100vh;
        background: var(--canvas);
        border-right: 1px solid var(--border-subtle);
        display: flex;
        flex-direction: column;
        position: fixed;
        left: 0;
        top: 0;
        z-index: 3000;
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: none; /* Dejar que app.css maneje shadows si es necesario */
    }

    .sidebar.closed {
        transform: translateX(-100%);
    }

    .sidebar-header {
        padding: 1.75rem 1.25rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .logo-container {
        display: flex;
        align-items: center;
        gap: 12px;
    }

    .logo-box {
        width: 32px;
        height: 32px;
        background: var(--accent-primary);
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 0 15px rgba(var(--accent-primary-rgb), 0.3);
    }

    .logo-box img {
        width: 20px;
        height: auto;
        filter: brightness(0) invert(1);
    }

    .logo-text-group {
        display: flex;
        flex-direction: column;
        gap: 0;
    }

    .logo-title {
        font-size: 1.1rem;
        font-weight: 700;
        letter-spacing: -0.02em;
        color: var(--text-primary);
    }

    .logo-tag {
        font-size: 0.7rem;
        font-weight: 700;
        color: var(--accent-primary);
        letter-spacing: 0.1em;
        margin-top: -2px;
    }

    .icon-btn-subtle {
        background: transparent;
        border: 1px solid var(--border-subtle);
        color: var(--text-tertiary);
        cursor: pointer;
        padding: 4px;
        border-radius: 6px;
        display: flex;
        transition: all 0.2s;
    }

    .icon-btn-subtle:hover {
        background: var(--surface-secondary);
        color: var(--text-primary);
        border-color: var(--border-hover);
    }

    .sidebar-nav {
        flex: 1;
        padding: 0 12px;
        display: flex;
        flex-direction: column;
        gap: 4px;
    }

    .nav-section-title {
        font-size: 11px;
        font-weight: 600;
        color: var(--text-tertiary);
        text-transform: uppercase;
        letter-spacing: 0.05em;
        margin: 1rem 0 0.75rem 12px;
    }

    .nav-link {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px 12px;
        border-radius: 6px;
        color: var(--text-secondary);
        text-decoration: none;
        font-size: 13px;
        font-weight: 500;
        transition: all 0.2s ease;
    }

    .nav-link:hover {
        background: var(--surface-secondary);
        color: var(--text-primary);
    }

    .nav-link.active {
        background: rgba(var(--accent-primary-rgb), 0.1);
        color: var(--accent-primary);
        font-weight: 600;
    }

    .icon-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .sidebar-footer {
        padding: 20px 12px;
        border-top: 1px solid var(--border-subtle);
    }

    .user-pill {
        background: var(--surface-secondary);
        border: 1px solid var(--border-subtle);
        border-radius: 8px;
        padding: 8px;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .user-info-min {
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .avatar-small {
        width: 24px;
        height: 24px;
        border-radius: 4px;
        background: var(--surface-elevated);
        border: 1px solid var(--border-subtle);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--text-tertiary);
    }

    .user-meta {
        display: flex;
        flex-direction: column;
        line-height: 1.1;
    }

    .u-name {
        font-size: 12px;
        font-weight: 600;
        color: var(--text-primary);
    }

    .u-role {
        font-size: 10px;
        color: var(--text-tertiary);
    }

    .logout-btn-min {
        background: transparent;
        border: none;
        color: var(--text-tertiary);
        cursor: pointer;
        padding: 4px;
        display: flex;
        transition: color 0.2s;
    }

    .logout-btn-min:hover {
        color: var(--status-danger);
    }
</style>
