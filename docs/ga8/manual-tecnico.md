# Manual técnico — Mi FinanzApp (GA8-220501096-AA1-EV02)

## 1. Propósito y alcance

Guía para instalar, configurar, compilar, probar, desplegar y mantener Mi FinanzApp. Va dirigido a
desarrolladores y administradores. El funcionamiento para el usuario final está en las pantallas de la aplicación.

## 2. Componentes del sistema

| Componente | Artefacto | Tecnología |
|---|---|---|
| Aplicación web + API REST | `mi-finanzapp-1.0.0.jar` | Java 17, Spring Boot 3.2.5, Thymeleaf, Spring Security |
| Módulo Servlets/JSP (categorías) | `mifinanzapp-servlets.war` | Jakarta Servlet 6, JSP/JSTL, Tomcat 10.1 |
| Base de datos | PostgreSQL (Neon / Supabase) | Tablas generadas por Hibernate |

## 3. Requisitos

- JDK 17 (variable `JAVA_HOME`).
- Maven 3.8+ o el wrapper `mvnw.cmd` incluido.
- Acceso a una base PostgreSQL (URL, usuario y contraseña).
- Tomcat 10.1 solo para el módulo de servlets.
- Opcional: Node.js 18+ y pnpm para actualizar Bootstrap/Chart.js; Postman para probar la API.

## 4. Instalación y configuración

1. Clonar: `git clone https://github.com/elisamerchan/mifinanzapp-java.git`.
2. Copiar `.env.example` a `.env` en la raíz y completar:
   ```
   DB_URL=jdbc:postgresql://<host>:5432/<base>?sslmode=require
   DB_USER=<usuario>
   DB_PASSWORD=<contraseña>
   APP_DEMO=true   # opcional: crea el usuario demo@mifinanzapp.test / demo1234
   ```
3. `DotenvInitializer` lee `.env` al arrancar (ejecutar desde la raíz del proyecto). En servidores se usan variables de entorno, no `.env`.

## 5. Compilación y pruebas

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
./mvnw.cmd clean package                                 # compila, ejecuta 72 pruebas y genera target/mi-finanzapp-1.0.0.jar
./mvnw.cmd -f mifinanzapp-servlets/pom.xml clean package  # 19 pruebas y target/mifinanzapp-servlets.war
./mvnw.cmd test -Dtest=GastoServiceTest                  # una clase de prueba
```

Las pruebas usan H2 en memoria y no tocan la base real. Resultados en `target/surefire-reports/`.

## 6. Ejecución y despliegue

| Caso | Comando / pasos | URL |
|---|---|---|
| Desarrollo | `./mvnw.cmd spring-boot:run` | http://localhost:8080 |
| JAR | `java -jar target/mi-finanzapp-1.0.0.jar` | http://localhost:8080 |
| Producción | `SPRING_PROFILES_ACTIVE=prod` + `DB_URL`, `DB_USER`, `DB_PASSWORD` como variables; `java -jar ...`; detrás de HTTPS | dominio del servidor |
| Servlets | Copiar el WAR en `%CATALINA_HOME%\webapps\` y arrancar Tomcat 10.1 | http://localhost:8080/mifinanzapp-servlets/ |
| Documentación API | Con la app en marcha | http://localhost:8080/swagger-ui.html |

Perfil `prod`: `ddl-auto=validate` (el esquema debe existir; arrancar una vez en el perfil por defecto para crearlo),
cookies de sesión `secure`, plantillas en caché. Para cambiar el puerto: `--server.port=9090`.

## 7. Base de datos

Tablas: `usuarios`, `categorias`, `ingresos`, `gastos`, `presupuestos`, `recordatorios`. Todas las tablas de datos tienen
`usuario_id` (FK a `usuarios`). Restricciones relevantes: `usuarios.email` único; presupuesto único por categoría + mes + año
(validado en el servicio); importes `NUMERIC(12,2)`.

Copia de seguridad (PostgreSQL): `pg_dump "<DB_URL sin jdbc:>" > respaldo.sql`; restaurar con `psql ... < respaldo.sql`.

## 8. Estructura del código

```
src/main/java/com/mifinanzapp/
  api/          controladores REST (/api/**), records de entrada/salida
  controller/   controladores MVC (vistas Thymeleaf)
  service/      reglas de negocio, transacciones, caché
  repository/   Spring Data JPA + IngresoJdbcRepository (SQL explícito)
  entity/       entidades JPA
  dto/, mapper/ objetos de transferencia y conversión
  config/       seguridad, caché, OpenAPI, .env, datos demo
  security/     carga de usuarios para Spring Security
  exception/    excepciones y manejador global
  util/         fechas y montos
src/main/resources/templates/   vistas por módulo + fragments/
src/main/resources/static/       css, js, vendor (Bootstrap, Chart.js)
mifinanzapp-servlets/            módulo independiente Servlets/JSP
docs/                            documentación (aa1, ga8, api, postman)
```

## 9. Cómo agregar un módulo nuevo

1. Entidad en `entity` con relación a `Usuario`.
2. Repositorio con consultas que filtren por `usuarioId` (`findByIdAndUsuarioId`).
3. Servicio con `@Transactional`; lanzar `ResourceNotFoundException` / `IllegalArgumentException`; `@CacheEvict("dashboard")` si afecta el dashboard.
4. Controlador web (`GET` listado, `POST /guardar`, `GET /editar/{id}`, `POST /eliminar/{id}`) y plantilla con los fragmentos comunes; enlace en `fragments/sidebar.html`.
5. Si aplica, controlador REST en `api` con un record `*Request` validado.
6. Pruebas unitarias del servicio (Mockito) y de flujo (MockMvc).

## 10. Solución de problemas

| Síntoma | Causa | Solución |
|---|---|---|
| `JAVA_HOME not found` | Variable no definida | `$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"` |
| La app tarda ~60 s en arrancar o en el primer login | Neon en reposo | Esperar; la siguiente conexión es normal |
| `Failed to configure a DataSource` | Falta `.env` o variables | Crear `.env` o definir `DB_URL`, `DB_USER`, `DB_PASSWORD` |
| `Schema-validation: missing column` en `prod` | Esquema desactualizado | Arrancar una vez sin perfil `prod` |
| 403 al enviar un formulario | Falta token CSRF | Usar `th:action` en el formulario |
| 401 en la API | Sin HTTP Basic o credenciales malas | Enviar `Authorization: Basic` con email y contraseña |
| El WAR no arranca en Tomcat 9 | Usa `jakarta.*` | Usar Tomcat 10.1 |
