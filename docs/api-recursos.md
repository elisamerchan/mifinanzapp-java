# Servicios web de Mi FinanzApp (AA5-EV03)

API REST de la aplicación: autenticación, categorías, ingresos, gastos, presupuestos, recordatorios y resumen.
La documentación interactiva (OpenAPI/Swagger) se genera automáticamente al iniciar la aplicación:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificación OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

El registro y el inicio de sesión están documentados en [api-auth.md](api-auth.md).

## Diseño

- Estilo REST sobre HTTP, JSON en peticiones y respuestas (`Content-Type: application/json`), prefijo `/api`.
- **Autenticación**: `/api/auth/**` es público. Todo lo demás exige **HTTP Basic** (cabecera `Authorization: Basic base64(email:contraseña)`),
  con las credenciales con las que se registró el usuario. Sin sesión, cookie ni CSRF. Sin credenciales o con credenciales
  incorrectas responde `401 {"mensaje":"Error en la autenticación"}`.
- **Aislamiento por usuario**: cada usuario solo ve y modifica sus propios datos. Pedir un recurso ajeno devuelve `404`, igual que uno inexistente.
- **Verbos**: `GET` consulta, `POST` crea (201), `PUT` reemplaza (200), `DELETE` elimina (204), `PATCH` cambia un estado.
- **Errores**: siempre JSON con `mensaje` (y `errores` cuando son validaciones de campos).

| Código | Cuándo | Cuerpo |
|---|---|---|
| 400 | Campos inválidos, JSON mal formado, id no numérico, regla de negocio incumplida | `{"mensaje":"Datos inválidos","errores":["..."]}` o `{"mensaje":"..."}` |
| 401 | Falta autenticación o es incorrecta | `{"mensaje":"Error en la autenticación"}` |
| 404 | El recurso no existe o es de otro usuario | `{"mensaje":"Gasto no encontrado con id: '9'"}` |
| 409 | Duplicado (usuario ya registrado, presupuesto repetido) | `{"mensaje":"..."}` |

### Capas

| Clase | Responsabilidad |
|---|---|
| `api/*ApiController` | Un controlador REST por recurso; traduce HTTP ↔ servicio |
| `api/*Request` | Cuerpos JSON de entrada con validaciones (Bean Validation) |
| `api/*Response`, `dto/*DTO` | Cuerpos JSON de salida (nunca se exponen las entidades JPA) |
| `api/ApiExceptionHandler` | Convierte excepciones en JSON (400, 404, 409); solo afecta al paquete `api` |
| `api/UsuarioActual` | Resuelve el usuario dueño de la petición a partir de las credenciales |
| `service/*` | Lógica de negocio reutilizada por la aplicación web y la API |
| `config/SecurityConfig` | Dos cadenas: `/api/**` (Basic, sin estado) y la web (formulario + CSRF) |
| `config/OpenApiConfig` | Metadatos de Swagger |

## Endpoints

Todos los ejemplos usan `-u elisa@test.com:secreto123` (HTTP Basic).

### Categorías — `/api/categorias`

| Método y ruta | Descripción | Éxito |
|---|---|---|
| `GET /api/categorias?tipo=GASTO\|INGRESO` | Lista las categorías (el filtro `tipo` es opcional) | 200 |
| `GET /api/categorias/{id}` | Consulta una categoría | 200 |
| `POST /api/categorias` | Crea una categoría | 201 |
| `PUT /api/categorias/{id}` | Actualiza una categoría | 200 |
| `DELETE /api/categorias/{id}` | Elimina una categoría (400 si tiene movimientos o presupuestos) | 204 |

Cuerpo: `{"nombre":"Mascotas","tipo":"GASTO","icono":"🐶"}` — `nombre` (máx. 100) y `tipo` obligatorios; `icono` opcional.
Respuesta: `{"id":12,"nombre":"Mascotas","tipo":"GASTO","icono":"🐶"}`

### Gastos — `/api/gastos`

| Método y ruta | Descripción | Éxito |
|---|---|---|
| `GET /api/gastos?pagina=0` | Lista paginada (20 por página, más recientes primero) | 200 |
| `GET /api/gastos?desde=2026-10-01&hasta=2026-10-31` | Lista sin paginar los gastos del rango | 200 |
| `GET /api/gastos/{id}` | Consulta un gasto | 200 |
| `POST /api/gastos` | Registra un gasto | 201 |
| `PUT /api/gastos/{id}` | Actualiza un gasto | 200 |
| `DELETE /api/gastos/{id}` | Elimina un gasto | 204 |

Cuerpo:

