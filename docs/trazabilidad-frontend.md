# Trazabilidad del front-end (GA7-220501096-AA4-EV03)

Relación entre las pantallas codificadas, los artefactos del ciclo de software (`docs/aa1`) y los archivos que las implementan.
Ubicación de las plantillas: `src/main/resources/templates/`.

| Pantalla | Ruta | Historia / caso de uso | Plantilla |
|---|---|---|---|
| Registro | `/auth/registro` | HU-01 / CU1 Registrarse | `auth/registro.html` |
| Inicio de sesión | `/auth/login` | HU-02 / CU2 Iniciar sesión | `auth/login.html` |
| Categorías | `/categorias` | HU-03 / CU3 Gestionar categorías | `categorias/index.html` |
| Ingresos | `/ingresos` | HU-04, HU-05 / CU4 Gestionar ingresos | `ingresos/index.html` |
| Gastos | `/gastos` | HU-06 / CU5 Gestionar gastos | `gastos/index.html` |
| Movimiento rápido | `/movimientos/nuevo` | CU6 Registrar movimiento rápido | `movimientos/nuevo.html` |
| Historial | `/historial` | HU-07 / CU7 Consultar historial | `historial/index.html` |
| Presupuestos | `/presupuestos` | HU-08 / CU8 Gestionar presupuestos | `presupuestos/index.html` |
| Recordatorios | `/recordatorios` | HU-09 / CU9 Gestionar recordatorios | `recordatorios/index.html` |
| Dashboard | `/dashboard` | HU-10 / CU10 Ver dashboard | `dashboard/index.html` |
| Reportes | `/reportes` | HU-10 / CU11 Ver reportes | `reportes/index.html` |
| Educación financiera | `/educacion` | HU-11 / CU12 Consultar artículos | `educacion/**` |
| Configuración | `/configuracion` | CU14 Configurar perfil | `configuracion/index.html` |
| Páginas de error | 400, 404, 409, 500 | Manejo de errores | `error/*.html` |

## Estructura del front-end

- **Fragmentos compartidos** (`fragments/`): `head`, `sidebar`, `header`, `scripts` y `paginacion`; todas las páginas los reutilizan.
- **Estilos** (`static/css`): `main.css` más `components/` y `layout/`.
- **Scripts** (`static/js`): `app.js`, `components/charts.js` (gráficos Chart.js), `components/sidebar.js` y `utils/formatters.js`.
- **Librerías** (`static/vendor`): Bootstrap 5.3.3 y Chart.js 4.4.1, servidas localmente.
- **Diseño responsivo** con Bootstrap (rejilla `col-12 col-lg-*`) y formularios con validación de servidor y mensajes `invalid-feedback`.
- **Seguridad**: los formularios usan `th:action`, que añade el token CSRF.

## Estándares y control de versiones

- Estándares: `docs/estandares-codificacion.md`. Cada plantilla lleva un comentario inicial que explica su propósito.
- Repositorio: https://github.com/elisamerchan/mifinanzapp-java
