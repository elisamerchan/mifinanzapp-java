# Guion del video y capturas (AA5-EV02)

Duración sugerida: 5 a 7 minutos. Graba pantalla completa con la barra de Windows (`Win + G`) u OBS, con micrófono.

## Preparación (antes de grabar)

1. Instalar Postman (https://www.postman.com/downloads/) y abrirlo.
2. Levantar la aplicación desde la carpeta del proyecto:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   ./mvnw.cmd spring-boot:run
   ```
   (necesita el `.env` con las credenciales de la base de datos; el primer arranque de Neon puede tardar hasta 60 s).
3. En Postman: **Import** y elegir los dos archivos de `docs/postman/`. Seleccionar el entorno **Mi FinanzApp - Local**.

## Guion

| Min | Qué mostrar | Qué decir (idea) |
|---|---|---|
| 0:00 | Portada: tu nombre, evidencia GA7-220501096-AA5-EV02 | "Soy Elisa Merchan, voy a probar la API de registro e inicio de sesión de Mi FinanzApp con Postman." |
| 0:30 | Terminal con la app corriendo y `http://localhost:8080` | "La API corre localmente en el puerto 8080." |
| 1:00 | Postman: colección importada y entorno seleccionado | "Importé la colección con 8 pruebas y el entorno con la URL base." |
| 1:30 | Request 1 (registro): pestaña Body, **Send**, ver 201 | "Registro correcto: devuelve 201 y el usuario creado." |
| 2:15 | Request 2 (usuario repetido): **Send**, ver 409 | "Si el usuario ya existe, devuelve 409." |
| 2:45 | Request 3 (datos inválidos): **Send**, ver 400 y la lista de errores | "Con email inválido y contraseña corta devuelve 400." |
| 3:15 | Request 4 (login correcto): **Send**, ver 200 "Autenticación satisfactoria" | "Con credenciales correctas, autenticación satisfactoria." |
| 3:45 | Requests 5 y 6 (contraseña incorrecta / usuario inexistente): ver 401 | "Con credenciales malas devuelve error en la autenticación; el mensaje es el mismo para no revelar qué cuentas existen." |
| 4:30 | Requests 7 y 8 (sin campos / JSON mal formado): ver 400 | "Valida campos obligatorios y JSON inválido." |
| 5:00 | **Runner**: ejecutar toda la colección, mostrar 8 requests y 18 verificaciones en verde | "Ejecuto toda la colección: pasan las 18 verificaciones." |
| 6:00 | Abrir el repositorio en GitHub y la carpeta `docs/postman` | "El código y las pruebas están en el repositorio." |

Consejo: antes de grabar, haz una ejecución de prueba. Cada ejecución del Runner crea un usuario nuevo, así que se puede repetir sin problemas.

## Capturas para el documento de pruebas

Haz estas capturas (una por prueba, mostrando petición **y** respuesta con el código de estado) y pégalas en un documento Word, cada una con su título y una línea con el resultado esperado y obtenido:

1. Postman con la colección y el entorno importados.
2. Registro correcto (201).
3. Registro de usuario repetido (409).
4. Registro con datos inválidos (400).
5. Login correcto (200).
6. Login con contraseña incorrecta (401).
7. Login con usuario inexistente (401).
8. Login sin campos (400).
9. Login con JSON mal formado (400).
10. Resultado del Runner (8 requests, 18 verificaciones, 0 fallos).

Estructura sugerida del documento: portada (nombre y evidencia) → herramienta y entorno (Postman, URL base) → tabla resumen de pruebas (caso, método y ruta, esperado, obtenido, estado) → capturas → conclusión → enlace al repositorio.

## Qué entregar en el ZIP `ELISA_MERCHAN_AA5_EV02.zip`

- `mifinanzapp-java/` (archivos del proyecto, incluida `docs/postman/`).
- Video (MP4).
- Documento con pruebas (Word o PDF).
- `ENDPOINT_AA5_EV02.txt`.
- `ENLACE_REPOSITORIO_AA5_EV02.txt`.
