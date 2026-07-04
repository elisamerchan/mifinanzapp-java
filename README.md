# Mi FinanzApp

Aplicación web para la gestión de finanzas personales. Permite registrar ingresos y gastos, administrar presupuestos, visualizar reportes y recibir recordatorios.

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17, Spring Boot 3.2.5 |
| Frontend | Thymeleaf, Bootstrap 5.3.3, Chart.js 4.4.1 |
| Base de datos | PostgreSQL (Neon.tech) |
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

### 4. Acceso

1. Regístrate en `/auth/registro` — se crearán categorías predeterminadas automáticamente
2. Inicia sesión en `/auth/login`
3. El dashboard principal mostrará tus finanzas

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
│       └── ...                  # Tests unitarios
└── scripts/
    └── copy-vendor.js           # Script para copiar assets de node_modules
```

## Endpoints Principales

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/auth/login` | GET | Página de inicio de sesión |
| `/auth/registro` | GET/POST | Registro de usuario |
| `/dashboard` | GET | Dashboard principal con resumen financiero |
| `/ingresos` | GET/POST | Gestión de ingresos |
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
