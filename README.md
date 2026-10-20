# Mi FinanzApp

Aplicación web para la gestión de finanzas personales. Permite registrar ingresos y gastos, administrar presupuestos, visualizar reportes y recibir recordatorios.

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17, Spring Boot 3.2.5 |
| Frontend | Thymeleaf, Bootstrap 5.3.3, Chart.js 4.4.1 |
| Base de datos | PostgreSQL (Neon.tech) |
| Acceso a datos | Spring Data JPA/Hibernate + JDBC explícito (`JdbcTemplate`) |
| Build | Maven |
| Cache | Caffeine (2 min TTL) |
| Seguridad | Spring Security + BCrypt |

## Requisitos

- Java 17 o superior
- Maven 3.8+ (incluye wrapper `mvnw.cmd`)
- Variable de entorno `JAVA_HOME` apuntando a tu JDK 17 (ej: `C:\Program Files\Java\jdk-17`)
- Node.js 18+ y pnpm (solo para assets frontend)

## Configuración Inicial

### 1. Variables de entorno

Copia el archivo de ejemplo y completa tus credenciales de base de datos:

```bash
cp .env.example .env
```

Edita `.env` con tus datos de conexión:

```env
DB_URL=jdbc:postgresql://<host>:5432/<database>?sslmode=require
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña
```

> **Nota sobre Neon (neon.tech)**: El plan gratuito de Neon pone la base de datos en estado de reposo después de períodos de inactividad. **El primer intento de conexión del día puede demorar hasta 60 segundos** mientras el servidor se reactiva. Una vez activo, el rendimiento es normal. Si ves que el formulario de login o registro tarda en cargar, espera unos segundos y la conexión se establecerá.

### 2. Assets frontend (opcional)

Los assets vendor (Bootstrap, Chart.js) están incluidos en `src/main/resources/static/vendor/`. Si deseas actualizarlos:

```bash
pnpm install
```

### 3. Compilar y ejecutar

> **⚠️ Importante**: El wrapper `mvnw.cmd` requiere la variable `JAVA_HOME`. Si no la tienes configurada, defínela antes de ejecutar:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
> ```
> O directamente en una línea:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"; ./mvnw.cmd spring-boot:run
> ```

```bash
# Compilar el proyecto (descarga dependencias, ejecuta tests)
./mvnw.cmd clean package

# Ejecutar la aplicación
./mvnw.cmd spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`.

### 4. Pruebas

```bash
./mvnw.cmd test
```

Ninguna prueba usa PostgreSQL:

- **Unitarias de servicios** (JUnit 5 + Mockito): reglas de negocio y autorización por propietario.
- **`IngresoJdbcRepositoryTest`**: SQL real contra H2 en memoria (modo PostgreSQL).
- **`FlujoWebTest`**: contexto completo con `MockMvc` sobre H2 (`src/test/resources/application.properties`): CSRF, validación, acceso a registros ajenos y carga de las páginas principales.

Límite conocido: el SQL se prueba en H2, no en PostgreSQL; conviene una prueba manual tras cambiar consultas.

### 5. Perfiles y despliegue

| Perfil | Uso | Diferencias |
|--------|-----|-------------|
| *(por defecto)* | Desarrollo | `ddl-auto=update`, plantillas sin caché |
| `prod` | Producción (`SPRING_PROFILES_ACTIVE=prod`) | `ddl-auto=validate` (el esquema debe existir), cookies de sesión `secure`, plantillas en caché |

En producción define `DB_URL`, `DB_USER` y `DB_PASSWORD` como variables del servidor (no uses `.env`), sirve la aplicación por HTTPS y arranca el jar con `java -jar target/mi-finanzapp-1.0.0.jar`. Antes de usar `prod` por primera vez, arranca una vez en el perfil por defecto para que Hibernate cree las tablas y columnas nuevas (por ejemplo `recordatorios.completado`).

### 6. Acceso

1. Regístrate en `/auth/registro` — se crearán categorías predeterminadas automáticamente
2. Inicia sesión en `/auth/login`
3. El dashboard principal mostrará tus finanzas

## JDBC y JPA

- **JDBC explícito** (`IngresoJdbcRepository`, SQL parametrizado con `JdbcTemplate`): crear, listar, buscar por id y usuario, actualizar y eliminar **ingresos**. Todas las consultas filtran por el usuario autenticado; si el ingreso no existe o es de otro usuario se responde 404.
- **JPA** sigue en: gastos, categorías, presupuestos, recordatorios, usuarios y las consultas agregadas de ingresos (`IngresoRepository`: totales, evolución mensual, dashboard y reportes).
- La protección CSRF está activa: todos los borrados y el cierre de sesión son `POST` con token.
- Las decisiones de diseño están en [`docs/decisiones.md`](docs/decisiones.md) y el historial de cambios en [`CHANGELOG.md`](CHANGELOG.md).

## Funcionalidades

- **Movimientos:** ingresos y gastos con validación, edición, borrado y paginación; historial con filtros (fecha, tipo, categoría, texto).
- **Planificación:** presupuestos mensuales por categoría con alertas al 80 % y 100 %; recordatorios con estado «hecho» y aviso de vencidos.
- **Análisis:** dashboard y reportes con gráficos.
- **Aprender:** 5 artículos, consejos personalizados según tus datos y calculadora de meta de ahorro.
- **Preferencias:** moneda (símbolo en todos los importes) y nombre en Configuración. El idioma se guarda pero la interfaz está solo en español.

## Estructura del Proyecto

```
mi-finanzapp/
├── pom.xml
├── .env.example
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/mifinanzapp/
│   │   │   ├── MiFinanzAppApplication.java
│   │   │   ├── config/          # Configuración Spring (Security, Cache, Web, etc.)
│   │   │   ├── controller/      # Controladores MVC
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # Entidades JPA
│   │   │   ├── exception/       # Excepciones personalizadas y manejador global
│   │   │   ├── mapper/          # Mapeadores Entity ↔ DTO
│   │   │   ├── repository/      # Repositorios Spring Data JPA
│   │   │   ├── security/        # Servicio de autenticación
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── util/            # Utilidades
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── css/         # Hojas de estilo
│   │       │   ├── js/          # JavaScript
│   │       │   └── vendor/      # Librerías externas (Bootstrap, Chart.js)
│   │       └── templates/       # Plantillas Thymeleaf
│   └── test/java/com/mifinanzapp/
│       └── ...                  # Unitarias, JDBC sobre H2 y flujo web
└── scripts/
    └── copy-vendor.js           # Script para copiar assets de node_modules
```

## Endpoints Principales

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/auth/login` | GET | Página de inicio de sesión |
| `/auth/registro` | GET/POST | Registro de usuario |
| `/dashboard` | GET | Dashboard principal con resumen financiero |
| `/ingresos` | GET/POST | Gestión de ingresos (paginado) |
| `/gastos` | GET/POST | Gestión de gastos |
| `/movimientos/nuevo` | GET/POST | Registro rápido de movimientos |
| `/presupuestos` | GET/POST | Administración de presupuestos |
| `/categorias` | GET/POST | Gestión de categorías |
| `/reportes` | GET | Reportes y gráficos |
| `/historial` | GET | Historial de movimientos |
| `/recordatorios` | GET/POST | Recordatorios financieros |
| `/configuracion` | GET/POST | Configuración del perfil |

## Licencia

Uso personal.
