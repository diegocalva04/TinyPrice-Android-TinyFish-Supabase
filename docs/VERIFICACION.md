# Verificaciones de TinyPrice

Fecha: 22 de octubre de 2026, zona America/Guayaquil. Los estados siguientes describen acciones ejecutadas; una implementación presente en código no equivale por sí sola a una prueba realizada.

## Android y herramientas

| Comprobación | Resultado |
|---|---|
| Proyecto, aplicación y paquete | IATinyFish, TinyPrice, com.apps.iatinyfish. Plantilla Empty Activity existente, creada originalmente por Android CLI; no se afirma creación mediante asistente gráfico. |
| Versiones del codelab | AGP 9.4.1, Wrapper 9.6.0, Kotlin Compose 2.2.10, BOM 2026.02.01, compile/target 37, min 23 y Java 11. Sin sustituciones. |
| Entorno | JDK 17 utilizado para las compilaciones; Android Studio Quail 3, 2026.1.3 Patch 1 detectado. SDK 37 instalado. Node 24.16.0 y Supabase CLI 2.121.0 comprobados. |
| `gradlew --version` y configuración | Gradle responde; `help` terminó con BUILD SUCCESSFUL. Distribución 9.6.0 protegida con SHA-256. |
| `:app:compileDebugKotlin` | BUILD SUCCESSFUL para la capa de datos y después para el ViewModel. |
| `:app:assembleDebug` | BUILD SUCCESSFUL en demostración y en modo remoto con la URL real. |
| Dispositivo | Teléfono Redmi 23100RN82L con Android 15/API 35, autorizado mediante ADB. |
| Skill Android | SKILL.md oficial instalado y CLI 1.0.16625370 operativa. La versión actual no generó las referencias ni el lock descritos por el codelab. |
| Gemini | Actividad opcional no ejecutada: Gemini CLI no estaba instalado. |

## Pruebas automatizadas

| Prueba | Resultado comprobado |
|---|---|
| Consulta vacía | Aprobada; cero llamadas al repositorio falso y mensaje esperado. |
| Búsqueda exitosa | Aprobada; ofertas publicadas y carga finalizada. |
| Error remoto | Aprobada; lista vacía y mensaje legible. |
| Resultado unitario | `:app:testDebugUnitTest`: tres pruebas, cero fallos, cero errores y ninguna omitida. XML en `app/build/test-results/testDebugUnitTest/`. |
| Pantalla inicial Compose | `:app:connectedDebugAndroidTest`: una prueba, cero fallos y ninguna omitida, comprobados en el XML de `app/build/outputs/androidTest-results/connected/debug/`. Se muestran Buscar y Nintendo Switch OLED. |

Antes de las ejecuciones instrumentadas aprobadas hubo intentos fallidos: interrupción del proceso, instalación rechazada con INSTALL_FAILED_USER_RESTRICTED y arranque que no completó el escenario genérico. La prueba final usa la actividad real con las mismas aserciones. Se conserva este diagnóstico y no se cuentan los intentos fallidos como aprobados. Se aprobó primero sobre el APK de demostración y después sobre la versión final con el modo remoto habilitado. En este teléfono fue necesario mantener TinyPrice en primer plano; se abrió la actividad con ADB durante el escenario, sin cambiar permisos ni aserciones. El informe final confirmó una prueba ejecutada y cero fallos.

## Ocho casos manuales del codelab

| Caso | Evidencia y resultado |
|---|---|
| 1. Buscar sin texto | La app mostró «Escribe un producto para iniciar la búsqueda» y Reintentar. |
| 2. Búsqueda válida | La sugerencia Nintendo Switch OLED devolvió tres ofertas de demostración. En remoto devolvió nueve resultados identificados como encontrados en la web. Se capturó el estado «Buscando ofertas…» en una búsqueda real. |
| 3. Detalle | Pixel Store mostró los datos ficticios originales; Nintendo mostró el resumen web, Precio por verificar y Ver en fuente. |
| 4. Atrás | El botón del sistema regresó del detalle a la lista sin cerrar TinyPrice. |
| 5. Fuente externa | Abrir fuente web lanzó Brave en `nintendo.com/es-mx/gaming-systems/switch/oled-model/`; Atrás devolvió a TinyPrice. |
| 6. Claro y oscuro | Se inspeccionaron visualmente ambos temas. Se corrigieron la superposición de la cabecera y el poco contraste de los textos blancos de la portada oscura. Estos textos utilizan onPrimary, sin cambiar la paleta. Se restauró el modo automático original del teléfono. |
| 7. Rotación | En orientación horizontal se conservaron consulta y resultados. Se restauró la orientación vertical original. |
| 8. Sin red y reintento | Se guardó el estado de red, se desactivó temporalmente la conectividad y apareció el error de conexión. Se restauró Wi-Fi activo y datos móviles desactivados, como estaban; Reintentar volvió a mostrar diez resultados reales. |

