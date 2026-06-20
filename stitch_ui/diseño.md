<!-- Dashboard de Inventario - Vista General -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>SIGA - Gestión de Inventario</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800;900&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "on-tertiary": "#ffffff",
                        "outline-variant": "#c7c5d3",
                        "on-error-container": "#93000a",
                        "background": "#fdf7ff",
                        "on-secondary-fixed-variant": "#004e5f",
                        "inverse-primary": "#bfc2ff",
                        "on-background": "#1d1b21",
                        "secondary-container": "#50d9fe",
                        "surface-tint": "#F0F9FF",
                        "on-primary-fixed-variant": "#393e8c",
                        "on-tertiary-fixed": "#002018",
                        "on-error": "#ffffff",
                        "on-tertiary-fixed-variant": "#005140",
                        "on-primary-fixed": "#070a61",
                        "tertiary-fixed": "#79f8d5",
                        "on-surface": "#1d1b21",
                        "surface-container-highest": "#e6e0e9",
                        "primary-container": "#070a61",
                        "primary-fixed": "#e0e0ff",
                        "success-vibrant": "#10B981",
                        "secondary-fixed-dim": "#4cd6fb",
                        "on-secondary": "#ffffff",
                        "outline": "#777682",
                        "surface-dim": "#ded8e1",
                        "surface-variant": "#e6e0e9",
                        "secondary": "#00677d",
                        "tertiary": "#000000",
                        "surface-container-low": "#f8f1fa",
                        "inverse-surface": "#322f36",
                        "error-container": "#ffdad6",
                        "tertiary-container": "#002018",
                        "on-primary": "#ffffff",
                        "surface-bright": "#fdf7ff",
                        "on-primary-container": "#777dcf",
                        "surface-container-high": "#ece6ef",
                        "error": "#ba1a1a",
                        "on-surface-variant": "#464651",
                        "primary-fixed-dim": "#bfc2ff",
                        "surface-container": "#f2ecf5",
                        "tertiary-fixed-dim": "#5adcb9",
                        "primary": "#000000",
                        "on-tertiary-container": "#009579",
                        "surface-container-lowest": "#ffffff",
                        "on-secondary-container": "#005c70",
                        "inverse-on-surface": "#f5eff7",
                        "on-secondary-fixed": "#001f27",
                        "border-muted": "#D9D9D9",
                        "secondary-fixed": "#b3ebff",
                        "surface": "#fdf7ff"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "sidebar-collapsed": "80px",
                        "row-padding": "16px",
                        "card-padding": "20px",
                        "sidebar-width": "260px",
                        "gutter": "24px",
                        "container-max": "1440px"
                    },
                    "fontFamily": {
                        "headline-sm": ["Hanken Grotesk"],
                        "body-md": ["Hanken Grotesk"],
                        "body-lg": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "headline-md": ["Hanken Grotesk"],
                        "headline-md-mobile": ["Hanken Grotesk"],
                        "label-caps": ["JetBrains Mono"]
                    },
                    "fontSize": {
                        "headline-sm": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "body-md": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                        "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                        "display-lg": ["40px", {"lineHeight": "48px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                        "headline-md": ["24px", {"lineHeight": "32px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                        "headline-md-mobile": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "label-caps": ["12px", {"lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500"}]
                    }
                },
            },
        }
    </script>
<style>
        body { font-family: 'Hanken Grotesk', sans-serif; }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .soft-elevation-1 { box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08); }
        .soft-elevation-2 { box-shadow: 0 10px 15px -3px rgba(15, 23, 42, 0.1); }
    </style>
</head>
<body class="bg-background text-on-surface">
<!-- SideNavBar -->
<aside class="fixed left-0 top-0 h-full w-sidebar-width bg-surface-container-lowest shadow-sm flex flex-col py-6 z-50">
<div class="px-6 mb-8">
<img alt="SIGA Logo" class="h-10 object-contain mb-1" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
<p class="font-label-caps text-label-caps text-outline uppercase">Administrador</p>
</div>
<nav class="flex-1">
<ul class="space-y-1">
<li>
<a class="flex items-center gap-3 py-3 px-6 border-l-4 border-secondary bg-surface-container-low text-secondary font-bold transition-all duration-200 active:scale-95" href="#">
<span class="material-symbols-outlined">dashboard</span>
<span class="font-label-caps text-label-caps">Inicio</span>
</a>
</li>
<li>
<a class="flex items-center gap-3 py-3 px-6 text-on-surface-variant hover:bg-surface-container-low transition-colors transition-all duration-200 active:scale-95" href="#">
<span class="material-symbols-outlined">inventory_2</span>
<span class="font-label-caps text-label-caps">Inventario</span>
</a>
</li>
<li>
<a class="flex items-center gap-3 py-3 px-6 text-on-surface-variant hover:bg-surface-container-low transition-colors transition-all duration-200 active:scale-95" href="#">
<span class="material-symbols-outlined">storefront</span>
<span class="font-label-caps text-label-caps">Locales</span>
</a>
</li>
<li>
<a class="flex items-center gap-3 py-3 px-6 text-on-surface-variant hover:bg-surface-container-low transition-colors transition-all duration-200 active:scale-95" href="#">
<span class="material-symbols-outlined">point_of_sale</span>
<span class="font-label-caps text-label-caps">POS</span>
</a>
</li>
<li>
<a class="flex items-center gap-3 py-3 px-6 text-on-surface-variant hover:bg-surface-container-low transition-colors transition-all duration-200 active:scale-95" href="#">
<span class="material-symbols-outlined">analytics</span>
<span class="font-label-caps text-label-caps">Reportes</span>
</a>
</li>
<li>
<a class="flex items-center gap-3 py-3 px-6 text-on-surface-variant hover:bg-surface-container-low transition-colors transition-all duration-200 active:scale-95" href="#">
<span class="material-symbols-outlined">settings</span>
<span class="font-label-caps text-label-caps">Configuración</span>
</a>
</li>
</ul>
</nav>
<div class="px-4 mt-auto">
<button class="w-full bg-secondary text-on-secondary py-3 rounded-lg font-bold flex items-center justify-center gap-2 transition-all hover:brightness-110 active:scale-95">
<span class="material-symbols-outlined">add_circle</span>
<span>Nuevo Movimiento</span>
</button>
</div>
</aside>
<!-- TopAppBar -->
<header class="fixed top-0 right-0 w-[calc(100%-theme(spacing.sidebar-width))] h-16 bg-surface flex justify-between items-center px-gutter z-40 border-b border-surface-container">
<div class="flex items-center gap-4 flex-1">
<div class="relative w-full max-w-md">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
<input class="w-full pl-10 pr-4 py-2 bg-surface-container-low border-none rounded-lg focus:ring-2 focus:ring-secondary text-body-md font-body-md outline-none transition-all" placeholder="Buscar productos, ventas..." type="text"/>
</div>
</div>
<div class="flex items-center gap-6">
<div class="flex items-center gap-4">
<button class="p-2 text-outline hover:bg-surface-container-low rounded-full transition-colors relative">
<span class="material-symbols-outlined">notifications</span>
<span class="absolute top-2 right-2 w-2 h-2 bg-error rounded-full"></span>
</button>
<div class="h-8 w-px bg-outline-variant"></div>
<button class="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-surface-container-low transition-colors group">
<span class="material-symbols-outlined text-secondary">location_on</span>
<span class="text-body-md font-medium">Sucursal Central</span>
</button>
</div>
<button class="flex items-center gap-3 pl-4">
<div class="text-right">
<p class="text-body-md font-bold leading-none">Carlos R.</p>
<p class="text-[10px] text-outline font-label-caps uppercase mt-1">Perfil</p>
</div>
<img alt="Avatar de usuario" class="w-9 h-9 rounded-full bg-surface-container-high border-2 border-surface-container-highest" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAh5O_1qw9OSmyJSubBqG0rHIdAkE3EWDoheyAunF_b9fFeD-U0MWm-_8DqRFs7gzrsL71pHxkFUfAGv29d7IDw8qn8JjmPRVnQHZC4R_SFHk8HGX7M-IX-8OH9e7yfsoNreFz9JQxd3AcDtZAz5BjWdaLeEtftn46mAB-7owcrCDdFyQ8yUE1XjAb1kaMK9OQnhjI82K9k7aPPd61OHNy5YoFEJ5mSTH8wnIw-UYPkS9UpnuHm9vnkE8xJghbyu0iUzG64qCmMV5wk"/>
</button>
</div>
</header>
<!-- Main Content -->
<main class="ml-[260px] pt-24 px-gutter pb-gutter min-h-screen">
<div class="flex justify-between items-end mb-8">
<div>
<h2 class="font-headline-md text-headline-md text-on-surface">Panel de Control</h2>
<p class="text-on-surface-variant font-body-md">Resumen operativo de hoy, 24 de Octubre</p>
</div>
<div class="flex gap-3">
<button class="px-4 py-2 rounded-lg bg-surface-container-high text-on-surface-variant font-bold text-body-md transition-all hover:bg-surface-variant active:scale-95 flex items-center gap-2">
<span class="material-symbols-outlined text-[20px]">calendar_today</span>
                    Últimos 7 días
                </button>
</div>
</div>
<div class="grid grid-cols-12 gap-gutter">
<!-- Metrics Section -->
<div class="col-span-12 lg:col-span-9 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-gutter mb-gutter">
<div class="bg-surface-container-lowest p-card-padding rounded-xl soft-elevation-1 flex flex-col transition-all hover:soft-elevation-2 group border border-surface-container-low">
<div class="flex justify-between items-start mb-4">
<div class="p-2 bg-secondary/10 text-secondary rounded-lg">
<span class="material-symbols-outlined">payments</span>
</div>
<div class="text-on-tertiary-container font-medium text-[12px] flex items-center gap-1">
<span class="material-symbols-outlined text-[14px]">trending_up</span>
                            +12%
                        </div>
</div>
<p class="text-outline font-label-caps uppercase text-[11px] mb-1">Ventas hoy</p>
<h3 class="text-headline-sm font-headline-sm text-secondary">$1,240.50</h3>
<div class="mt-4 h-8 w-full flex items-end gap-1 opacity-60 group-hover:opacity-100 transition-opacity">
<div class="flex-1 bg-secondary/20 rounded-t h-[40%]"></div>
<div class="flex-1 bg-secondary/20 rounded-t h-[60%]"></div>
<div class="flex-1 bg-secondary/20 rounded-t h-[45%]"></div>
<div class="flex-1 bg-secondary/20 rounded-t h-[75%]"></div>
<div class="flex-1 bg-secondary/20 rounded-t h-[90%]"></div>
<div class="flex-1 bg-secondary/40 rounded-t h-[100%]"></div>
</div>
</div>
<div class="bg-surface-container-lowest p-card-padding rounded-xl soft-elevation-1 flex flex-col transition-all hover:soft-elevation-2 border border-surface-container-low">
<div class="flex justify-between items-start mb-4">
<div class="p-2 bg-secondary-container/20 text-on-secondary-container rounded-lg">
<span class="material-symbols-outlined">warning</span>
</div>
</div>
<p class="text-outline font-label-caps uppercase text-[11px] mb-1">Bajo stock</p>
<h3 class="text-headline-sm font-headline-sm text-on-secondary-container">14 Alertas</h3>
<p class="text-[12px] text-outline-variant mt-2 font-body-md">Requieren reposición inmediata</p>
</div>
<div class="bg-surface-container-lowest p-card-padding rounded-xl soft-elevation-1 flex flex-col transition-all hover:soft-elevation-2 border border-surface-container-low">
<div class="flex justify-between items-start mb-4">
<div class="p-2 bg-surface-container-high text-on-surface-variant rounded-lg">
<span class="material-symbols-outlined">pending_actions</span>
</div>
</div>
<p class="text-outline font-label-caps uppercase text-[11px] mb-1">Pedidos pendientes</p>
<h3 class="text-headline-sm font-headline-sm text-on-surface">28 Guías</h3>
<p class="text-[12px] text-outline-variant mt-2 font-body-md">12 por despachar hoy</p>
</div>
<div class="bg-surface-container-lowest p-card-padding rounded-xl soft-elevation-1 flex flex-col transition-all hover:soft-elevation-2 border border-surface-container-low">
<div class="flex justify-between items-start mb-4">
<div class="p-2 bg-surface-container-high text-on-surface-variant rounded-lg">
<span class="material-symbols-outlined">trending_up</span>
</div>
</div>
<p class="text-outline font-label-caps uppercase text-[11px] mb-1">Local con más actividad</p>
<h3 class="text-headline-sm font-headline-sm text-secondary">Local Norte</h3>
<p class="text-[12px] text-outline-variant mt-2 font-body-md">32% del volumen total</p>
</div>
</div>
<!-- Table Section -->
<div class="col-span-12 lg:col-span-9">
<div class="bg-surface-container-lowest rounded-xl soft-elevation-1 overflow-hidden border border-surface-container-low">
<div class="px-6 py-5 flex justify-between items-center bg-surface-bright">
<h3 class="font-headline-sm text-headline-sm">Movimientos de Inventario Recientes</h3>
<button class="text-secondary font-bold text-body-md hover:underline">Ver todo</button>
</div>
<div class="overflow-x-auto">
<table class="w-full text-left border-collapse">
<thead>
<tr class="bg-surface-container-low">
<th class="px-6 py-4 font-label-caps text-label-caps text-outline uppercase">Producto</th>
<th class="px-6 py-4 font-label-caps text-label-caps text-outline uppercase">Local</th>
<th class="px-6 py-4 font-label-caps text-label-caps text-outline uppercase text-center">Tipo</th>
<th class="px-6 py-4 font-label-caps text-label-caps text-outline uppercase text-right">Cantidad</th>
<th class="px-6 py-4 font-label-caps text-label-caps text-outline uppercase text-right">Fecha</th>
</tr>
</thead>
<tbody class="divide-y divide-surface-container-low">
<tr class="hover:bg-surface-container-low transition-colors group">
<td class="px-6 py-4">
<div class="flex items-center gap-3">
<div class="w-10 h-10 rounded bg-surface-container flex items-center justify-center">
<span class="material-symbols-outlined text-outline">smartphone</span>
</div>
<div>
<p class="text-body-md font-bold">iPhone 15 Pro Max</p>
<p class="text-[11px] text-outline">SKU: APP-15PM-256</p>
</div>
</div>
</td>
<td class="px-6 py-4 font-body-md">Sucursal Central</td>
<td class="px-6 py-4 text-center">
<span class="px-3 py-1 rounded-full bg-tertiary-fixed text-on-tertiary-fixed text-[11px] font-bold">Entrada</span>
</td>
<td class="px-6 py-4 font-label-caps text-right font-bold text-on-surface">+12</td>
<td class="px-6 py-4 font-body-md text-outline text-right">Hace 15 min</td>
</tr>
<tr class="hover:bg-surface-container-low transition-colors group">
<td class="px-6 py-4">
<div class="flex items-center gap-3">
<div class="w-10 h-10 rounded bg-surface-container flex items-center justify-center">
<span class="material-symbols-outlined text-outline">headphones</span>
</div>
<div>
<p class="text-body-md font-bold">AirPods Gen 3</p>
<p class="text-[11px] text-outline">SKU: APP-AIR3-G</p>
</div>
</div>
</td>
<td class="px-6 py-4 font-body-md">Local Norte</td>
<td class="px-6 py-4 text-center">
<span class="px-3 py-1 rounded-full bg-secondary-fixed text-on-secondary-fixed text-[11px] font-bold">Salida</span>
</td>
<td class="px-6 py-4 font-label-caps text-right font-bold text-on-surface">-4</td>
<td class="px-6 py-4 font-body-md text-outline text-right">Hace 42 min</td>
</tr>
</tbody>
</table>
</div>
</div>
</div>
<!-- Right Sidebar / Quick Access -->
<div class="col-span-12 lg:col-span-3 space-y-gutter">
<div class="bg-surface-container-lowest p-card-padding rounded-xl soft-elevation-1 border border-surface-container-low">
<h4 class="font-headline-sm text-headline-sm mb-6 flex items-center gap-2">
<span class="material-symbols-outlined text-secondary">bolt</span>
                        Accesos Rápidos
                    </h4>
<div class="space-y-3">
<button class="w-full flex items-center gap-4 p-4 rounded-xl border border-outline-variant hover:border-secondary hover:bg-surface-container-low transition-all group active:scale-[0.98]">
<div class="p-2 bg-secondary/10 text-secondary rounded-lg group-hover:bg-secondary group-hover:text-white transition-colors">
<span class="material-symbols-outlined">shopping_cart</span>
</div>
<div class="text-left">
<p class="text-body-md font-bold">Nueva Venta</p>
<p class="text-[11px] text-outline font-body-md">Abrir Terminal POS</p>
</div>
</button>
<button class="w-full flex items-center gap-4 p-4 rounded-xl border border-outline-variant hover:border-secondary hover:bg-surface-container-low transition-all group active:scale-[0.98]">
<div class="p-2 bg-tertiary-fixed text-on-tertiary-fixed rounded-lg group-hover:bg-secondary group-hover:text-white transition-colors">
<span class="material-symbols-outlined">add_business</span>
</div>
<div class="text-left">
<p class="text-body-md font-bold">Cargar Stock</p>
<p class="text-[11px] text-outline font-body-md">Registrar entrada</p>
</div>
</button>
</div>
</div>
<!-- Distribution Chart Mini -->
<div class="bg-surface-container-lowest p-card-padding rounded-xl soft-elevation-1 border border-surface-container-low">
<h4 class="font-headline-sm text-headline-sm mb-4">Stock por Local</h4>
<div class="space-y-4">
<div>
<div class="flex justify-between text-body-md mb-1">
<span>Sucursal Central</span>
<span class="font-bold">45%</span>
</div>
<div class="w-full bg-surface-container-high rounded-full h-2">
<div class="bg-secondary h-2 rounded-full" style="width: 45%"></div>
</div>
</div>
<div>
<div class="flex justify-between text-body-md mb-1">
<span>Local Norte</span>
<span class="font-bold">30%</span>
</div>
<div class="w-full bg-surface-container-high rounded-full h-2">
<div class="bg-secondary-fixed-dim h-2 rounded-full" style="width: 30%"></div>
</div>
</div>
</div>
</div>
<!-- Status Card -->
<div class="bg-secondary text-on-secondary p-card-padding rounded-xl soft-elevation-1 relative overflow-hidden group">
<div class="relative z-10">
<h4 class="font-headline-sm text-headline-sm mb-2">Sincronización OK</h4>
<p class="text-body-md opacity-80 mb-4">Todos los locales están en línea y reportando movimientos en tiempo real.</p>
<button class="bg-white/10 hover:bg-white/20 transition-colors py-2 px-4 rounded-lg text-[12px] font-bold backdrop-blur-md">
                            Ver estado de red
                        </button>
</div>
<span class="material-symbols-outlined absolute -bottom-4 -right-4 text-[100px] opacity-10 group-hover:scale-110 transition-transform duration-700">wifi_tethering</span>
</div>
</div>
</div>
</main>
<!-- Floating Action Button (IA Assistant) -->
<button class="fixed bottom-8 right-8 w-16 h-16 bg-secondary text-white rounded-full soft-elevation-2 flex items-center justify-center hover:scale-110 active:scale-95 transition-all shadow-xl z-50 p-1 border-4 border-white">
<img alt="IA Assistant" class="w-full h-full object-contain brightness-0 invert" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
</button>
<script>
        document.querySelectorAll('button, a').forEach(el => {
            el.addEventListener('click', (e) => {
                if(el.tagName === 'A' && el.getAttribute('href') === '#') e.preventDefault();
                console.log('Interaction detected on:', el.textContent.trim());
            });
        });
    </script>
</body></html>

<!-- Análisis Predictivo e Insights de IA -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Análisis Predictivo de Ventas - SIGA</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .glass-card {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(8px);
            border: 1px solid rgba(229, 238, 255, 0.5);
        }
        .ai-shimmer {
            background: linear-gradient(90deg, transparent, rgba(0, 149, 121, 0.1), transparent);
            background-size: 200% 100%;
            animation: shimmer 3s infinite;
        }
        @keyframes shimmer {
            0% { background-position: -200% 0; }
            100% { background-position: 200% 0; }
        }
        .custom-scrollbar::-webkit-scrollbar {
            width: 4px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
            background: #f1f1f1;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
            background: #009579;
            border-radius: 10px;
        }
    </style>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "on-tertiary": "#ffffff",
                        "outline-variant": "#c7c5d3",
                        "on-error-container": "#93000a",
                        "background": "#fdf7ff",
                        "on-secondary-fixed-variant": "#004e5f",
                        "inverse-primary": "#bfc2ff",
                        "on-background": "#1d1b21",
                        "secondary-container": "#50d9fe",
                        "surface-tint": "#F0F9FF",
                        "on-primary-fixed-variant": "#393e8c",
                        "on-tertiary-fixed": "#002018",
                        "on-error": "#ffffff",
                        "on-tertiary-fixed-variant": "#005140",
                        "on-primary-fixed": "#070a61",
                        "tertiary-fixed": "#79f8d5",
                        "on-surface": "#1d1b21",
                        "surface-container-highest": "#e6e0e9",
                        "primary-container": "#070a61",
                        "primary-fixed": "#e0e0ff",
                        "success-vibrant": "#10B981",
                        "secondary-fixed-dim": "#4cd6fb",
                        "on-secondary": "#ffffff",
                        "outline": "#777682",
                        "surface-dim": "#ded8e1",
                        "surface-variant": "#e6e0e9",
                        "secondary": "#00677d",
                        "tertiary": "#000000",
                        "surface-container-low": "#f8f1fa",
                        "inverse-surface": "#322f36",
                        "error-container": "#ffdad6",
                        "tertiary-container": "#002018",
                        "on-primary": "#ffffff",
                        "surface-bright": "#fdf7ff",
                        "on-primary-container": "#777dcf",
                        "surface-container-high": "#ece6ef",
                        "error": "#ba1a1a",
                        "on-surface-variant": "#464651",
                        "primary-fixed-dim": "#bfc2ff",
                        "surface-container": "#f2ecf5",
                        "tertiary-fixed-dim": "#5adcb9",
                        "primary": "#000000",
                        "on-tertiary-container": "#009579",
                        "surface-container-lowest": "#ffffff",
                        "on-secondary-container": "#005c70",
                        "inverse-on-surface": "#f5eff7",
                        "on-secondary-fixed": "#001f27",
                        "border-muted": "#D9D9D9",
                        "secondary-fixed": "#b3ebff",
                        "surface": "#fdf7ff",
                        "sidebar-width": "260px"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "sidebar-collapsed": "80px",
                        "gutter": "24px",
                        "sidebar-width": "260px",
                        "row-padding": "16px",
                        "container-max": "1440px",
                        "card-padding": "20px",
                        "margin-mobile": "16px",
                        "base": "8px",
                        "margin-desktop": "32px"
                    },
                    "fontFamily": {
                        "body-lg": ["Hanken Grotesk"],
                        "label-caps": ["JetBrains Mono"],
                        "headline-sm": ["Hanken Grotesk"],
                        "headline-md-mobile": ["Hanken Grotesk"],
                        "body-md": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "headline-md": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                        "label-caps": ["12px", {"lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500"}],
                        "headline-sm": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "headline-md-mobile": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "body-md": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                        "display-lg": ["40px", {"lineHeight": "48px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                        "headline-md": ["24px", {"lineHeight": "32px", "letterSpacing": "-0.01em", "fontWeight": "600"}]
                    }
                },
            },
        }
    </script>
</head>
<body class="bg-background font-body-md text-on-surface">
<!-- Top Navigation Bar -->
<header class="fixed top-0 w-full z-40 bg-surface shadow-sm flex justify-between items-center h-16 px-gutter border-b border-outline-variant">
<div class="flex items-center gap-8">
<img alt="SIGA Logo" class="h-10 w-auto object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
<nav class="hidden md:flex gap-6">
<a class="font-headline-sm text-headline-sm text-on-surface-variant hover:bg-surface-container-low transition-colors px-3 py-1 rounded-lg" href="#">Dashboard</a>
<a class="font-headline-sm text-headline-sm text-on-tertiary-container border-b-2 border-on-tertiary-container px-3 py-1" href="#">Analytics</a>
<a class="font-headline-sm text-headline-sm text-on-surface-variant hover:bg-surface-container-low transition-colors px-3 py-1 rounded-lg" href="#">Inventory</a>
</nav>
</div>
<div class="flex items-center gap-4">
<button class="p-2 rounded-full hover:bg-surface-container-low transition-colors">
<span class="material-symbols-outlined" data-icon="smart_toy">smart_toy</span>
</button>
<button class="p-2 rounded-full hover:bg-surface-container-low transition-colors">
<span class="material-symbols-outlined" data-icon="notifications">notifications</span>
</button>
<button class="p-2 rounded-full hover:bg-surface-container-low transition-colors">
<span class="material-symbols-outlined" data-icon="settings">settings</span>
</button>
<div class="w-10 h-10 rounded-full overflow-hidden bg-surface-container ml-2">
<img alt="User Profile" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBx8D6E94T4t5Ye2lF-Lw6F7Qpn77uwq4e9lX-UT53vNdR6OH-yJAiGeOYejEvXScIi653rX8tm2j4X5VeN0fCMJHmN_VsJt2Zc_Wqeunqvk-4h60PEED2r9FQyDLqZprXXJVXnPdHxsUVFR1XwaNkdLIp8axzw4hzRcOD5tdfsPXr1A9E9spccY_wJ_1jgtwXdnr9Mp80vhN6irTIsSNmeHnw6eUu4s1Mi5xmJmlvz0hoMmjNNzolpQUWijkUevDT7d_ZvWzngs7lJ"/>
</div>
</div>
</header>
<!-- Side Navigation Bar -->
<aside class="fixed left-0 top-0 h-full w-sidebar-width z-50 bg-surface-container-lowest border-r border-outline-variant flex flex-col shadow-sm pt-16">
<div class="p-6 flex flex-col gap-1">
<div class="flex items-center gap-3 mb-6">
<div class="w-10 h-10 bg-on-tertiary-container/10 rounded-lg flex items-center justify-center text-on-tertiary-container">
<img alt="S" class="w-6 h-6" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
</div>
<div>
<h2 class="font-headline-sm text-headline-sm font-bold text-on-surface">SIGA Core</h2>
<p class="text-[10px] text-on-surface-variant opacity-70">v3.1 Predictive Agent</p>
</div>
</div>
<nav class="flex flex-col gap-1">
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all active:scale-95" href="#">
<span class="material-symbols-outlined" data-icon="dashboard">dashboard</span>
<span>Overview</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all active:scale-95" href="#">
<span class="material-symbols-outlined" data-icon="inventory_2">inventory_2</span>
<span>Inventory</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all active:scale-95" href="#">
<span class="material-symbols-outlined" data-icon="point_of_sale">point_of_sale</span>
<span>POS</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all active:scale-95" href="#">
<span class="material-symbols-outlined" data-icon="support_agent">support_agent</span>
<span>Agents</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 bg-on-tertiary-container/5 text-on-tertiary-container border-l-4 border-on-tertiary-container transition-all active:scale-95" href="#">
<span class="material-symbols-outlined" data-icon="analytics">analytics</span>
<span>Reports</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all active:scale-95" href="#">
<span class="material-symbols-outlined" data-icon="forum">forum</span>
<span>AI Chat</span>
</a>
</nav>
</div>
<div class="mt-auto p-4 flex flex-col gap-2">
<button class="w-full py-3 bg-on-tertiary-container text-on-tertiary rounded-xl font-bold flex items-center justify-center gap-2 shadow-md active:scale-95 transition-transform">
<span class="material-symbols-outlined" data-icon="add_circle">add_circle</span>
                New Task
            </button>
<div class="border-t border-outline-variant my-4"></div>
<a class="flex items-center gap-3 px-4 py-2 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined" data-icon="help">help</span>
<span>Help</span>
</a>
<a class="flex items-center gap-3 px-4 py-2 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined" data-icon="logout">logout</span>
<span>Logout</span>
</a>
</div>
</aside>
<!-- Main Content -->
<main class="pl-[260px] pt-16 min-h-screen">
<div class="p-gutter max-w-container-max mx-auto">
<!-- Header Section -->
<div class="flex justify-between items-end mb-8">
<div>
<h2 class="font-display-lg text-display-lg text-primary">Análisis Predictivo de Ventas</h2>
<p class="text-on-surface-variant font-body-lg">Proyecciones inteligentes y gestión automatizada de suministros impulsada por la inteligencia de SIGA.</p>
</div>
<div class="flex gap-3">
<button class="flex items-center gap-2 px-4 py-2 rounded-lg bg-surface shadow-sm text-on-surface-variant border border-outline-variant hover:bg-surface-container-low transition-all">
<span class="material-symbols-outlined" data-icon="calendar_today">calendar_today</span>
<span>Próximos 30 días</span>
</button>
<button class="flex items-center gap-2 px-4 py-2 rounded-lg bg-on-tertiary-container text-on-tertiary shadow-lg hover:opacity-90 transition-all">
<span class="material-symbols-outlined" data-icon="refresh">refresh</span>
<span>Recalcular Modelos</span>
</button>
</div>
</div>
<!-- Bento Grid Layout -->
<div class="grid grid-cols-12 gap-6">
<!-- Time Series Chart (Main) -->
<div class="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl p-card-padding shadow-sm relative overflow-hidden border border-outline-variant/30">
<div class="flex justify-between items-center mb-6">
<h3 class="font-headline-sm text-headline-sm flex items-center gap-2">
<span class="material-symbols-outlined text-on-tertiary-container" data-icon="trending_up">trending_up</span>
                            Proyección de Demanda vs. Ventas Históricas
                        </h3>
