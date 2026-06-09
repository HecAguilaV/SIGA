# Especificaciones de Diseño Actualizadas: Ecosistema SIGA Pro (A2UI)

## 1. Identidad Visual y Marca

- **Nombre del Producto:** SIGA Pro / SIGA Core
- **Logotipo:** Uso de la "S" icónica integrada en componentes clave, especialmente en el botón flotante del Agente IA.
- **Paleta de Colores (Basada en SIGA):**
  - **Primario (Teal Profundo):** `#03045e` / `#0084d8` (extraídos del logo). Usado en headers, barras laterales y botones principales.
  - **Acento (Cian Vibrante):** `#80ffdb`. Usado para estados de éxito, botones de acción secundaria y el aura del Agente IA.
  - **Fondos (Superficies):** `#fdf7ff` para áreas de trabajo y blanco puro para cards.
  - **Estados:** Rojo suave para alertas críticas, Naranja sutil para stock bajo.

## 2. Tipografía y Formas

- **Fuente Principal:** Hanken Grotesk.
- **Radios (Roundness):** 8px (ROUND_EIGHT) para todos los contenedores y botones.
- **Elevación:** Sombras suaves (Soft Shadows) para separar cards del fondo sin usar bordes pesados.

## 3. Estructura de Componentes (Nombres de Archivo de Referencia)

- **SideNavBar:** Barra lateral blanca con logos de SIGA.
- **TopAppBar:** Barra superior con buscador y selectores de local.
- **KPI Cards:** Paneles de métricas con micro-gráficos en tonos Teal/Cian.
- **Tablas de Datos:** Filas con alto padding y badges de colores SIGA.

## 4. Archivos de Referencia de Marca

- **Logo Principal:** `Logo_SIGA.png`
- **Iconografía/Favicon:** `favicon-96x96.png`, `apple-touch-icon.png`
- **Guía Visual:** `Mockup_Web.png` (usado para la extracción de los colores Teal/Cian originales).

## 5. Lógica de Interacción A2UI

- El Agente IA siempre utiliza el isotipo de la "S" de SIGA como identificador visual.
- Las sugerencias de la IA se presentan con un resplandor cian para diferenciarlas de las acciones manuales.
