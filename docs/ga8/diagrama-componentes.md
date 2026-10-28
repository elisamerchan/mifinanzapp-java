# Diagrama de componentes (GA8-220501096-AA1-EV01)

```mermaid
flowchart LR
    nav["Navegador web<br/>(Bootstrap 5, Chart.js)"]
    cli["Cliente REST<br/>(Postman / Swagger UI)"]
    subgraph APP["Mi FinanzApp (Spring Boot 3.2.5)"]
        sec["Spring Security<br/>formulario + HTTP Basic"]
        web["Componente Web MVC<br/>controller + Thymeleaf"]
        rest["Componente API REST<br/>api/*ApiController"]
        doc["springdoc OpenAPI<br/>/swagger-ui.html"]
        neg["Componente de negocio<br/>service/*Service"]
        cache["Caché Caffeine<br/>(dashboard, 2 min)"]
        jpa["Acceso a datos JPA<br/>Spring Data / Hibernate"]
        jdbc["Acceso a datos JDBC<br/>JdbcTemplate"]
    end
    db[("PostgreSQL<br/>(Neon / Supabase)")]
    h2[("H2 en memoria<br/>(pruebas)")]
    srv["Módulo Servlets/JSP<br/>(WAR en Tomcat)"]

    nav -->|HTTP| sec
    cli -->|HTTP JSON| sec
    sec --> web
    sec --> rest
    cli --> doc
    web --> neg
    rest --> neg
    neg --> cache
    neg --> jpa
    neg --> jdbc
    jpa --> db
    jdbc --> db
    jpa -.pruebas.-> h2
    jdbc -.pruebas.-> h2
    srv --> h2
```

| Componente | Interfaz que ofrece | Depende de |
|---|---|---|
| Web MVC | Páginas HTML (`/dashboard`, `/ingresos`, ...) | Negocio, Thymeleaf, Security |
| API REST | JSON en `/api/**` | Negocio, Bean Validation, Security |
| Negocio | Servicios Java (`GastoService`, `IngresoService`, ...) | Acceso a datos, Caché |
| Acceso a datos | Repositorios JPA y JDBC | PostgreSQL (H2 en pruebas) |
| Seguridad | Autenticación por formulario (web) y HTTP Basic (API), BCrypt, CSRF | `CustomUserDetailsService` |
| Módulo Servlets/JSP | CRUD de categorías en `/categorias` (WAR) | JDBC sobre H2 |