<div class="flex items-center gap-4 text-xs font-label-caps">
<span class="flex items-center gap-1"><span class="w-3 h-3 rounded-full bg-on-tertiary-container"></span> Histórico</span>
<span class="flex items-center gap-1"><span class="w-3 h-3 rounded-full bg-on-tertiary-container/30"></span> Predictivo</span>
</div>
</div>
<!-- Simplified Visual Graph Placeholder -->
<div class="h-64 w-full flex items-end gap-2 px-4 relative">
<div class="absolute inset-0 flex flex-col justify-between opacity-5 pointer-events-none">
<div class="border-b border-on-surface w-full"></div>
<div class="border-b border-on-surface w-full"></div>
<div class="border-b border-on-surface w-full"></div>
<div class="border-b border-on-surface w-full"></div>
</div>
<!-- Generating Bars - Teal/Cyan Palette -->
<div class="flex-1 bg-on-tertiary-container/20 h-[40%] rounded-t-sm"></div>
<div class="flex-1 bg-on-tertiary-container/30 h-[45%] rounded-t-sm"></div>
<div class="flex-1 bg-on-tertiary-container/25 h-[38%] rounded-t-sm"></div>
<div class="flex-1 bg-on-tertiary-container/40 h-[55%] rounded-t-sm"></div>
<div class="flex-1 bg-on-tertiary-container/50 h-[65%] rounded-t-sm"></div>
<div class="flex-1 bg-on-tertiary-container/60 h-[75%] rounded-t-sm"></div>
<!-- Transition to Prediction -->
<div class="flex-1 bg-on-tertiary-container/10 h-[80%] rounded-t-sm border-t-2 border-dashed border-on-tertiary-container"></div>
<div class="flex-1 bg-on-tertiary-container/10 h-[85%] rounded-t-sm border-t-2 border-dashed border-on-tertiary-container"></div>
<div class="flex-1 bg-on-tertiary-container/15 h-[90%] rounded-t-sm border-t-2 border-dashed border-on-tertiary-container"></div>
<div class="flex-1 bg-on-tertiary-container/20 h-[82%] rounded-t-sm border-t-2 border-dashed border-on-tertiary-container"></div>
<div class="flex-1 bg-on-tertiary-container/25 h-[78%] rounded-t-sm border-t-2 border-dashed border-on-tertiary-container"></div>
<div class="flex-1 bg-on-tertiary-container/30 h-[95%] rounded-t-sm border-t-2 border-dashed border-on-tertiary-container"></div>
</div>
<div class="flex justify-between mt-4 px-4 text-[10px] font-label-caps text-on-surface-variant">
<span>ENE</span><span>FEB</span><span>MAR</span><span>ABR</span><span>MAY</span><span>JUN</span>
<span class="text-on-tertiary-container font-bold">HOY</span>
<span>JUL</span><span>AGO</span><span>SEP</span><span>OCT</span><span>NOV</span><span>DIC</span>
</div>
</div>
<!-- Agent Insights (Side) -->
<div class="col-span-12 lg:col-span-4 flex flex-col gap-6">
<div class="bg-primary-container text-on-primary rounded-xl p-card-padding shadow-md flex-1 relative overflow-hidden">
<div class="ai-shimmer absolute inset-0 pointer-events-none"></div>
<div class="relative z-10">
<div class="flex items-center gap-3 mb-4">
<img alt="SIGA AI" class="w-8 h-8 object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
<h3 class="font-headline-sm text-headline-sm">Insights de SIGA</h3>
</div>
<div class="space-y-4">
<div class="bg-white/5 p-3 rounded-lg border border-white/10">
<p class="text-body-md text-on-primary-container italic font-medium leading-relaxed">
                                        "He detectado un patrón de demanda atípico en la categoría 'Electrónica'. Se recomienda aumentar el stock en un 15% para el próximo trimestre."
                                    </p>
</div>
<div class="flex flex-col gap-3">
<div class="flex items-start gap-3">
<span class="material-symbols-outlined text-secondary-fixed text-sm mt-1" data-icon="bolt">bolt</span>
<div>
<p class="text-sm font-bold">Optimización de Pedidos</p>
<p class="text-xs text-on-primary-container">Consolida envíos de 3 proveedores para ahorrar 12% en logística.</p>
</div>
</div>
<div class="flex items-start gap-3">
<span class="material-symbols-outlined text-secondary-fixed text-sm mt-1" data-icon="warning">warning</span>
<div>
<p class="text-sm font-bold">Riesgo de Rotura</p>
<p class="text-xs text-on-primary-container">SKU-9042 tiene un 85% de probabilidad de agotarse en 4 días.</p>
</div>
</div>
</div>
</div>
</div>
</div>
</div>
<!-- Stock Crítico Section -->
<div class="col-span-12 bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col border border-outline-variant/30">
<div class="p-card-padding flex justify-between items-center border-b border-outline-variant/30">
<div class="flex items-center gap-3">
<span class="material-symbols-outlined text-error" data-icon="inventory" style="font-variation-settings: 'FILL' 1;">inventory</span>
<h3 class="font-headline-sm text-headline-sm">Stock Crítico &amp; Sugerencias de Reposición</h3>
</div>
<button class="text-on-tertiary-container font-bold text-sm hover:underline flex items-center gap-1">
                            Ver todo el inventario
                            <span class="material-symbols-outlined text-sm" data-icon="arrow_forward">arrow_forward</span>
</button>
</div>
<div class="overflow-x-auto">
<table class="w-full text-left">
<thead>
<tr class="bg-surface-container-low text-[11px] font-label-caps text-on-surface-variant uppercase tracking-wider">
<th class="px-gutter py-4">Producto / SKU</th>
<th class="px-gutter py-4 text-center">Stock Actual</th>
<th class="px-gutter py-4 text-center">Tasa de Venta (Sem)</th>
<th class="px-gutter py-4 text-center">Sugerencia IA</th>
<th class="px-gutter py-4 text-right">Acción Recomendada</th>
</tr>
</thead>
<tbody class="divide-y divide-outline-variant/20">
<tr class="hover:bg-surface-container-low transition-colors group">
<td class="px-gutter py-row-padding">
<div class="flex items-center gap-4">
<div class="w-12 h-12 rounded-lg bg-surface overflow-hidden border border-outline-variant/30">
<img class="w-full h-full object-cover" data-alt="Product" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBcJWDRiqIuInXRgE5gYP_ZON07qO8wz4AtXcbVUDovNY66zieU3bguaVoSk-lEvapnKgSokxzfciJy048h68GJJ_NiP9mfocdjeBpMKFO8WncdhydrkUutBlYkQm6fbczKu8fTV-zRmcyJG-IBflACNFvrV9uPrdPa133ytO-tymOVE5xMFhs1fgeudXR_CXYio2p8Vo5z5moaRlPjN3zJa14q5UEtgXTxJAqZq_FZycrRVN9UhgzPFtonwd5Y_1obqGMDspuWSttQ"/>
</div>
<div>
<p class="font-bold text-on-surface">Nexus Keyboard MK-2</p>
<p class="text-xs text-on-surface-variant">ID: NX-889021</p>
</div>
</div>
</td>
<td class="px-gutter py-row-padding text-center">
<span class="px-3 py-1 rounded-full bg-error-container text-on-error-container text-xs font-bold">12 u.</span>
</td>
<td class="px-gutter py-row-padding text-center text-on-surface-variant">
                                        45 u. <span class="text-on-tertiary-container text-[10px] font-bold">↑ 8%</span>
</td>
<td class="px-gutter py-row-padding text-center">
<span class="text-on-tertiary-container font-bold">+150 u.</span>
</td>
<td class="px-gutter py-row-padding text-right">
<button class="px-4 py-2 bg-on-tertiary-container text-on-tertiary rounded-lg text-xs font-bold hover:opacity-90 active:scale-95 transition-all shadow-sm">
                                            Auto-Reponer
                                        </button>
</td>
</tr>
<tr class="hover:bg-surface-container-low transition-colors group">
<td class="px-gutter py-row-padding">
<div class="flex items-center gap-4">
<div class="w-12 h-12 rounded-lg bg-surface overflow-hidden border border-outline-variant/30">
<img class="w-full h-full object-cover" data-alt="Product" src="https://lh3.googleusercontent.com/aida-public/AB6AXuB2oV71rC8ucLx2GcPNKG7NwwPEKxkrjscNfpQ6JKmYJ3qwPAbvMBj79E0NS9BtyE7uHmMZTlSJSHYq9Bgir7WSTZFiryV0uD0Dgldkxr2iySgHaM2xfWmwBrIv7irOOJzJlj6evWFZQT9fK6jGvahLGegiZ0LIZTlW7RFEdPNWA6jez5LNpK7jqlHkYVaM-p-L6G6WCJLwZa1K7MbTdhwZr3j4QZXR7o78UOz9AsbBFMBgJs0auOASpU_L_jMApc7KA_aINoEEuMhA"/>
</div>
<div>
<p class="font-bold text-on-surface">Studio Wireless X-5</p>
<p class="text-xs text-on-surface-variant">ID: NX-772102</p>
</div>
</div>
</td>
<td class="px-gutter py-row-padding text-center">
<span class="px-3 py-1 rounded-full bg-secondary-fixed text-on-secondary-fixed text-xs font-bold">5 u.</span>
</td>
<td class="px-gutter py-row-padding text-center text-on-surface-variant">
                                        12 u. <span class="text-on-error-container text-[10px] font-bold">↓ 3%</span>
</td>
<td class="px-gutter py-row-padding text-center">
<span class="text-on-tertiary-container font-bold">+40 u.</span>
</td>
<td class="px-gutter py-row-padding text-right">
<button class="px-4 py-2 bg-on-tertiary-container text-on-tertiary rounded-lg text-xs font-bold hover:opacity-90 active:scale-95 transition-all shadow-sm">
                                            Auto-Reponer
                                        </button>
</td>
</tr>
</tbody>
</table>
</div>
</div>
<!-- Secondary Analysis Cards -->
<div class="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl p-card-padding shadow-sm border border-outline-variant/30">
<div class="flex justify-between items-start mb-4">
<div>
<p class="text-on-surface-variant text-xs font-label-caps mb-1">Confianza del Modelo</p>
<h4 class="text-headline-sm font-headline-sm font-bold">98.4%</h4>
</div>
<div class="w-10 h-10 bg-on-tertiary-container/10 text-on-tertiary-container rounded-full flex items-center justify-center">
<span class="material-symbols-outlined" data-icon="verified">verified</span>
</div>
</div>
<div class="w-full bg-surface-container rounded-full h-2 overflow-hidden">
<div class="bg-on-tertiary-container h-full" style="width: 98.4%"></div>
</div>
<p class="text-[10px] mt-2 text-on-surface-variant italic">Basado en datos de SIGA de los últimos 24 meses.</p>
</div>
<div class="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl p-card-padding shadow-sm border-l-4 border-on-tertiary-container border border-outline-variant/30">
<div class="flex justify-between items-start mb-4">
<div>
<p class="text-on-surface-variant text-xs font-label-caps mb-1">Impacto Financiero</p>
<h4 class="text-headline-sm font-headline-sm font-bold text-on-tertiary-container">+$12,450</h4>
</div>
<div class="w-10 h-10 bg-on-tertiary-container/10 text-on-tertiary-container rounded-full flex items-center justify-center">
<span class="material-symbols-outlined" data-icon="payments">payments</span>
</div>
</div>
<p class="text-body-md text-on-surface-variant">Ahorro proyectado por optimización de inventario este mes.</p>
</div>
<div class="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl p-card-padding shadow-sm border border-outline-variant/30">
<div class="flex justify-between items-start mb-4">
<div>
<p class="text-on-surface-variant text-xs font-label-caps mb-1">Alertas IA activas</p>
<h4 class="text-headline-sm font-headline-sm font-bold text-on-error-container">03</h4>
</div>
<div class="w-10 h-10 bg-error-container/20 text-error rounded-full flex items-center justify-center">
<span class="material-symbols-outlined" data-icon="notification_important">notification_important</span>
</div>
</div>
<div class="flex gap-2">
<span class="w-2 h-2 rounded-full bg-error animate-pulse"></span>
<span class="w-2 h-2 rounded-full bg-error opacity-50"></span>
<span class="w-2 h-2 rounded-full bg-error opacity-20"></span>
</div>
</div>
</div>
</div>
</main>
<!-- Floating Action Button (Contextual) -->
<button class="fixed bottom-8 right-8 w-14 h-14 bg-on-tertiary-container text-on-tertiary rounded-full shadow-2xl flex items-center justify-center hover:scale-110 active:scale-95 transition-all z-50 group">
<img alt="S" class="w-7 h-7" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
<span class="absolute right-16 bg-primary-container text-on-primary px-3 py-1 rounded-lg text-xs whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
            Hablar con SIGA AI
        </span>
</button>
<script>
        // Micro-interactions for table rows
        document.querySelectorAll('tr').forEach(row => {
            row.addEventListener('mouseenter', () => {
                row.classList.add('shadow-md');
            });
            row.addEventListener('mouseleave', () => {
                row.classList.remove('shadow-md');
            });
        });

        // Simple animation for the "AI Insight" shimmer
        const insightCard = document.querySelector('.ai-shimmer');
        if (insightCard) {
            setInterval(() => {
                insightCard.style.opacity = (Math.random() * 0.15 + 0.05).toString();
            }, 3000);
        }
    </script>
</body></html>

<!-- Ingreso de Productos Asistido (A2UI) -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&amp;family=JetBrains+Mono:wght@500&amp;family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .glass-panel {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
        }
        .scan-animation {
            background: linear-gradient(to bottom, transparent 0%, #009579 50%, transparent 100%);
            height: 2px;
            width: 100%;
            position: absolute;
            animation: scan 3s infinite linear;
        }
        @keyframes scan {
            0% { top: 0%; opacity: 0; }
            10% { opacity: 1; }
            90% { opacity: 1; }
            100% { top: 100%; opacity: 0; }
        }
        .pulse-teal {
            animation: pulse-teal 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
        }
        @keyframes pulse-teal {
            0%, 100% { opacity: 1; transform: scale(1); }
            50% { opacity: .7; transform: scale(1.05); }
        }
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background: #c7c5d3; border-radius: 10px; }
    </style>
<script id="tailwind-config">
        tailwind.config = {
          darkMode: "class",
          theme: {
            extend: {
              "colors": {
                "on-tertiary": "#ffffff",
                "outline-variant": "#c7c5d3",
                "on-error-container": "#93000a",
                "background": "#fdf7ff",
                "on-secondary-fixed-variant": "#004e5f",
                "inverse-primary": "#bfc2ff",
                "on-background": "#1d1b21",
                "secondary-container": "#50d9fe",
                "surface-tint": "#F0F9FF",
                "on-primary-fixed-variant": "#393e8c",
                "on-tertiary-fixed": "#002018",
                "on-error": "#ffffff",
                "on-tertiary-fixed-variant": "#005140",
                "on-primary-fixed": "#070a61",
                "tertiary-fixed": "#79f8d5",
                "on-surface": "#1d1b21",
                "surface-container-highest": "#e6e0e9",
                "primary-container": "#070a61",
                "primary-fixed": "#e0e0ff",
                "success-vibrant": "#10B981",
                "secondary-fixed-dim": "#4cd6fb",
                "on-secondary": "#ffffff",
                "outline": "#777682",
                "surface-dim": "#ded8e1",
                "surface-variant": "#e6e0e9",
                "secondary": "#00677d",
                "tertiary": "#000000",
                "surface-container-low": "#f8f1fa",
                "inverse-surface": "#322f36",
                "error-container": "#ffdad6",
                "tertiary-container": "#002018",
                "on-primary": "#ffffff",
                "surface-bright": "#fdf7ff",
                "on-primary-container": "#777dcf",
                "surface-container-high": "#ece6ef",
                "error": "#ba1a1a",
                "on-surface-variant": "#464651",
                "primary-fixed-dim": "#bfc2ff",
                "surface-container": "#f2ecf5",
                "tertiary-fixed-dim": "#5adcb9",
                "primary": "#000000",
                "on-tertiary-container": "#009579",
                "surface-container-lowest": "#ffffff",
                "on-secondary-container": "#005c70",
                "inverse-on-surface": "#f5eff7",
                "on-secondary-fixed": "#001f27",
                "border-muted": "#D9D9D9",
                "secondary-fixed": "#b3ebff",
                "surface": "#fdf7ff"
              },
              "borderRadius": {
                "DEFAULT": "0.25rem",
                "lg": "0.5rem",
                "xl": "0.75rem",
                "full": "9999px"
              },
              "spacing": {
                "sidebar-width": "260px",
                "gutter": "24px",
                "container-max": "1280px"
              },
              "fontFamily": {
                "body-sm": ["Hanken Grotesk"],
                "code-sm": ["JetBrains Mono"],
                "headline-lg-mobile": ["Hanken Grotesk"],
                "title-md": ["Hanken Grotesk"],
                "display-lg": ["Hanken Grotesk"],
                "headline-lg": ["Hanken Grotesk"],
                "body-lg": ["Hanken Grotesk"],
                "label-md": ["Hanken Grotesk"]
              },
              "fontSize": {
                "body-sm": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                "code-sm": ["13px", {"lineHeight": "18px", "fontWeight": "400"}],
                "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "600"}],
                "title-md": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                "display-lg": ["56px", {"lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                "label-md": ["14px", {"lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500"}]
              }
            },
          },
        }
    </script>
</head>
<body class="bg-background font-body-lg text-on-background min-h-screen overflow-hidden">
<!-- Top Navigation -->
<header class="fixed top-0 w-full z-40 bg-surface shadow-sm flex justify-between items-center h-16 px-gutter border-b border-outline-variant">
<div class="flex items-center gap-4">
<img alt="SIGA Logo" class="h-8 w-auto" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
<span class="bg-surface-container-high px-2 py-1 rounded-lg text-label-md text-on-surface-variant">V2.4 SIGA AGENT</span>
</div>
<div class="flex items-center gap-6">
<div class="flex items-center gap-2 text-on-surface-variant font-body-sm">
<span class="material-symbols-outlined">analytics</span>
<span>Análisis de Red</span>
</div>
<div class="flex items-center gap-2">
<button class="p-2 hover:bg-surface-container-low rounded-full transition-colors">
<span class="material-symbols-outlined">notifications</span>
</button>
<img alt="User Profile" class="w-8 h-8 rounded-full border-2 border-outline-variant" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAk0GdSB7wFHCjlerMjxOibG6tnnHHgMDVE3rzyG6Ftv3ZcPWT2JZZGqRf-oyW1hMw0GxKkyJapvZY7rHJT5q-lp-udu756smjSb_4phm3Knp6VWZdwo0UJE4E_35iH_2A77WKXyqtudDXieY3zfaLMTjLD-GmZNLYLwNoVsG6LhtGqt-8EZPctKqbqfomZAnUMT5zXBzhF4AykbmjFN6PLHlg4vYSrWPCT7yTLB2M-l1Eue7fCn3aUjJfd7IDVN_4GsOGO_AFvlDjk"/>
</div>
</div>
</header>
<!-- Side Navigation -->
<aside class="fixed left-0 top-0 h-full w-sidebar-width z-50 bg-surface-container-lowest shadow-md flex flex-col border-r border-outline-variant">
<div class="p-6 flex flex-col gap-1">
<img alt="SIGA Logo" class="h-10 w-auto self-start mb-2" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
<p class="text-on-surface-variant text-[10px] font-bold tracking-widest uppercase">SISTEMA INTELIGENTE</p>
</div>
<nav class="flex-1 px-4 mt-4 space-y-1">
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all rounded-lg" href="#">
<span class="material-symbols-outlined">dashboard</span>
<span class="text-label-md">Overview</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 bg-on-tertiary-container/5 text-on-tertiary-container border-l-4 border-on-tertiary-container font-semibold" href="#">
<span class="material-symbols-outlined">inventory_2</span>
<span class="text-label-md">Inventory</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all rounded-lg" href="#">
<span class="material-symbols-outlined">point_of_sale</span>
<span class="text-label-md">POS</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all rounded-lg" href="#">
<span class="material-symbols-outlined">support_agent</span>
<span class="text-label-md">Agents</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all rounded-lg" href="#">
<span class="material-symbols-outlined">analytics</span>
<span class="text-label-md">Reports</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all rounded-lg" href="#">
<span class="material-symbols-outlined">forum</span>
<span class="text-label-md">AI Chat</span>
</a>
</nav>
<div class="p-6">
<button class="w-full bg-on-tertiary-container text-white py-3 px-4 rounded-xl font-bold flex items-center justify-center gap-2 hover:shadow-lg transition-all active:scale-95">
<span class="material-symbols-outlined">add</span>
<span>New Task</span>
</button>
</div>
</aside>
<!-- Main Canvas -->
<main class="ml-sidebar-width pt-16 h-screen overflow-hidden flex flex-col">
<!-- Header Interface -->
<div class="px-gutter py-6 flex justify-between items-center bg-surface/50 backdrop-blur-sm border-b border-outline-variant/30">
<div>
<h1 class="font-headline-lg text-headline-lg text-on-surface">Ingreso Asistido por Agente</h1>
<p class="text-on-surface-variant font-body-lg">Sube una factura o usa comandos de voz para actualizar existencias.</p>
</div>
<div class="flex gap-3">
<button class="px-4 py-2 border border-outline text-on-surface rounded-xl font-medium hover:bg-surface-container-low transition-colors">Cancelar</button>
<button class="px-6 py-2 bg-on-tertiary-container text-white rounded-xl font-bold shadow-md hover:shadow-lg transition-all">Confirmar Ingreso</button>
</div>
</div>
<!-- Interactive Core Layout -->
<div class="flex-1 flex overflow-hidden p-6 gap-6">
<!-- Left Panel: Data Extraction & Voice -->
<div class="w-2/5 flex flex-col gap-6 overflow-hidden">
<!-- Dropzone -->
<div class="relative flex-1 bg-surface-container-lowest rounded-2xl shadow-sm border-2 border-dashed border-outline-variant flex flex-col items-center justify-center p-8 transition-all hover:border-on-tertiary-container group">
<div class="scan-animation"></div>
<div class="flex flex-col items-center text-center space-y-4">
<div class="w-16 h-16 bg-surface-container rounded-full flex items-center justify-center text-on-tertiary-container group-hover:scale-110 transition-transform">
<span class="material-symbols-outlined text-4xl">cloud_upload</span>
</div>
<div>
<h3 class="font-title-md text-title-md">Cargar Factura</h3>
<p class="text-on-surface-variant text-body-sm">Arrastra un PDF, JPG o PNG para procesar</p>
</div>
<button class="mt-2 text-on-tertiary-container font-bold text-body-sm underline decoration-2 underline-offset-4">Explorar archivos</button>
</div>
</div>
<!-- Voice/Agent Feed -->
<div class="h-48 bg-inverse-surface rounded-2xl p-6 flex flex-col justify-between text-white shadow-xl relative overflow-hidden border border-on-tertiary-container/30">
<div class="flex justify-between items-start">
<div class="flex gap-2 items-center">
<div class="w-2.5 h-2.5 bg-tertiary-fixed rounded-full pulse-teal"></div>
<span class="text-xs uppercase tracking-widest text-tertiary-fixed font-bold">SIGA Voice Active</span>
</div>
<span class="material-symbols-outlined text-tertiary-fixed/50">waves</span>
</div>
<div class="text-body-lg italic opacity-90 border-l-4 border-tertiary-fixed pl-4">
                    "Agregando 50 unidades de Aceite Sintético 5W30 a la sucursal Norte..."
                </div>
<div class="flex justify-center">
<button class="w-16 h-16 rounded-full bg-tertiary-fixed flex items-center justify-center hover:scale-110 transition-all shadow-[0_0_20px_rgba(121,248,213,0.3)] group relative overflow-hidden" id="siga-mic">
<img alt="S" class="w-8 h-8 object-contain z-10" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
<div class="absolute inset-0 bg-white/20 scale-0 group-hover:scale-100 transition-transform rounded-full"></div>
</button>
</div>
<!-- Ambient Effect -->
<div class="absolute -right-10 -bottom-10 w-40 h-40 bg-on-tertiary-container/40 rounded-full blur-3xl"></div>
</div>
</div>
<!-- Right Panel: Validation -->
<div class="flex-1 bg-surface-container-lowest rounded-2xl shadow-sm flex flex-col border border-outline-variant/30 overflow-hidden">
<div class="p-5 border-b border-outline-variant/30 flex justify-between items-center bg-surface-container-low">
<div class="flex items-center gap-3">
<span class="material-symbols-outlined text-on-tertiary-container">verified</span>
<h2 class="font-title-md text-title-md">Validación de Datos Extraídos</h2>
</div>
<div class="flex items-center gap-2">
<span class="text-body-sm text-on-surface-variant">Items detectados:</span>
<span class="bg-on-tertiary-container/10 text-on-tertiary-container px-3 py-1 rounded-full font-bold text-sm">12</span>
</div>
</div>
<!-- Preview List -->
<div class="flex-1 overflow-y-auto">
<table class="w-full text-left">
<thead class="sticky top-0 bg-surface-container-high text-label-md text-on-surface-variant z-10">
<tr>
<th class="p-4 uppercase tracking-wider">Producto</th>
<th class="p-4 uppercase tracking-wider text-center">Cantidad</th>
<th class="p-4 uppercase tracking-wider">Costo Unit.</th>
<th class="p-4 uppercase tracking-wider">Acciones</th>
</tr>
</thead>
<tbody class="divide-y divide-outline-variant/20">
<tr class="hover:bg-surface-container-low transition-colors">
<td class="p-4">
<div class="flex flex-col">
<span class="font-semibold text-on-surface">Aceite Sintético 5W30</span>
<span class="text-xs text-on-surface-variant font-mono">REF: MO-00982</span>
</div>
</td>
<td class="p-4 text-center">
<div class="flex items-center justify-center gap-2">
<input class="w-16 h-9 border border-outline-variant rounded-lg text-center font-bold focus:ring-2 focus:ring-on-tertiary-container/20 focus:border-on-tertiary-container transition-all" type="text" value="50"/>
<span class="text-on-tertiary-container font-bold">+</span>
</div>
</td>
<td class="p-4 font-mono font-medium">$12.50</td>
<td class="p-4">
<div class="flex gap-2">
<button class="p-2 text-on-tertiary-fixed-variant bg-tertiary-fixed/30 rounded-lg hover:bg-tertiary-fixed transition-colors">
<span class="material-symbols-outlined text-sm">check</span>
</button>
<button class="p-2 text-error bg-error-container/40 rounded-lg hover:bg-error-container transition-colors">
<span class="material-symbols-outlined text-sm">delete</span>
</button>
</div>
</td>
</tr>
<tr class="hover:bg-surface-container-low transition-colors bg-on-tertiary-container/5">
<td class="p-4">
<div class="flex flex-col">
<span class="font-semibold text-on-surface">Filtro de Aire Premium</span>
<span class="text-xs text-on-surface-variant font-mono">REF: AF-2201</span>
</div>
</td>
<td class="p-4 text-center">
<div class="flex items-center justify-center gap-2">
<input class="w-16 h-9 border border-outline-variant rounded-lg text-center font-bold focus:ring-2 focus:ring-on-tertiary-container/20 focus:border-on-tertiary-container transition-all" type="text" value="12"/>
<span class="text-on-tertiary-container font-bold">+</span>
</div>
</td>
<td class="p-4 font-mono font-medium">$8.90</td>
<td class="p-4">
<div class="flex gap-2">
<button class="p-2 text-on-tertiary-fixed-variant bg-tertiary-fixed/30 rounded-lg hover:bg-tertiary-fixed transition-colors">
<span class="material-symbols-outlined text-sm">check</span>
</button>
<button class="p-2 text-error bg-error-container/40 rounded-lg hover:bg-error-container transition-colors">
<span class="material-symbols-outlined text-sm">delete</span>
</button>
</div>
</td>
</tr>
<tr class="hover:bg-surface-container-low transition-colors">
<td class="p-4">
<div class="flex flex-col">
<span class="font-semibold text-on-surface">Pastillas Freno Delantero</span>
<span class="text-xs text-on-surface-variant font-mono">REF: BP-5541</span>
</div>
</td>
<td class="p-4 text-center">
<div class="flex items-center justify-center gap-2">
<input class="w-16 h-9 border border-outline-variant rounded-lg text-center font-bold focus:ring-2 focus:ring-on-tertiary-container/20 focus:border-on-tertiary-container transition-all" type="text" value="8"/>
<span class="text-on-tertiary-container font-bold">+</span>
</div>
</td>
<td class="p-4 font-mono font-medium">$45.00</td>
<td class="p-4">
<div class="flex gap-2">
<button class="p-2 text-on-tertiary-fixed-variant bg-tertiary-fixed/30 rounded-lg hover:bg-tertiary-fixed transition-colors">
<span class="material-symbols-outlined text-sm">check</span>
</button>
<button class="p-2 text-error bg-error-container/40 rounded-lg hover:bg-error-container transition-colors">
<span class="material-symbols-outlined text-sm">delete</span>
</button>
</div>
</td>
</tr>
</tbody>
</table>
</div>
<!-- Footer Summary -->
<div class="p-6 bg-surface-container-low border-t border-outline-variant/30 grid grid-cols-3 gap-6">
<div class="flex flex-col">
<span class="text-label-md text-on-surface-variant uppercase">Proveedor Detectado</span>
<span class="font-bold text-on-surface">Distribuidora Autopartes Global</span>
</div>
<div class="flex flex-col">
<span class="text-label-md text-on-surface-variant uppercase">IVA (19%)</span>
<span class="font-mono text-on-surface">$234.50</span>
</div>
<div class="flex flex-col text-right">
<span class="text-label-md text-on-tertiary-container uppercase">Total Estimado</span>
<span class="font-display-lg text-2xl text-on-tertiary-container font-bold">$1,468.90</span>
</div>
</div>
</div>
</div>
</main>
<script>
    // Interaction for SIGA Voice button
    const micBtn = document.getElementById('siga-mic');
    if (micBtn) {
        micBtn.addEventListener('click', () => {
            micBtn.classList.toggle('pulse-teal');
            micBtn.classList.toggle('bg-tertiary-fixed-dim');
        });
    }

    // Drag and drop visual feedback
    const dropzone = document.querySelector('.group');
    if (dropzone) {
        dropzone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropzone.classList.add('border-on-tertiary-container', 'bg-on-tertiary-container/5');
        });
        dropzone.addEventListener('dragleave', () => {
            dropzone.classList.remove('border-on-tertiary-container', 'bg-on-tertiary-container/5');
        });
        dropzone.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.add('animate-pulse');
            setTimeout(() => {
                this.classList.remove('animate-pulse');
                alert('Factura SIGA procesada con éxito.');
            }, 1200);
        });
    }
</script>
</body></html>

