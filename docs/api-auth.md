# Servicio web de registro e inicio de sesión (AA5-EV01)

## Caso

Servicio web para registrar usuarios e iniciar sesión. Recibe un **usuario y una contraseña**: si la autenticación es
correcta responde con un mensaje de autenticación satisfactoria; en caso contrario, con un error de autenticación.

El *usuario* es el email con el que se registra la cuenta (es el mismo identificador que usa la aplicación web).

## Diseño

- Estilo REST sobre HTTP, cuerpos de petición y respuesta en JSON (`Content-Type: application/json`).
- Prefijo `/api`, separado de las rutas de la aplicación web (que devuelven vistas HTML).
- Rutas públicas (`/api/auth/**`); sin estado: no crea sesión ni cookie.
- No exige token CSRF (solo se ignora para `/api/**`; la aplicación web lo sigue exigiendo).
- Las contraseñas se guardan con BCrypt; nunca se devuelven en las respuestas.
- El error de login es el mismo para usuario inexistente y contraseña incorrecta, para no revelar qué cuentas existen.

### Capas

| Clase | Responsabilidad |
|---|---|
| `api/AuthApiController` | Recibe las peticiones y arma las respuestas |
| `api/RegistroRequest`, `api/CredencialesRequest` | Cuerpos JSON con validaciones |
| `api/ApiExceptionHandler` | Errores en JSON (400, 409); solo afecta al paquete `api` |
| `service/UsuarioService.registrar` / `.autenticar` | Lógica de negocio reutilizada por la web y la API |
| `config/SecurityConfig` | Rutas públicas y exclusión de CSRF para `/api/**` |

## Endpoints

### `POST /api/auth/registro`

Cuerpo:

```json
{ "usuario": "elisa@test.com", "password": "secreto123", "nombre": "Elisa" }
```

`nombre` es opcional. `password` requiere al menos 6 caracteres y `usuario` debe ser un email.

| Código | Cuándo | Respuesta |
|---|---|---|
| 201 | Usuario creado | `{"mensaje":"Usuario registrado correctamente","usuario":"elisa@test.com"}` |
| 400 | Datos inválidos o JSON mal formado | `{"mensaje":"Datos inválidos","errores":["..."]}` |
| 409 | El usuario ya existe | `{"mensaje":"Usuario ya existe con email: 'elisa@test.com'"}` |

### `POST /api/auth/login`

Cuerpo:

```json
{ "usuario": "elisa@test.com", "password": "secreto123" }
```

| Código | Cuándo | Respuesta |
|---|---|---|
| 200 | Credenciales correctas | `{"mensaje":"Autenticación satisfactoria","usuario":"elisa@test.com"}` |
| 401 | Usuario inexistente o contraseña incorrecta | `{"mensaje":"Error en la autenticación"}` |
| 400 | Faltan campos o JSON mal formado | `{"mensaje":"Datos inválidos","errores":["..."]}` |

## Probar con curl

```bash
curl -i -X POST http://localhost:8080/api/auth/registro -H "Content-Type: application/json" \
     -d '{"usuario":"elisa@test.com","password":"secreto123","nombre":"Elisa"}'

curl -i -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
     -d '{"usuario":"elisa@test.com","password":"secreto123"}'
```

## Pruebas automáticas

`src/test/java/com/mifinanzapp/api/AuthApiControllerTest.java` (8 pruebas, contexto completo sobre H2):
registro correcto, usuario repetido (409), datos inválidos (400), login correcto (200), contraseña incorrecta (401),
usuario inexistente (401), cuerpo vacío (400) y JSON mal formado (400).
Ejecución: `./mvnw.cmd test`.
