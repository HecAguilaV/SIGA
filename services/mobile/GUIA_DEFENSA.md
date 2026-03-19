# 🎓 GUÍA DE DEFENSA - SIGA MOBILE V2

Este documento es tu **hoja de ruta** para la defensa. Sigue cada punto paso a paso.

---

## 1. INICIO Y ENTORNO

*   **Qué mostrar**: Ejecuta la app en el emulador (Android Studio > Run 'app'). Muestra el Dashboard cargando los datos.
*   **Configuración**:
    *   Muestra el archivo `build.gradle.kts` (Module: app) para ver las dependencias (Retrofit, Ktor, Compose, Navigation).
    *   **Ruta**: `SIGA APP/build.gradle.kts`
*   **Backend**: Menciona que los microservicios están corriendo (aunque sea simulado o local). La app apunta a `ApiService.kt` donde se define la URL base (`API_BASE_URL`).

## 2. ARQUITECTURA (MVVM)

*   **Explicación**: Usamos **MVVM (Model-View-ViewModel)** para separar lógica de diseño.
*   **Estructura de Carpetas**:
    *   `data/`: Datos y Conexiones.
        *   **Model**: `data/model/` (Ej: `StockItem.kt`, `Sale.kt`). *Entidades de datos.*
        *   **Repository**: `data/repository/SaaSRepository.kt`. *Fuente única de verdad (API + Cache).*
    *   `ui/`: Interfaz de Usuario.
        *   **Screens**: `DashboardScreen.kt`, `InventoryScreen.kt`. *Vistas Composable.*
        *   **ViewModel**: `ui/viewmodel/GlobalViewModel.kt`. *Gestiona el estado (loading, datos).*
*   **Demostración**: Abre `GlobalViewModel.kt` y muestra cómo `_dollarIndicator` guarda el estado y la UI lo observa.

## 3. DISEÑO VISUAL Y USABILIDAD (Material 3)

*   **Qué mostrar**:
    *   Los colores en `ui/theme/Color.kt`. Explica el uso de **ModernBlue** y **AccentCyan** para dar confianza y tecnología.
    *   La navegación inferior (BottomBar) y el **Dashboard Fusion** (Tarjetas, Scroll horizontal).
*   **Accesibilidad**: Textos grandes, contrastes altos (Blanco sobre Azul), íconos claros.
*   **Archivo Clave**: `ui/theme/Theme.kt` (Configuración de MaterialTheme).

## 4. FORMULARIOS Y VALIDACIÓN

*   **Demostración**: Ve a **Inventario** > Botón **(+)** (FloatingActionButton).
*   **Acción**: Intenta guardar un producto vacío.
    *   *Feedback*: Aparece un texto rojo "El nombre es obligatorio".
*   **Código**:
    *   **Ruta**: `InventoryScreen.kt` (Busca `showAddDialog`).
    *   **Lógica**: Mira dentro del `Button(onClick = { ... })`. Ahí están los `if (newProductName.isBlank())`.
    *   **Justificación**: "La validación es reactiva; el usuario ve el error al instante sin recargar."

## 5. GESTIÓN DE ESTADO (StateFlow)

*   **Explicación**: La app reacciona a los datos. Si llega el dólar, se muestra. Si carga, sale `...`.
*   **Código Clave**:
    *   En `DashboardScreen.kt`:
        ```kotlin
        val dollarState by globalViewModel.dollarIndicator.collectAsState()
        ```
    *   Esto hace que la UI se redibuje sola cuando el ViewModel actualiza el valor.

## 6. ANIMACIONES

*   **Demostración**:
    *   Entra al **Chat con IA** (Botón "Soporte" o Micrófono). Muestra cómo los mensajes suben suavemente (`animateScrollToItem`).
    *   Opcional: En Inventario, al cargar, se usa `Crossfade` para transicionar de "Cargando" a la lista.
*   **Ruta**: `InventoryScreen.kt` (Busca `Crossfade`).

## 7. PERSISTENCIA LOCAL (SessionManager)