<!-- Centro de Control A2UI - Agentes Inteligentes -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>SIGA - Agentes y Automatización</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<!-- Tailwind Configuration based on DESIGN_SYSTEM_23 -->
<script id="tailwind-config">
      tailwind.config = {
        darkMode: "class",
        theme: {
          extend: {
            "colors": {
                "on-tertiary": "#ffffff",
                "outline-variant": "#c7c5d3",
                "on-error-container": "#93000a",
                "background": "#fdf7ff",
                "on-secondary-fixed-variant": "#004e5f",
                "inverse-primary": "#bfc2ff",
                "on-background": "#1d1b21",
                "secondary-container": "#50d9fe",
                "surface-tint": "#F0F9FF",
                "on-primary-fixed-variant": "#393e8c",
                "on-tertiary-fixed": "#002018",
                "on-error": "#ffffff",
                "on-tertiary-fixed-variant": "#005140",
                "on-primary-fixed": "#070a61",
                "tertiary-fixed": "#79f8d5",
                "on-surface": "#1d1b21",
                "surface-container-highest": "#e6e0e9",
                "primary-container": "#070a61",
                "primary-fixed": "#e0e0ff",
                "success-vibrant": "#10B981",
                "secondary-fixed-dim": "#4cd6fb",
                "on-secondary": "#ffffff",
                "outline": "#777682",
                "surface-dim": "#ded8e1",
                "surface-variant": "#e6e0e9",
                "secondary": "#00677d",
                "tertiary": "#000000",
                "surface-container-low": "#f8f1fa",
                "inverse-surface": "#322f36",
                "error-container": "#ffdad6",
                "tertiary-container": "#002018",
                "on-primary": "#ffffff",
                "surface-bright": "#fdf7ff",
                "on-primary-container": "#777dcf",
                "surface-container-high": "#ece6ef",
                "error": "#ba1a1a",
                "on-surface-variant": "#464651",
                "primary-fixed-dim": "#bfc2ff",
                "surface-container": "#f2ecf5",
                "tertiary-fixed-dim": "#5adcb9",
                "primary": "#000000",
                "on-tertiary-container": "#009579",
                "surface-container-lowest": "#ffffff",
                "on-secondary-container": "#005c70",
                "inverse-on-surface": "#f5eff7",
                "on-secondary-fixed": "#001f27",
                "border-muted": "#D9D9D9",
                "secondary-fixed": "#b3ebff",
                "surface": "#fdf7ff"
            },
            "borderRadius": {
                "DEFAULT": "0.25rem",
                "lg": "0.5rem",
                "xl": "0.75rem",
                "full": "9999px"
            },
            "spacing": {
                "sidebar-collapsed": "80px",
                "gutter": "24px",
                "sidebar-width": "260px",
                "row-padding": "16px",
                "container-max": "1280px",
                "card-padding": "20px",
                "margin-mobile": "16px",
                "base": "8px",
                "margin-desktop": "32px"
            },
            "fontFamily": {
                "body-sm": ["Hanken Grotesk"],
                "code-sm": ["JetBrains Mono"],
                "headline-lg-mobile": ["Hanken Grotesk"],
                "title-md": ["Hanken Grotesk"],
                "display-lg": ["Hanken Grotesk"],
                "headline-lg": ["Hanken Grotesk"],
                "body-lg": ["Hanken Grotesk"],
                "label-md": ["Hanken Grotesk"],
                "body-md": ["Hanken Grotesk"],
                "headline-sm": ["Hanken Grotesk"],
                "headline-md": ["Hanken Grotesk"],
                "label-caps": ["JetBrains Mono"]
            },
            "fontSize": {
                "body-sm": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                "code-sm": ["13px", {"lineHeight": "18px", "fontWeight": "400"}],
                "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "600"}],
                "title-md": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                "display-lg": ["56px", {"lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                "label-md": ["14px", {"lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500"}],
                "body-md": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                "headline-sm": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                "headline-md": ["24px", {"lineHeight": "32px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                "label-caps": ["12px", {"lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500"}]
            }
          },
        },
      }
    </script>
<style>
      .material-symbols-outlined {
        font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
      }
      .glass-card {
        background: rgba(255, 255, 255, 0.7);
        backdrop-filter: blur(8px);
        border: 1px solid rgba(255, 255, 255, 0.3);
      }
      .nexus-shadow {
        box-shadow: 0 1px 3px rgba(3, 4, 94, 0.08);
      }
      .nexus-shadow-hover:hover {
        box-shadow: 0 10px 15px -3px rgba(3, 4, 94, 0.1);
      }
      ::-webkit-scrollbar {
        width: 6px;
      }
      ::-webkit-scrollbar-track {
        background: transparent;
      }
      ::-webkit-scrollbar-thumb {
        background: #e6e0e9;
        border-radius: 10px;
      }
    </style>
</head>
<body class="bg-background text-on-background font-body-md min-h-screen overflow-x-hidden">
<!-- TopNavBar -->
<nav class="fixed top-0 w-full z-40 bg-surface shadow-sm flex justify-between items-center h-16 px-gutter border-b border-outline-variant/30">
<div class="flex items-center gap-8">
<img alt="SIGA Logo" class="h-10 w-auto object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
<div class="hidden md:flex items-center gap-6">
<a class="font-label-md text-label-md text-on-surface-variant hover:bg-surface-container-low transition-colors px-3 py-2 rounded" href="#">Dashboard</a>
<a class="font-label-md text-label-md text-on-surface-variant hover:bg-surface-container-low transition-colors px-3 py-2 rounded" href="#">Analytics</a>
<a class="font-label-md text-label-md text-on-surface-variant hover:bg-surface-container-low transition-colors px-3 py-2 rounded" href="#">Inventory</a>
</div>
</div>
<div class="flex items-center gap-4">
<button class="material-symbols-outlined text-secondary p-2 rounded-full hover:bg-surface-container-low transition-colors">smart_toy</button>
<button class="material-symbols-outlined text-secondary p-2 rounded-full hover:bg-surface-container-low transition-colors">notifications</button>
<button class="material-symbols-outlined text-secondary p-2 rounded-full hover:bg-surface-container-low transition-colors">settings</button>
<div class="w-8 h-8 rounded-full overflow-hidden border border-outline-variant">
<img alt="User Profile" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAPCnHbI1xztZVMTo5hthmRL0APLthVvZJ2mIBRPZEYkDiqxohAWySBLmGf0UJGx_KoLZzlLeU5mGfUtLCb8_JndPXK_q_G9W04bAgODNGf1x7YdFnXHo11lSHtfxPY5925tPjnPxmwet5JYx-1oFJwtSwNJAKpvSuvXOlx3u00l3tHdzXUbxyd6LqlQCw0YClrkxnLLuG0ytqML2OUaKYJNrNICHAcfJQHCdtU5_iZm6hJhm6ZUySXM7v0nhtHKFajXsH9WfVMwnHH"/>
</div>
</div>
</nav>
<!-- SideNavBar -->
<aside class="fixed left-0 top-0 h-full w-sidebar-width z-50 bg-surface-container-lowest flex flex-col border-r border-outline-variant shadow-md">
<div class="p-6">
<div class="flex items-center gap-3 mb-8">
<div class="w-10 h-10 bg-secondary-container flex items-center justify-center rounded-lg">
<span class="material-symbols-outlined text-on-secondary-container" style="font-variation-settings: 'FILL' 1;">dataset</span>
</div>
<div>
<h1 class="font-title-md text-title-md font-bold text-on-surface">SIGA Core</h1>
<p class="text-[10px] text-on-surface-variant uppercase tracking-widest font-label-caps">v2.4 Agent-Enabled</p>
</div>
</div>
<nav class="space-y-1">
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">dashboard</span>
<span class="font-body-md text-body-md">Overview</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">inventory_2</span>
<span class="font-body-md text-body-md">Inventory</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">point_of_sale</span>
<span class="font-body-md text-body-md">POS</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 bg-secondary-container/10 text-secondary border-l-4 border-secondary transition-all" href="#">
<span class="material-symbols-outlined">support_agent</span>
<span class="font-body-md text-body-md">Agents</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">analytics</span>
<span class="font-body-md text-body-md">Reports</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">forum</span>
<span class="font-body-md text-body-md">AI Chat</span>
</a>
</nav>
<button class="mt-8 w-full bg-secondary-container text-on-secondary-container py-3 rounded-lg font-bold flex items-center justify-center gap-2 active:scale-95 duration-150 shadow-md">
<span class="material-symbols-outlined">add</span>
                New Task
            </button>
</div>
<div class="mt-auto p-6 border-t border-outline-variant">
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">help</span>
<span class="font-body-md text-body-md">Help</span>
</a>
<a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-all" href="#">
<span class="material-symbols-outlined">logout</span>
<span class="font-body-md text-body-md">Logout</span>
</a>
</div>
</aside>
<!-- Main Content Canvas -->
<main class="ml-sidebar-width pt-16 min-h-screen bg-surface">
<div class="max-w-container-max mx-auto p-gutter">
<!-- Header Section -->
<header class="flex justify-between items-end mb-8">
<div>
<h2 class="font-headline-md text-headline-md text-on-surface mb-1">Agentes y Automatización (SIGA A2UI)</h2>
<p class="text-on-surface-variant font-body-md">Gestión centralizada de inteligencia operativa y flujos de trabajo autónomos.</p>
</div>
<div class="flex gap-3">
<div class="flex -space-x-2">
<div class="w-8 h-8 rounded-full border-2 border-white bg-tertiary-fixed-dim flex items-center justify-center text-[10px] font-bold text-on-tertiary-fixed">A1</div>
<div class="w-8 h-8 rounded-full border-2 border-white bg-secondary-fixed flex items-center justify-center text-[10px] font-bold text-on-secondary-fixed">A2</div>
<div class="w-8 h-8 rounded-full border-2 border-white bg-primary-fixed flex items-center justify-center text-[10px] font-bold text-on-primary-fixed">A3</div>
</div>
<span class="bg-on-tertiary-container/10 text-on-tertiary-container px-3 py-1 rounded-full text-xs font-bold self-center">3 Agentes Activos</span>
</div>
</header>
<!-- Status Grid -->
<div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
<div class="bg-surface-container-lowest p-card-padding rounded-lg nexus-shadow nexus-shadow-hover transition-all border-l-4 border-secondary-container">
<div class="flex justify-between items-start mb-4">
<span class="material-symbols-outlined text-secondary-container">speed</span>
<span class="text-xs font-label-caps text-on-surface-variant">EFICIENCIA GLOBAL</span>
</div>
<h3 class="font-display-lg text-display-lg text-on-surface">94.2%</h3>
<p class="text-on-tertiary-container text-xs font-bold mt-2 flex items-center gap-1">
<span class="material-symbols-outlined text-xs">trending_up</span> +2.4% vs ayer
                    </p>
</div>
<div class="bg-surface-container-lowest p-card-padding rounded-lg nexus-shadow nexus-shadow-hover transition-all border-l-4 border-primary-container">
<div class="flex justify-between items-start mb-4">
<span class="material-symbols-outlined text-on-primary-container">task_alt</span>
<span class="text-xs font-label-caps text-on-surface-variant">TAREAS COMPLETADAS</span>
</div>
<h3 class="font-display-lg text-display-lg text-on-surface">1,284</h3>
<p class="text-on-surface-variant text-xs mt-2">Promedio 428/agente</p>
</div>
<div class="bg-surface-container-lowest p-card-padding rounded-lg nexus-shadow nexus-shadow-hover transition-all border-l-4 border-outline">
<div class="flex justify-between items-start mb-4">
<span class="material-symbols-outlined text-outline">pending_actions</span>
<span class="text-xs font-label-caps text-on-surface-variant">PENDIENTES EN COLA</span>
</div>
<h3 class="font-display-lg text-display-lg text-on-surface">12</h3>
<div class="w-full bg-surface-container-high h-1.5 rounded-full mt-4">
<div class="bg-secondary-container h-full rounded-full w-[15%]"></div>
</div>
</div>
</div>
<!-- Main Layout Grid -->
<div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
<!-- Central Panel: Agent History & Actions -->
<section class="lg:col-span-8 bg-surface-container-lowest rounded-lg nexus-shadow overflow-hidden">
<div class="p-6 border-b border-outline-variant flex justify-between items-center bg-white">
<h4 class="font-headline-sm text-headline-sm text-on-surface">Historial de Ejecución</h4>
<div class="flex gap-2">
<button class="material-symbols-outlined p-2 text-on-surface-variant hover:bg-surface-container rounded-lg transition-colors">filter_list</button>
<button class="material-symbols-outlined p-2 text-on-surface-variant hover:bg-surface-container rounded-lg transition-colors">refresh</button>
</div>
</div>
<div class="overflow-x-auto">
<table class="w-full text-left">
<thead>
<tr class="bg-surface-container-low">
<th class="px-6 py-4 font-label-caps text-on-surface-variant">AGENTE</th>
<th class="px-6 py-4 font-label-caps text-on-surface-variant">ACCIÓN</th>
<th class="px-6 py-4 font-label-caps text-on-surface-variant">ESTADO</th>
<th class="px-6 py-4 font-label-caps text-on-surface-variant">TIEMPO</th>
</tr>
</thead>
<tbody class="divide-y divide-outline-variant">
<tr class="hover:bg-surface-container-low/50 transition-colors group">
<td class="px-6 py-row-padding">
<div class="flex items-center gap-3">
<div class="w-8 h-8 rounded bg-secondary-container/20 flex items-center justify-center">
<span class="material-symbols-outlined text-secondary-container text-sm" style="font-variation-settings: 'FILL' 1;">precision_manufacturing</span>
</div>
<span class="font-bold">SIGA-StockA</span>
</div>
</td>
<td class="px-6 py-row-padding">
<div class="text-sm">Actualización masiva: <span class="text-on-surface-variant">Categoría 'Zapatos'</span></div>
<div class="text-[10px] text-outline">ID: JOB-9283-A</div>
</td>
<td class="px-6 py-row-padding">
<span class="px-3 py-1 rounded-full bg-on-tertiary-container/10 text-on-tertiary-container text-xs font-bold">Exitoso</span>
</td>
<td class="px-6 py-row-padding text-sm text-on-surface-variant">Hace 2m</td>
</tr>
<tr class="hover:bg-surface-container-low/50 transition-colors group">
<td class="px-6 py-row-padding">
<div class="flex items-center gap-3">
<div class="w-8 h-8 rounded bg-primary-container/20 flex items-center justify-center">
<span class="material-symbols-outlined text-primary-container text-sm" style="font-variation-settings: 'FILL' 1;">rocket_launch</span>
</div>
<span class="font-bold">SIGA-Importer</span>
</div>
</td>
<td class="px-6 py-row-padding">
<div class="text-sm">Ingreso de productos: <span class="text-on-surface-variant">Importación XML (200 items)</span></div>
<div class="w-32 bg-surface-container h-1 rounded-full mt-1">
<div class="bg-secondary-container h-full w-[75%] rounded-full"></div>
</div>
</td>
<td class="px-6 py-row-padding">
<span class="px-3 py-1 rounded-full bg-secondary-fixed text-on-secondary-fixed-variant text-xs font-bold animate-pulse">Procesando</span>
</td>
<td class="px-6 py-row-padding text-sm text-on-surface-variant">Activo</td>
</tr>
<tr class="hover:bg-surface-container-low/50 transition-colors group">
<td class="px-6 py-row-padding">
<div class="flex items-center gap-3">
<div class="w-8 h-8 rounded bg-on-surface-variant/20 flex items-center justify-center">
<span class="material-symbols-outlined text-on-surface-variant text-sm" style="font-variation-settings: 'FILL' 1;">analytics</span>
</div>
<span class="font-bold">SIGA-Reporter</span>
</div>
</td>
<td class="px-6 py-row-padding">
<div class="text-sm">Generación de Reporte: <span class="text-on-surface-variant">Cierre de Ventas Semanal</span></div>
</td>
<td class="px-6 py-row-padding">
<span class="px-3 py-1 rounded-full bg-on-tertiary-container/10 text-on-tertiary-container text-xs font-bold">Enviado</span>
</td>
<td class="px-6 py-row-padding text-sm text-on-surface-variant">Hace 15m</td>
</tr>
<tr class="hover:bg-surface-container-low/50 transition-colors group">
<td class="px-6 py-row-padding">
<div class="flex items-center gap-3">
<div class="w-8 h-8 rounded bg-secondary-container/20 flex items-center justify-center">
<span class="material-symbols-outlined text-secondary-container text-sm" style="font-variation-settings: 'FILL' 1;">precision_manufacturing</span>
</div>
<span class="font-bold">SIGA-StockA</span>
</div>
</td>
<td class="px-6 py-row-padding">
<div class="text-sm">Corrección de discrepancia: <span class="text-on-surface-variant">Almacén Central B</span></div>
</td>
<td class="px-6 py-row-padding">
<span class="px-3 py-1 rounded-full bg-error-container text-on-error-container text-xs font-bold">Interrumpido</span>
</td>
<td class="px-6 py-row-padding text-sm text-on-surface-variant">Hace 1h</td>
</tr>
</tbody>
</table>
</div>
</section>
<!-- Sidebar Chat / Command Panel -->
<section class="lg:col-span-4 space-y-6">
<!-- IA Chat Container -->
<div class="bg-primary-container text-white rounded-lg nexus-shadow h-[500px] flex flex-col overflow-hidden relative">
<div class="p-4 border-b border-white/10 flex items-center gap-3 z-10">
<img alt="SIGA Logo" class="w-6 h-6 object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
<h5 class="font-bold">SIGA AI Assistant</h5>
</div>
<div class="flex-1 overflow-y-auto p-4 space-y-4 z-10" id="chat-container">
<div class="flex gap-3">
<div class="w-8 h-8 rounded-full bg-secondary-container flex items-center justify-center flex-shrink-0">
<span class="material-symbols-outlined text-on-secondary-container text-xs">auto_awesome</span>
</div>
<div class="bg-white/10 rounded-lg p-3 text-sm max-w-[80%]">
                                    He detectado que el stock de 'Cámaras Pro V3' está por debajo del umbral mínimo en 3 tiendas. ¿Deseas que inicie el proceso de reabastecimiento automático?
                                </div>
</div>
<div class="flex flex-row-reverse gap-3">
<div class="w-8 h-8 rounded-full bg-primary-fixed flex items-center justify-center flex-shrink-0">
<span class="material-symbols-outlined text-on-primary-fixed text-xs">person</span>
</div>
<div class="bg-secondary-container rounded-lg p-3 text-sm text-on-secondary-container max-w-[80%] shadow-lg">
                                    Sí, procede con la orden de compra habitual para el proveedor principal.
                                </div>
</div>
<div class="flex gap-3">
<div class="w-8 h-8 rounded-full bg-secondary-container flex items-center justify-center flex-shrink-0">
<span class="material-symbols-outlined text-on-secondary-container text-xs">auto_awesome</span>
</div>
<div class="bg-white/10 rounded-lg p-3 text-sm max-w-[80%]">
                                    Entendido. Iniciando SIGA-StockA para generar la OC #4592. Tiempo estimado de ejecución: 15 segundos.
                                </div>
</div>
</div>
<div class="p-4 border-t border-white/10 bg-primary-container/80 backdrop-blur z-10">
<div class="relative">
<input class="w-full bg-white/5 border border-white/20 rounded-lg py-3 pl-4 pr-12 text-sm focus:ring-2 focus:ring-secondary-container focus:border-transparent transition-all outline-none placeholder-white/40" placeholder="Escribe un comando..." type="text"/>
<button class="absolute right-3 top-1/2 -translate-y-1/2 material-symbols-outlined text-secondary-container">send</button>
</div>
<div class="flex gap-2 mt-3">
<button class="text-[10px] bg-white/5 hover:bg-white/10 px-2 py-1 rounded border border-white/10 transition-colors uppercase font-label-caps">Status update</button>
<button class="text-[10px] bg-white/5 hover:bg-white/10 px-2 py-1 rounded border border-white/10 transition-colors uppercase font-label-caps">Check inventory</button>
</div>
</div>
</div>
<!-- Task Efficiency Card -->
<div class="bg-surface-container-lowest p-card-padding rounded-lg nexus-shadow">
<h6 class="font-bold text-on-surface mb-4 flex items-center gap-2">
<span class="material-symbols-outlined text-secondary-container text-sm">auto_graph</span>
                            Proyección de Ahorro
                        </h6>
<div class="space-y-4">
<div>
<div class="flex justify-between text-xs mb-1">
<span class="text-on-surface-variant">Horas hombre ahorradas</span>
<span class="font-bold text-on-surface">42h / semana</span>
</div>
<div class="w-full bg-surface-container h-2 rounded-full">
<div class="bg-secondary-container h-full w-[80%] rounded-full"></div>
</div>
</div>
<div class="grid grid-cols-2 gap-4 mt-6">
<div class="text-center p-3 bg-surface-container-low rounded-lg">
<p class="text-[10px] text-on-surface-variant font-label-caps">ERRORES EVITADOS</p>
<p class="text-xl font-bold text-on-tertiary-container">124</p>
</div>
<div class="text-center p-3 bg-surface-container-low rounded-lg">
<p class="text-[10px] text-on-surface-variant font-label-caps">ROI EST.</p>
<p class="text-xl font-bold text-secondary">x3.2</p>
</div>
</div>
</div>
</div>
</section>
</div>
</div>
</main>
<!-- Contextual FAB -->
<button class="fixed bottom-8 right-8 w-14 h-14 bg-secondary-container text-on-secondary-container rounded-full shadow-2xl flex items-center justify-center hover:scale-110 active:scale-95 transition-all z-50 group">
<span class="material-symbols-outlined text-3xl">bolt</span>
<span class="absolute right-16 bg-on-surface text-white px-3 py-1 rounded text-xs opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap pointer-events-none">Optimización Rápida</span>
</button>
<script>
        // Micro-interactions for the UI
        document.querySelectorAll('tr').forEach(row => {
            row.addEventListener('click', () => {
                row.classList.add('bg-secondary-container/5');
                setTimeout(() => {
                    row.classList.remove('bg-secondary-container/5');
                }, 400);
            });
        });

        const chatContainer = document.getElementById('chat-container');
        chatContainer.scrollTop = chatContainer.scrollHeight;

        const refreshBtn = document.querySelector('[data-icon="refresh"]');
        if(refreshBtn) {
            refreshBtn.addEventListener('click', function() {
                this.classList.add('animate-spin');
                setTimeout(() => this.classList.remove('animate-spin'), 1000);
            });
        }
    </script>
</body></html>

<!-- Punto de Venta (POS) -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;900&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
      tailwind.config = {
        darkMode: "class",
        theme: {
          extend: {
            "colors": {
                "on-tertiary": "#ffffff",
                "outline-variant": "#c7c5d3",
                "on-error-container": "#93000a",
                "background": "#fdf7ff",
                "on-secondary-fixed-variant": "#004e5f",
                "inverse-primary": "#bfc2ff",
                "on-background": "#1d1b21",
                "secondary-container": "#50d9fe",
                "surface-tint": "#F0F9FF",
                "on-primary-fixed-variant": "#393e8c",
                "on-tertiary-fixed": "#002018",
                "on-error": "#ffffff",
                "on-tertiary-fixed-variant": "#005140",
                "on-primary-fixed": "#070a61",
                "tertiary-fixed": "#79f8d5",
                "on-surface": "#1d1b21",
                "surface-container-highest": "#e6e0e9",
                "primary-container": "#070a61",
                "primary-fixed": "#e0e0ff",
                "success-vibrant": "#10B981",
                "secondary-fixed-dim": "#4cd6fb",
                "on-secondary": "#ffffff",
                "outline": "#777682",
                "surface-dim": "#ded8e1",
                "surface-variant": "#e6e0e9",
                "secondary": "#00677d",
                "tertiary": "#000000",
                "surface-container-low": "#f8f1fa",
                "inverse-surface": "#322f36",
                "error-container": "#ffdad6",
                "tertiary-container": "#002018",
                "on-primary": "#ffffff",
                "surface-bright": "#fdf7ff",
                "on-primary-container": "#777dcf",
                "surface-container-high": "#ece6ef",
                "error": "#ba1a1a",
                "on-surface-variant": "#464651",
                "primary-fixed-dim": "#bfc2ff",
                "surface-container": "#f2ecf5",
                "tertiary-fixed-dim": "#5adcb9",
                "primary": "#000000",
                "on-tertiary-container": "#009579",
                "surface-container-lowest": "#ffffff",
                "on-secondary-container": "#005c70",
                "inverse-on-surface": "#f5eff7",
                "on-secondary-fixed": "#001f27",
                "border-muted": "#D9D9D9",
                "secondary-fixed": "#b3ebff",
                "surface": "#fdf7ff"
            },
            "borderRadius": {
                "DEFAULT": "0.25rem",
                "lg": "0.5rem",
                "xl": "0.75rem",
                "full": "9999px"
            },
            "spacing": {
                "sidebar-collapsed": "80px",
                "row-padding": "16px",
                "card-padding": "20px",
                "sidebar-width": "260px",
                "gutter": "24px",
                "container-max": "1280px"
            },
            "fontFamily": {
                "body-sm": ["Hanken Grotesk"],
                "code-sm": ["JetBrains Mono"],
                "headline-lg-mobile": ["Hanken Grotesk"],
                "title-md": ["Hanken Grotesk"],
                "display-lg": ["Hanken Grotesk"],
                "headline-lg": ["Hanken Grotesk"],
                "body-lg": ["Hanken Grotesk"],
                "label-md": ["Hanken Grotesk"],
                "body-md": ["Hanken Grotesk"],
                "headline-sm": ["Hanken Grotesk"],
                "label-caps": ["JetBrains Mono"]
            },
            "fontSize": {
                "body-sm": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                "code-sm": ["13px", {"lineHeight": "18px", "fontWeight": "400"}],
                "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "600"}],
                "title-md": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                "display-lg": ["56px", {"lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                "label-md": ["14px", {"lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500"}],
                "headline-sm": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                "label-caps": ["12px", {"lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500"}]
            }
          },
        },
      }
    </script>
<style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
            display: inline-block;
            line-height: 1;
            text-transform: none;
            letter-spacing: normal;
            word-wrap: normal;
            white-space: nowrap;
            direction: ltr;
        }
        .hide-scrollbar::-webkit-scrollbar { display: none; }
        .hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
        
        .product-card:active { transform: scale(0.97); }
        .transition-soft { transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); }
    </style>
</head>
<body class="bg-background text-on-background font-body-md overflow-hidden h-screen flex">
<!-- Collapsed SideNavBar -->
<aside class="fixed left-0 top-0 h-full w-20 bg-surface-container-lowest shadow-sm flex flex-col py-6 items-center z-50 border-r border-outline-variant">
<div class="mb-10 px-2">
<img alt="SIGA Logo" class="w-10 h-10 object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
</div>
<nav class="flex flex-col gap-4 w-full px-2">
<a class="flex flex-col items-center gap-1 py-3 text-on-surface-variant hover:bg-surface-container transition-soft rounded-xl group" href="#">
<span class="material-symbols-outlined text-[24px]">dashboard</span>
</a>
<a class="flex flex-col items-center gap-1 py-3 text-on-surface-variant hover:bg-surface-container transition-soft rounded-xl group" href="#">
<span class="material-symbols-outlined text-[24px]">inventory_2</span>
</a>
<a class="flex flex-col items-center gap-1 py-3 text-on-surface-variant hover:bg-surface-container transition-soft rounded-xl group" href="#">
<span class="material-symbols-outlined text-[24px]">storefront</span>
</a>
<a class="flex flex-col items-center gap-1 py-3 bg-secondary-container text-on-secondary-container font-bold transition-soft rounded-xl" href="#">
<span class="material-symbols-outlined text-[24px]">point_of_sale</span>
</a>
<a class="flex flex-col items-center gap-1 py-3 text-on-surface-variant hover:bg-surface-container transition-soft rounded-xl group" href="#">
<span class="material-symbols-outlined text-[24px]">analytics</span>
</a>
<div class="mt-4 px-2">
<button class="w-full aspect-square flex items-center justify-center bg-surface-container rounded-xl hover:bg-surface-container-high transition-soft group">
<img alt="AI Assistant" class="w-6 h-6 grayscale opacity-60 group-hover:grayscale-0 group-hover:opacity-100 transition-soft" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F"/>
</button>
</div>
<a class="flex flex-col items-center gap-1 py-3 text-on-surface-variant hover:bg-surface-container transition-soft rounded-xl group mt-auto" href="#">
<span class="material-symbols-outlined text-[24px]">settings</span>
</a>
</nav>
</aside>
<!-- Main Content Area -->
<main class="ml-20 flex-1 flex flex-col h-full">
<!-- TopAppBar -->
<header class="h-16 flex justify-between items-center px-gutter bg-surface border-b border-outline-variant">
<div class="flex items-center gap-8 flex-1 max-w-4xl">
<img alt="SIGA" class="h-8 w-auto" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
<div class="relative w-full max-w-md">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
<input class="w-full bg-surface-container-low border-none rounded-xl pl-10 pr-4 py-2 focus:ring-2 focus:ring-secondary-container transition-soft font-body-md text-on-surface" placeholder="Buscar productos por nombre o SKU..." type="text"/>
</div>
</div>
<div class="flex items-center gap-6">
<div class="flex items-center gap-3">
<span class="material-symbols-outlined text-outline cursor-pointer hover:text-on-surface transition-soft">notifications</span>
<span class="material-symbols-outlined text-outline cursor-pointer hover:text-on-surface transition-soft">location_on</span>
</div>
<div class="h-8 w-[1px] bg-outline-variant"></div>
<div class="flex items-center gap-3">
<div class="text-right">
<p class="font-headline-sm text-[14px] font-bold">Inventario Master</p>
<p class="text-[12px] text-outline">Local Principal</p>
</div>
<div class="w-10 h-10 rounded-full bg-surface-container-high flex items-center justify-center border border-outline-variant overflow-hidden">
<img alt="Perfil" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBhnWdSthdUehVfIHqNzajStcU6GgBNZZkryjULgcDmd2m7ou8o4URdA0w1q2uXXAB-_qBCxmrNIkDQ_5Yhq3JPp5obaaEZgDuxuXKPauuWk5yr3B9PkbRsqEtS5dlvhI3Wr2TrZpKMjf2OYAa4pYQyqFEP1xsuJchSpbrw1RQF5DcF1kA4VwBuk1g0RGFTDTA66zuA4Z1xqKSiDkDi6LrZPY1U8v8SWyCeO9PaJWhoGTCmQMY0h0GY6fvJp2Me6ME41g24bBJkpyBq"/>
</div>
</div>
</div>
</header>
<!-- POS Workspace -->
<div class="flex-1 flex overflow-hidden">
<!-- Left Side: Product Selector -->
<section class="flex-1 flex flex-col bg-background p-6 overflow-hidden">
<!-- Categories Bar -->
<div class="flex gap-3 mb-6 overflow-x-auto hide-scrollbar pb-2">
<button class="px-6 py-2 rounded-lg bg-on-secondary-container text-on-primary font-bold whitespace-nowrap shadow-sm transition-soft active:scale-95">Todos</button>
<button class="px-6 py-2 rounded-lg bg-white text-on-surface-variant hover:bg-surface-container transition-soft whitespace-nowrap shadow-sm border border-outline-variant">Electrónica</button>
<button class="px-6 py-2 rounded-lg bg-white text-on-surface-variant hover:bg-surface-container transition-soft whitespace-nowrap shadow-sm border border-outline-variant">Hogar</button>
<button class="px-6 py-2 rounded-lg bg-white text-on-surface-variant hover:bg-surface-container transition-soft whitespace-nowrap shadow-sm border border-outline-variant">Alimentos</button>
<button class="px-6 py-2 rounded-lg bg-white text-on-surface-variant hover:bg-surface-container transition-soft whitespace-nowrap shadow-sm border border-outline-variant">Ropa</button>
<button class="px-6 py-2 rounded-lg bg-white text-on-surface-variant hover:bg-surface-container transition-soft whitespace-nowrap shadow-sm border border-outline-variant">Herramientas</button>
<button class="px-6 py-2 rounded-lg bg-white text-on-surface-variant hover:bg-surface-container transition-soft whitespace-nowrap shadow-sm border border-outline-variant">Deportes</button>
</div>
<!-- Product Grid -->
<div class="flex-1 overflow-y-auto grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 pr-2">
<!-- Product Item 1 -->
<div class="product-card bg-white p-4 rounded-xl shadow-sm border border-transparent hover:border-on-secondary-container transition-soft cursor-pointer flex flex-col group">
<div class="aspect-square rounded-lg bg-surface-container-low mb-4 overflow-hidden relative">
<img class="w-full h-full object-cover group-hover:scale-105 transition-soft" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDH-zIxKnuuS8h2xStB6UdpLZGLppHacH7vanszSvVhuB7zHIjxP4CdsrrrLCeLBcQMBlAgaE4VoK7SZyfRyCyB6jAiwZnhm8zefJNgR3PgCyrP0OhWQAiFfTdhV0d5nDzjbq-fmdfThTdSSDT5C8HgKwx9hdlBWznXLa5VzF8kIzuNj0oNL1lCbIzO89enMpR-MvsNuBSDXmpPguo5rPMz4iboavgb6OKWjkSC4ckBZP0J4esMajvaPkIMRL604b4dLSjICBTEzttA"/>
<div class="absolute top-2 right-2 bg-on-secondary-container/90 text-on-primary px-2 py-1 rounded text-[10px] font-bold">STOCK: 12</div>
</div>
<h3 class="font-headline-sm text-[16px] text-on-surface mb-1 truncate">Audífonos Pro Bass</h3>
<p class="font-label-caps text-on-secondary-container font-bold text-[18px] mt-auto">$129.90</p>
</div>
<!-- Product Item 2 -->
<div class="product-card bg-white p-4 rounded-xl shadow-sm border border-transparent hover:border-on-secondary-container transition-soft cursor-pointer flex flex-col group">
<div class="aspect-square rounded-lg bg-surface-container-low mb-4 overflow-hidden relative">
<img class="w-full h-full object-cover group-hover:scale-105 transition-soft" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBYduVRLfS3-X3onPPh-Elhl7F0w57Kf-Tm6_qbpwxAkVhKWgu4-jm8S2DlXiA4LQq_2xP0bHRNXm5_M6L6zaxhtWsJr0_wsMDHck5oRlRnFn4cj08qq3vQXm-RrRwq2oOVP73mLEcqoS3LUJNRROPP6sQcV1u8ZRZp0hSKLApfMmCRVZoXvFM9mNWLJ1F56SE3nBdsDQGt0jT29MUsqTpO-i2MnjIemyHi6jyCvPyDQN5E4TL2w6bnh8gcGaTKIs9XWOADQSzOWOgy"/>
<div class="absolute top-2 right-2 bg-on-secondary-container/90 text-on-primary px-2 py-1 rounded text-[10px] font-bold">STOCK: 5</div>
</div>
<h3 class="font-headline-sm text-[16px] text-on-surface mb-1 truncate">Reloj Minimalist White</h3>
<p class="font-label-caps text-on-secondary-container font-bold text-[18px] mt-auto">$85.00</p>
</div>
<!-- Product Item 3 -->
<div class="product-card bg-white p-4 rounded-xl shadow-sm border border-transparent hover:border-on-secondary-container transition-soft cursor-pointer flex flex-col group">
<div class="aspect-square rounded-lg bg-surface-container-low mb-4 overflow-hidden relative">
<img class="w-full h-full object-cover group-hover:scale-105 transition-soft" src="https://lh3.googleusercontent.com/aida-public/AB6AXuArujRRjI40ST6yoKbpGn5pF6V1UOjPmfYzrF4UzypBMCZ6zdtdiRMSpIFOkqQYe-kuBsRgT56Y5VbqY6cjqTCqwjGRyY56AfiDZUudUm6kVXkV-Ms75y9Oz6hkTDeKORoxwEG4dDN4zIOX6DdSdKsk_UGQtWgp1fPvae3rFNJNKCF6qFzhnYPLbMn-GnzXIGEpmPWukP6_eCWHNIgjw2giahCLA8zu-CYgNCiij_NvQPmaAFp7XkIOY9Sp_y73G0tqrZttVRizKuLd"/>
<div class="absolute top-2 right-2 bg-on-secondary-container/90 text-on-primary px-2 py-1 rounded text-[10px] font-bold">STOCK: 8</div>
</div>
<h3 class="font-headline-sm text-[16px] text-on-surface mb-1 truncate">Cámara Retro Shot</h3>
<p class="font-label-caps text-on-secondary-container font-bold text-[18px] mt-auto">$210.00</p>
</div>
<!-- Product Item 4 -->
<div class="product-card bg-white p-4 rounded-xl shadow-sm border border-transparent hover:border-on-secondary-container transition-soft cursor-pointer flex flex-col group">
<div class="aspect-square rounded-lg bg-surface-container-low mb-4 overflow-hidden relative">
<img class="w-full h-full object-cover group-hover:scale-105 transition-soft" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBMAxigKlsxp-rj_oEYcCxTBmPGAdtVouY1uaUmK6YSq1kyE3PLATNSkCKhJ_PQ6TioMW6f05eCPbhc08YUDlT_puqJYNNo9tqe7dMHMyTmWHZVw57p1EUhu98QEj6lht4sxZSlpqdscCGgqkytolwhXQRXxMwPtDHUMGuTMeQUYzxsGDxKM1Ll0ZIYgOVYvMTwZy6zIZWw6iBdYYu4I_VjnztNmqdpdchyCSKTSjY4shupHoKfPoFyxh3sYZk0SjUDk1i0p0wT34Z-"/>
<div class="absolute top-2 right-2 bg-on-secondary-container/90 text-on-primary px-2 py-1 rounded text-[10px] font-bold">STOCK: 20</div>
</div>
<h3 class="font-headline-sm text-[16px] text-on-surface mb-1 truncate">Zapatillas Air Speed</h3>
<p class="font-label-caps text-on-secondary-container font-bold text-[18px] mt-auto">$95.50</p>
</div>
</div>
</section>
<!-- Right Side: Shopping Cart -->
<section class="w-[400px] bg-white border-l border-outline-variant flex flex-col z-10">
<!-- Customer & Document Select -->
<div class="p-6 border-b border-surface-container">
<div class="mb-4">
<label class="block font-label-caps text-[10px] text-outline mb-2 uppercase tracking-widest">Cliente</label>
<div class="flex items-center gap-2 p-3 bg-surface-container-low rounded-xl cursor-pointer hover:bg-surface-container transition-soft border border-outline-variant">
<span class="material-symbols-outlined text-on-secondary-container">person_add</span>
<div class="flex-1">
<p class="font-body-md font-bold text-on-surface">Seleccionar Cliente</p>
</div>
<span class="material-symbols-outlined text-outline">chevron_right</span>
</div>
</div>
<div>
<label class="block font-label-caps text-[10px] text-outline mb-2 uppercase tracking-widest">Tipo de Documento</label>
<div class="flex p-1 bg-surface-container-low rounded-xl border border-outline-variant">
<button class="flex-1 py-2 text-[12px] font-bold rounded-lg bg-white shadow-sm text-on-secondary-container transition-soft">Boleta</button>
<button class="flex-1 py-2 text-[12px] font-medium rounded-lg text-outline-variant hover:text-outline transition-soft">Factura</button>
</div>
</div>
</div>
<!-- Items List -->
<div class="flex-1 overflow-y-auto p-6">
<div class="flex items-center justify-between mb-4">
<h2 class="font-headline-sm text-on-surface">Carrito Actual</h2>
<button class="text-error font-body-md text-[12px] hover:underline">Vaciar</button>
</div>
<div class="space-y-4">
<!-- Cart Item 1 -->
<div class="flex gap-3 group">
<div class="w-12 h-12 rounded-lg bg-surface-container-low overflow-hidden">
<img class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDKMQ7IrRr8yLBVj_cXTqPlX5fVIvhwpNrw4DYFxMro5TXPcPZmWHBiHpBUHZQQAGg5Z8uIQI9AtxV_tPZgUCAVY4GBjyx1Hg-im_ekKkay8-IqoapJBAY0e29d66g_SW-uphqg95kfH6phcf-NaJsFeSyo_jGy-eWQFkqzpV0uWa40IbvIG17lmSN5oOwGYF51LWliP4alpcnmTkaUrSGM0vEVSRvDvhnyI5Zd2BXB5uaoc9QtriOaXryh5ZAbCKxn3qHeipP_l5il"/>
</div>
<div class="flex-1 min-w-0">
<p class="font-body-md font-bold text-on-surface truncate">Audífonos Pro Bass</p>
<p class="text-[12px] text-outline">$129.90 x 1</p>
</div>
<div class="flex items-center gap-2">
<div class="flex items-center bg-surface-container-low rounded-lg p-1 border border-outline-variant">
<button class="w-6 h-6 flex items-center justify-center hover:bg-white rounded transition-soft text-on-surface-variant">
<span class="material-symbols-outlined text-[16px]">remove</span>
</button>
<span class="px-2 font-bold text-[12px]">1</span>
<button class="w-6 h-6 flex items-center justify-center hover:bg-white rounded transition-soft text-on-surface-variant">
<span class="material-symbols-outlined text-[16px]">add</span>
</button>
</div>
<button class="text-outline-variant hover:text-error transition-soft">
<span class="material-symbols-outlined text-[18px]">delete</span>
</button>
</div>
</div>
<!-- Cart Item 2 -->
<div class="flex gap-3 group">
<div class="w-12 h-12 rounded-lg bg-surface-container-low overflow-hidden">
<img class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuB-Z6dfK5gPgHElxeBOv9LzGBydtmax-VuhtbAr5ihiHg8ADNFGqh95aOfPlsJNMdxJaN5lObdlXPQgKSGDH9hQLJ9DbSi5EcxRvSySPhjXUckug_xTnJQhiq2099FAUaoDUMoyVAEnsWUgFByhNDhJZM6_8xUAcIzFFYEeuNAgME2nuHselDh7yf5zcCFTpEcMNwXO-_QejTw4IwxmpNg0ntPqxiFCdtJtPO5L9K7Fk95__c7pZAQCf6qIs7RhmTO5NL16WaGnhxdn"/>
</div>
<div class="flex-1 min-w-0">
<p class="font-body-md font-bold text-on-surface truncate">Reloj Minimalist White</p>
<p class="text-[12px] text-outline">$85.00 x 2</p>
</div>
<div class="flex items-center gap-2">
<div class="flex items-center bg-surface-container-low rounded-lg p-1 border border-outline-variant">
<button class="w-6 h-6 flex items-center justify-center hover:bg-white rounded transition-soft text-on-surface-variant">
<span class="material-symbols-outlined text-[16px]">remove</span>
</button>
<span class="px-2 font-bold text-[12px]">2</span>
<button class="w-6 h-6 flex items-center justify-center hover:bg-white rounded transition-soft text-on-surface-variant">
<span class="material-symbols-outlined text-[16px]">add</span>
</button>
</div>
<button class="text-outline-variant hover:text-error transition-soft">
<span class="material-symbols-outlined text-[18px]">delete</span>
</button>
</div>
</div>
</div>
</div>
<!-- Payment Summary -->
<div class="p-6 bg-surface-container-low rounded-t-3xl border-t border-outline-variant">
<div class="space-y-2 mb-6">
<div class="flex justify-between text-on-surface-variant">
<span class="font-body-md">Subtotal</span>
<span class="font-label-caps">$299.90</span>
</div>
<div class="flex justify-between text-on-surface-variant">
<span class="font-body-md">IGV (18%)</span>
<span class="font-label-caps">$54.00</span>
</div>
<div class="h-[1px] bg-outline-variant/30 my-4"></div>
<div class="flex justify-between items-end">
<span class="font-headline-sm text-on-surface">Total</span>
<span class="font-headline-md text-on-secondary-container text-[32px] font-black leading-none">$353.90</span>
</div>
</div>
<div class="flex flex-col gap-3">
<button class="w-full bg-on-secondary-container text-on-primary py-4 rounded-xl font-bold text-lg flex items-center justify-center gap-3 shadow-lg hover:brightness-110 active:scale-[0.98] transition-soft">
<span class="material-symbols-outlined">payments</span>
                            Pagar
                        </button>
<div class="grid grid-cols-2 gap-3">
<button class="py-3 rounded-xl border border-outline-variant font-bold text-[12px] hover:bg-surface transition-soft bg-white">Suspender</button>
<button class="py-3 rounded-xl border border-outline-variant font-bold text-[12px] hover:bg-surface transition-soft bg-white">Cotización</button>
</div>
</div>
</div>
</section>
</div>
</main>
<script>
        // Micro-interactions and cart logic (mock)
        const products = document.querySelectorAll('.product-card');
        products.forEach(product => {
            product.addEventListener('click', () => {
                // Flash feedback
                product.classList.add('ring-2', 'ring-on-secondary-container');
                setTimeout(() => product.classList.remove('ring-2', 'ring-on-secondary-container'), 300);
            });
        });

        const paymentBtn = document.querySelector('button.bg-on-secondary-container');
        paymentBtn.addEventListener('click', () => {
            const originalContent = paymentBtn.innerHTML;
            paymentBtn.innerHTML = '<span class="material-symbols-outlined animate-spin">refresh</span> Procesando...';
            setTimeout(() => {
                paymentBtn.innerHTML = '<span class="material-symbols-outlined">check_circle</span> Éxito';
                setTimeout(() => {
                    paymentBtn.innerHTML = originalContent;
                }, 1500);
            }, 800);
        });
    </script>
</body></html>

<!-- Gestión de Stock por Sucursal -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Gestión de Stock por Local | SIGA Inventario</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800;900&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "on-tertiary": "#ffffff",
                        "outline-variant": "#c7c5d3",
                        "on-error-container": "#93000a",
                        "background": "#fdf7ff",
                        "on-secondary-fixed-variant": "#004e5f",
                        "inverse-primary": "#bfc2ff",
                        "on-background": "#1d1b21",
                        "secondary-container": "#50d9fe",
                        "surface-tint": "#F0F9FF",
                        "on-primary-fixed-variant": "#393e8c",
                        "on-tertiary-fixed": "#002018",
                        "on-error": "#ffffff",
                        "on-tertiary-fixed-variant": "#005140",
                        "on-primary-fixed": "#070a61",
                        "tertiary-fixed": "#79f8d5",
                        "on-surface": "#1d1b21",
                        "surface-container-highest": "#e6e0e9",
                        "primary-container": "#070a61",
                        "primary-fixed": "#e0e0ff",
                        "success-vibrant": "#10B981",
                        "secondary-fixed-dim": "#4cd6fb",
                        "on-secondary": "#ffffff",
                        "outline": "#777682",
                        "surface-dim": "#ded8e1",
                        "surface-variant": "#e6e0e9",
                        "secondary": "#00677d",
                        "tertiary": "#000000",
                        "surface-container-low": "#f8f1fa",
                        "inverse-surface": "#322f36",
                        "error-container": "#ffdad6",
                        "tertiary-container": "#002018",
                        "on-primary": "#ffffff",
                        "surface-bright": "#fdf7ff",
                        "on-primary-container": "#777dcf",
                        "surface-container-high": "#ece6ef",
                        "error": "#ba1a1a",
                        "on-surface-variant": "#464651",
                        "primary-fixed-dim": "#bfc2ff",
                        "surface-container": "#f2ecf5",
                        "tertiary-fixed-dim": "#5adcb9",
                        "primary": "#000000",
                        "on-tertiary-container": "#009579",
                        "surface-container-lowest": "#ffffff",
                        "on-secondary-container": "#005c70",
                        "inverse-on-surface": "#f5eff7",
                        "on-secondary-fixed": "#001f27",
                        "border-muted": "#D9D9D9",
                        "secondary-fixed": "#b3ebff",
                        "surface": "#fdf7ff"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "sidebar-collapsed": "80px",
                        "row-padding": "16px",
                        "card-padding": "20px",
                        "sidebar-width": "260px",
                        "gutter": "24px",
                        "container-max": "1280px"
                    },
                    "fontFamily": {
                        "body-sm": ["Hanken Grotesk"],
                        "code-sm": ["JetBrains Mono"],
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "title-md": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "headline-lg": ["Hanken Grotesk"],
                        "body-lg": ["Hanken Grotesk"],
                        "label-md": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "body-sm": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                        "code-sm": ["13px", {"lineHeight": "18px", "fontWeight": "400"}],
                        "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "600"}],
                        "title-md": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "display-lg": ["56px", {"lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                        "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                        "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                        "label-md": ["14px", {"lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500"}]
                    }
                },
            },
        }
    </script>
<style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
        .stock-bar-container { height: 8px; border-radius: 4px; overflow: hidden; background: #e6e0e9; }
        .stock-bar-fill { height: 100%; transition: width 0.5s ease-out; }
        .custom-scrollbar::-webkit-scrollbar { width: 6px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: #c7c5d3; border-radius: 10px; }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #777682; }
    </style>
</head>
<body class="bg-background text-on-surface font-body-lg overflow-hidden flex h-screen">
<!-- SideNavBar Component -->
<aside class="fixed left-0 top-0 h-full w-sidebar-width bg-surface-container-lowest shadow-sm flex flex-col py-6 z-50">
<div class="px-6 mb-8 flex items-center gap-3">
<div class="w-12 h-auto flex items-center justify-center">
<img alt="SIGA Logo" class="w-full h-auto object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDhMMyDehFF1GidcFlm_MqD13PVJD2LpaD8ZpmvDee9Y511YXYL1k_NspwZcEU3ysh3gF9mUltdnux1ZjSKqlZ9Mf00lEGmj1ozZkRW3uA4Sqmf5a_vdJINKltoFZv2nA2t1C6MUFA8r0_vaa1LPa53m8bv76IUVKn6oYXl4ZNJ1wX3IsRpzwsY-tk89ZFHoCQub6pG7kVJG6o7kmrAFZqW9YJxHKqI2Zyt-CmqeXef4y7k8SUsXufv07rVAkrOIDtoy_0Q88ZFDH7K"/>
</div>
<div>
<h1 class="font-title-md text-title-md font-bold text-secondary">SIGA Pro</h1>
<p class="font-label-md text-[10px] text-outline uppercase tracking-widest">Administrador</p>
</div>
</div>
<nav class="flex-1 space-y-1">
<a class="flex items-center gap-3 py-3 px-4 text-on-surface-variant hover:bg-surface-container transition-colors" href="#">
<span class="material-symbols-outlined">dashboard</span>
<span class="font-label-md text-label-md">Inicio</span>
</a>
<a class="flex items-center gap-3 py-3 px-4 border-l-4 border-secondary bg-surface-container-highest text-secondary font-bold" href="#">
<span class="material-symbols-outlined">inventory_2</span>
<span class="font-label-md text-label-md">Inventario</span>
</a>
<a class="flex items-center gap-3 py-3 px-4 text-on-surface-variant hover:bg-surface-container transition-colors" href="#">
<span class="material-symbols-outlined">storefront</span>
<span class="font-label-md text-label-md">Locales</span>
</a>
<a class="flex items-center gap-3 py-3 px-4 text-on-surface-variant hover:bg-surface-container transition-colors" href="#">
<span class="material-symbols-outlined">point_of_sale</span>
<span class="font-label-md text-label-md">POS</span>
</a>
<a class="flex items-center gap-3 py-3 px-4 text-on-surface-variant hover:bg-surface-container transition-colors" href="#">
<span class="material-symbols-outlined">analytics</span>
<span class="font-label-md text-label-md">Reportes</span>
</a>
<a class="flex items-center gap-3 py-3 px-4 text-on-surface-variant hover:bg-surface-container transition-colors" href="#">
<span class="material-symbols-outlined">settings</span>
<span class="font-label-md text-label-md">Configuración</span>
</a>
</nav>
<div class="px-4 mt-auto">
<button class="w-full py-3 bg-secondary text-on-primary rounded-lg font-bold flex items-center justify-center gap-2 shadow-sm transition-all duration-200 active:scale-95">
<span class="material-symbols-outlined">add_circle</span>
<span class="font-label-md text-label-md">Nuevo Movimiento</span>
</button>
</div>
</aside>
<div class="flex-1 ml-[260px] flex flex-col h-full relative">
<!-- TopAppBar Component -->
<header class="fixed top-0 right-0 w-[calc(100%-theme(spacing.sidebar-width))] h-16 bg-surface flex justify-between items-center px-gutter z-40">
<div class="flex items-center gap-4 flex-1">
<div class="relative w-full max-w-md">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
<input class="w-full bg-surface-container-low border-none rounded-full pl-10 pr-4 py-2 text-body-sm focus:ring-2 focus:ring-secondary-container transition-all outline-none" placeholder="Buscar productos, SKU o marcas..." type="text"/>
</div>
</div>
<div class="flex items-center gap-6">
<button class="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-surface-container-low transition-colors text-on-surface">
<span class="material-symbols-outlined">sync_alt</span>
<span class="font-body-sm">Cambiar Local</span>
</button>
<div class="flex items-center gap-4">
<button class="relative p-2 text-outline hover:text-on-surface transition-colors">
<span class="material-symbols-outlined">notifications</span>
<span class="absolute top-1 right-1 w-2 h-2 bg-secondary rounded-full"></span>
</button>
<div class="flex items-center gap-3 border-l pl-6 border-outline-variant">
<div class="text-right">
<p class="font-body-sm font-bold leading-tight">Admin Perfil</p>
<p class="text-xs text-outline leading-tight">Super Usuario</p>
</div>
<img alt="Avatar de usuario" class="w-10 h-10 rounded-full border-2 border-surface-container-high object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCPl56Q3v2b0f-POwipTkJHQEmW5CWXYfTrVeMcU8gPU3pQ1MekBwiLzz_Hoj9p3oNdba8pa7ynj9vIwk5qIY1O3sn3sromsonQNZjcz_3oDjONbpmOOCa9IZXcLfNWAP0QPnerV6WBmxUI0lEiJNegSDqV_awpbNww0_-xSOmPrWh1hrUYp6ERUqjTPREGADkrgZZSxX2K_G63ons8C3lQrYCFeJKMF-fqUUfI8vVS6Q7lFZz9hCwEZjCj0j28sWwEhOVBm4VaiT0b"/>
</div>
</div>
</div>
</header>
<!-- Main Content Canvas -->
<main class="mt-16 p-gutter flex flex-col gap-6 overflow-y-auto custom-scrollbar flex-1 bg-surface-bright">
<!-- Page Header Area -->
<div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
<div>
<h2 class="font-headline-lg text-headline-lg text-on-surface">Control de Stock por Local</h2>
<p class="text-on-surface-variant">Visualiza y ajusta existencias en tiempo real con SIGA.</p>
</div>
<div class="flex items-center gap-3">
<div class="flex flex-col gap-1">
<label class="text-[10px] font-bold text-outline uppercase px-1">Seleccionar Local</label>
<div class="relative min-w-[200px]">
<select class="w-full appearance-none bg-surface-container-lowest border border-outline-variant px-4 py-2.5 rounded-lg text-body-sm font-semibold text-on-surface focus:border-secondary focus:ring-0 transition-colors">
<option>Sucursal Central - Madrid</option>
<option>Depósito Norte - Bilbao</option>
<option>Tienda Sur - Málaga</option>
<option>Showroom Outlet - Barcelona</option>
</select>
<span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-outline">expand_more</span>
</div>
</div>
<div class="flex items-center self-end gap-2">
<button class="px-4 py-2.5 bg-surface-container-lowest border border-outline-variant text-on-surface rounded-lg font-bold flex items-center gap-2 hover:bg-surface-container-low transition-colors active:scale-95">
<span class="material-symbols-outlined">download</span> CSV
                        </button>
<button class="px-4 py-2.5 bg-surface-container-lowest border border-outline-variant text-on-surface rounded-lg font-bold flex items-center gap-2 hover:bg-surface-container-low transition-colors active:scale-95">
<span class="material-symbols-outlined">picture_as_pdf</span> PDF
                        </button>
</div>
</div>
</div>
<!-- Dashboard Grid Layout -->
<div class="grid grid-cols-12 gap-6">
<!-- Filter Sidebar/Panel -->
<aside class="col-span-12 lg:col-span-3 space-y-6">
<div class="bg-surface-container-lowest p-card-padding rounded-xl shadow-sm space-y-6 border border-surface-container">
<div class="flex items-center justify-between border-b border-outline-variant pb-4">
<h3 class="font-title-md text-title-md">Filtros Avanzados</h3>
<button class="text-secondary text-xs font-bold hover:underline">Limpiar</button>
</div>
<div class="space-y-4">
<div>
<h4 class="text-xs font-bold text-outline uppercase mb-3">Categoría</h4>
<div class="space-y-2">
<label class="flex items-center gap-3 group cursor-pointer">
<input checked="" class="w-4 h-4 rounded border-outline-variant text-secondary focus:ring-secondary" type="checkbox"/>
<span class="text-body-sm text-on-surface group-hover:text-secondary transition-colors">Electrónica</span>
</label>
<label class="flex items-center gap-3 group cursor-pointer">
<input class="w-4 h-4 rounded border-outline-variant text-secondary focus:ring-secondary" type="checkbox"/>
<span class="text-body-sm text-on-surface group-hover:text-secondary transition-colors">Hogar &amp; Deco</span>
</label>
<label class="flex items-center gap-3 group cursor-pointer">
<input class="w-4 h-4 rounded border-outline-variant text-secondary focus:ring-secondary" type="checkbox"/>
<span class="text-body-sm text-on-surface group-hover:text-secondary transition-colors">Indumentaria</span>
</label>
</div>
</div>
<div>
<h4 class="text-xs font-bold text-outline uppercase mb-3">Marcas</h4>
<div class="flex flex-wrap gap-2">
<button class="px-3 py-1.5 rounded-full bg-secondary-container/10 text-secondary text-xs font-bold border border-secondary-container/30">Samsung</button>
<button class="px-3 py-1.5 rounded-full bg-surface-container-high text-on-surface-variant text-xs font-medium hover:bg-surface-container-highest transition-colors">Apple</button>
<button class="px-3 py-1.5 rounded-full bg-surface-container-high text-on-surface-variant text-xs font-medium hover:bg-surface-container-highest transition-colors">Sony</button>
</div>
</div>
<div>
<h4 class="text-xs font-bold text-outline uppercase mb-3">Estado de Stock</h4>
<div class="space-y-2">
<label class="flex items-center justify-between w-full p-2 rounded-lg border border-transparent hover:border-outline-variant transition-all cursor-pointer">
<div class="flex items-center gap-2">
<span class="w-3 h-3 rounded-full bg-on-tertiary-container"></span>
<span class="text-body-sm">Saludable</span>
</div>
<span class="text-xs font-mono text-outline">124</span>
</label>
<label class="flex items-center justify-between w-full p-2 rounded-lg border border-transparent hover:border-outline-variant transition-all cursor-pointer">
<div class="flex items-center gap-2">
<span class="w-3 h-3 rounded-full bg-amber-500"></span>
<span class="text-body-sm">Bajo</span>
</div>
<span class="text-xs font-mono text-outline">42</span>
</label>
<label class="flex items-center justify-between w-full p-2 rounded-lg border border-transparent hover:border-outline-variant transition-all cursor-pointer">
<div class="flex items-center gap-2">
<span class="w-3 h-3 rounded-full bg-error"></span>
<span class="text-body-sm">Crítico</span>
</div>
<span class="text-xs font-mono text-outline">18</span>
</label>
</div>
</div>
</div>
</div>
<!-- Small Insight Card -->
<div class="bg-primary-container p-card-padding rounded-xl relative overflow-hidden group">
<div class="relative z-10 text-on-primary">
<p class="text-xs opacity-70 font-label-md uppercase tracking-wider mb-1">Total de Valor Inventario</p>
<h4 class="text-2xl font-black">€482,900.50</h4>
<div class="mt-4 flex items-center gap-2 text-secondary-container font-bold text-sm">
<span class="material-symbols-outlined text-sm">trending_up</span>
                                +12.5% vs Mes Anterior
                            </div>
</div>
<div class="absolute -right-4 -bottom-4 opacity-10 group-hover:scale-110 transition-transform duration-500">
<span class="material-symbols-outlined text-[100px]" style="font-variation-settings: 'FILL' 1;">analytics</span>
</div>
</div>
</aside>
<!-- Data Table Main Content -->
<div class="col-span-12 lg:col-span-9 bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden border border-surface-container">
<div class="overflow-x-auto">
<table class="w-full text-left border-collapse">
<thead>
<tr class="bg-surface-container-low">
<th class="px-6 py-4 font-label-md text-label-md text-outline uppercase tracking-wider">Producto &amp; SKU</th>
<th class="px-6 py-4 font-label-md text-label-md text-outline uppercase tracking-wider">Categoría</th>
<th class="px-6 py-4 font-label-md text-label-md text-outline uppercase tracking-wider">Stock Físico</th>
<th class="px-6 py-4 font-label-md text-label-md text-outline uppercase tracking-wider">Nivel de Salud</th>
<th class="px-6 py-4 font-label-md text-label-md text-outline uppercase tracking-wider text-right">Acción</th>
</tr>
</thead>
<tbody class="divide-y divide-surface-container">
<!-- Row 1 -->
<tr class="hover:bg-surface-container-low transition-all group">
<td class="px-6 py-5">
<div class="flex items-center gap-4">
<div class="w-12 h-12 bg-surface-container rounded-lg overflow-hidden flex-shrink-0">
<img alt="Producto" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCyXOBhwCpkSJDgIDZTfyfPVlqnkKHfCLYtMrSNhaf3mr3AG2A0vzc-wSSizKMpz6E_CNwks-xmBq09Xy1aFq076ONGdRf9qd4ZM1oIa0BqcQkBDjCAHsYpSZy07miJeC_t1mR7-vYi1ASpXSErn7cqyXMwMPGi0bhINlraF5KuMDFXear9aCcNj8byscobhiGEVRKA_u5Dgfs8FVgqCqq-rZy_FSPykT6OsuzBZ-4ngvzQjv6Etc_EKGrgMGzsE-81Bb59jjS_co2k"/>
</div>
<div>
<p class="font-bold text-on-surface">Smartwatch Series X7</p>
<p class="text-xs font-mono text-outline">SKU: SMW-7889-BL</p>
</div>
</div>
</td>
<td class="px-6 py-5">
<span class="px-2.5 py-1 bg-secondary-container/10 rounded text-xs font-bold text-on-secondary-container">Electrónica</span>
</td>
<td class="px-6 py-5">
<span class="text-title-md font-bold text-on-surface">142 <span class="text-xs text-outline font-normal">unid.</span></span>
</td>
<td class="px-6 py-5 w-64">
<div class="flex flex-col gap-1.5">
<div class="flex justify-between text-[10px] font-bold uppercase text-on-tertiary-container">
<span>Saludable</span>
<span>85%</span>
</div>
<div class="stock-bar-container">
<div class="stock-bar-fill bg-on-tertiary-container" style="width: 85%;"></div>
</div>
</div>
</td>
<td class="px-6 py-5 text-right">
<button class="inline-flex items-center gap-2 px-3 py-2 text-secondary hover:bg-secondary/10 rounded-lg font-bold transition-colors">
<span class="material-symbols-outlined text-lg">edit_note</span> Ajustar
                                        </button>
</td>
</tr>
<!-- Row 2 -->
<tr class="hover:bg-surface-container-low transition-all group">
<td class="px-6 py-5">
<div class="flex items-center gap-4">
<div class="w-12 h-12 bg-surface-container rounded-lg overflow-hidden flex-shrink-0">
<img alt="Producto" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBu38yCK6_i7SxJJtASMPoB4o2-sLZZJaAHJpNOaXckM6CaqeUbB4rB8ViDxCgIfdG3UoJgt8Xf1a6khlwd8dmEjWUf_xeChsdKBZSo_OXba7gG9H6pVJ2-qWJxy6W0MqMIqblBldyFkvjz3m6RlefqYYBZSCxfZdw0kAMcob_CJXwxSd_cA0POyVSU6QkAmktYIdblx_0oCUqxcCLICCAM9NdEveHqH6jBhXdxZnnYXn4racTYulPcERYMqp2Fuc1tazFOBVmQ-56d"/>
</div>
<div>
<p class="font-bold text-on-surface">Zapatillas Runner Pro 5</p>
<p class="text-xs font-mono text-outline">SKU: SHO-1122-RD</p>
</div>
</div>
</td>
<td class="px-6 py-5">
<span class="px-2.5 py-1 bg-surface-container-high rounded text-xs font-bold text-on-surface-variant">Calzado</span>
</td>
<td class="px-6 py-5">
<span class="text-title-md font-bold text-on-surface">12 <span class="text-xs text-outline font-normal">unid.</span></span>
</td>
<td class="px-6 py-5 w-64">
<div class="flex flex-col gap-1.5">
<div class="flex justify-between text-[10px] font-bold uppercase text-amber-600">
<span>Stock Bajo</span>
<span>20%</span>
</div>
<div class="stock-bar-container">
<div class="stock-bar-fill bg-amber-500" style="width: 20%;"></div>
</div>
</div>
</td>
<td class="px-6 py-5 text-right">
<button class="inline-flex items-center gap-2 px-3 py-2 text-secondary hover:bg-secondary/10 rounded-lg font-bold transition-colors">
<span class="material-symbols-outlined text-lg">edit_note</span> Ajustar
                                        </button>
</td>
</tr>
<!-- Row 3 -->
<tr class="hover:bg-surface-container-low transition-all group">
<td class="px-6 py-5">
<div class="flex items-center gap-4">
<div class="w-12 h-12 bg-surface-container rounded-lg overflow-hidden flex-shrink-0">
<img alt="Producto" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCZFV6lU_sBBsQxniM1_OKbdMNfL87CYqg6oTDSrCMnhEjwXVd5OOtXajSJL9oFJ1jpHbFX2rjwaW6QTXS4NQglzNeEV0ol_e2WHG0T1tHfxeVfL8eIUpI9s6ZRduIjTUj36klhPnyLshmKb59qBwympcLws9294igWjipNqVjbp0bH9aGNjiZlanIR1AHUXKikybDFqfyDIB1LYTPagzILnxKaoIHhGuW9aaf0Qf_5vYy0PlRefzQp9Z6qFal_8uuWOjPurJXfksJR"/>
</div>
<div>
<p class="font-bold text-on-surface">Auriculares Bose QC45</p>
<p class="text-xs font-mono text-outline">SKU: AUD-9901-BK</p>
</div>
</div>
</td>
<td class="px-6 py-5">
<span class="px-2.5 py-1 bg-secondary-container/10 rounded text-xs font-bold text-on-secondary-container">Electrónica</span>
</td>
<td class="px-6 py-5">
<span class="text-title-md font-bold text-error">2 <span class="text-xs text-outline font-normal">unid.</span></span>
</td>
<td class="px-6 py-5 w-64">
<div class="flex flex-col gap-1.5">
<div class="flex justify-between text-[10px] font-bold uppercase text-error">
<span>Crítico</span>
<span>4%</span>
</div>
<div class="stock-bar-container">
<div class="stock-bar-fill bg-error" style="width: 4%;"></div>
</div>
</div>
</td>
<td class="px-6 py-5 text-right">
<button class="inline-flex items-center gap-2 px-3 py-2 text-secondary hover:bg-secondary/10 rounded-lg font-bold transition-colors">
<span class="material-symbols-outlined text-lg">edit_note</span> Ajustar
                                        </button>
</td>
</tr>
<!-- Row 4 -->
<tr class="hover:bg-surface-container-low transition-all group">
<td class="px-6 py-5">
<div class="flex items-center gap-4">
<div class="w-12 h-12 bg-surface-container rounded-lg overflow-hidden flex-shrink-0">
<img alt="Producto" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuB9tA2ZdekSXEW5fOfdEMKwqBJjBZONcqGNCQctkRfUOVcDoxbHJAhAu6dJkWQmAwBA6H73nJRjqhqzCyFgcHKWmy6Mj5LNX_Kw-TD-qRxSPZAmds6h1sxmxblixN4eMiqYbGMsIa1_m3N4sFQdX9aTV1RT-l8paQDfsQ8QgHX0JC7U7LSNFIUrLH9J9JdS5GAExIOUu1l7xL56joVY5NqgBAG9olbF06l-RomGXCLAWqR5z464eBa15Vo_A2vS4O9Ugg_J8ofNpmFk"/>
</div>
<div>
<p class="font-bold text-on-surface">Mochila Trekking Explorer</p>
<p class="text-xs font-mono text-outline">SKU: ACC-3342-BN</p>
</div>
</div>
</td>
<td class="px-6 py-5">
<span class="px-2.5 py-1 bg-surface-container-high rounded text-xs font-bold text-on-surface-variant">Accesorios</span>
</td>
<td class="px-6 py-5">
<span class="text-title-md font-bold text-on-surface">56 <span class="text-xs text-outline font-normal">unid.</span></span>
</td>
<td class="px-6 py-5 w-64">
<div class="flex flex-col gap-1.5">
<div class="flex justify-between text-[10px] font-bold uppercase text-on-tertiary-container">
<span>Saludable</span>
<span>65%</span>
</div>
<div class="stock-bar-container">
<div class="stock-bar-fill bg-on-tertiary-container" style="width: 65%;"></div>
</div>
</div>
</td>
<td class="px-6 py-5 text-right">
<button class="inline-flex items-center gap-2 px-3 py-2 text-secondary hover:bg-secondary/10 rounded-lg font-bold transition-colors">
<span class="material-symbols-outlined text-lg">edit_note</span> Ajustar
                                        </button>
</td>
</tr>
</tbody>
</table>
</div>
<!-- Table Footer Pagination -->
<div class="p-6 bg-surface-container-low border-t border-surface-container flex items-center justify-between">
<p class="text-sm text-on-surface-variant">Mostrando <span class="font-bold">1-10</span> de <span class="font-bold">184</span> productos</p>
<div class="flex items-center gap-1">
<button class="p-2 rounded hover:bg-surface-container-highest text-outline disabled:opacity-30" disabled="">
<span class="material-symbols-outlined">chevron_left</span>
</button>
<button class="w-8 h-8 rounded bg-secondary text-on-primary font-bold text-sm">1</button>
<button class="w-8 h-8 rounded hover:bg-surface-container-highest text-on-surface-variant font-bold text-sm">2</button>
<button class="w-8 h-8 rounded hover:bg-surface-container-highest text-on-surface-variant font-bold text-sm">3</button>
<span class="px-2 text-outline">...</span>
<button class="w-8 h-8 rounded hover:bg-surface-container-highest text-on-surface-variant font-bold text-sm">19</button>
<button class="p-2 rounded hover:bg-surface-container-highest text-outline">
<span class="material-symbols-outlined">chevron_right</span>
</button>
</div>
</div>
</div>
</div>
</main>
</div>
<!-- Quick Adjustment Modal -->
<div class="hidden fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm" id="adjustmentModal">
<div class="bg-surface-container-lowest w-full max-w-md rounded-xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200">
<div class="p-6 border-b border-outline-variant flex justify-between items-center bg-surface-container-low">
<h3 class="font-title-md text-title-md">Ajuste de Stock Rápido</h3>
<button class="p-2 hover:bg-surface-container-highest rounded-full transition-colors" onclick="toggleModal()">
<span class="material-symbols-outlined">close</span>
</button>
</div>
<div class="p-6 space-y-6">
<div class="flex items-center gap-4 bg-surface-container-low p-4 rounded-xl border border-surface-container">
<img alt="Thumb" class="w-12 h-12 rounded-lg object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAE_tlkdXJzJxrL0mWxgvUxoKLyhcLVwDDM66gcp86gkVZvlPKPhnfPzePooNp7t9WQl3u2VIzvDXIkaMbh4D7b1Yf2ukkZP8aPbML_iT4m8OXYULK6cabOwi7RSv6lZe1Jqh6jv6ZadMHp9doVFfGTSynnmngX5AO8184dbiA0xO2TxxL4Nhw65kbV96wYgOd5SzQYVQ7S1iHqhewAzIlFbQS8_sw-qZ6On7YUb2K6-hnSLh11U7H31pAWHT7CORFJcA_pYibnIB_0"/>
<div>
<p class="font-bold text-on-surface leading-tight">Smartwatch Series X7</p>
<p class="text-xs text-outline">Stock Actual: 142 unid.</p>
</div>
</div>
<div class="grid grid-cols-2 gap-4">
<button class="p-4 rounded-xl border-2 border-secondary bg-secondary/5 flex flex-col items-center gap-2 group transition-all active:scale-95">
<span class="material-symbols-outlined text-3xl text-secondary">add_box</span>
<span class="font-bold text-on-surface">Ingreso</span>
</button>
<button class="p-4 rounded-xl border-2 border-transparent bg-surface-container-low flex flex-col items-center gap-2 hover:border-outline-variant transition-all active:scale-95">
<span class="material-symbols-outlined text-3xl text-outline">indeterminate_check_box</span>
<span class="font-bold text-on-surface">Egreso</span>
</button>
</div>
<div class="space-y-4">
<div>
<label class="block text-xs font-bold text-outline uppercase mb-2">Cantidad a ajustar</label>
<input class="w-full text-center text-3xl font-black bg-surface-container-low border border-outline-variant rounded-xl py-4 focus:ring-2 focus:ring-secondary outline-none" type="number" value="1"/>
</div>
<div>
<label class="block text-xs font-bold text-outline uppercase mb-2">Motivo del ajuste</label>
<select class="w-full bg-surface-container-low border border-outline-variant rounded-xl py-3 px-4 text-body-sm focus:ring-2 focus:ring-secondary outline-none appearance-none">
<option>Reposición de Mercadería</option>
<option>Corrección de Inventario</option>
<option>Producto Dañado</option>
<option>Devolución de Cliente</option>
</select>
</div>
</div>
</div>
<div class="p-6 bg-surface-container-low border-t border-outline-variant flex gap-3">
<button class="flex-1 py-3 font-bold text-on-surface hover:bg-surface-container-highest rounded-xl transition-colors" onclick="toggleModal()">Cancelar</button>
<button class="flex-1 py-3 font-bold bg-secondary text-on-primary rounded-xl shadow-lg shadow-secondary/20 hover:bg-secondary/90 transition-colors" onclick="toggleModal()">Confirmar Ajuste</button>
</div>
</div>
</div>
<script>
        function toggleModal() {
            const modal = document.getElementById('adjustmentModal');
            modal.classList.toggle('hidden');
        }

        document.querySelectorAll('button').forEach(btn => {
            if (btn.innerText.includes('Ajustar')) {
                btn.addEventListener('click', toggleModal);
            }
        });
    </script>
</body></html>

<!-- Panel de Administración - Gestión de Empresa (SIGA) -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>SIGA Dashboard - Nexus Admin</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&amp;family=JetBrains+Mono&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "outline": "#777682",
                        "on-secondary-fixed": "#001f27",
                        "tertiary-container": "#002018",
                        "outline-variant": "#c7c5d3",
                        "secondary-fixed-dim": "#4cd6fb",
                        "surface-dim": "#ded8e1",
                        "surface-variant": "#e6e0e9",
                        "background": "#fdf7ff",
                        "surface-container-low": "#f8f1fa",
                        "on-surface": "#1d1b21",
                        "inverse-on-surface": "#f5eff7",
                        "on-secondary-container": "#005c70",
                        "on-primary-fixed-variant": "#393e8c",
                        "surface-bright": "#fdf7ff",
                        "on-tertiary-fixed-variant": "#005140",
                        "primary-fixed-dim": "#bfc2ff",
                        "secondary-container": "#50d9fe",
                        "primary-fixed": "#e0e0ff",
                        "on-error-container": "#93000a",
                        "on-error": "#ffffff",
                        "on-secondary": "#ffffff",
                        "surface-container-high": "#ece6ef",
                        "error": "#ba1a1a",
                        "on-primary-fixed": "#070a61",
                        "surface-tint": "#F0F9FF",
                        "on-tertiary-container": "#009579",
                        "error-container": "#ffdad6",
                        "on-surface-variant": "#464651",
                        "border-muted": "#D9D9D9",
                        "surface-container-highest": "#e6e0e9",
                        "on-background": "#1d1b21",
                        "secondary": "#00677d",
                        "primary": "#000000",
                        "on-primary-container": "#777dcf",
                        "on-primary": "#ffffff",
                        "tertiary-fixed-dim": "#5adcb9",
                        "on-tertiary-fixed": "#002018",
                        "surface-container": "#f2ecf5",
                        "on-secondary-fixed-variant": "#004e5f",
                        "surface-container-lowest": "#ffffff",
                        "inverse-surface": "#322f36",
                        "tertiary-fixed": "#79f8d5",
                        "surface": "#fdf7ff",
                        "on-tertiary": "#ffffff",
                        "inverse-primary": "#bfc2ff",
                        "success-vibrant": "#10B981",
                        "primary-container": "#070a61",
                        "tertiary": "#000000",
                        "secondary-fixed": "#b3ebff"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "container-max": "1280px",
                        "margin-desktop": "32px",
                        "gutter": "24px",
                        "margin-mobile": "16px",
                        "base": "8px"
                    },
                    "fontFamily": {
                        "display-lg": ["Hanken Grotesk"],
                        "code-sm": ["jetbrainsMono"],
                        "title-md": ["Hanken Grotesk"],
                        "label-md": ["Hanken Grotesk"],
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "body-sm": ["Hanken Grotesk"],
                        "headline-lg": ["Hanken Grotesk"],
                        "body-lg": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "display-lg": ["56px", {"lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                        "code-sm": ["13px", {"lineHeight": "18px", "fontWeight": "400"}],
                        "title-md": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "label-md": ["14px", {"lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500"}],
                        "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "600"}],
                        "body-sm": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                        "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                        "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}]
                    }
                }
            }
        }
    </script>
