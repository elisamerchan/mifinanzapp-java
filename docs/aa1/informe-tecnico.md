# Informe técnico — Plan de trabajo y tecnologías seleccionadas

## 1. Descripción del proyecto

Mi FinanzApp es una aplicación web para la gestión de finanzas personales: ingresos, gastos, categorías,
presupuestos mensuales, reportes, recordatorios y contenido educativo.

## 2. Tecnologías seleccionadas

| Capa | Tecnología | Versión | Motivo |
|---|---|---|---|
| Lenguaje | Java | 17 (LTS) | Soporte a largo plazo y tipado fuerte. |
| Framework web | Spring Boot | 3.2.5 | Configuración mínima y ecosistema maduro. |
| Vistas | Thymeleaf | (gestionada por Spring Boot) | Plantillas HTML server-side sin JavaScript obligatorio. |
| Seguridad | Spring Security + BCrypt | (gestionada por Spring Boot) | Autenticación por formulario y contraseñas cifradas. |
| Persistencia | Spring Data JPA / Hibernate | 6.4.4 | Mapeo objeto-relacional para la mayoría de entidades. |
| Acceso JDBC | `JdbcTemplate` | (Spring JDBC) | SQL explícito y parametrizado para el CRUD de ingresos. |
| Base de datos | PostgreSQL | — | Base de datos relacional gestionada (Neon). |
| Caché | Caffeine | — | Caché en memoria de 2 minutos para el dashboard. |
| Frontend | Bootstrap 5.3.3, Chart.js 4.4.1 | — | Diseño responsive y gráficos. |
| Build | Maven (wrapper) | — | Gestión de dependencias y empaquetado. |
| Pruebas | JUnit 5, Mockito, H2, MockMvc | — | Pruebas unitarias, de repositorio y de flujo web. |
| Control de versiones | Git + GitHub | — | Historial de cambios y entrega. |

## 3. Arquitectura

Arquitectura en capas bajo `com.mifinanzapp`:

```
Navegador ──► controller ──► service ──► repository ──► PostgreSQL
                 │              │
                 ▼              ▼
              Thymeleaf      entity / dto / mapper
```

- **controller**: recibe peticiones HTTP y devuelve vistas.
- **service**: reglas de negocio y control de propiedad por usuario.
- **repository**: acceso a datos (JPA y JDBC).
- **entity**: modelo de dominio (ver [diagrama-clases.md](diagrama-clases.md)).
- **exception**: manejador global que traduce errores a páginas 400, 404, 409 y 500.

## 4. Decisiones de diseño principales

Las decisiones completas están en [docs/decisiones.md](../decisiones.md). Resumen:

- JDBC explícito solo para ingresos, para cumplir el requisito de SQL explícito sin reescribir lo que ya funciona en JPA.
- Autorización en cada consulta con `usuario_id`; el id de la URL nunca se considera prueba de propiedad.
- CSRF activo; los borrados son `POST`.

## 5. Plan de trabajo

| Fase | Actividad | Entregable |
|---|---|---|
| 1 | Levantamiento de requisitos y historias de usuario | [historias-de-usuario.md](historias-de-usuario.md) |
| 2 | Modelado: casos de uso y modelo de dominio | [casos-de-uso.md](casos-de-uso.md), [diagrama-clases.md](diagrama-clases.md) |
| 3 | Selección de tecnologías y arquitectura | Este informe |
| 4 | Implementación de entidades, repositorios y servicios | Código en `src/main/java` |
| 5 | Implementación de controladores y vistas | Plantillas en `src/main/resources/templates` |
| 6 | CRUD JDBC de ingresos | `IngresoJdbcRepository` |
| 7 | Seguridad, validación y autorización por propietario | `SecurityConfig`, servicios |
| 8 | Pruebas automatizadas | 46 pruebas en `src/test` |
| 9 | Módulo de servlets y JSP (evidencia AA2-EV02) | Carpeta `mifinanzapp-servlets/` |
| 10 | Documentación y entrega | `docs/`, etiquetas Git, enlace al repositorio |

## 6. Riesgos conocidos

- **Neon (plan gratuito)**: la primera conexión del día puede tardar hasta 60 segundos.
- **Pruebas sobre H2**: el SQL se prueba en H2 en modo PostgreSQL; conviene una prueba manual contra PostgreSQL tras cambiar consultas.
- **Esquema automático**: en desarrollo `ddl-auto=update` modifica la base de datos al arrancar; en producción se usa `validate`.
