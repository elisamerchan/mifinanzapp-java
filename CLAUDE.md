# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Mi FinanzApp: a server-rendered personal-finance web app (incomes, expenses, categories, monthly budgets, reports, reminders). Java 17 + Spring Boot 3.2.5, Thymeleaf + Bootstrap 5 + Chart.js, PostgreSQL (Neon/Supabase). Code, comments, Javadoc, routes and UI text are in Spanish — keep new code consistent (e.g. `gasto`, `ingreso`, `presupuesto`, `listarPorUsuario`, `buscarPorEmail`).

## Commands (Windows; only `mvnw.cmd` exists, no Unix `mvnw`)

`mvnw.cmd` requires `JAVA_HOME` (JDK 17):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
./mvnw.cmd spring-boot:run          # run at http://localhost:8080
./mvnw.cmd clean package            # build + tests
./mvnw.cmd test                     # all tests
./mvnw.cmd test -Dtest=UsuarioServiceTest                 # one class
./mvnw.cmd test -Dtest=UsuarioServiceTest#registrarShouldThrowExceptionWhenEmailExists  # one method
pnpm install                        # only to refresh vendor assets (postinstall runs scripts/copy-vendor.js)
```

Tests never touch PostgreSQL: service tests are plain JUnit 5 + Mockito (constructor-injected mocks), `IngresoJdbcRepositoryTest` runs the real SQL on in-memory H2 (PostgreSQL mode), and `FlujoWebTest` boots the full context with MockMvc on H2 (`src/test/resources/application.properties`).

## Configuration

- DB credentials come from `DB_URL`, `DB_USER`, `DB_PASSWORD` (see `.env.example`). `config/DotenvInitializer` (registered manually in `MiFinanzAppApplication.main`, not as a bean) loads `.env` from the working directory into system properties before the context starts, and skips values containing `<`/`>` placeholders. Run from the project root so `.env` is found.
- `spring.jpa.hibernate.ddl-auto=update`: the schema is derived from the JPA entities; there are no migrations. Entity changes alter the live DB on startup.
- Neon free tier sleeps: the first connection of the day can take ~60 s.

## Architecture

Classic layered MVC under `com.mifinanzapp`: `controller` → `service` → `repository` (Spring Data JPA, plus `IngresoJdbcRepository` with explicit `JdbcTemplate` SQL for the incomes CRUD) → `entity`, plus `dto`/`mapper` (only for Gasto, Ingreso, user registration), `exception`, `security`, `util` (`DateUtils`, `MonetaryUtils`).

- **Multi-tenancy by user**: every entity belongs to a `Usuario`. Controllers take `Authentication auth`, resolve the user with `usuarioService.buscarPorEmail(auth.getName())` (email = username), and pass `usuario.getId()` to services/repositories. New queries must filter by `usuarioId`.
- **Controllers are server-rendered, not REST** (the only REST code is `com.mifinanzapp.api`: `/api/auth/registro` and `/api/auth/login`, JSON, CSRF-exempt under `/api/**`, see `docs/api-auth.md`): GET returns a Thymeleaf view (`"gastos/index"`), forms POST to `/<recurso>/guardar` then `redirect:`; edit is `GET /<recurso>/editar/{id}` (reuses the index view with the form pre-filled), delete is `POST /<recurso>/eliminar/{id}` (a GET must never modify data).
- **Security** (`config/SecurityConfig`): form login at `/auth/login` (success → `/dashboard`), logout at `/auth/logout` (POST), `/auth/**` and static paths public, everything else authenticated. CSRF is enabled: forms must use `th:action` so Thymeleaf adds the token. Passwords use BCrypt; `security/CustomUserDetailsService` loads users by email.
- **Registration** (`UsuarioService.registrar`) also seeds the user's default categories; it throws `DuplicateResourceException` for an existing email.
- **Categories** have a `TipoCategoria` (INGRESO/GASTO); forms list only the matching type via `categoriaService.listarPorTipo`.
- **Dashboard caching**: `DashboardService` methods are `@Cacheable("dashboard")` with keys like `#usuarioId + '-ingresos'` (Caffeine, 2 min TTL, `config/CacheConfig`). Every write in `GastoService`, `IngresoService`, `PresupuestoService` has `@CacheEvict(value = "dashboard", allEntries = true)`. Add the same eviction to any new write path that affects dashboard figures.
- **Errors**: `exception/GlobalExceptionHandler` maps `ResourceNotFoundException`→`error/404`, `DuplicateResourceException`→`error/409`, `IllegalArgumentException`→`error/400`, anything else→`error/500`. Throw these from services instead of returning nulls or ResponseEntities.
- **Global model attributes**: `config/GlobalControllerAdvice` adds `currentUri` (sidebar active link), `nombre`, `email` to every view.

## Frontend

- Templates in `src/main/resources/templates/<modulo>/index.html` compose shared fragments: `fragments/head :: head('Título')`, `fragments/sidebar :: sidebar`, `fragments/header :: header('Título')`, `fragments/scripts :: scripts`. New pages follow the same structure and need a sidebar entry.
- Static assets: `static/css` (`main.css` + `components/`, `layout/`), `static/js` (`app.js`, `components/charts.js`, `components/sidebar.js`, `utils/formatters.js`). Bootstrap and Chart.js are served locally from `static/vendor/` (committed; no CDN). Thymeleaf cache is off, so template edits show up on reload.