<style>
        body { font-family: 'Hanken Grotesk', sans-serif; background-color: #fdf7ff; }
        .glass-card {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 4px 12px rgba(3, 4, 94, 0.05);
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
            vertical-align: middle;
        }
        .active-nav-bg {
            background: linear-gradient(90deg, rgba(76, 214, 251, 0.15) 0%, transparent 100%);
        }
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background: #c7c5d3; border-radius: 10px; }
    </style>
</head>
<body class="text-on-surface">
<!-- SideNavBar Anchor -->
<aside class="bg-primary-container h-screen w-64 fixed left-0 top-0 border-r border-outline-variant flex flex-col h-full py-8 px-4 shadow-sm z-20">
<div class="mb-10 px-2 flex items-center gap-3">
<img alt="SIGA Enterprise Logo" class="h-10 w-auto" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCba17KNy_-xrFGxeOpbe6VBAl-cVlc6W2iEoKwbtmijFKiDy_OolU-v7TdcyQAXsFx3MvpaRDqWEgX7xHl-navHr02LaOePLoe7H6JSglulgliwLqbKM1aCR2Q8rM1SJ0Jq4BOzvM4MSsvGL41UpxmqQbjRxJhnXZsitiWHWldaQyQdc_5v5by0WmA7C2Z54CHvIqNgAFK-iGFAX7Cu4kQtrOaKTHjSAa1-HoTUab8Ake_g2n7lW_blXUjaNW77sZBCK5_iJTmNfXE"/>
<div class="flex flex-col">
<span class="font-display-lg text-headline-lg font-bold text-secondary-fixed-dim leading-none">SIGA</span>
<span class="font-label-md text-label-md text-secondary-fixed-dim/70">Nexus Admin</span>
</div>
</div>
<nav class="flex-1 space-y-2">
<!-- Inicio Active -->
<a class="flex items-center gap-4 px-4 py-3 text-secondary-fixed-dim font-bold border-r-4 border-secondary-fixed-dim active-nav-bg transition-colors duration-200 group" href="#">
<span class="material-symbols-outlined">dashboard</span>
<span class="font-label-md text-label-md">Inicio</span>
</a>
<a class="flex items-center gap-4 px-4 py-3 text-on-secondary-fixed-variant hover:bg-on-secondary-fixed-variant/10 hover:text-secondary-fixed-dim transition-colors duration-200 group" href="#">
<span class="material-symbols-outlined">inventory_2</span>
<span class="font-label-md text-label-md">Inventario</span>
</a>
<a class="flex items-center gap-4 px-4 py-3 text-on-secondary-fixed-variant hover:bg-on-secondary-fixed-variant/10 hover:text-secondary-fixed-dim transition-colors duration-200 group" href="#">
<span class="material-symbols-outlined">store</span>
<span class="font-label-md text-label-md">Locales</span>
</a>
<a class="flex items-center gap-4 px-4 py-3 text-on-secondary-fixed-variant hover:bg-on-secondary-fixed-variant/10 hover:text-secondary-fixed-dim transition-colors duration-200 group" href="#">
<span class="material-symbols-outlined">group</span>
<span class="font-label-md text-label-md">Usuarios</span>
</a>
<a class="flex items-center gap-4 px-4 py-3 text-on-secondary-fixed-variant hover:bg-on-secondary-fixed-variant/10 hover:text-secondary-fixed-dim transition-colors duration-200 group" href="#">
<span class="material-symbols-outlined">point_of_sale</span>
<span class="font-label-md text-label-md">POS</span>
</a>
<a class="flex items-center gap-4 px-4 py-3 text-on-secondary-fixed-variant hover:bg-on-secondary-fixed-variant/10 hover:text-secondary-fixed-dim transition-colors duration-200 group" href="#">
<span class="material-symbols-outlined">settings</span>
<span class="font-label-md text-label-md">Configuración</span>
</a>
</nav>
<div class="mt-auto pt-6 border-t border-on-secondary-fixed-variant/20">
<button class="w-full bg-secondary-fixed-dim text-on-secondary-fixed font-bold py-3 px-4 rounded-lg flex items-center justify-center gap-2 scale-95 active:scale-90 transition-transform">
<span class="material-symbols-outlined">assessment</span>
<span class="font-label-md">Generar Reporte</span>
</button>
</div>
</aside>
<!-- TopNavBar Anchor -->
<header class="fixed top-0 right-0 w-[calc(100%-16rem)] z-10 bg-surface/80 backdrop-blur-md flex justify-between items-center h-16 px-gutter border-b border-outline-variant shadow-sm">
<div class="flex items-center gap-4 flex-1">
<div class="relative w-full max-w-md focus-within:ring-2 focus-within:ring-secondary-fixed-dim rounded-full transition-all">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
<input class="w-full bg-surface-container-low border-none rounded-full py-2 pl-10 pr-4 text-body-sm focus:ring-0" placeholder="Buscar en el dashboard..." type="text"/>
</div>
</div>
<div class="flex items-center gap-6">
<button class="relative text-on-surface-variant hover:text-secondary transition-colors">
<span class="material-symbols-outlined">notifications</span>
<span class="absolute top-0 right-0 w-2 h-2 bg-error rounded-full border-2 border-surface"></span>
</button>
<button class="text-on-surface-variant hover:text-secondary transition-colors">
<span class="material-symbols-outlined">help</span>
</button>
<div class="flex items-center gap-3 ml-4 pl-4 border-l border-outline-variant">
<div class="text-right hidden sm:block">
<p class="text-label-md font-bold text-on-surface leading-none">Elizabeth Nexus</p>
<p class="text-[11px] text-on-surface-variant">Dueña de Negocio</p>
</div>
<img alt="Avatar del Administrador" class="w-10 h-10 rounded-full border-2 border-secondary-container object-cover" data-alt="A professional headshot of a middle-aged woman with a confident smile, set against a blurred modern office background. The lighting is bright and clean, characteristic of a high-end corporate profile. Her attire is professional yet approachable, reflecting her status as a business owner in a tech-forward ecosystem." src="https://lh3.googleusercontent.com/aida-public/AB6AXuBLzA7-PIq56r0UR6WGtj9xR_9TAR-S8IjiiFjoCACKh9gNIC74gzS2hhfaE9mLYtKPIsotU1LXOyrrCHEBX85vjGRrg0YN2tmP-8gztF2MVJqdaoI7BCooTNMhA2tA0B2gWHXGR7iiOQIqs51Xju4e07549YBuvKBVoCbUbYe5WjZZlBDBVuc5bK2jJdFv1g3GqcU1cB8VopG76SDecH8r14CRpTF2XSdibHu-uu6TnTbZDvi7ulayHNmp6drHsvUlDmJ0adJ6N6JE"/>
</div>
</div>
</header>
<!-- Main Canvas -->
<main class="ml-64 pt-24 p-gutter min-h-screen">
<!-- Welcome Header -->
<div class="mb-10">
<h1 class="font-headline-lg text-headline-lg text-on-surface">Panel de Gestión General</h1>
<p class="text-body-lg text-on-surface-variant mt-1">Monitorea el rendimiento de tus sucursales y equipo en tiempo real.</p>
</div>
<!-- KPI Bento Grid -->
<div class="grid grid-cols-1 md:grid-cols-4 gap-gutter mb-10">
<div class="glass-card p-6 rounded-xl border-l-4 border-secondary">
<div class="flex justify-between items-start mb-4">
<span class="text-on-surface-variant font-label-md">Ventas Hoy</span>
<span class="material-symbols-outlined text-secondary">payments</span>
</div>
<div class="text-3xl font-bold text-on-surface">$12,450.00</div>
<div class="flex items-center gap-1 text-success-vibrant text-sm mt-2 font-medium">
<span class="material-symbols-outlined text-sm">trending_up</span>
<span>+8.4% vs ayer</span>
</div>
</div>
<div class="glass-card p-6 rounded-xl border-l-4 border-tertiary-fixed-dim">
<div class="flex justify-between items-start mb-4">
<span class="text-on-surface-variant font-label-md">Empleados Activos</span>
<span class="material-symbols-outlined text-tertiary-fixed-dim" style="font-variation-settings: 'FILL' 1;">badge</span>
</div>
<div class="text-3xl font-bold text-on-surface">24</div>
<div class="text-on-surface-variant text-sm mt-2">En 3 turnos rotativos</div>
</div>
<div class="glass-card p-6 rounded-xl border-l-4 border-secondary-container">
<div class="flex justify-between items-start mb-4">
<span class="text-on-surface-variant font-label-md">Locales Operativos</span>
<span class="material-symbols-outlined text-secondary-container">storefront</span>
</div>
<div class="text-3xl font-bold text-on-surface">5 / 6</div>
<div class="text-on-surface-variant text-sm mt-2">1 en mantenimiento preventivo</div>
</div>
<div class="glass-card p-6 rounded-xl border-l-4 border-on-primary-container">
<div class="flex justify-between items-start mb-4">
<span class="text-on-surface-variant font-label-md">Stock Crítico</span>
<span class="material-symbols-outlined text-error">warning</span>
</div>
<div class="text-3xl font-bold text-on-surface">12 Art.</div>
<div class="text-error text-sm mt-2 font-medium">Requiere reabastecimiento</div>
</div>
</div>
<!-- Main Workspace Layout -->
<div class="grid grid-cols-1 lg:grid-cols-3 gap-gutter items-start">
<!-- User Management Card (Left Column) -->
<div class="lg:col-span-2 glass-card rounded-xl overflow-hidden">
<div class="px-6 py-4 border-b border-outline-variant bg-surface-container-low flex justify-between items-center">
<h2 class="font-title-md text-title-md text-on-surface flex items-center gap-2">
<span class="material-symbols-outlined text-secondary">group</span>
                        Gestión de Usuarios
                    </h2>
<button class="text-secondary font-label-md hover:underline">Ver todos</button>
</div>
<div class="divide-y divide-outline-variant/30">
<!-- User Row -->
<div class="px-6 py-4 flex items-center justify-between hover:bg-surface-tint/20 transition-colors">
<div class="flex items-center gap-4">
<img class="w-10 h-10 rounded-full object-cover" data-alt="Close-up portrait of a woman with dark hair and glasses, representing a business executive. The environment is a clean, minimalist professional setting with soft lighting and a cool teal color palette. Her expression is calm and authoritative." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDcaiiSLpchdp3tjDlf1k4nQIBqnH6SoUun-XNMax0-sT_Xbw7q6ZWCkSyXFiTLnijWIYzQZxpJB4TPwa52D0Mihi7VFSIL2FwSsB1caDZg-u7JJnxCRrclnV2TxFZXFhuLS4pjG3p55ReVAq5fhwX9YurvDfQnPY2kai9TtOgKT2qC-7Dyb410462buxvGsBAxJswPjGic532u4OanqZmDOq5IRDgnFS0QrsoTdmctPSlc0_522iPdnF3pyOWn8aKfJidCFsZjDERl"/>
<div>
<p class="font-bold text-on-surface">Elizabeth Vasquez</p>
<p class="text-sm text-on-surface-variant">elizabeth.v@siganexus.com</p>
</div>
</div>
<div class="flex items-center gap-8">
<span class="px-3 py-1 bg-secondary-fixed text-on-secondary-fixed text-xs font-bold rounded-full">Dueña</span>
<span class="text-sm text-on-surface-variant">Sede Central</span>
<button class="p-2 text-outline hover:text-secondary"><span class="material-symbols-outlined">more_vert</span></button>
</div>
</div>
<!-- User Row -->
<div class="px-6 py-4 flex items-center justify-between hover:bg-surface-tint/20 transition-colors">
<div class="flex items-center gap-4">
<img class="w-10 h-10 rounded-full object-cover" data-alt="A professional male worker in a modern warehouse environment, wearing a safety vest over corporate casual clothing. The background features organized high-tech shelving systems. The lighting is bright and industrial, with a clean aesthetic that fits a modern inventory management role." src="https://lh3.googleusercontent.com/aida-public/AB6AXuBrdQEjSsHyqxWCYquTrvVADNZB8RjZr4RFjW5qfS4IDNGlJ7ClYI2oZyNzmmr-xTOWorYEutM-bAjRBX2AXFAum0KSVY8ZC8yvVXZ84evgpNmWS6Taao9HK0Pw90zhVJK3qRmF36RTT7vWhGc4HH5ihOVHwdd8T48lGn_mOV9lfMZyUCY8Nws92b2djjS1Njrt0pEoCAuY4k_U4VC0tdRQsSkiRZMoD7576v2KulsRm0luBn7N89jOkOGnCLo1eMQOi637Ua1EjWp9"/>
<div>
<p class="font-bold text-on-surface">Héctor Méndez</p>
<p class="text-sm text-on-surface-variant">hector.inv@siganexus.com</p>
</div>
</div>
<div class="flex items-center gap-8">
<span class="px-3 py-1 bg-tertiary-fixed text-on-tertiary-fixed text-xs font-bold rounded-full">Inventario</span>
<span class="text-sm text-on-surface-variant">Depósito Norte</span>
<button class="p-2 text-outline hover:text-secondary"><span class="material-symbols-outlined">more_vert</span></button>
</div>
</div>
<!-- User Row -->
<div class="px-6 py-4 flex items-center justify-between hover:bg-surface-tint/20 transition-colors">
<div class="flex items-center gap-4">
<img class="w-10 h-10 rounded-full object-cover" data-alt="Portrait of a young professional woman in a retail or service point setting. She has a friendly, service-oriented expression. The background is a clean, modern store environment with teal accents and warm lighting. The image style is polished and high-quality, representing a professional cashier or service staff member." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCiMnDJZXuqAw96Jon7zZR4Enlx2l0XhPOA4xwkljMueaIWHjmW8HQM80YIKmwK3jULgJiKZ1xgzap7UpkN_5Tqqj-McjZgMpisOVwrMJnJBx_xEjZXHuiF2bfcfjN18Su2gpzlZezadQ_yafkMZ1MgeiuTE1YBOdlVtoBwuoF6fzvw891x9D_yQNtAaE8-czJO6DlLSoqRwxnc7GFBboWitXuOvUkqneqhsoxnFWkvXma_NqT09PSH-mdR9sODOThwBqunaEDNZn7w"/>
<div>
<p class="font-bold text-on-surface">Yesenia Torres</p>
<p class="text-sm text-on-surface-variant">yesenia.pos@siganexus.com</p>
</div>
</div>
<div class="flex items-center gap-8">
<span class="px-3 py-1 bg-secondary-container text-on-secondary-container text-xs font-bold rounded-full">Cajera</span>
<span class="text-sm text-on-surface-variant">Sucursal Plaza</span>
<button class="p-2 text-outline hover:text-secondary"><span class="material-symbols-outlined">more_vert</span></button>
</div>
</div>
</div>
</div>
<!-- Branch & Catalog Control (Right Column) -->
<div class="space-y-gutter">
<!-- Locales Card -->
<div class="glass-card rounded-xl p-6">
<div class="flex justify-between items-center mb-6">
<h3 class="font-title-md text-title-md text-on-surface flex items-center gap-2">
<span class="material-symbols-outlined text-secondary-container">location_on</span>
                            Locales Activos
                        </h3>
<button class="p-1 rounded-full hover:bg-surface-variant transition-colors">
<span class="material-symbols-outlined text-on-surface-variant">add_circle</span>
</button>
</div>
<div class="space-y-4">
<div class="flex items-center gap-3 p-3 rounded-lg border border-outline-variant/20 bg-surface-container-lowest">
<div class="w-12 h-12 rounded-lg bg-secondary-fixed-dim/20 flex items-center justify-center text-secondary">
<span class="material-symbols-outlined">home_work</span>
</div>
<div class="flex-1">
<p class="font-bold text-sm text-on-surface">Sede Central</p>
<p class="text-[11px] text-on-surface-variant">Av. Principal 123, CMDX</p>
</div>
<span class="w-2 h-2 rounded-full bg-success-vibrant"></span>
</div>
<div class="flex items-center gap-3 p-3 rounded-lg border border-outline-variant/20 bg-surface-container-lowest">
<div class="w-12 h-12 rounded-lg bg-tertiary-fixed-dim/20 flex items-center justify-center text-on-tertiary-container">
<span class="material-symbols-outlined">shopping_bag</span>
</div>
<div class="flex-1">
<p class="font-bold text-sm text-on-surface">Sucursal Plaza</p>
<p class="text-[11px] text-on-surface-variant">Centro Comercial Altamira</p>
</div>
<span class="w-2 h-2 rounded-full bg-success-vibrant"></span>
</div>
</div>
</div>
<!-- Catalog Shortcut Card -->
<div class="bg-primary-container text-on-primary rounded-xl p-6 shadow-lg overflow-hidden relative group">
<!-- Atmospheric Subtle Effect -->
<div class="absolute -right-10 -bottom-10 w-40 h-40 bg-secondary-fixed-dim/20 rounded-full blur-3xl group-hover:scale-125 transition-transform duration-700"></div>
<h3 class="font-title-md text-title-md mb-2 relative z-10">Catálogo Pro</h3>
<p class="text-sm text-secondary-fixed-dim/80 mb-6 relative z-10">Gestiona categorías y productos de forma masiva.</p>
<div class="grid grid-cols-2 gap-3 relative z-10">
<button class="bg-white/10 hover:bg-white/20 transition-colors py-3 rounded-lg text-center text-sm font-medium border border-white/10">
<span class="material-symbols-outlined block mb-1">category</span>
                            Categorías
                        </button>
<button class="bg-white/10 hover:bg-white/20 transition-colors py-3 rounded-lg text-center text-sm font-medium border border-white/10">
<span class="material-symbols-outlined block mb-1">inventory</span>
                            Productos
                        </button>
</div>
</div>
</div>
</div>
<!-- Performance Graph Area (Asymmetric Layout) -->
<div class="mt-10 glass-card rounded-xl p-6">
<div class="flex justify-between items-center mb-8">
<div>
<h3 class="font-title-md text-title-md text-on-surface">Actividad del Sistema</h3>
<p class="text-sm text-on-surface-variant">Resumen de operaciones en la última semana</p>
</div>
<div class="flex bg-surface-container-high rounded-lg p-1">
<button class="px-4 py-1.5 rounded-md bg-surface text-xs font-bold text-secondary shadow-sm">Semanal</button>
<button class="px-4 py-1.5 rounded-md text-xs font-medium text-on-surface-variant">Mensual</button>
</div>
</div>
<div class="h-64 w-full flex items-end justify-between gap-4 px-4 pb-2">
<!-- Simple representative bar chart for visual quality -->
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-container/40 rounded-t-lg transition-all duration-500 group-hover:bg-secondary-container h-[60%] relative">
<div class="absolute -top-8 left-1/2 -translate-x-1/2 bg-on-surface text-white text-[10px] py-1 px-2 rounded opacity-0 group-hover:opacity-100 transition-opacity">$2.1k</div>
</div>
<span class="text-[10px] text-on-surface-variant font-bold">LUN</span>
</div>
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-container/40 rounded-t-lg transition-all duration-500 group-hover:bg-secondary-container h-[45%]"></div>
<span class="text-[10px] text-on-surface-variant font-bold">MAR</span>
</div>
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-container/40 rounded-t-lg transition-all duration-500 group-hover:bg-secondary-container h-[85%]"></div>
<span class="text-[10px] text-on-surface-variant font-bold">MIE</span>
</div>
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-container/40 rounded-t-lg transition-all duration-500 group-hover:bg-secondary-container h-[70%]"></div>
<span class="text-[10px] text-on-surface-variant font-bold">JUE</span>
</div>
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-fixed-dim rounded-t-lg h-[95%] relative">
<div class="absolute -top-8 left-1/2 -translate-x-1/2 bg-on-surface text-white text-[10px] py-1 px-2 rounded opacity-100 transition-opacity">$4.5k</div>
</div>
<span class="text-[10px] text-on-surface-variant font-bold">VIE</span>
</div>
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-container/40 rounded-t-lg transition-all duration-500 group-hover:bg-secondary-container h-[30%]"></div>
<span class="text-[10px] text-on-surface-variant font-bold">SAB</span>
</div>
<div class="flex flex-col items-center gap-2 flex-1 group">
<div class="w-full bg-secondary-container/40 rounded-t-lg transition-all duration-500 group-hover:bg-secondary-container h-[20%]"></div>
<span class="text-[10px] text-on-surface-variant font-bold">DOM</span>
</div>
</div>
</div>
</main>
<script>
        // Micro-interactions and atmospheric effects
        document.addEventListener('DOMContentLoaded', () => {
            const cards = document.querySelectorAll('.glass-card');
            cards.forEach(card => {
                card.addEventListener('mouseenter', () => {
                    card.style.transform = 'translateY(-2px)';
                    card.style.transition = 'transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)';
                });
                card.addEventListener('mouseleave', () => {
                    card.style.transform = 'translateY(0)';
                });
            });

            // Active state logic fallback check
            const navItems = document.querySelectorAll('aside nav a');
            navItems.forEach(item => {
                item.addEventListener('click', (e) => {
                    navItems.forEach(n => {
                        n.classList.remove('text-secondary-fixed-dim', 'font-bold', 'border-r-4', 'border-secondary-fixed-dim', 'active-nav-bg');
                        n.classList.add('text-on-secondary-fixed-variant');
                    });
                    item.classList.add('text-secondary-fixed-dim', 'font-bold', 'border-r-4', 'border-secondary-fixed-dim', 'active-nav-bg');
                    item.classList.remove('text-on-secondary-fixed-variant');
                });
            });
        });
    </script>
</body></html>

<!-- Panel Global de Administración - SIGA Platform (Ops & SaaS) -->
<!DOCTYPE html><html class="light" lang="es"><head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<title>SIGA Master Admin Dashboard</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&amp;family=JetBrains+Mono:wght@400&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<script id="tailwind-config">
        tailwind.config = {
          darkMode: "class",
          theme: {
            extend: {
              "colors": {
                      "on-surface": "#1d1b21",
                      "tertiary-fixed-dim": "#5adcb9",
                      "on-primary": "#ffffff",
                      "inverse-on-surface": "#f5eff7",
                      "outline": "#777682",
                      "surface-container-highest": "#e6e0e9",
                      "border-muted": "#D9D9D9",
                      "on-error": "#ffffff",
                      "on-error-container": "#93000a",
                      "on-secondary-fixed": "#001f27",
                      "primary": "#000000",
                      "secondary": "#00677d",
                      "surface-container-low": "#f8f1fa",
                      "inverse-surface": "#322f36",
                      "on-tertiary": "#ffffff",
                      "on-surface-variant": "#464651",
                      "on-primary-fixed-variant": "#393e8c",
                      "tertiary-fixed": "#79f8d5",
                      "surface-tint": "#5157a6",
                      "surface-dim": "#ded8e1",
                      "primary-container": "#070a61",
                      "surface-bright": "#fdf7ff",
                      "on-secondary-container": "#005c70",
                      "secondary-container": "#50d9fe",
                      "on-tertiary-fixed": "#002018",
                      "surface": "#fdf7ff",
                      "surface-container-high": "#ece6ef",
                      "error-container": "#ffdad6",
                      "inverse-primary": "#bfc2ff",
                      "primary-fixed-dim": "#bfc2ff",
                      "tertiary": "#000000",
                      "on-tertiary-fixed-variant": "#005140",
                      "background": "#fdf7ff",
                      "primary-fixed": "#e0e0ff",
                      "secondary-fixed-dim": "#4cd6fb",
                      "on-tertiary-container": "#009579",
                      "surface-variant": "#e6e0e9",
                      "on-background": "#1d1b21",
                      "surface-container": "#f2ecf5",
                      "surface-container-lowest": "#ffffff",
                      "on-primary-container": "#777dcf",
                      "on-primary-fixed": "#070a61",
                      "error": "#ba1a1a",
                      "secondary-fixed": "#b3ebff",
                      "tertiary-container": "#002018",
                      "success-vibrant": "#10B981",
                      "on-secondary-fixed-variant": "#004e5f",
                      "outline-variant": "#c7c5d3",
                      "on-secondary": "#ffffff"
              },
              "borderRadius": {
                      "DEFAULT": "0.25rem",
                      "lg": "0.5rem",
                      "xl": "0.75rem",
                      "full": "9999px"
              },
              "spacing": {
                      "margin-mobile": "16px",
                      "margin-desktop": "32px",
                      "base": "8px",
                      "container-max": "1280px",
                      "gutter": "24px"
              },
              "fontFamily": {
                      "headline-lg": [
                              "Hanken Grotesk"
                      ],
                      "title-md": [
                              "Hanken Grotesk"
                      ],
                      "headline-lg-mobile": [
                              "Hanken Grotesk"
                      ],
                      "code-sm": [
                              "jetbrainsMono"
                      ],
                      "body-lg": [
                              "Hanken Grotesk"
                      ],
                      "body-sm": [
                              "Hanken Grotesk"
                      ],
                      "display-lg": [
                              "Hanken Grotesk"
                      ],
                      "label-md": [
                              "Hanken Grotesk"
                      ]
              },
              "fontSize": {
                      "headline-lg": [
                              "32px",
                              {
                                      "lineHeight": "40px",
                                      "letterSpacing": "-0.01em",
                                      "fontWeight": "600"
                              }
                      ],
                      "title-md": [
                              "20px",
                              {
                                      "lineHeight": "28px",
                                      "fontWeight": "600"
                              }
                      ],
                      "headline-lg-mobile": [
                              "28px",
                              {
                                      "lineHeight": "36px",
                                      "fontWeight": "600"
                              }
                      ],
                      "code-sm": [
                              "13px",
                              {
                                      "lineHeight": "18px",
                                      "fontWeight": "400"
                              }
                      ],
                      "body-lg": [
                              "16px",
                              {
                                      "lineHeight": "24px",
                                      "fontWeight": "400"
                              }
                      ],
                      "body-sm": [
                              "14px",
                              {
                                      "lineHeight": "20px",
                                      "fontWeight": "400"
                              }
                      ],
                      "display-lg": [
                              "56px",
                              {
                                      "lineHeight": "64px",
                                      "letterSpacing": "-0.02em",
                                      "fontWeight": "700"
                              }
                      ],
                      "label-md": [
                              "14px",
                              {
                                      "lineHeight": "16px",
                                      "letterSpacing": "0.01em",
                                      "fontWeight": "500"
                              }
                      ]
              }
      },
          },
        }
      </script>
<style>
        body {
            background-color: #FFFFFF;
            color: #1d1b21;
        }
        /* Tonal Layers based on Style Guidance */
        .surface-level-1 {
            background-color: #FAFAFA;
            border: 1px solid #D9D9D9;
        }
        .shadow-ambient {
            box-shadow: 0px 4px 12px rgba(3, 4, 94, 0.08);
        }
        .card-header-tint {
            background-color: #F0F9FF;
        }
    </style>
</head>
<body class="font-body-lg text-body-lg text-on-surface antialiased bg-white flex min-h-screen">
<!-- SideNavBar (from JSON blueprint) -->
<nav class="hidden md:flex bg-primary-container h-screen w-64 fixed left-0 top-0 border-r border-outline-variant shadow-sm flex-col py-8 px-4 z-20">
<!-- Brand / Header -->
<div class="mb-12 px-2 flex items-center gap-3">
<img alt="SIGA Enterprise Logo" class="h-10 w-auto object-contain bg-white rounded p-1" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAQN-qYd7wWHwSLa-Scwk9UCoFeaperu2LAIqZ9WkoGi3B_0PNDiqGbIyligwAre_ywg2t8ssggRWyQSZjKY1cT_FAZ10C1pzPIMp6_r3wC7DO_zNyXuq2mDRuoSJTeGMhyn3I4MJXsuJ00GvzxEYHuBxv2U06NDIw98a85fl_MOCZyKGzHoMkrzFHlo2b5Oaz7Sq_sotMlcNxfnTgTfV_ze6vxjZafiKK0h0nqRaHjDzHNXj_B0ryG9dC_DMF_D2lhk9HIZ00MG6I7">
<div>
<h1 class="font-display-lg text-title-md font-bold text-secondary-fixed-dim tracking-tight">SIGA</h1>
<p class="font-label-md text-label-md text-tertiary-fixed-dim">Platform Owner</p>
</div>
</div>
<!-- Navigation Links -->
<ul class="flex flex-col gap-2 flex-grow">
<!-- Active state based on JSON intent: Dashboard / Overview -->
<li class="">
<a class="flex items-center gap-3 px-4 py-3 rounded-lg text-secondary-fixed-dim font-bold border-r-4 border-secondary-fixed-dim bg-on-secondary-fixed-variant/20 hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined" style="font-variation-settings: &quot;FILL&quot; 1;">dashboard</span>
<span class="font-label-md text-label-md">Inicio</span>
</a>
</li><li class=""><a class="flex items-center gap-3 px-4 py-3 rounded-lg text-on-secondary-fixed-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#"><span class="material-symbols-outlined">group</span><span class="font-label-md text-label-md">Clientes</span></a></li><li class=""><a class="flex items-center gap-3 px-4 py-3 rounded-lg text-on-secondary-fixed-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#"><span class="material-symbols-outlined">payments</span><span class="font-label-md text-label-md">Planes SaaS</span></a></li><li class=""><a class="flex items-center gap-3 px-4 py-3 rounded-lg text-on-secondary-fixed-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#"><span class="material-symbols-outlined">support_agent</span><span class="font-label-md text-label-md">Soporte</span></a></li>


<li class="">
<a class="flex items-center gap-3 px-4 py-3 rounded-lg text-on-secondary-fixed-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined">group</span>
<span class="font-label-md text-label-md">Usuarios</span>
</a>
</li>

<li class="">
<a class="flex items-center gap-3 px-4 py-3 rounded-lg text-on-secondary-fixed-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined">settings</span>
<span class="font-label-md text-label-md">Configuración</span>
</a>
</li>
</ul>
<!-- CTA -->
<div class="mt-auto pt-4">
<button class="w-full bg-secondary-fixed-dim text-primary-container font-label-md text-label-md font-bold py-3 px-4 rounded-lg hover:bg-tertiary-fixed-dim transition-colors flex items-center justify-center gap-2 shadow-sm">
<span class="material-symbols-outlined">analytics</span>
                Generar Reporte
            </button>
</div>
</nav>
<!-- Main Content Area -->
<div class="flex-1 md:ml-64 bg-white min-h-screen">
<!-- TopNavBar (from JSON blueprint) -->
<header class="bg-surface/80 backdrop-blur-md fixed top-0 right-0 w-[calc(100%-16rem)] z-10 border-b border-outline-variant shadow-sm flex justify-between items-center h-16 px-gutter transition-all duration-300">
<div class="flex items-center gap-4">
<h2 class="font-title-md text-title-md font-bold text-primary tracking-tight">SIGA Dashboard</h2>
</div>
<div class="flex items-center gap-6">
<!-- Search (on_left in JSON logic applied relative to trailing icons) -->
<div class="relative hidden lg:block">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline-variant text-sm">search</span>
<input class="pl-10 pr-4 py-2 border border-border-muted rounded-lg bg-surface-container-lowest text-body-sm font-body-sm focus:border-secondary focus:ring-1 focus:ring-secondary outline-none w-64 transition-all" placeholder="Buscar tenants, IDs..." type="text">
</div>
<div class="flex items-center gap-4 text-on-surface-variant">
<button class="hover:text-secondary transition-colors relative">
<span class="material-symbols-outlined">notifications</span>
<span class="absolute top-0 right-0 w-2 h-2 bg-error rounded-full"></span>
</button>
<button class="hover:text-secondary transition-colors">
<span class="material-symbols-outlined">help</span>
</button>
<div class="h-8 w-8 rounded-full bg-surface-variant overflow-hidden border border-border-muted ml-2">
<img alt="Avatar del Administrador" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeP-ZeOe-8f4zUwqQK3iQZbaWfe2vMLLGb_vVqeG5wsd4hAkhCwzMsqT8TvpskXx1ytRFzAeOdgX-S9T_ty5EAMruKLltvcttto8YIQA9MOSai6mhudlCJII2dlOb0DrCMro2mESrssJAej_xPVHU_VL68Y0kTQYeditQAhBfBsXCo_QLdM_y2BSvvG44nbOolkgxawXng7o_9TZzqCyWx-ns0eLHczeTdkthJlbUGaqF-j2ddr8ZwhVPxYzvMxQG0rAA6SGrNX2uO">
</div>
</div>
</div>
</header>
<!-- Canvas / Dashboard Content -->
<main class="pt-24 pb-12 px-margin-mobile md:px-gutter max-w-container-max mx-auto space-y-8">
<!-- Section 1: Metrics & MRR Overview (Bento Grid Style) -->
<section>
<div class="flex items-baseline justify-between mb-4">
<h3 class="font-title-md text-title-md font-semibold text-primary">Métricas de Plataforma</h3>
<span class="font-body-sm text-body-sm text-outline flex items-center gap-1">
<span class="material-symbols-outlined text-sm">sync</span> Actualizado hace 2 min
                    </span>
</div>
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
<!-- MRR Card -->
<div class="surface-level-1 rounded-xl p-6 flex flex-col justify-between h-full hover:shadow-ambient hover:border-secondary transition-all group">
<div class="flex justify-between items-start mb-4">
<div>
<p class="font-label-md text-label-md text-on-surface-variant mb-1">MRR Total</p>
<h4 class="font-headline-lg text-headline-lg text-primary">$45,280</h4>
</div>
<div class="p-2 bg-secondary/10 rounded-lg text-secondary group-hover:scale-110 transition-transform">
<span class="material-symbols-outlined">payments</span>
</div>
</div>
<div class="flex items-center gap-2 mt-auto">
<span class="text-success-vibrant bg-success-vibrant/10 px-2 py-0.5 rounded text-sm font-medium flex items-center gap-1">
<span class="material-symbols-outlined text-[16px]">trending_up</span> +12.5%
                            </span>
<span class="font-body-sm text-body-sm text-outline text-xs">vs mes anterior</span>
</div>
</div>
<!-- Active Tenants Card -->
<div class="surface-level-1 rounded-xl p-6 flex flex-col justify-between h-full hover:shadow-ambient hover:border-secondary transition-all group">
<div class="flex justify-between items-start mb-4">
<div>
<p class="font-label-md text-label-md text-on-surface-variant mb-1">Tenants Activos</p>
<h4 class="font-headline-lg text-headline-lg text-primary">1,204</h4>
</div>
<div class="p-2 bg-primary-container/10 rounded-lg text-primary-container group-hover:scale-110 transition-transform">
<span class="material-symbols-outlined">domain</span>
</div>
</div>
<div class="flex items-center gap-2 mt-auto">
<span class="text-success-vibrant bg-success-vibrant/10 px-2 py-0.5 rounded text-sm font-medium flex items-center gap-1">
<span class="material-symbols-outlined text-[16px]">trending_up</span> +48
                            </span>
<span class="font-body-sm text-body-sm text-outline text-xs">nuevos este mes</span>
</div>
</div>
<!-- Churn Rate Card -->
<div class="surface-level-1 rounded-xl p-6 flex flex-col justify-between h-full hover:shadow-ambient hover:border-secondary transition-all group">
<div class="flex justify-between items-start mb-4">
<div>
<p class="font-label-md text-label-md text-on-surface-variant mb-1">Churn Rate</p>
<h4 class="font-headline-lg text-headline-lg text-primary">1.2%</h4>
</div>
<div class="p-2 bg-error/10 rounded-lg text-error group-hover:scale-110 transition-transform">
<span class="material-symbols-outlined">person_remove</span>
</div>
</div>
<div class="flex items-center gap-2 mt-auto">
<span class="text-error bg-error/10 px-2 py-0.5 rounded text-sm font-medium flex items-center gap-1">
<span class="material-symbols-outlined text-[16px]">trending_up</span> +0.2%
                            </span>
<span class="font-body-sm text-body-sm text-outline text-xs">vs mes anterior</span>
</div>
</div>
<!-- Support Tickets Card -->
<div class="surface-level-1 rounded-xl p-6 flex flex-col justify-between h-full hover:shadow-ambient hover:border-secondary transition-all group">
<div class="flex justify-between items-start mb-4">
<div>
<p class="font-label-md text-label-md text-on-surface-variant mb-1">Tickets Abiertos</p>
<h4 class="font-headline-lg text-headline-lg text-primary">34</h4>
</div>
<div class="p-2 bg-[#80FFDB]/20 rounded-lg text-[#005140] group-hover:scale-110 transition-transform">
<span class="material-symbols-outlined">support_agent</span>
</div>
</div>
<div class="flex items-center gap-2 mt-auto">
<span class="text-on-surface-variant bg-surface-variant px-2 py-0.5 rounded text-sm font-medium flex items-center gap-1">
<span class="material-symbols-outlined text-[16px]">trending_down</span> -5
                            </span>
<span class="font-body-sm text-body-sm text-outline text-xs">resueltos hoy</span>
</div>
</div>
</div>
</section>
</main>
</div>


</body></html>

<!-- Landing Page - SIGA Sistema Inteligente -->
<!DOCTYPE html><html class="light" lang="es"><head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<title>SIGA - Gestión de Negocios con IA</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&amp;family=JetBrains+Mono:wght@400&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "on-error": "#ffffff",
                        "tertiary": "#000000",
                        "tertiary-fixed-dim": "#5adcb9",
                        "tertiary-container": "#002018",
                        "on-tertiary": "#ffffff",
                        "success-vibrant": "#10B981",
                        "surface-dim": "#ded8e1",
                        "secondary-fixed-dim": "#4cd6fb",
                        "primary": "#000000",
                        "on-secondary": "#ffffff",
                        "outline": "#777682",
                        "on-tertiary-fixed-variant": "#005140",
                        "surface": "#fdf7ff",
                        "surface-container-low": "#f8f1fa",
                        "primary-fixed-dim": "#bfc2ff",
                        "error": "#ba1a1a",
                        "primary-container": "#070a61",
                        "inverse-surface": "#322f36",
                        "surface-bright": "#fdf7ff",
                        "surface-tint": "#F0F9FF",
                        "surface-container-highest": "#e6e0e9",
                        "on-secondary-fixed-variant": "#004e5f",
                        "surface-container-high": "#ece6ef",
                        "on-secondary-fixed": "#001f27",
                        "on-primary-fixed-variant": "#393e8c",
                        "on-surface-variant": "#464651",
                        "inverse-on-surface": "#f5eff7",
                        "on-tertiary-fixed": "#002018",
                        "on-primary": "#ffffff",
                        "on-primary-container": "#777dcf",
                        "surface-container-lowest": "#ffffff",
                        "inverse-primary": "#bfc2ff",
                        "on-primary-fixed": "#070a61",
                        "border-muted": "#D9D9D9",
                        "on-surface": "#1d1b21",
                        "background": "#fdf7ff",
                        "secondary-container": "#50d9fe",
                        "on-secondary-container": "#005c70",
                        "secondary": "#00677d",
                        "on-tertiary-container": "#009579",
                        "on-error-container": "#93000a",
                        "on-background": "#1d1b21",
                        "error-container": "#ffdad6",
                        "surface-variant": "#e6e0e9",
                        "surface-container": "#f2ecf5",
                        "secondary-fixed": "#b3ebff",
                        "primary-fixed": "#e0e0ff",
                        "tertiary-fixed": "#79f8d5",
                        "outline-variant": "#c7c5d3"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "container-max": "1280px",
                        "margin-desktop": "32px",
                        "gutter": "24px",
                        "margin-mobile": "16px",
                        "base": "8px"
                    },
                    "fontFamily": {
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "title-md": ["Hanken Grotesk"],
                        "code-sm": ["jetbrainsMono"],
                        "body-lg": ["Hanken Grotesk"],
                        "headline-lg": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "body-sm": ["Hanken Grotesk"],
                        "label-md": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "600"}],
                        "title-md": ["20px", {"lineHeight": "28px", "fontWeight": "600"}],
                        "code-sm": ["13px", {"lineHeight": "18px", "fontWeight": "400"}],
                        "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                        "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                        "display-lg": ["56px", {"lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                        "body-sm": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                        "label-md": ["14px", {"lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500"}]
                    }
                }
            }
        }
    </script>
