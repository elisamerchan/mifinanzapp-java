# Diagrama de paquetes (GA8-220501096-AA1-EV01)

Paquetes de la aplicación principal (`com.mifinanzapp`) y del módulo de servlets (`com.mifinanzapp.servlets`).
Las flechas indican dependencia ("usa").

```mermaid
flowchart TB
    subgraph PRES["Capa de presentación"]
        controller["controller<br/>(MVC + Thymeleaf)"]
        api["api<br/>(REST JSON)"]
        templates["templates / static<br/>(HTML, CSS, JS)"]
    end
    subgraph NEG["Capa de negocio"]
        service["service"]
        dto["dto"]
        mapper["mapper"]
        exception["exception"]
    end
    subgraph DAT["Capa de datos"]
        repository["repository<br/>(JPA + JDBC)"]
        entity["entity"]
    end
    subgraph TRANS["Transversales"]
        config["config"]
        security["security"]
        util["util"]
    end
    controller --> service
    controller --> templates
    controller --> dto
    api --> service
    api --> dto
    service --> repository
    service --> entity
    service --> exception
    mapper --> dto
    mapper --> entity
    controller --> mapper
    repository --> entity
    config --> security
    security --> repository
    controller --> util
    service --> util
```

| Paquete | Responsabilidad |
|---|---|
| `controller` | Controladores MVC que reciben peticiones del navegador y devuelven vistas Thymeleaf. |
| `api` | Controladores REST (`/api/**`), records de entrada con validación y respuestas DTO. |
| `service` | Reglas de negocio, autorización por propietario, transacciones y caché. |
| `dto` / `mapper` | Objetos de transferencia y conversión entidad ↔ DTO. |
| `repository` | Acceso a datos: Spring Data JPA y `IngresoJdbcRepository` con SQL explícito. |
| `entity` | Modelo de dominio JPA (Usuario, Categoria, Ingreso, Gasto, Presupuesto, Recordatorio). |
| `exception` | Excepciones de negocio y manejador global de errores. |
| `security` | Carga de usuarios para Spring Security (`CustomUserDetailsService`). |
| `config` | Seguridad, caché, OpenAPI, carga de `.env`, datos demo y atributos globales. |
| `util` | Utilidades de fechas y montos. |
| `servlets.*` | Módulo independiente con Servlets/JSP: `config`, `dao`, `modelo`, `servlet`. |
