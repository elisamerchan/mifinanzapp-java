# Changelog

## [Sin publicar] — Camino al MVP

### Seguridad
- Autorización por propietario en ingresos, gastos, categorías, presupuestos y recordatorios: editar, borrar o sobrescribir un registro ajeno devuelve 404.
- Al guardar se valida que la categoría sea del usuario y del tipo correcto (INGRESO/GASTO).
- CSRF habilitado. Los borrados (y el cierre de sesión) son `POST` con token.
- Eliminadas las credenciales por defecto `admin/admin` de `application.properties`.

### Datos
- CRUD de ingresos con JDBC explícito (`IngresoJdbcRepository`, `JdbcTemplate`, SQL parametrizado).
- Listados de ingresos, gastos e historial paginados (20 por página).
- Presupuestos: no se permite duplicar categoría + mes + año.
- Recordatorios: campo `completado` (columna nueva, `ddl-auto=update` la añade).

### Interfaz
- Barra lateral agrupada (Movimientos, Planificación, Análisis, Aprender) con enlaces a Ingresos y Gastos.
- Mensajes de confirmación y de error tras cada acción; errores de validación por campo sin perder lo escrito.
- Símbolo de la moneda elegida en Configuración en todos los importes.
- Estados vacíos, estilos unificados (utilidades CSS en lugar de estilos en línea), etiquetas accesibles y foco visible.
- Historial con filtros por fecha, tipo, categoría y texto.
- Alertas en el inicio al llegar al 80 % de un presupuesto y para recordatorios pendientes.

### Contenido educativo
- 5 artículos cortos, consejos personalizados según los datos del mes y calculadora de meta de ahorro.

### Calidad
- 44 pruebas: unitarias de servicios, repositorio JDBC sobre H2 y flujo web con `MockMvc`.
- Perfil `prod` (`ddl-auto=validate`, cookies seguras) y `show-sql` desactivado por defecto.