<style>
        .glass-card {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        .text-gradient {
            background: linear-gradient(135deg, #00677d 0%, #00B4D8 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .bg-gradient-siga {
            background: linear-gradient(135deg, #00B4D8 0%, #80FFDB 100%);
        }
        .sidebar-active {
            border-left: 4px solid #00677d;
            background-color: rgba(80, 217, 254, 0.1);
            color: #00677d;
        }
    </style>
</head>
<body class="bg-background text-on-background font-body-lg overflow-x-hidden">
<!-- TopNavBar -->
<header class="fixed top-0 w-full z-50 bg-surface/80 backdrop-blur-md shadow-sm opacity-100 translate-y-0">
<nav class="max-w-container-max mx-auto h-16 px-gutter flex justify-between items-center">
<div class="flex items-center gap-2">
<img alt="SIGA Logo" class="h-8" src="https://lh3.googleusercontent.com/aida-public/AB6AXuC-bVFs6Qe_H005dGNgWP4p-dG6F6yakKRuiFtSmhFHlKLQHiU82cNqMDNr0okSeXk2kr6_n5cNj1J2lIvWa-7YQES-l4085Y9NhOmWR3WFP3oczp4Kr9jD4hBKRgpG4WS-8RIfKbhHLp-ZDRNObuh5-jrEue3peSwd15KWKQjBfqJZpQQkCZtZ-xMOd9EG4Pus8CmyOSQcrm6ufiKeBnmfI3_tcGyRTfKea8ixDniB820IThBTmImC7BUbBsIeCrcBnLIEEMsOjK4z">

</div>
<div class="hidden md:flex items-center gap-8">
<a class="font-label-md text-label-md text-on-surface-variant hover:text-secondary transition-colors" href="#planes">Planes</a>
<a class="font-label-md text-label-md text-on-surface-variant hover:text-secondary transition-colors" href="#pilares">Características</a>
<a class="font-label-md text-label-md text-on-surface-variant hover:text-secondary transition-colors" href="#historia">Historia</a>
<a class="font-label-md text-label-md px-6 py-2 border border-secondary text-secondary rounded-full hover:bg-secondary/5 transition-all" href="/login">Login</a>
<a class="font-label-md text-label-md px-6 py-2 bg-gradient-siga text-white rounded-full shadow-md hover:shadow-lg transition-all active:scale-95" href="#">Probar Gratis</a>
</div>
<button class="md:hidden text-on-surface">
<span class="material-symbols-outlined">menu</span>
</button>
</nav>
</header>
<main class="pt-16">
<!-- Hero Section -->
<section class="relative min-h-[90vh] flex items-center justify-center overflow-hidden px-margin-mobile md:px-0 transition-all duration-1000 opacity-100 translate-y-0">
<div class="absolute inset-0 z-0">

</div>
<div class="max-w-container-max mx-auto grid md:grid-cols-2 gap-12 items-center relative z-10 px-gutter">
<div class="space-y-8 animate-fade-in">
<h1 class="font-display-lg text-display-lg md:text-[64px] leading-tight font-bold text-on-surface">
                        Gestiona tu negocio con la <span class="text-gradient">velocidad de la IA</span>
</h1>
<p class="font-title-md text-title-md text-on-surface-variant max-w-xl">
                        Traducimos la intención de tu negocio en acciones automáticas. Nexus A2UI es el primer ERP que te escucha y ejecuta.
                    </p>
<div class="flex flex-col sm:flex-row gap-4">
<button class="bg-secondary text-white px-8 py-4 rounded-xl font-label-md text-lg shadow-xl hover:bg-secondary-container hover:text-on-secondary-container transition-all flex items-center justify-center gap-2">
                            Empieza tu prueba gratuita
                            <span class="material-symbols-outlined">arrow_forward</span>
</button>
<button class="glass-card px-8 py-4 rounded-xl font-label-md text-lg text-on-surface hover:bg-surface-container-high transition-all">
                            Ver Demo en vivo
                        </button>
</div>
</div>
<div class="relative group">
<div class="absolute -inset-4 bg-secondary-container/20 blur-3xl rounded-full group-hover:bg-secondary-container/30 transition-all"></div>
<div class="relative rounded-2xl shadow-2xl glass-card overflow-hidden transform transition-transform group-hover:scale-[1.02] duration-500">{{DATA:SCREEN:SCREEN_32}}</div>
<!-- Floating Widget Example -->
<div class="absolute -bottom-8 -left-8 glass-card p-4 rounded-xl shadow-xl border-secondary/20 hidden md:block animate-bounce-slow">
<div class="flex items-center gap-3">
<div class="w-10 h-10 bg-gradient-siga rounded-full flex items-center justify-center text-white">
<span class="material-symbols-outlined">smart_toy</span>
</div>
<div>
<p class="text-xs font-bold text-secondary">A2UI AI Agent</p>
<p class="text-xs text-on-surface-variant">Optimizando inventario...</p>
</div>
</div>
</div>
</div>
</div>
</section>
<!-- Pilares Section -->
<section class="py-24 bg-surface-container-lowest transition-all duration-1000 opacity-100 translate-y-0" id="pilares">
<div class="max-w-container-max mx-auto px-gutter">
<div class="text-center mb-16 space-y-4">
<h2 class="font-headline-lg text-headline-lg font-bold text-on-surface">Los pilares de la nueva gestión</h2>
<p class="font-body-lg text-on-surface-variant max-w-2xl mx-auto">Diseñamos una plataforma que elimina el ruido para que te enfoques en lo que importa.</p>
</div>
<div class="grid md:grid-cols-3 gap-8">
<!-- Card 1 -->
<div class="p-8 rounded-2xl bg-white border border-outline-variant hover:border-secondary transition-all hover:shadow-xl group">
<div class="w-14 h-14 bg-surface-tint rounded-xl flex items-center justify-center text-secondary mb-6 group-hover:bg-secondary group-hover:text-white transition-colors">
<span class="material-symbols-outlined text-3xl">bolt</span>
</div>
<h3 class="font-title-md text-title-md font-bold mb-3">Menos Fricción</h3>
<p class="font-body-sm text-on-surface-variant">Olvida las interfaces complejas. Navegación fluida y carga instantánea para un flujo de trabajo sin interrupciones.</p>
</div>
<!-- Card 2 -->
<div class="p-8 rounded-2xl bg-white border border-outline-variant hover:border-secondary transition-all hover:shadow-xl group">
<div class="w-14 h-14 bg-surface-tint rounded-xl flex items-center justify-center text-secondary mb-6 group-hover:bg-secondary group-hover:text-white transition-colors">
<span class="material-symbols-outlined text-3xl">psychology</span>
</div>
<h3 class="font-title-md text-title-md font-bold mb-3">Más Intención</h3>
<p class="font-body-sm text-on-surface-variant">Nuestro sistema entiende qué quieres lograr. Solo tienes que pedirlo y Nexus Core prepara el terreno.</p>
</div>
<!-- Card 3 -->
<div class="p-8 rounded-2xl bg-white border border-outline-variant hover:border-secondary transition-all hover:shadow-xl group">
<div class="w-14 h-14 bg-surface-tint rounded-xl flex items-center justify-center text-secondary mb-6 group-hover:bg-secondary group-hover:text-white transition-colors">
<span class="material-symbols-outlined text-3xl">settings_suggest</span>
</div>
<h3 class="font-title-md text-title-md font-bold mb-3">Automatización</h3>
<p class="font-body-sm text-on-surface-variant">Desde el control de stock hasta el reporte de ventas semanal, deja que la IA haga el trabajo pesado por ti.</p>
</div>
</div>
</div>
</section>
<!-- A2UI Co-piloto -->
<section class="py-24 bg-surface transition-all duration-1000 opacity-100 translate-y-0">
<div class="max-w-container-max mx-auto px-gutter grid md:grid-cols-2 gap-16 items-center">
<div class="order-2 md:order-1 relative">
<div class="grid grid-cols-2 gap-4">
<div class="space-y-4">
<div class="glass-card p-6 rounded-2xl shadow-sm border-secondary/10">
<span class="material-symbols-outlined text-secondary">inventory_2</span>
<p class="mt-4 font-bold">Stock Inteligente</p>
<p class="text-xs text-on-surface-variant">Predicción de quiebre en 5 días.</p>
</div>
<div class="bg-secondary-container/10 p-6 rounded-2xl border border-secondary-fixed">
<span class="material-symbols-outlined text-secondary">forum</span>
<p class="mt-4 font-bold text-secondary">Agente Chat</p>
<p class="text-xs text-secondary-fixed-dim">"Reabastece el SKU-402."</p>
</div>
</div>
<div class="pt-8 space-y-4">
<div class="bg-primary-container p-6 rounded-2xl shadow-xl text-white">
<span class="material-symbols-outlined text-tertiary-fixed">analytics</span>
<p class="mt-4 font-bold">Reporte Diario</p>
<p class="text-xs text-primary-fixed-dim">Ventas +24% vs ayer.</p>
</div>
<div class="rounded-2xl shadow-md border border-outline-variant overflow-hidden">{{DATA:SCREEN:SCREEN_27}}</div>
</div>
</div>
</div>
<div class="order-1 md:order-2 space-y-6">
<span class="text-secondary font-label-md tracking-widest uppercase">Nexus A2UI</span>
<h2 class="font-headline-lg text-headline-lg font-bold leading-tight">Tu Co-piloto Inteligente para Operaciones</h2>
<p class="font-body-lg text-on-surface-variant">
                        Nexus A2UI no es solo un panel de control; es un compañero que aprende de tu negocio. Detecta patrones de venta, anticipa necesidades de inventario y te sugiere acciones en tiempo real a través de un lenguaje natural.
                    </p>
<ul class="space-y-4">
<li class="flex items-start gap-3">
<span class="material-symbols-outlined text-success-vibrant">check_circle</span>
<span class="font-body-sm">Predicción automática de demanda estacional.</span>
</li>
<li class="flex items-start gap-3">
<span class="material-symbols-outlined text-success-vibrant">check_circle</span>
<span class="font-body-sm">Órdenes de compra sugeridas con un click.</span>
</li>
<li class="flex items-start gap-3">
<span class="material-symbols-outlined text-success-vibrant">check_circle</span>
<span class="font-body-sm">Detección de anomalías en transacciones POS.</span>
</li>
</ul>
</div>
</div>
</section>
<!-- Nuestra Historia -->
<section class="py-24 bg-primary-container text-white relative overflow-hidden transition-all duration-1000 opacity-100 translate-y-0" id="historia">

<div class="max-w-4xl mx-auto px-gutter text-center relative z-10">
<h2 class="font-headline-lg text-headline-lg font-bold mb-8">Nacidos para recuperar tu tiempo</h2>
<div class="space-y-6 font-body-lg text-primary-fixed-dim leading-relaxed">
<p class="">
                        SIGA nació del deseo de transformar la experiencia de gestión para dueños de PYMEs. Vimos a emprendedores como Elizabeth, atrapados entre hojas de cálculo y procesos manuales, perdiendo el enfoque en lo que realmente aman de su negocio.
                    </p>
<p class="">
                        Nuestra misión es simple: crear herramientas que funcionen de manera invisible pero potente, permitiéndote recuperar el control y, sobre todo, tu tiempo. Porque un negocio exitoso no debería significar una vida sin descanso.
                    </p>
</div>
<div class="mt-12 flex justify-center gap-12 border-t border-white/10 pt-12">
<div>
<p class="text-4xl font-bold text-secondary-fixed">500+</p>
<p class="text-sm uppercase tracking-wider text-primary-fixed">Negocios</p>
</div>
<div>
<p class="text-4xl font-bold text-secondary-fixed">15k</p>
<p class="text-sm uppercase tracking-wider text-primary-fixed">Horas Ahorradas</p>
</div>
<div>
<p class="text-4xl font-bold text-secondary-fixed">24/7</p>
<p class="text-sm uppercase tracking-wider text-primary-fixed">IA Activa</p>
</div>
</div>
</div>
</section>
<!-- Planes Section -->
<section class="py-24 bg-surface-container-low transition-all duration-1000 opacity-100 translate-y-0" id="planes">
<div class="max-w-container-max mx-auto px-gutter">
<div class="text-center mb-16">
<h2 class="font-headline-lg text-headline-lg font-bold">Planes diseñados para crecer</h2>
<p class="text-on-surface-variant">Escalabilidad total desde el primer día.</p>
</div>
<div class="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
<!-- Emprendedor Pro -->
<div class="glass-card p-10 rounded-3xl border-2 border-transparent hover:border-secondary transition-all">
<h3 class="font-headline-sm text-2xl font-bold mb-2">Emprendedor Pro</h3>
<p class="text-on-surface-variant mb-6">Ideal para negocios en crecimiento.</p>
<div class="flex items-baseline gap-1 mb-8">
<span class="text-4xl font-bold">$49</span>
<span class="text-on-surface-variant">/mes</span>
</div>
<ul class="space-y-4 mb-10">
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary">done</span> Gestión de Inventario</li>
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary">done</span> POS Ilimitado</li>
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary">done</span> Reportes Básicos de IA</li>
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary">done</span> 5 Usuarios</li>
</ul>
<button class="w-full py-4 rounded-xl border border-secondary text-secondary font-bold hover:bg-secondary hover:text-white transition-all">Seleccionar Plan</button>
</div>
<!-- Crecimiento -->
<div class="bg-primary-container p-10 rounded-3xl text-white shadow-2xl relative overflow-hidden">
<div class="absolute top-4 right-4 bg-secondary-container px-3 py-1 rounded-full text-on-secondary-container text-xs font-bold uppercase tracking-widest">Recomendado</div>
<h3 class="font-headline-sm text-2xl font-bold mb-2">Crecimiento</h3>
<p class="text-primary-fixed-dim mb-6">Para operaciones multi-sucursal.</p>
<div class="flex items-baseline gap-1 mb-8">
<span class="text-4xl font-bold">$99</span>
<span class="text-primary-fixed-dim">/mes</span>
</div>
<ul class="space-y-4 mb-10">
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary-fixed">done</span> Todo en Pro</li>
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary-fixed">done</span> Agentes de IA Avanzados</li>
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary-fixed">done</span> Multi-depósito</li>
<li class="flex gap-3"><span class="material-symbols-outlined text-secondary-fixed">done</span> Soporte Prioritario 24/7</li>
</ul>
<button class="w-full py-4 rounded-xl bg-gradient-siga text-on-secondary font-bold shadow-lg hover:shadow-secondary/50 transition-all">Seleccionar Plan</button>
</div>
</div>
</div>
</section>
<!-- CTA Final -->
<section class="py-20 transition-all duration-1000 opacity-100 translate-y-0">
<div class="max-w-container-max mx-auto px-gutter">
<div class="bg-surface-tint rounded-3xl p-12 text-center border border-secondary/10 relative overflow-hidden">
<div class="absolute -right-20 -top-20 w-64 h-64 bg-secondary-container/20 rounded-full blur-3xl"></div>
<div class="relative z-10 max-w-2xl mx-auto space-y-8">
<h2 class="font-display-lg text-4xl md:text-5xl font-bold text-on-surface">¿Listo para transformar tu negocio?</h2>
<p class="font-body-lg text-on-surface-variant">Únete a cientos de dueños que ya están operando con la velocidad de Nexus Core.</p>
<button class="bg-secondary text-white px-10 py-5 rounded-full font-bold text-lg shadow-xl hover:scale-105 active:scale-95 transition-all">Empezar ahora</button>
</div>
</div>
</div>
</section>
</main>
<!-- Footer -->
<footer class="bg-surface-container-highest pt-20 pb-10">
<div class="max-w-container-max mx-auto px-gutter">
<div class="grid md:grid-cols-4 gap-12 mb-16">
<div class="col-span-1 md:col-span-1 space-y-4">
<div class="flex items-center gap-2 mb-6">
<img alt="SIGA Logo" class="h-6" src="https://lh3.googleusercontent.com/aida-public/AB6AXuC-bVFs6Qe_H005dGNgWP4p-dG6F6yakKRuiFtSmhFHlKLQHiU82cNqMDNr0okSeXk2kr6_n5cNj1J2lIvWa-7YQES-l4085Y9NhOmWR3WFP3oczp4Kr9jD4hBKRgpG4WS-8RIfKbhHLp-ZDRNObuh5-jrEue3peSwd15KWKQjBfqJZpQQkCZtZ-xMOd9EG4Pus8CmyOSQcrm6ufiKeBnmfI3_tcGyRTfKea8ixDniB820IThBTmImC7BUbBsIeCrcBnLIEEMsOjK4z">

</div>
<p class="text-sm text-on-surface-variant">Liderando la revolución de los ERP con inteligencia artificial agent-enabled para PYMES.</p>
</div>
<div>
<h4 class="font-bold mb-6">Producto</h4>
<ul class="space-y-3 text-sm text-on-surface-variant">
<li class=""><a class="hover:text-secondary" href="#">Dashboard Nexus</a></li>
<li class=""><a class="hover:text-secondary" href="#">Gestión Inventario</a></li>
<li class=""><a class="hover:text-secondary" href="#">Agentes IA</a></li>
<li class=""><a class="hover:text-secondary" href="#">Seguridad</a></li>
</ul>
</div>
<div>
<h4 class="font-bold mb-6">Compañía</h4>
<ul class="space-y-3 text-sm text-on-surface-variant">
<li class=""><a class="hover:text-secondary" href="#">Sobre Nosotros</a></li>
<li class=""><a class="hover:text-secondary" href="#">Carreras</a></li>
<li class=""><a class="hover:text-secondary" href="#">Blog</a></li>
<li class=""><a class="hover:text-secondary" href="#">Contacto</a></li>
</ul>
</div>
<div>
<h4 class="font-bold mb-6">Legal</h4>
<ul class="space-y-3 text-sm text-on-surface-variant">
<li class=""><a class="hover:text-secondary" href="#">Privacidad</a></li>
<li class=""><a class="hover:text-secondary" href="#">Términos</a></li>
<li class=""><a class="hover:text-secondary" href="#">Cookies</a></li>
</ul>
</div>
</div>
<div class="border-t border-outline-variant pt-8 flex flex-col md:flex-row justify-between items-center gap-4 text-xs text-on-surface-variant">
<p class="">© 2024 ERP Nexus A2UI. Todos los derechos reservados.</p>
<div class="flex gap-6">
<a class="hover:text-secondary" href="#">LinkedIn</a>
<a class="hover:text-secondary" href="#">Twitter</a>
<a class="hover:text-secondary" href="#">Instagram</a>
</div>
</div>
</div>
</footer>
<script>
        // Simple reveal animation observer
        const observerOptions = {
            threshold: 0.1
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('opacity-100', 'translate-y-0');
                    entry.target.classList.remove('opacity-0', 'translate-y-10');
                }
            });
        }, observerOptions);

        document.querySelectorAll('section').forEach(section => {
            section.classList.add('transition-all', 'duration-1000', 'opacity-0', 'translate-y-10');
            observer.observe(section);
        });

        // Initialize first section immediately
        const hero = document.querySelector('header');
        if (hero) {
            hero.classList.remove('opacity-0', 'translate-y-10');
            hero.classList.add('opacity-100', 'translate-y-0');
        }
    </script>