```json
{ "monto": 25000, "fecha": "2026-10-15", "descripcion": "Almuerzo", "metodoPago": "EFECTIVO", "categoriaId": 3 }
```

`monto` > 0, `fecha`, `metodoPago` y `categoriaId` obligatorios. `metodoPago`: `EFECTIVO`, `TARJETA_DEBITO`, `TARJETA_CREDITO`,
`TRANSFERENCIA`, `NEQUI`, `DAVIPLATA`, `OTRO`. La categoría debe ser del usuario y de tipo `GASTO` (si no, 400/404).

Respuesta (201/200):

```json
{ "id": 7, "monto": 25000, "fecha": "2026-10-15", "descripcion": "Almuerzo", "metodoPago": "EFECTIVO",
  "categoriaId": 3, "categoriaNombre": "Alimentación", "categoriaIcono": "🍔" }
```

Listado paginado: `{"contenido":[...],"pagina":0,"totalPaginas":1,"totalElementos":1}`.

### Ingresos — `/api/ingresos`

Mismas rutas y respuestas que gastos, sin `metodoPago`; la categoría debe ser de tipo `INGRESO`.

```json
{ "monto": 2000000, "fecha": "2026-10-01", "descripcion": "Salario", "categoriaId": 1 }
```

### Presupuestos — `/api/presupuestos`

| Método y ruta | Descripción | Éxito |
|---|---|---|
| `GET /api/presupuestos` | Lista con lo gastado, el porcentaje y si se excedió | 200 |
| `GET /api/presupuestos/{id}` | Consulta un presupuesto | 200 |
| `POST /api/presupuestos` | Crea un presupuesto (409 si ya existe categoría + mes + año) | 201 |
| `PUT /api/presupuestos/{id}` | Actualiza un presupuesto | 200 |
| `DELETE /api/presupuestos/{id}` | Elimina un presupuesto | 204 |

Cuerpo: `{"categoriaId":3,"mes":10,"anio":2026,"montoLimite":500000}` — `mes` 1–12, `anio` 2000–2100, `montoLimite` > 0, categoría de tipo `GASTO`.

Respuesta:

```json
{ "id": 1, "mes": 10, "anio": 2026, "montoLimite": 500000, "gastado": 25000, "porcentaje": 5.0,
  "excedido": false, "categoriaId": 3, "categoriaNombre": "Alimentación" }
```

### Recordatorios — `/api/recordatorios`

| Método y ruta | Descripción | Éxito |
|---|---|---|
| `GET /api/recordatorios` | Lista los recordatorios | 200 |
| `GET /api/recordatorios/{id}` | Consulta un recordatorio | 200 |
| `POST /api/recordatorios` | Crea un recordatorio | 201 |
| `PUT /api/recordatorios/{id}` | Actualiza un recordatorio | 200 |
| `PATCH /api/recordatorios/{id}/completar` | Alterna el estado completado | 200 |
| `DELETE /api/recordatorios/{id}` | Elimina un recordatorio | 204 |

Cuerpo: `{"titulo":"Pagar arriendo","descripcion":"Día 5","fecha":"2026-11-05"}` — `titulo` (máx. 200) y `fecha` obligatorios.
Respuesta: `{"id":1,"titulo":"Pagar arriendo","descripcion":"Día 5","fecha":"2026-11-05","completado":false}`

### Resumen — `GET /api/resumen`

Devuelve `200` con los totales del mes y el balance general:

```json
{ "totalIngresosMes": 2000000, "totalGastosMes": 25000, "balance": 1975000, "porcentajeAhorro": 98.8, "movimientosDelMes": 2 }
```

## Probar con curl

```bash
curl -i -u elisa@test.com:secreto123 http://localhost:8080/api/categorias?tipo=GASTO

curl -i -u elisa@test.com:secreto123 -X POST http://localhost:8080/api/gastos -H "Content-Type: application/json" \
     -d '{"monto":25000,"fecha":"2026-10-15","descripcion":"Almuerzo","metodoPago":"EFECTIVO","categoriaId":3}'

curl -i http://localhost:8080/api/gastos        # sin credenciales: 401
```

## Pruebas

- Automáticas: `src/test/java/com/mifinanzapp/api/RecursosApiTest.java` (18 pruebas con contexto completo sobre H2: seguridad 401,
  CRUD de cada recurso, validaciones 400, 404, 409 y aislamiento entre usuarios) y `AuthApiControllerTest.java` (8 pruebas). Ejecución: `mvnw.cmd test`.
- Postman: `docs/postman/MiFinanzApp-API-Completa.postman_collection.json` (51 peticiones, 76 verificaciones) con el entorno
  `MiFinanzApp-Local.postman_environment.json`. Ver `docs/postman/README.md`.
