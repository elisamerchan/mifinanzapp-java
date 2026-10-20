# Estándares de codificación

Estándares aplicados en el proyecto (módulos Spring Boot y servlets). Se basan en las convenciones
de Java (Oracle / Google Java Style) con las excepciones de idioma indicadas abajo.

## 1. Idioma

- Nombres de clases, métodos, variables, paquetes, comentarios y textos de interfaz en **español**,
  siguiendo el dominio del negocio (`gasto`, `ingreso`, `presupuesto`).
- Nombres de tablas y columnas en **snake_case** y en español (`usuario_id`, `monto_limite`).
- Los identificadores técnicos de Java y de las APIs (`Override`, `getId`, `List`) quedan en inglés.

## 2. Nombramiento

| Elemento | Convención | Ejemplo |
|---|---|---|
| Paquetes | minúsculas, sin guiones | `com.mifinanzapp.servlets.dao` |
| Clases | PascalCase, sustantivo | `CategoriaDao`, `GastoService` |
| Interfaces | PascalCase, sin prefijo `I` | `IngresoRepository` |
| Excepciones | PascalCase terminado en `Exception` | `ResourceNotFoundException` |
| Métodos | camelCase, verbo + complemento | `listarPorUsuario`, `buscarPorEmail` |
| Variables | camelCase, descriptivas | `totalGastos`, `usuarioId` |
| Booleanos | prefijo que expresa estado | `completado`, `esNuevo` |
| Constantes | UPPER_SNAKE_CASE | `LONGITUD_MAXIMA_NOMBRE` |
| Parámetros de URL y formularios | minúsculas | `accion`, `id` |
| Rutas HTTP | minúsculas, plural para colecciones | `/categorias`, `/gastos` |

## 3. Formato

- Indentación de **4 espacios**; sin tabuladores.
- Una sentencia por línea; llaves en la misma línea que la cabecera.
- Líneas de hasta **120 caracteres**.
- Codificación de archivos **UTF-8**.
- `switch` con sintaxis de flecha (`case X ->`) cuando el valor es una constante.

## 4. Comentarios

- Cada clase pública debe tener un **Javadoc** que diga para qué existe.
- Cada método público de servicios, repositorios y servlets debe tener Javadoc con su propósito y, si aplica, `@return`.
- Los comentarios explican **el porqué**, no repiten el código (`// Mismo modo que la aplicación real…`).
- Las constantes y el SQL tienen un comentario cuando su origen no es obvio.

## 5. Buenas prácticas aplicadas

- **Inyección por constructor**; los servicios se prueban con mocks sin contexto de Spring.
- **Sin SQL concatenado**: todas las consultas usan `PreparedStatement` o `JdbcTemplate` con parámetros.
- **Recursos JDBC** cerrados con *try-with-resources*.
- **Errores**: las excepciones de acceso a datos se envuelven en una excepción propia (`AccesoDatosException`).
- **Validación** en un solo lugar del modelo (`Categoria.validar()`), con mensajes listos para mostrar.
- **Post/Redirect/Get** tras cada escritura en formularios.

## 6. Herramientas de verificación

- Pruebas automatizadas: `./mvnw.cmd test` (aplicación principal, 46 pruebas) y `./mvnw.cmd -f mifinanzapp-servlets/pom.xml test` (módulo de servlets, 19 pruebas).
- Revisión manual de nombres y comentarios antes de cada etiqueta de entrega.