</body></html>

<!-- Monitor de Métricas Ops - Detalle de Microservicios (SIGA) -->
<!DOCTYPE html>

<html class="light" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>SIGA Operations Dashboard</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@100..900&display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "surface-container-lowest": "#ffffff",
                        "background": "#fdf7ff",
                        "on-tertiary": "#ffffff",
                        "surface-dim": "#ded8e1",
                        "tertiary-fixed-dim": "#5adcb9",
                        "primary": "#000000",
                        "error": "#ba1a1a",
                        "secondary-fixed": "#b3ebff",
                        "border-muted": "#D9D9D9",
                        "inverse-on-surface": "#f5eff7",
                        "on-error-container": "#93000a",
                        "surface-bright": "#fdf7ff",
                        "on-secondary-fixed-variant": "#004e5f",
                        "on-error": "#ffffff",
                        "secondary": "#00677d",
                        "primary-fixed-dim": "#bfc2ff",
                        "on-secondary-container": "#005c70",
                        "success-vibrant": "#10B981",
                        "tertiary-container": "#002018",
                        "on-surface-variant": "#464651",
                        "surface-container-highest": "#e6e0e9",
                        "tertiary-fixed": "#79f8d5",
                        "surface-container": "#f2ecf5",
                        "on-tertiary-fixed-variant": "#005140",
                        "error-container": "#ffdad6",
                        "inverse-primary": "#bfc2ff",
                        "outline-variant": "#c7c5d3",
                        "surface-tint": "#F0F9FF",
                        "surface": "#fdf7ff",
                        "on-tertiary-fixed": "#002018",
                        "outline": "#777682",
                        "on-surface": "#1d1b21",
                        "primary-fixed": "#e0e0ff",
                        "on-secondary-fixed": "#001f27",
                        "on-background": "#1d1b21",
                        "surface-container-high": "#ece6ef",
                        "secondary-container": "#50d9fe",
                        "on-primary": "#ffffff",
                        "surface-variant": "#e6e0e9",
                        "on-primary-container": "#777dcf",
                        "inverse-surface": "#322f36",
                        "secondary-fixed-dim": "#4cd6fb",
                        "on-tertiary-container": "#009579",
                        "surface-container-low": "#f8f1fa",
                        "on-primary-fixed-variant": "#393e8c",
                        "primary-container": "#070a61",
                        "on-primary-fixed": "#070a61",
                        "tertiary": "#000000",
                        "on-secondary": "#ffffff",
                        "brand-deep": "#03045e",
                        "brand-cyan": "#80ffdb"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "margin-mobile": "16px",
                        "gutter": "24px",
                        "base": "8px",
                        "margin-desktop": "32px",
                        "container-max": "1280px"
                    },
                    "fontFamily": {
                        "display-lg": ["Hanken Grotesk"],
                        "body-sm": ["Hanken Grotesk"],
                        "code-sm": ["jetbrainsMono", "monospace"],
                        "body-lg": ["Hanken Grotesk"],
                        "label-md": ["Hanken Grotesk"],
                        "title-md": ["Hanken Grotesk"],
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "headline-lg": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "display-lg": ["56px", { "lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700" }],
                        "body-sm": ["14px", { "lineHeight": "20px", "fontWeight": "400" }],
                        "code-sm": ["13px", { "lineHeight": "18px", "fontWeight": "400" }],
                        "body-lg": ["16px", { "lineHeight": "24px", "fontWeight": "400" }],
                        "label-md": ["14px", { "lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500" }],
                        "title-md": ["20px", { "lineHeight": "28px", "fontWeight": "600" }],
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "600" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600" }]
                    }
                }
            }
        }
    </script>
