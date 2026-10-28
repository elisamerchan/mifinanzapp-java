# Ambientes de desarrollo y pruebas (GA8-220501096-AA1-EV01)

## Ambiente de desarrollo

| Elemento | Valor |
|---|---|
| Sistema operativo | Windows 10/11 |
| JDK | Java 17 (LTS), variable `JAVA_HOME` apuntando al JDK |
| Build | Maven con wrapper `mvnw.cmd` |
| IDE | Visual Studio Code con Extension Pack for Java y Spring Boot Tools (también funciona IntelliJ IDEA) |
| Servidor | Tomcat embebido de Spring Boot, puerto 8080 |
| Base de datos | PostgreSQL gestionado (Neon / Supabase) |
| Configuración | Archivo `.env` (no versionado) con `DB_URL`, `DB_USER`, `DB_PASSWORD`; plantilla en `.env.example` |
| Datos de ejemplo | `APP_DEMO=true` crea el usuario `demo@mifinanzapp.test` con 6 meses de datos |
| Control de versiones | Git + GitHub (`https://github.com/elisamerchan/mifinanzapp-java`) |

Arranque: `./mvnw.cmd spring-boot:run` → `http://localhost:8080`.

## Ambiente de pruebas

| Elemento | Valor |
|---|---|
| Framework | JUnit 5, Mockito, Spring Boot Test, MockMvc, Spring Security Test |
| Base de datos | H2 en memoria en modo PostgreSQL (`src/test/resources/application.properties`, `ddl-auto=create-drop`) |
| Aislamiento | Ninguna prueba usa la base real ni el archivo `.env` |
| Ejecución | `./mvnw.cmd test` (aplicación, 72 pruebas) y `./mvnw.cmd -f mifinanzapp-servlets/pom.xml test` (servlets, 19 pruebas) |
| Reportes | `target/surefire-reports/` |

## Ambiente de producción (perfil `prod`)

- `SPRING_PROFILES_ACTIVE=prod`: `ddl-auto=validate`, cookies de sesión `secure`, plantillas en caché.
- Credenciales como variables de entorno del servidor; se sirve por HTTPS.
- Ejecución: `java -jar target/mi-finanzapp-1.0.0.jar`.