La segunda consulta Laptop gamer se escribió en el campo y se ejecutó desde el teclado. Se verificaron diez resultados web, sin precios inventados. La semántica accesible se inspeccionó mediante el árbol de UI y se comprobó que los glifos decorativos ya no aparecen como textos accesibles. Se revisaron visualmente la portada y los resultados con fuente ampliada a 1.3, y se restauró el valor original. No se ejecutó un recorrido auditivo completo con TalkBack.

## TinyFish y Supabase

| Comprobación | Resultado |
|---|---|
| TinyFish, acceso | Cuenta funcional; se abrió el referido exacto del codelab. Panel autenticado y credencial utilizada por el servicio. No se comprobó por separado la asignación del beneficio de referido. |
| Créditos | El panel mostró saldo disponible de 15 USD durante la comprobación. No se asume que ese saldo ni los límites permanezcan constantes. |
| Search directo | GET con la consulta Nintendo Switch OLED ofertas, EC y es: HTTP 200 y diez resultados. Se reconocieron position, site_name, title, snippet y url. |
| Supabase, cuenta | Cuenta existente utilizada con autorización expresa del usuario. Acceso con GitHub; el registro por correo del tutorial no se ejecutó. |
| Proyecto real | `tinyprice-codelab`, referencia `pkehuyfzmejxsfytuygq`, organización personal gratuita y región us-east-1. Dashboard con estado Healthy. Sin tablas creadas por esta aplicación. |
| Enlace y despliegue | `supabase link`, configuración del secreto y `functions deploy search-products` ejecutados. La CLI confirmó ACTIVE, versión 1 y verify_jwt=false. El despliegue por API funcionó sin Docker local. |
| Función | Código idéntico al bloque TypeScript del paso 12, incluido timeout de 15 segundos y exactamente un Deno.serve. |
| Rotación | La huella del archivo privado cambió respecto de la clave anterior. Se actualizó el secreto de Supabase y una nueva búsqueda volvió a responder HTTP 200. |
| Endpoint alojado | `https://pkehuyfzmejxsfytuygq.supabase.co/functions/v1/search-products`; POST real HTTP 200, nueve ofertas y searchedAt. |
| Entradas inválidas | Consulta ausente y consulta de un carácter: 400. GET: 405. JSON inválido: 400. |
| Invocations | Dashboard inspeccionado: se localizaron las solicitudes 200, 400 y 405 y sus marcas temporales. |
| Integración | Recorrido completo realizado desde el teléfono, incluyendo apertura de una fuente y segunda consulta. |

La validación local del código comprobó 200, 400, 405, 429, 502 y 503. Para el mapeo se utilizó una respuesta real previamente registrada de TinyFish; los errores de proveedor y de transporte se simularon únicamente en la comprobación aislada, sin modificar el código desplegado.

**Límite del contador:** la solicitud número 21 de una misma instancia aislada devolvió 429. En cambio, 26 solicitudes con JSON inválido al despliegue devolvieron 400 y no se obtuvo 429. No se marca aprobado un límite remoto compartido. El propio codelab advierte que el Map no comparte estado entre instancias y se pierde al reiniciar. Tampoco se provocaron deliberadamente fallos 502/503 en el servicio alojado después de configurarlo.

## Seguridad y fidelidad

- Se inspeccionaron los blobs del historial y los archivos preparados para publicación buscando la clave vigente y patrones de tokens privados; no se encontraron credenciales versionadas.
- `local.properties`, `node_modules/`, archivos de entorno privados y resultados de compilación están ignorados. El único archivo de entorno publicado es un ejemplo con marcador.
- Se inspeccionaron los contenidos descomprimidos de los APK de demostración y remoto: no contienen la clave de TinyFish, el patrón de sus claves ni TINYFISH_API_KEY.
- Android solo conoce la URL pública de Supabase. Las respuestas verificadas no contienen la credencial y conservan enlaces HTTP(S). No se añaden scraping, autenticación del cliente ni base de datos.
- Veinte archivos Android coinciden íntegramente con la referencia completa. Los tres archivos distintos corresponden a las propiedades remotas, las correcciones de márgenes/semántica/contraste y la regla de la prueba instrumentada. El backend coincide con el bloque del paso 12. El wrapper conserva su contenido y añade el permiso ejecutable para Unix.
- La referencia completa incluye Tech Mart como tercera oferta ficticia; no se inventó ese dato. El fragmento explicativo solo muestra las dos primeras ofertas.
- No se publicó en Google Play ni se configuró una distribución pública. La función conserva las limitaciones didácticas advertidas por el tutorial.

## Publicación y reproducción

El resultado de la publicación y la comprobación desde una copia nueva se registran al finalizar en esta sección. El historial de implementación está en [HISTORIAL.md](HISTORIAL.md).