<style>
        body { font-family: 'Hanken Grotesk', sans-serif; }
        .glass-card {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.5);
        }
        .terminal-bg {
            background: #1e1e1e;
            color: #d4d4d4;
            font-family: 'JetBrains Mono', monospace;
        }
    </style>
</head>
<body class="bg-background text-on-background min-h-screen flex">
<!-- SideNavBar (Shared Component) -->
<nav class="hidden md:flex h-screen w-64 fixed left-0 top-0 flex-col py-8 px-4 border-r border-outline-variant shadow-sm bg-primary-container dark:bg-tertiary-container z-20">
<div class="mb-10 px-4">
<h1 class="font-display-lg text-display-lg font-bold text-secondary-fixed-dim">SIGA</h1>
<p class="font-label-md text-label-md text-on-secondary-fixed-variant/70 mt-1">Nexus Admin</p>
</div>
<ul class="space-y-2 flex-1">
<!-- Active Tab: Configuración (Maps to Ops/Monitoring context) -->
<li>
<a class="flex items-center px-4 py-3 rounded-lg text-secondary-fixed-dim font-bold border-r-4 border-secondary-fixed-dim bg-on-secondary-fixed-variant/20 hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mr-3">settings</span>
<span class="font-label-md text-label-md">Configuración</span>
</a>
</li>
<!-- Inactive Tabs -->
<li>
<a class="flex items-center px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mr-3">dashboard</span>
<span class="font-label-md text-label-md">Inicio</span>
</a>
</li>
<li>
<a class="flex items-center px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mr-3">inventory_2</span>
<span class="font-label-md text-label-md">Inventario</span>
</a>
</li>
<li>
<a class="flex items-center px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mr-3">store</span>
<span class="font-label-md text-label-md">Locales</span>
</a>
</li>
<li>
<a class="flex items-center px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mr-3">group</span>
<span class="font-label-md text-label-md">Usuarios</span>
</a>
</li>
<li>
<a class="flex items-center px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200 scale-95 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mr-3">point_of_sale</span>
<span class="font-label-md text-label-md">POS</span>
</a>
</li>
</ul>
<div class="mt-auto px-4">
<button class="w-full py-3 bg-secondary-fixed-dim text-on-secondary-fixed font-label-md text-label-md rounded-lg shadow-sm hover:opacity-90 transition-opacity">Generar Reporte</button>
</div>
</nav>
<!-- Main Content Area -->
<div class="flex-1 md:ml-64 flex flex-col min-h-screen">
<!-- TopNavBar (Shared Component) -->
<header class="flex justify-between items-center h-16 px-gutter border-b border-outline-variant dark:border-outline shadow-sm bg-surface/80 backdrop-blur-md dark:bg-surface-dim/80 fixed top-0 right-0 w-[calc(100%-16rem)] z-10">
<div class="flex items-center">
<h2 class="font-title-md text-title-md font-bold text-primary dark:text-on-surface">SIGA Dashboard</h2>
</div>
<div class="flex items-center space-x-4">
<div class="relative focus-within:ring-2 focus-within:ring-secondary-fixed-dim rounded-full">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
<input class="pl-10 pr-4 py-1.5 bg-surface-container rounded-full border-none focus:ring-0 text-body-sm w-48 transition-all focus:w-64" placeholder="Buscar..." type="text"/>
</div>
<button class="text-on-surface-variant hover:text-secondary transition-colors p-2 rounded-full">
<span class="material-symbols-outlined">notifications</span>
</button>
<button class="text-on-surface-variant hover:text-secondary transition-colors p-2 rounded-full">
<span class="material-symbols-outlined">help</span>
</button>
<img alt="Avatar del Administrador" class="w-8 h-8 rounded-full border border-border-muted" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCQtcMEg3OCmyVrNZPLNR0VxQJoJ5xMv0k_7490pH3ZCsJoHEbhvvJ0eTLqy3lACOgL8SWNTC_0Le1mems3qMe2yntMDYSGmDMZVY6AuZFftb_LqWKz5RpILf64tCF_nV3W6CMpxQYJJ2pOXTwzvfIVz4iZQdATJsd9uqL0U2CB7z3MjoaWy5uwq4OXL2Ww3f-NPtdSH48BCNYaVB_Ve4Ye1vtWzwLu7tfAlFApe_kYeJXmylYs6GfqTUNInSDkjE45Zub1Wg2xH3AA"/>
</div>
</header>
<!-- Canvas -->
<main class="flex-1 pt-24 px-gutter pb-8 max-w-container-max mx-auto w-full">
<!-- Page Header & Actions -->
<div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
<div>
<h1 class="font-headline-lg text-headline-lg text-brand-deep">Ops & Monitoring</h1>
<p class="font-body-lg text-body-lg text-on-surface-variant mt-1">Real-time health overview of Nexus microservices cluster.</p>
</div>
<div class="flex gap-3">
<button class="px-4 py-2 bg-surface text-brand-deep border border-brand-deep font-label-md text-label-md rounded-lg hover:bg-surface-tint transition-colors flex items-center shadow-sm">
<span class="material-symbols-outlined mr-2 text-[18px]">cleaning_services</span>
                        Clear Cache
                    </button>
<button class="px-4 py-2 bg-brand-deep text-white font-label-md text-label-md rounded-lg hover:bg-opacity-90 transition-colors flex items-center shadow-sm">
<span class="material-symbols-outlined mr-2 text-[18px]">restart_alt</span>
                        Restart All
                    </button>
</div>
</div>
<!-- Bento Grid Layout -->
<div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
<!-- Infrastructure Health (Top Left) -->
<div class="lg:col-span-8 glass-card rounded-xl p-6 shadow-sm border border-border-muted">
<div class="flex justify-between items-center mb-4">
<h3 class="font-title-md text-title-md text-primary">Infrastructure Health</h3>
<span class="px-3 py-1 bg-tertiary-fixed/20 text-tertiary-container font-label-md text-label-md rounded-full text-xs">Healthy</span>
</div>
<div class="grid grid-cols-1 md:grid-cols-2 gap-6 h-48">
<!-- Simulated Chart Area 1 -->
<div class="bg-surface-container-lowest rounded-lg border border-border-muted p-4 relative overflow-hidden">
<p class="font-label-md text-label-md text-on-surface-variant mb-2">Global RAM Usage</p>
<div class="flex items-end h-24 gap-1 mt-4 opacity-70">
<div class="w-1/6 bg-brand-cyan h-1/3 rounded-t-sm"></div>
<div class="w-1/6 bg-brand-cyan h-1/2 rounded-t-sm"></div>
<div class="w-1/6 bg-brand-cyan h-2/3 rounded-t-sm"></div>
<div class="w-1/6 bg-brand-cyan h-1/2 rounded-t-sm"></div>
<div class="w-1/6 bg-brand-cyan h-4/5 rounded-t-sm"></div>
<div class="w-1/6 bg-brand-deep h-3/4 rounded-t-sm"></div>
</div>
<div class="absolute top-4 right-4 font-headline-lg text-headline-lg text-brand-deep">64%</div>
</div>
<!-- Simulated Chart Area 2 -->
<div class="bg-surface-container-lowest rounded-lg border border-border-muted p-4 relative overflow-hidden">
<p class="font-label-md text-label-md text-on-surface-variant mb-2">Network Traffic</p>
<div class="flex items-end h-24 gap-1 mt-4 opacity-70">
<div class="w-1/6 bg-secondary-container h-1/4 rounded-t-sm"></div>
<div class="w-1/6 bg-secondary-container h-2/5 rounded-t-sm"></div>
<div class="w-1/6 bg-secondary-container h-1/3 rounded-t-sm"></div>
<div class="w-1/6 bg-secondary-container h-3/5 rounded-t-sm"></div>
<div class="w-1/6 bg-secondary-container h-2/5 rounded-t-sm"></div>
<div class="w-1/6 bg-brand-cyan h-1/2 rounded-t-sm"></div>
</div>
<div class="absolute top-4 right-4 font-headline-lg text-headline-lg text-secondary">2.4 Gbps</div>
</div>
</div>
</div>
<!-- Real-time Events (Top Right) -->
<div class="lg:col-span-4 terminal-bg rounded-xl p-4 shadow-sm flex flex-col h-[280px]">
<div class="flex justify-between items-center mb-3 border-b border-[#333] pb-2">
<h3 class="text-sm font-semibold flex items-center">
<span class="material-symbols-outlined text-[16px] mr-2">terminal</span>
                            Live Events
                        </h3>
<div class="flex gap-1.5">
<div class="w-2.5 h-2.5 rounded-full bg-red-500"></div>
<div class="w-2.5 h-2.5 rounded-full bg-yellow-500"></div>
<div class="w-2.5 h-2.5 rounded-full bg-green-500"></div>
</div>
</div>
<div class="flex-1 overflow-y-auto space-y-2 text-[12px] opacity-90 pr-2 custom-scrollbar">
<p><span class="text-gray-500">[10:42:01]</span> <span class="text-green-400">INFO</span>: siga-inventory scale-out successful</p>
<p><span class="text-gray-500">[10:41:45]</span> <span class="text-blue-400">DEBUG</span>: Gateway routing table updated</p>
<p><span class="text-gray-500">[10:39:12]</span> <span class="text-yellow-400">WARN</span>: High latency detected in siga-auth</p>
<p><span class="text-gray-500">[10:38:05]</span> <span class="text-green-400">INFO</span>: Automated backup completed</p>
<p><span class="text-gray-500">[10:35:22]</span> <span class="text-red-400">ERROR</span>: Connection timeout in siga-sales db pool</p>
<p><span class="text-gray-500">[10:35:25]</span> <span class="text-blue-400">DEBUG</span>: Retry mechanism engaged for siga-sales</p>
<p><span class="text-gray-500">[10:36:01]</span> <span class="text-green-400">INFO</span>: siga-sales connection restored</p>
</div>
<button class="mt-3 w-full py-1.5 border border-[#444] rounded text-xs hover:bg-[#2a2a2a] transition-colors">View Deployment Logs</button>
</div>
<!-- Microservices Grid (Bottom Full Width) -->
<div class="lg:col-span-12 mt-4">
<h3 class="font-title-md text-title-md text-primary mb-4 flex items-center">
<span class="material-symbols-outlined mr-2">dns</span>
                        Microservices Status
                    </h3>
<div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
<!-- Service Card: Auth -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-border-muted shadow-sm hover:border-brand-cyan hover:shadow-md transition-all">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-auth</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v2.1.4</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-tertiary-fixed/20 text-tertiary-container">
<span class="w-1.5 h-1.5 rounded-full bg-success-vibrant mr-1.5"></span> UP
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium">12%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-brand-cyan h-1.5 rounded-full" style="width: 12%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium">256 MB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 45%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-on-surface-variant">14d 2h</span>
</div>
</div>
</div>
<!-- Service Card: Billing -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-border-muted shadow-sm hover:border-brand-cyan hover:shadow-md transition-all">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-billing</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v1.8.0</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-tertiary-fixed/20 text-tertiary-container">
<span class="w-1.5 h-1.5 rounded-full bg-success-vibrant mr-1.5"></span> UP
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium">8%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-brand-cyan h-1.5 rounded-full" style="width: 8%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium">512 MB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 60%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-on-surface-variant">5d 12h</span>
</div>
</div>
</div>
<!-- Service Card: Inventory -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-border-muted shadow-sm hover:border-brand-cyan hover:shadow-md transition-all">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-inventory</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v3.0.1</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-tertiary-fixed/20 text-tertiary-container">
<span class="w-1.5 h-1.5 rounded-full bg-success-vibrant mr-1.5"></span> UP
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium text-error">85%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-error h-1.5 rounded-full" style="width: 85%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium">1.2 GB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 75%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-on-surface-variant">2d 4h</span>
</div>
</div>
</div>
<!-- Service Card: Sales -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-error/30 shadow-sm hover:border-error/60 transition-all bg-error-container/10">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-sales</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v2.5.0</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-error-container text-on-error-container">
<span class="w-1.5 h-1.5 rounded-full bg-error mr-1.5 animate-pulse"></span> DOWN
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium text-outline">0%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-outline h-1.5 rounded-full" style="width: 0%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium text-outline">0 MB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-outline h-1.5 rounded-full" style="width: 0%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-error">Offline</span>
</div>
</div>
</div>
<!-- Additional Services Row to complete 7 -->
<!-- Service Card: Agent -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-border-muted shadow-sm hover:border-brand-cyan hover:shadow-md transition-all">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-agent</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v1.1.0</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-secondary-container/30 text-on-secondary-container">
<span class="w-1.5 h-1.5 rounded-full bg-secondary mr-1.5"></span> STARTING
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium">45%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 45%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium">128 MB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 30%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-on-surface-variant">0d 0h</span>
</div>
</div>
</div>
<!-- Service Card: Gateway -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-border-muted shadow-sm hover:border-brand-cyan hover:shadow-md transition-all">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-gateway</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v4.0.0</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-tertiary-fixed/20 text-tertiary-container">
<span class="w-1.5 h-1.5 rounded-full bg-success-vibrant mr-1.5"></span> UP
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium">22%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-brand-cyan h-1.5 rounded-full" style="width: 22%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium">800 MB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 65%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-on-surface-variant">30d 14h</span>
</div>
</div>
</div>
<!-- Service Card: Registry -->
<div class="bg-surface-container-lowest rounded-xl p-5 border border-border-muted shadow-sm hover:border-brand-cyan hover:shadow-md transition-all">
<div class="flex justify-between items-start mb-4">
<div>
<h4 class="font-label-md text-label-md font-bold text-brand-deep">siga-registry</h4>
<p class="text-xs text-on-surface-variant mt-0.5">v1.0.5</p>
</div>
<span class="flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-tertiary-fixed/20 text-tertiary-container">
<span class="w-1.5 h-1.5 rounded-full bg-success-vibrant mr-1.5"></span> UP
                                </span>
