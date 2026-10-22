# TinyPrice · IATinyFish

Aplicación Android desarrollada siguiendo exclusivamente el [codelab de epanchanaf](https://epanchanaf.github.io/tinyprice-codelab/) y su referencia completa enlazada. Permite buscar productos, consultar hasta diez resultados, revisar un detalle y abrir la fuente original.

**Arquitectura:** Android → Supabase Edge Function → TinyFish Search → Supabase → Android. Android nunca recibe la clave de TinyFish. Los resultados reales muestran **Ver en fuente**: Search no garantiza precios, descuentos, disponibilidad ni valoraciones normalizados.

## Tecnologías y requisitos

| Componente | Configuración |
|---|---|
| Proyecto / aplicación / paquete | IATinyFish / TinyPrice / com.apps.iatinyfish |
| Android Studio | Compatible con AGP 9.4.1 y SDK 37; entorno comprobado: Quail 3, 2026.1.3 Patch 1 |
| JDK para Gradle | 17 o superior; compilación verificada con JDK 17 |
| Java del código | 11 |
| AGP / Gradle Wrapper | 9.4.1 / 9.6.0, con SHA-256 de la distribución |
| Kotlin Compose / Compose BOM | 2.2.10 / 2026.02.01 |
| SDK de compilación / destino / mínimo | 37 / 37 / 23 |
| Datos y estado | ViewModel, StateFlow, corrutinas, Retrofit 3.0.0, Gson y OkHttp |
| Backend | TypeScript, Deno.serve, Supabase Cloud y TinyFish Search |
| Herramientas de despliegue | Node.js y Supabase CLI local; verificadas: Node 24.16.0 y CLI 2.121.0 |

## Abrir y compilar

Clona el repositorio en una carpeta `IATinyFish` y abre **esa carpeta**, la que contiene `settings.gradle.kts`, en Android Studio. Instala Android SDK Platform 37, Build-Tools y Platform-Tools; selecciona JDK 17 o superior para Gradle. Android Studio genera `local.properties` con la ruta local del SDK; ese archivo no se versiona.

Desde esa raíz, en PowerShell:

```powershell
.\gradlew.bat --version
.\gradlew.bat :app:assembleDebug
```

En macOS/Linux usa `./gradlew` en lugar de `.\gradlew.bat`. El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. Conecta un teléfono autorizado API 23 o superior o inicia un emulador y ejecuta el módulo `app` desde Android Studio.

## Modo remoto

La configuración final de `gradle.properties` activa la fuente remota y contiene solamente la URL pública del proyecto de este laboratorio:

```properties
useRemoteDeals=true
edgeRuntimeBaseUrl=https://pkehuyfzmejxsfytuygq.supabase.co/functions/v1
```

Así Android Studio y la terminal usan el mismo modo. También puede indicarse explícitamente:

```powershell
.\gradlew.bat :app:assembleDebug -PuseRemoteDeals=true -PedgeRuntimeBaseUrl=https://pkehuyfzmejxsfytuygq.supabase.co/functions/v1
```

Para reproducir el backend en otra cuenta, sustituye la referencia por la de tu propio proyecto. La propiedad debe terminar en `/functions/v1`, con o sin barra final; Retrofit añade `search-products`. La URL es pública y no constituye una credencial.

## Modo de demostración

```powershell
.\gradlew.bat :app:assembleDebug -PuseRemoteDeals=false
```

`PreviewDealsRepository` devuelve los datos ficticios exactos de la referencia completa: Pixel Store, Game Hub y Tech Mart. La lista los identifica como **de demostración**; no son ofertas reales. Los dominios ficticios no habilitan la acción de abrir una fuente web.

El módulo conserva `false` como valor de respaldo si no existe la propiedad. En esta entrega `gradle.properties` la establece en `true` después de verificar Supabase; por eso la demo se selecciona explícitamente con `-PuseRemoteDeals=false`.

## Preparar Supabase y TinyFish

Sigue los pasos de cuentas y referido del codelab. Crea una API key de TinyFish y comprueba sus créditos y límites en el panel. Crea un proyecto propio de Supabase Cloud, guarda privadamente su contraseña y obtén su referencia. Este laboratorio no crea tablas.

Desde la raíz:

```powershell
npm ci
npx supabase --version
npx supabase login
npx supabase projects list
npx supabase link --project-ref YOUR_PROJECT_REF
```

La carpeta `supabase/` y la función ya están incluidas; `supabase init` y `functions new search-products` se utilizaron para generarlas y no se deben repetir sobre los archivos existentes.

En el Dashboard configura **Edge Functions → Secrets → TINYFISH_API_KEY**. Como alternativa, copia `supabase/.env.example` a `supabase/.env.production.local`, coloca la clave solamente allí y comprueba primero que Git lo ignore:

```powershell
git check-ignore supabase/.env.production.local
npx supabase secrets set --env-file supabase/.env.production.local
npx supabase functions deploy search-products
```

La función acepta POST con `{"query":"Nintendo Switch OLED"}` y devuelve `query`, `offers` y `searchedAt`. Valida consultas de 2 a 200 caracteres, limita la respuesta a diez ofertas y aplica un timeout de 15 segundos al proveedor.

**Alcance didáctico:** `verify_jwt=false` deja el endpoint público. El contador de 20 solicitudes/minuto reside en memoria de una instancia y no garantiza un límite compartido. Esta configuración no es una protección suficiente para distribución pública o producción.

## Estructura

```text
app/src/main/java/com/apps/iatinyfish/
  MainActivity.kt
  data/model/ProductOffer.kt
  data/remote/                 # DTO, Retrofit y cliente HTTP
  data/repository/             # Demo, remoto y proveedor
  presentation/deals/          # Estado, eventos, ViewModel y Compose
  ui/theme/                   # Paleta y temas originales
app/src/test/                 # ViewModel y control de Dispatchers.Main
app/src/androidTest/          # Pantalla inicial de Compose
supabase/config.toml
supabase/functions/search-products/index.ts
docs/                         # Análisis, verificaciones e historial
```

## Pruebas y estado comprobado

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

La última orden requiere un dispositivo autorizado, desbloqueado y con la instalación por USB permitida. No basta con leer `BUILD SUCCESSFUL`: revisa los XML de resultados, ya que se observó una instalación rechazada que no ejecutó pruebas.

Se verificaron la compilación, tres pruebas unitarias y una prueba instrumentada de Compose sin fallos. TinyFish respondió con diez resultados a la prueba directa; la función desplegada devolvió nueve ofertas para Nintendo Switch OLED. El teléfono mostró esos resultados, abrió Nintendo en el navegador y completó una segunda búsqueda de Laptop gamer con diez resultados. También se comprobó el error sin conexión y el reintento tras restaurar la red.

Consulta [el informe detallado](docs/VERIFICACION.md), [el análisis de los 16 pasos](docs/CODELAB.md) y [el historial de cambios](docs/HISTORIAL.md) para las comprobaciones, diferencias respecto de los fragmentos y limitaciones reales del entorno.

## Secretos

No coloques la API key en Kotlin, BuildConfig, Gradle, capturas ni Git. El repositorio excluye `local.properties`, `.env` privados, `node_modules`, compilaciones y metadatos locales. La clave utilizada inicialmente se rotó y el secreto alojado se actualizó y comprobó con otra búsqueda exitosa.

La publicación del código en GitHub no equivale a publicar la aplicación en Google Play. No se generaron claves de firma de producción ni se distribuyó la app públicamente.
