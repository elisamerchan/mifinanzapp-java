# Decisiones técnicas

| Tema | Decisión | Motivo |
|------|----------|--------|
| JDBC vs JPA | JDBC explícito solo para el CRUD de ingresos; el resto sigue en JPA | Cumple el requisito de SQL explícito sin reescribir lo que ya funciona. Las agregaciones (dashboard, reportes) siguen en `IngresoRepository`. |
| Transacciones | `JpaTransactionManager` comparte la conexión con `JdbcTemplate` | Un `@Transactional` cubre operaciones JDBC y JPA sin configuración extra. |
| Autorización | Cada consulta de lectura, edición o borrado incluye `usuario_id` (`findByIdAndUsuarioId`, `WHERE id = ? AND usuario_id = ?`) | El `id` que llega del formulario o la URL nunca se considera prueba de propiedad. |
| CSRF | Activo; los formularios usan `th:action`, que añade el token | Un `GET` no debe modificar datos, y sin token un sitio ajeno podría enviar peticiones en nombre del usuario. |
| Errores de formulario | `@Valid` + `BindingResult`; errores de negocio vuelven al formulario con un mensaje flash | El usuario no pierde lo escrito ni ve una página de error por un dato inválido. |
| Educación | Artículos como plantillas Thymeleaf, registrados en una lista cerrada del controlador | Sin base de datos ni dependencias; el slug se valida contra la lista para no exponer plantillas arbitrarias. |
| Pruebas | H2 en modo PostgreSQL para pruebas; nunca se usa la base real | Aislamiento total. El SQL es estándar, pero no se ha ejecutado contra PostgreSQL en las pruebas. |
| Paginación | `LIMIT/OFFSET` en JDBC, `Pageable` en JPA; el historial pagina en memoria | El historial une dos fuentes y ya está acotado por fechas. Si crece, pasar a una consulta `UNION`. |