*   **Qué decir**: "Usamos `SharedPreferences` encapsulado en `SessionManager` para persistir la sesión del usuario y su rol."
*   **Demostración**: Cierra la app y ábrela. Sigues logueado (si implementaste login real) o el rol se mantiene.
*   **Código**: `data/local/SessionManager.kt`.
    *   Muestra funciones como `saveAuthToken` y `getUserRole`.

## 8. RECURSOS NATIVOS (Voz y Cámara)

*   **Doble Recurso**: Integraste **Servicios de Voz (IA)** y **Cámara (Escáner)**.
*   **Demostración CÁMARA**:
    *   En el Dashboard, toca el **Botón Central Negro (Escáner)**.
    *   Se abrirá la app de cámara del dispositivo.
    *   *Justificación*: "Usamos un `Intent` nativo de Android (`ACTION_IMAGE_CAPTURE`) para invocar la cámara del sistema sin reinventar la rueda."
*   **Demostración VOZ**:
    *   Abre el menú inferior (Botón Soporte).
    *   Toca el micrófono 🎙️.
*   **Código**: 
    *   Cámara: `DashboardScreen.kt` (Busca `FloatingActionButton`).
    *   Voz: `service/VoiceService.kt`.

## 9. MICROSERVICIOS Y API PROPIA

*   **Explicación**: El backend es Spring Boot (microservicios). La app lo consume vía Retrofit/Ktor.
*   **Endpoints**: Muestra `data/network/ApiService.kt`.
    *   `GET /api/saas/ventas` (Ventas)
    *   `GET /api/saas/stock` (Inventario)
*   **Integración**: En `SaaSRepository.kt`, la función `getStock()` llama a la API y devuelve los datos limpios a la UI.

## 10. API EXTERNA (Pública)

*   **Qué mostrar**: Las tarjetas de **Dólar, UF y UTM** en el Dashboard.
*   **Explicación**: "Nos conectamos a `mindicador.cl` en tiempo real."
*   **Código**: `data/repository/SaaSRepository.kt`.
    *   Busca la función: `fetchDollarIndicator()`. Se conecta a `https://mindicador.cl/api/dolar`.

## 11. PRUEBAS UNITARIAS

*   **Ejecución**:
    1.  En Android Studio, panel izquierdo (Project).
    2.  Ve a `src/test/java/com/example/sigaapp`.
    3.  Abre `BusinessLogicTest.kt`.
    4.  Clic derecho sobre la clase -> **Run 'BusinessLogicTest'**.
    5.  Muestra las barras verdes ✅.
*   **Explicación**: "Probamos la lógica de negocio pura (cálculo de márgenes y alertas de quiebre de stock) aislada de la interfaz, asegurando que las matemáticas del negocio sean correctas."

## 12. APK FIRMADO

*   **Cómo se hace**:
    *   Menú `Build` > `Generate Signed Bundle / APK`.
    *   Eliges `APK`.
    *   Creas una `KeyStore` (almacén de llaves) con password.
    *   Build Type: `Release`.
*   **Justificación**: "Esto asegura que la app es auténtica y no ha sido manipulada. Es requisito para subir a Play Store."
*   *Nota*: No necesitas generarlo en vivo (tarda), solo explica los pasos.

## 13. MODIFICACIÓN EN TIEMPO REAL

*   **Escenario**: El profesor te pide "Cambia el color de la tarjeta de Inventario a Naranja".
*   **Acción rápida**:
    1.  Abre `DashboardScreen.kt`.
    2.  Busca `LiveMetricTile` (Inventory).
    3.  Cambia `iconColor = ModernBlue` a `Color.Unspecified` o `Color(0xFFFFA500)` (Naranja).
    4.  Dale al rayo ⚡ (Apply Changes) o Run de nuevo.
    5.  ¡Listo!

---

**¡Tú tienes el control!** La app es sólida. Céntrate en mostrar las funcionalidades que funcionan perfecto (Inventario, Dashboard, API Dólar, Validaciones). Si algo falla, di "Estamos trabajando en esa optimización para la v2.1". ¡Éxito! 🚀
