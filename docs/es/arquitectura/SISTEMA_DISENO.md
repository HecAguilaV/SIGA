# Sistema de Diseño SIGA

## 1. Tema Visual y Atmósfera
- **Concepto:** "Precisión Técnica Inspirada en Linear"
- **Vibe:** Ultra-minimalista, preciso, dashboard de alta densidad, centrado en el desarrollador.
- **Iluminación:** Modo oscuro por defecto. Vacíos de espacio profundo con texto nítido de alto contraste y sutiles acentos brillantes.
- **Lenguaje de Formas:** Esquinas afiladas con redondeo muy ligero (`2px` a `6px`). Sin adornos innecesarios. Bordes ultra-finos (hairline).

## 2. Paleta de Colores y Roles

### Base y Superficies
| Nombre | Hex | Uso |
|------|-----|-------|
| `canvas` | `#0E0F11` | Fondo principal de la aplicación. Vacío puro. |
| `surface-primary` | `#1A1B1E` | Tarjetas primarias, paneles, modales. Apenas elevado. |
| `surface-secondary`| `#25262B` | Estados hover, paneles secundarios, inputs. |
| `surface-elevated` | `#2C2E33` | Dropdowns, popovers, elementos activos. |

### Tipografía
| Nombre | Hex | Uso |
|------|-----|-------|
| `text-primary` | `#FFFFFF` | Encabezados, valores primarios. Alto contraste. |
| `text-secondary`| `#A1A1AA` | Cuerpo de texto, etiquetas, información secundaria. |
| `text-tertiary` | `#71717A` | Texto de marcador de posición, estados deshabilitados, timestamps. |

### Acentos y Semántica
| Nombre | Hex | Uso |
|------|-----|-------|
| `accent-primary`| `#5E6AD2` | Púrpura Linear. CTAs primarios, estados activos, indicadores brillantes. |
| `accent-hover`  | `#737EE0` | Anillos de enfoque, estados hover para acentos primarios. |
| `status-success`| `#10B981` | Completado, online, métricas positivas (Esmeralda). |
| `status-warning`| `#F59E0B` | Pendiente, alertas (Ámbar). |
| `status-danger` | `#EF4444` | Errores, acciones destructivas (Rojo). |
| `border-subtle` | `rgba(255,255,255,0.06)` | Divisores, esquemas de paneles. |

## 3. Reglas de Tipografía
- **Familia de Fuentes:** `Inter`, `SF Pro Display`, `system-ui`, sans-serif.
- **Tracking (Interletrado):** Más apretado para encabezados (`-0.02em`), neutral para el cuerpo.
- **Escala:**
  - `Display:` 36px, Peso 600, Tracking -0.03em, `text-primary`
  - `H1:` 24px, Peso 600, Tracking -0.02em, `text-primary`
  - `H2:` 18px, Peso 500, Tracking -0.01em, `text-primary`
  - `Cuerpo:` 14px, Peso 400, Altura de línea 1.6, `text-secondary`
  - `Caption:` 12px, Peso 500, `text-tertiary`
  - `Mono:` `JetBrains Mono`, `ui-monospace`. 13px. Usado para SKUs, IDs, Código.

## 4. Estilos de Componentes

### Botones de Acción
- **Primario:** Fondo `accent-primary`, Texto `#FFF`, Sin borde, Radio `6px`, Altura `32px`, Padding `0 12px`, Tamaño de fuente `13px`, Peso `500`. Sombra: `0 2px 4px rgba(0,0,0,0.2), inset 0 1px 0 rgba(255,255,255,0.1)`.
- **Secundario:** Fondo `transparent`, Texto `text-secondary`, Borde `1px solid border-subtle`, Radio `6px`. Hover: Fondo `surface-secondary`, Texto `text-primary`.

### Tarjetas de Datos (KPIs / Insights)
- Fondo: `surface-primary`.
- Borde: `1px solid border-subtle`.
- Radio de Borde: `8px`.
- Padding: `16px`.
- Transición: `border-color 0.2s ease, transform 0.2s ease`.
- Estado Hover: El borde se vuelve `rgba(255,255,255,0.15)`, transformación `translateY(-1px)`.

## 5. Profundidad y Elevación
- **Nivel 0 (`canvas`):** Fondo de página. Sin sombras.
- **Nivel 1 (`surface-primary`):** Tarjetas. Sombra: `0 1px 2px rgba(0,0,0,0.3)`.
- **Nivel 2 (`surface-elevated`):** Dropdowns. Sombra: `0 8px 24px rgba(0,0,0,0.4), 0 0 0 1px border-subtle`. Backdrop-filter: `blur(12px)`.

## 6. Qué hacer y Qué no hacer
- **HACER** usar negro absoluto o gris muy oscuro para los fondos para que los colores resalten.
- **HACER** usar el tamaño de tipografía para establecer jerarquía en lugar de colores.
- **NO** usar bordes de más de `1px`.
- **NO** usar radios de borde superiores a `8px` para elementos de datos (mantenerlo nítido).
- **NO** usar banners coloridos grandes. Mantener el color reservado para datos, estados y acentos precisos.