</div>
<div class="space-y-3">
<div>
<div class="flex justify-between text-xs mb-1"><span>CPU</span><span class="font-medium">5%</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-brand-cyan h-1.5 rounded-full" style="width: 5%"></div></div>
</div>
<div>
<div class="flex justify-between text-xs mb-1"><span>RAM</span><span class="font-medium">200 MB</span></div>
<div class="w-full bg-surface-variant rounded-full h-1.5"><div class="bg-secondary h-1.5 rounded-full" style="width: 25%"></div></div>
</div>
<div class="pt-2 border-t border-border-muted/50 text-[11px] text-outline flex justify-between">
<span>Uptime</span><span class="font-medium text-on-surface-variant">45d 6h</span>
</div>
</div>
</div>
</div>
</div>
</div>
</main>
</div>
</body></html>

<!-- DevOps & Quality Center - SIGA Pro -->
<!DOCTYPE html><html class="light" lang="es"><head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<title>SIGA - DevOps &amp; Quality Center</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500&amp;display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "background": "#fdf7ff",
                        "secondary-fixed": "#b3ebff",
                        "on-surface": "#1d1b21",
                        "on-secondary-fixed": "#001f27",
                        "surface-tint": "#5157a6",
                        "inverse-on-surface": "#f5eff7",
                        "surface-container": "#f2ecf5",
                        "secondary-container": "#50d9fe",
                        "success-vibrant": "#10B981",
                        "tertiary-container": "#002018",
                        "outline-variant": "#c7c5d3",
                        "on-primary-container": "#777dcf",
                        "inverse-primary": "#bfc2ff",
                        "on-tertiary-fixed": "#002018",
                        "surface-container-high": "#ece6ef",
                        "secondary": "#00677d",
                        "inverse-surface": "#322f36",
                        "surface": "#fdf7ff",
                        "on-secondary-fixed-variant": "#004e5f",
                        "tertiary": "#000000",
                        "on-surface-variant": "#464651",
                        "on-primary-fixed-variant": "#393e8c",
                        "primary-fixed": "#e0e0ff",
                        "on-secondary": "#ffffff",
                        "error-container": "#ffdad6",
                        "primary-container": "#070a61",
                        "surface-container-lowest": "#ffffff",
                        "tertiary-fixed-dim": "#5adcb9",
                        "surface-variant": "#e6e0e9",
                        "border-muted": "#D9D9D9",
                        "on-primary-fixed": "#070a61",
                        "on-error": "#ffffff",
                        "surface-bright": "#fdf7ff",
                        "tertiary-fixed": "#79f8d5",
                        "primary": "#000000",
                        "secondary-fixed-dim": "#4cd6fb",
                        "outline": "#777682",
                        "on-background": "#1d1b21",
                        "surface-dim": "#ded8e1",
                        "on-tertiary": "#ffffff",
                        "on-secondary-container": "#005c70",
                        "on-primary": "#ffffff",
                        "primary-fixed-dim": "#bfc2ff",
                        "on-error-container": "#93000a",
                        "surface-container-highest": "#e6e0e9",
                        "on-tertiary-container": "#009579",
                        "error": "#ba1a1a",
                        "surface-container-low": "#f8f1fa",
                        "on-tertiary-fixed-variant": "#005140"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "base": "8px",
                        "margin-mobile": "16px",
                        "margin-desktop": "32px",
                        "gutter": "24px",
                        "container-max": "1280px"
                    },
                    "fontFamily": {
                        "title-md": ["Hanken Grotesk"],
                        "body-lg": ["Hanken Grotesk"],
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "headline-lg": ["Hanken Grotesk"],
                        "code-sm": ["jetbrainsMono"],
                        "label-md": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "body-sm": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "title-md": ["20px", { "lineHeight": "28px", "fontWeight": "600" }],
                        "body-lg": ["16px", { "lineHeight": "24px", "fontWeight": "400" }],
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "600" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600" }],
                        "code-sm": ["13px", { "lineHeight": "18px", "fontWeight": "400" }],
                        "label-md": ["14px", { "lineHeight": "16px", "letterSpacing": "0.01em", "fontWeight": "500" }],
                        "display-lg": ["56px", { "lineHeight": "64px", "letterSpacing": "-0.02em", "fontWeight": "700" }],
                        "body-sm": ["14px", { "lineHeight": "20px", "fontWeight": "400" }]
                    }
                }
            }
        }
    </script>
<style>
        body { font-family: 'Hanken Grotesk', sans-serif; background-color: #fdf7ff; }
        .glass-panel {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(16px);
            border: 1px solid rgba(217, 217, 217, 0.5);
            box-shadow: 0px 4px 12px rgba(3, 4, 94, 0.04);
        }
    </style>
</head>
<body class="bg-background text-on-background min-h-screen flex antialiased">
<!-- SideNavBar (SIGA) -->
<nav class="hidden md:flex h-screen w-64 fixed left-0 top-0 flex-col py-8 px-4 bg-primary-container dark:bg-tertiary-container border-r border-outline-variant dark:border-outline shadow-sm z-20">
<div class="flex items-center gap-4 mb-12 px-4">
<div class="w-12 h-12 bg-secondary-fixed-dim rounded-lg flex items-center justify-center shrink-0">
<span class="font-display-lg text-display-lg font-bold text-secondary-fixed-dim text-white text-xl" style="font-size: 24px; line-height: 24px;">S</span>
</div>
<div>
<h1 class="font-title-md text-title-md font-bold text-white">SIGA</h1>
<p class="font-label-md text-label-md text-secondary-fixed-dim">Nexus Admin</p>
</div>
</div>
<ul class="flex flex-col gap-2 flex-1"><li class=""><a class="flex items-center gap-4 px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200" href="#"><span class="material-symbols-outlined">dashboard</span> <span class="font-label-md text-label-md">Inicio</span></a></li><li class=""><a class="flex items-center gap-4 px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200" href="#"><span class="material-symbols-outlined">corporate_fare</span> <span class="font-label-md text-label-md">Tenencia</span></a></li><li class=""><a class="flex items-center gap-4 px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200" href="#"><span class="material-symbols-outlined">dns</span> <span class="font-label-md text-label-md">Microservicios</span></a></li><li class=""><a class="flex items-center gap-4 px-4 py-3 rounded-lg text-secondary-fixed-dim font-bold border-r-4 border-secondary-fixed-dim bg-on-secondary-fixed-variant/20" href="#"><span class="material-symbols-outlined">terminal</span> <span class="font-label-md text-label-md">DevOps</span></a></li><li class=""><a class="flex items-center gap-4 px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200" href="#"><span class="material-symbols-outlined">subscriptions</span> <span class="font-label-md text-label-md">Planes SaaS</span></a></li><li class=""><a class="flex items-center gap-4 px-4 py-3 rounded-lg text-on-secondary-fixed-variant dark:text-outline-variant hover:text-secondary-fixed-dim hover:bg-on-secondary-fixed-variant/10 transition-colors duration-200" href="#"><span class="material-symbols-outlined">settings</span> <span class="font-label-md text-label-md">Configuración</span></a></li></ul>
<div class="mt-auto px-4">
<button class="w-full py-3 bg-secondary-fixed-dim text-on-secondary-fixed font-label-md text-label-md rounded-lg flex justify-center items-center gap-2 hover:bg-secondary-fixed transition-colors scale-95 active:scale-90 transition-transform">
                Generar Reporte
            </button>
</div>
</nav>
<!-- Main Content Area -->
<div class="flex-1 flex flex-col md:ml-64 min-h-screen">
<!-- TopNavBar (SIGA) -->
<header class="fixed top-0 right-0 w-full md:w-[calc(100%-16rem)] h-16 bg-surface/80 backdrop-blur-md dark:bg-surface-dim/80 border-b border-outline-variant dark:border-outline shadow-sm z-10 flex justify-between items-center px-gutter">
<div class="flex items-center gap-4">
<button class="md:hidden text-primary"><span class="material-symbols-outlined">menu</span></button>
<h2 class="font-title-md text-title-md font-bold text-primary dark:text-on-surface hidden md:block">SIGA Dashboard <span class="text-secondary font-normal text-sm ml-2 px-2 py-1 bg-secondary/10 rounded-full">DevOps &amp; Quality</span></h2>
</div>
<div class="flex items-center gap-6">
<div class="relative hidden sm:block focus-within:ring-2 focus-within:ring-secondary-fixed-dim rounded-full">
<span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
<input class="pl-10 pr-4 py-2 bg-surface-container rounded-full border-none text-body-sm focus:ring-0 w-64 text-on-surface" placeholder="Buscar métricas..." type="text">
</div>
<div class="flex items-center gap-4">
<button class="text-on-surface-variant hover:text-secondary transition-colors"><span class="material-symbols-outlined">notifications</span></button>
<button class="text-on-surface-variant hover:text-secondary transition-colors"><span class="material-symbols-outlined">help</span></button>
<img alt="Avatar del Administrador" class="w-8 h-8 rounded-full border border-outline-variant cursor-pointer" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDE4Y4r6mIBDIDwAEVseVLlHhztVrNdn3sYk_DxV-X4xdL-NQKFmUwflRYaMV9MXMcCIdmPmBqAB80DPYqYBGpzXKEErR6jEM82rDg3SnAzScOcn8HKhZRmaPwnNmT1zi0RBiWvoR17Uk21xR278zx6v0TVKmK59twxzNIbzekhKyTLtPC7hkytp9FWv0gBCSvHvxnUobhf6IgAie3nxuoX8HhAwIhN7XiAniiGFNBUBRxvpSGdNbuVSzi1ehHDdasPkGnN89CgIZCe">
</div>
</div>
</header>
<!-- Main Canvas -->
<main class="flex-1 p-margin-mobile md:p-gutter mt-16 max-w-container-max mx-auto w-full flex flex-col gap-gutter">
<!-- Page Header -->
<div class="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
<div>
<div class="flex items-center gap-3 mb-2">
<h1 class="font-headline-lg-mobile md:font-headline-lg text-headline-lg-mobile md:text-headline-lg text-primary">Quality Center</h1>
<span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-tertiary-fixed/20 border border-tertiary-fixed text-tertiary-fixed-dim text-xs font-semibold tracking-wide">
<span class="w-2 h-2 rounded-full bg-success-vibrant animate-pulse"></span> MONITOREO ACTIVO
                        </span>
</div>
<p class="font-body-lg text-body-lg text-on-surface-variant">Estado global del ecosistema SIGA Enterprise y métricas de despliegue continuo.</p>
</div>
<div class="flex gap-3">
<button class="px-4 py-2 border border-primary text-primary font-label-md text-label-md rounded-lg hover:bg-surface-container transition-colors flex items-center gap-2">
<span class="material-symbols-outlined text-sm">sync</span> Sincronizar
                    </button>
<button class="px-4 py-2 bg-secondary text-white font-label-md text-label-md rounded-lg hover:bg-on-secondary-container transition-colors flex items-center gap-2">
<span class="material-symbols-outlined text-sm">rocket_launch</span> Desplegar
                    </button>
</div>
</div>
<!-- KPI Cards Grid -->
<div class="grid grid-cols-1 md:grid-cols-3 gap-6">
<!-- KPI 1 -->
<div class="glass-panel rounded-xl p-6 relative overflow-hidden group hover:-translate-y-1 transition-transform duration-300">
<div class="absolute top-0 right-0 w-32 h-32 bg-secondary/5 rounded-full -translate-y-16 translate-x-16 group-hover:scale-110 transition-transform"></div>
<div class="flex justify-between items-start mb-4">
<div class="w-10 h-10 rounded-lg bg-surface-container flex items-center justify-center text-secondary">
<span class="material-symbols-outlined">fact_check</span>
</div>
<span class="text-success-vibrant font-label-md text-label-md flex items-center gap-1 bg-success-vibrant/10 px-2 py-1 rounded-md"><span class="material-symbols-outlined text-[16px]">arrow_upward</span> 4.2%</span>
</div>
<h3 class="font-label-md text-label-md text-on-surface-variant mb-1 uppercase tracking-wider">Cobertura Global Test</h3>
<div class="flex items-baseline gap-2">
<span class="font-display-lg text-[40px] leading-[48px] font-bold text-primary">87.4%</span>
<span class="text-body-sm text-on-surface-variant">/ 100%</span>
</div>
<div class="w-full bg-surface-variant h-1.5 rounded-full mt-4 overflow-hidden">
<div class="bg-secondary h-full rounded-full" style="width: 87.4%"></div>
</div>
</div>
<!-- KPI 2 -->
<div class="glass-panel rounded-xl p-6 relative overflow-hidden group hover:-translate-y-1 transition-transform duration-300">
<div class="absolute top-0 right-0 w-32 h-32 bg-secondary-fixed-dim/5 rounded-full -translate-y-16 translate-x-16 group-hover:scale-110 transition-transform"></div>
<div class="flex justify-between items-start mb-4">
<div class="w-10 h-10 rounded-lg bg-surface-container flex items-center justify-center text-secondary-fixed-dim">
<span class="material-symbols-outlined">bug_report</span>
</div>
<span class="text-success-vibrant font-label-md text-label-md flex items-center gap-1 bg-success-vibrant/10 px-2 py-1 rounded-md"><span class="material-symbols-outlined text-[16px]">check</span> Óptimo</span>
</div>
<h3 class="font-label-md text-label-md text-on-surface-variant mb-1 uppercase tracking-wider">Deuda Técnica (SonarQube)</h3>
<div class="flex items-baseline gap-2">
<span class="font-display-lg text-[40px] leading-[48px] font-bold text-primary">A</span>
<span class="text-body-sm text-on-surface-variant">Rating</span>
</div>
<p class="text-xs text-on-surface-variant mt-4 flex items-center gap-1"><span class="material-symbols-outlined text-[14px]">history</span> Último scan hace 2h</p>
</div>
<!-- KPI 3 -->
<div class="glass-panel rounded-xl p-6 relative overflow-hidden group hover:-translate-y-1 transition-transform duration-300">
<div class="absolute top-0 right-0 w-32 h-32 bg-primary-container/5 rounded-full -translate-y-16 translate-x-16 group-hover:scale-110 transition-transform"></div>
<div class="flex justify-between items-start mb-4">
<div class="w-10 h-10 rounded-lg bg-surface-container flex items-center justify-center text-primary-container">
<span class="material-symbols-outlined">security</span>
</div>
<span class="text-secondary font-label-md text-label-md flex items-center gap-1 bg-secondary/10 px-2 py-1 rounded-md">1 Pendiente</span>
</div>
<h3 class="font-label-md text-label-md text-on-surface-variant mb-1 uppercase tracking-wider">Vulnerabilidades Críticas</h3>
<div class="flex items-baseline gap-2">
<span class="font-display-lg text-[40px] leading-[48px] font-bold text-primary">0</span>
<span class="text-body-sm text-on-surface-variant">Detectadas</span>
</div>
<p class="text-xs text-on-surface-variant mt-4 flex items-center gap-1"><span class="material-symbols-outlined text-[14px]">shield_lock</span> Snyk &amp; Trivy Pass</p>
</div>
</div>
<!-- Two Column Layout -->
<div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
<!-- Left Col: Table (2/3 width) -->
<div class="lg:col-span-2 glass-panel rounded-xl flex flex-col h-full">
<div class="p-6 border-b border-outline-variant bg-[#F0F9FF] rounded-t-xl flex justify-between items-center">
<h3 class="font-title-md text-title-md text-primary">Componentes del Monorepo</h3>
<div class="flex gap-2">
<div class="relative">
<span class="material-symbols-outlined absolute left-2.5 top-1/2 -translate-y-1/2 text-on-surface-variant text-sm">search</span>
<input class="pl-8 pr-3 py-1.5 text-sm bg-white rounded-md border border-outline-variant focus:border-secondary focus:ring-1 focus:ring-secondary w-48" id="componentSearch" placeholder="Filtrar..." type="text">
</div>
</div>
</div>
<div class="p-0 overflow-x-auto">
<table class="w-full text-left border-collapse" id="componentsTable">
<thead>
<tr class="bg-surface-container-low text-on-surface-variant font-label-md text-label-md uppercase tracking-wider text-xs border-b border-outline-variant">
<th class="p-4 font-medium">Módulo</th>
<th class="p-4 font-medium">Versión</th>
<th class="p-4 font-medium">Pipeline</th>
<th class="p-4 font-medium">Cobertura</th>
<th class="p-4 font-medium text-right">Acción</th>
</tr>
</thead>
<tbody class="text-body-sm divide-y divide-outline-variant/50" id="tableBody">
                    <tr class="hover:bg-surface-container/50 transition-colors">
                        <td class="p-4 font-medium text-primary">SIGA Core API</td>
                        <td class="p-4 text-on-surface-variant font-code-sm">v2.4.1</td>
                        <td class="p-4">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium text-success-vibrant bg-success-vibrant/10">
                                <span class="material-symbols-outlined text-[14px]">check_circle</span>
                                Passed
                            </span>
                        </td>
                        <td class="p-4">
                            <div class="flex items-center gap-2">
                                <div class="w-16 bg-surface-variant h-1.5 rounded-full overflow-hidden">
                                    <div class="bg-success-vibrant h-full rounded-full" style="width: 92%"></div>
                                </div>
                                <span class="text-xs text-on-surface-variant">92%</span>
                            </div>
                        </td>
                        <td class="p-4 text-right">
                            <button class="text-on-surface-variant hover:text-secondary transition-colors" title="Ver Logs">
                                <span class="material-symbols-outlined text-[20px]">terminal</span>
                            </button>
                        </td>
                    </tr>
                
                    <tr class="hover:bg-surface-container/50 transition-colors">
                        <td class="p-4 font-medium text-primary">Inventory Worker</td>
                        <td class="p-4 text-on-surface-variant font-code-sm">v1.8.0</td>
                        <td class="p-4">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium text-success-vibrant bg-success-vibrant/10">
                                <span class="material-symbols-outlined text-[14px]">check_circle</span>
                                Passed
                            </span>
                        </td>
                        <td class="p-4">
                            <div class="flex items-center gap-2">
                                <div class="w-16 bg-surface-variant h-1.5 rounded-full overflow-hidden">
                                    <div class="bg-success-vibrant h-full rounded-full" style="width: 85%"></div>
                                </div>
                                <span class="text-xs text-on-surface-variant">85%</span>
                            </div>
                        </td>
                        <td class="p-4 text-right">
                            <button class="text-on-surface-variant hover:text-secondary transition-colors" title="Ver Logs">
                                <span class="material-symbols-outlined text-[20px]">terminal</span>
                            </button>
                        </td>
                    </tr>
                
                    <tr class="hover:bg-surface-container/50 transition-colors">
                        <td class="p-4 font-medium text-primary">POS Frontend (React)</td>
                        <td class="p-4 text-on-surface-variant font-code-sm">v3.1.2</td>
                        <td class="p-4">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium text-secondary-fixed-dim bg-secondary-fixed-dim/10">
                                <span class="material-symbols-outlined text-[14px]">pending</span>
                                Running
                            </span>
                        </td>
                        <td class="p-4">
                            <div class="flex items-center gap-2">
                                <div class="w-16 bg-surface-variant h-1.5 rounded-full overflow-hidden">
                                    <div class="bg-secondary h-full rounded-full" style="width: 78%"></div>
                                </div>
                                <span class="text-xs text-on-surface-variant">78%</span>
                            </div>
                        </td>
                        <td class="p-4 text-right">
                            <button class="text-on-surface-variant hover:text-secondary transition-colors" title="Ver Logs">
                                <span class="material-symbols-outlined text-[20px]">terminal</span>
                            </button>
                        </td>
                    </tr>
                
                    <tr class="hover:bg-surface-container/50 transition-colors">
                        <td class="p-4 font-medium text-primary">Auth Service</td>
                        <td class="p-4 text-on-surface-variant font-code-sm">v1.1.5</td>
                        <td class="p-4">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium text-error bg-error-container text-on-error-container">
                                <span class="material-symbols-outlined text-[14px]">cancel</span>
                                Failed
                            </span>
                        </td>
                        <td class="p-4">
                            <div class="flex items-center gap-2">
                                <div class="w-16 bg-surface-variant h-1.5 rounded-full overflow-hidden">
                                    <div class="bg-success-vibrant h-full rounded-full" style="width: 95%"></div>
                                </div>
                                <span class="text-xs text-on-surface-variant">95%</span>
                            </div>
                        </td>
                        <td class="p-4 text-right">
                            <button class="text-on-surface-variant hover:text-secondary transition-colors" title="Ver Logs">
                                <span class="material-symbols-outlined text-[20px]">terminal</span>
                            </button>
                        </td>
                    </tr>
                
                    <tr class="hover:bg-surface-container/50 transition-colors">
                        <td class="p-4 font-medium text-primary">Reporting Engine</td>
                        <td class="p-4 text-on-surface-variant font-code-sm">v0.9.1-beta</td>
                        <td class="p-4">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium text-success-vibrant bg-success-vibrant/10">
                                <span class="material-symbols-outlined text-[14px]">check_circle</span>
                                Passed
                            </span>
                        </td>
                        <td class="p-4">
                            <div class="flex items-center gap-2">
                                <div class="w-16 bg-surface-variant h-1.5 rounded-full overflow-hidden">
                                    <div class="bg-error h-full rounded-full" style="width: 65%"></div>
                                </div>
                                <span class="text-xs text-on-surface-variant">65%</span>
                            </div>
                        </td>
                        <td class="p-4 text-right">
                            <button class="text-on-surface-variant hover:text-secondary transition-colors" title="Ver Logs">
                                <span class="material-symbols-outlined text-[20px]">terminal</span>
                            </button>
                        </td>
                    </tr>
                </tbody>
</table>
</div>
</div>
<!-- Right Col: Security & Docker (1/3 width) -->
<div class="flex flex-col gap-6">
<!-- Security Glass Card -->
<div class="glass-panel rounded-xl p-6">
<div class="flex items-center gap-3 mb-6">
<span class="material-symbols-outlined text-secondary">verified_user</span>
<h3 class="font-title-md text-title-md text-primary">Cumplimiento SOC2</h3>
</div>
<ul class="flex flex-col gap-4">
<li class="flex items-center justify-between">
<div class="flex items-center gap-3">
<div class="w-2 h-2 rounded-full bg-success-vibrant"></div>
<span class="text-body-sm text-on-surface-variant">Rotación de Claves AWS</span>
</div>
<span class="text-success-vibrant font-medium text-xs bg-success-vibrant/10 px-2 py-1 rounded">OK</span>
</li>
<li class="flex items-center justify-between">
<div class="flex items-center gap-3">
<div class="w-2 h-2 rounded-full bg-success-vibrant"></div>
<span class="text-body-sm text-on-surface-variant">MFA Enforced</span>
</div>
<span class="text-success-vibrant font-medium text-xs bg-success-vibrant/10 px-2 py-1 rounded">OK</span>
</li>
<li class="flex items-center justify-between">
<div class="flex items-center gap-3">
<div class="w-2 h-2 rounded-full bg-error"></div>
<span class="text-body-sm text-on-surface-variant">Auditoría IAM Logs</span>
</div>
<span class="text-error font-medium text-xs bg-error-container px-2 py-1 rounded text-on-error-container">Revisar</span>
</li>
</ul>
</div>
<!-- Docker Glass Card -->
<div class="glass-panel rounded-xl p-6 flex-1">
<div class="flex items-center justify-between mb-6">
<div class="flex items-center gap-3">
<span class="material-symbols-outlined text-primary-container">dns</span>
<h3 class="font-title-md text-title-md text-primary">Nodos K8s</h3>
</div>
<span class="text-xs text-on-surface-variant">us-east-1</span>
</div>
<div class="space-y-4">
<div>
<div class="flex justify-between text-xs mb-1">
<span class="text-on-surface-variant font-code-sm">CPU Cluster Usage</span>
<span class="font-medium text-primary">64%</span>
</div>
<div class="w-full bg-surface-variant h-1.5 rounded-full overflow-hidden">
<div class="bg-primary-container h-full rounded-full" style="width: 64%"></div>
</div>
</div>
<div>
<div class="flex justify-between text-xs mb-1">
<span class="text-on-surface-variant font-code-sm">Memory Usage</span>
<span class="font-medium text-primary">82%</span>
</div>
<div class="w-full bg-surface-variant h-1.5 rounded-full overflow-hidden">
<div class="bg-secondary h-full rounded-full" style="width: 82%"></div>
</div>
</div>
</div>
</div>
</div>
</div>
<!-- Chart Panel -->
<div class="glass-panel rounded-xl p-6 w-full">
<div class="flex justify-between items-center mb-6">
<h3 class="font-title-md text-title-md text-primary">Despliegues por Entorno (Últimos 7 días)</h3>
<select class="text-sm bg-surface-container border-none rounded-md focus:ring-secondary py-1 pl-3 pr-8 text-on-surface">
<option>Esta semana</option>
<option>Mes actual</option>
</select>
</div>
<div class="w-full h-[300px] relative">
<canvas id="deploymentsChart" width="1852" height="600" style="display: block; box-sizing: border-box; height: 300px; width: 926px;"></canvas>
</div>
</div>
<!-- Footer -->
<footer class="mt-8 pb-8 text-center">
<p class="font-body-sm text-body-sm text-outline tracking-wider flex items-center justify-center gap-2">
<span class="w-4 h-[1px] bg-outline"></span>
                    Un Soñador con Poca RAM
                    <span class="w-4 h-[1px] bg-outline"></span>
</p>
</footer>
</main>
</div>
<script>
        // Data and Logic
        const devopsData = [
            { id: 1, module: 'SIGA Core API', version: 'v2.4.1', pipeline: 'Passed', coverage: 92, lastDeploy: 'hace 2h' },
            { id: 2, module: 'Inventory Worker', version: 'v1.8.0', pipeline: 'Passed', coverage: 85, lastDeploy: 'hace 5h' },
            { id: 3, module: 'POS Frontend (React)', version: 'v3.1.2', pipeline: 'Running', coverage: 78, lastDeploy: 'en progreso' },
            { id: 4, module: 'Auth Service', version: 'v1.1.5', pipeline: 'Failed', coverage: 95, lastDeploy: 'hace 1d' },
            { id: 5, module: 'Reporting Engine', version: 'v0.9.1-beta', pipeline: 'Passed', coverage: 65, lastDeploy: 'hace 2d' }
        ];

        function renderTable(data) {
            const tbody = document.getElementById('tableBody');
            tbody.innerHTML = '';
            
            data.forEach(item => {
                let statusIcon = '';
                let statusClass = '';
                
                if(item.pipeline === 'Passed') { statusIcon = 'check_circle'; statusClass = 'text-success-vibrant bg-success-vibrant/10'; }
                else if(item.pipeline === 'Failed') { statusIcon = 'cancel'; statusClass = 'text-error bg-error-container text-on-error-container'; }
                else { statusIcon = 'pending'; statusClass = 'text-secondary-fixed-dim bg-secondary-fixed-dim/10'; }

                const row = `
                    <tr class="hover:bg-surface-container/50 transition-colors">
                        <td class="p-4 font-medium text-primary">${item.module}</td>
                        <td class="p-4 text-on-surface-variant font-code-sm">${item.version}</td>
                        <td class="p-4">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${statusClass}">
                                <span class="material-symbols-outlined text-[14px]">${statusIcon}</span>
                                ${item.pipeline}
                            </span>
                        </td>
                        <td class="p-4">
                            <div class="flex items-center gap-2">
                                <div class="w-16 bg-surface-variant h-1.5 rounded-full overflow-hidden">
                                    <div class="${item.coverage > 80 ? 'bg-success-vibrant' : (item.coverage > 70 ? 'bg-secondary' : 'bg-error')} h-full rounded-full" style="width: ${item.coverage}%"></div>
                                </div>
                                <span class="text-xs text-on-surface-variant">${item.coverage}%</span>
                            </div>
                        </td>
                        <td class="p-4 text-right">
                            <button class="text-on-surface-variant hover:text-secondary transition-colors" title="Ver Logs">
                                <span class="material-symbols-outlined text-[20px]">terminal</span>
                            </button>
                        </td>
                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', row);
            });
        }

        // Initialize Table
        renderTable(devopsData);

        // Search Logic
        document.getElementById('componentSearch').addEventListener('input', (e) => {
            const term = e.target.value.toLowerCase();
            const filtered = devopsData.filter(item => item.module.toLowerCase().includes(term));
            renderTable(filtered);
        });

        // Chart.js Setup
        const ctx = document.getElementById('deploymentsChart').getContext('2d');
        
        // Use corporate colors from Tailwind Config
        const colorTeal = '#00677d'; // secondary
        const colorBlue = '#070a61'; // primary-container
        const colorCyan = '#4cd6fb'; // secondary-fixed-dim

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
                datasets: [
                    {
                        label: 'Producción',
                        data: [2, 1, 3, 2, 4, 0, 1],
                        backgroundColor: colorTeal,
                        borderRadius: 4,
                        barPercentage: 0.6
                    },
                    {
                        label: 'Staging',
                        data: [5, 3, 6, 4, 8, 2, 2],
                        backgroundColor: colorCyan,
                        borderRadius: 4,
                        barPercentage: 0.6
                    },
                    {
                        label: 'Desarrollo',
                        data: [12, 15, 10, 18, 14, 5, 4],
                        backgroundColor: colorBlue,
                        borderRadius: 4,
                        barPercentage: 0.6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                        align: 'end',
                        labels: {
                            usePointStyle: true,
                            boxWidth: 8,
                            font: { family: 'Hanken Grotesk', size: 12 },
                            color: '#464651' // on-surface-variant
                        }
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        backgroundColor: 'rgba(29, 27, 33, 0.9)', // on-surface
                        titleFont: { family: 'Hanken Grotesk', size: 13 },
                        bodyFont: { family: 'Hanken Grotesk', size: 12 },
                        padding: 12,
                        cornerRadius: 8
                    }
                },
                scales: {
                    x: {
                        stacked: true,
                        grid: { display: false, drawBorder: false },
                        ticks: { font: { family: 'Hanken Grotesk', size: 12 }, color: '#777682' }
                    },
                    y: {
                        stacked: true,
                        grid: { color: '#e6e0e9', drawBorder: false, borderDash: [4, 4] },
                        ticks: { font: { family: 'Hanken Grotesk', size: 12 }, color: '#777682', stepSize: 5 }
                    }
                },
                interaction: {
                    mode: 'nearest',
                    axis: 'x',
                    intersect: false
                }
            }
        });
    </script>


</body></html>