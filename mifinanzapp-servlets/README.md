# Mi FinanzApp — Módulo Servlets y JSP (AA2-EV02)

Aplicación web independiente con **Servlets** (Jakarta EE 10) y **JSP** + JSTL, que implementa el CRUD de
categorías con formularios HTML que usan los métodos `GET` y `POST`.

Es independiente de la aplicación Spring Boot del directorio padre: comparte el repositorio y el dominio
(categorías de ingreso y gasto), pero no el código.

## Requisitos

- JDK 17
- Maven (o el wrapper `../mvnw.cmd`)
- Tomcat 10.1 para ejecutar el WAR (las clases `jakarta.*` no funcionan en Tomcat 9)

## Compilar y probar

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
../mvnw.cmd -f pom.xml clean package
```

Genera `target/mifinanzapp-servlets.war` y ejecuta 19 pruebas:

- `CategoriaDaoTest` (7): JDBC contra H2 en memoria, con el mismo modo que la aplicación.
- `CategoriaServletTest` (12): métodos GET y POST con peticiones simuladas (Mockito).

## Desplegar en Tomcat 10.1

1. Copiar `target/mifinanzapp-servlets.war` en `%CATALINA_HOME%\webapps\`.
2. Arrancar Tomcat y abrir `http://localhost:8080/mifinanzapp-servlets/`.

La base de datos H2 es **en memoria**: al reiniciar Tomcat vuelven los datos de ejemplo.

## Estructura

```
src/main/java/com/mifinanzapp/servlets/
├── config/InicializadorBaseDatos.java   Crea H2, la tabla y los datos de ejemplo al arrancar
├── dao/CategoriaDao.java                CRUD con JDBC y PreparedStatement
├── dao/AccesoDatosException.java        Excepción de acceso a datos
├── modelo/Categoria.java                Modelo y validación
└── servlet/CategoriaServlet.java        Controlador: GET y POST sobre /categorias
src/main/webapp/
├── index.jsp                            Redirige a /categorias
└── WEB-INF/
    ├── web.xml
    └── jsp/
        ├── fragmentos/                  Cabecera y pie (directiva include)
        └── categorias/                  listado.jsp y formulario.jsp (JSTL y EL)
pruebas-tomcat/Smoke.java                Prueba de humo HTTP contra Tomcat embebido
```

## Rutas y métodos

| Método | URL | Acción |
|---|---|---|
| GET | `/categorias` o `?accion=listar` | Tabla de categorías |
| GET | `?accion=nuevo` | Formulario vacío |
| GET | `?accion=editar&id=N` | Formulario con los datos de la categoría (404 si no existe) |
| POST | `accion=guardar` (con `id` opcional, `nombre`, `tipo`) | Crea o actualiza; si hay errores vuelve al formulario |
| POST | `accion=eliminar&id=N` | Elimina (404 si no existe) |

Los POST redirigen con PRG (Post/Redirect/Get), así recargar la página no repite la operación.

## Prueba de humo con Tomcat embebido

Comprueba por HTTP el CRUD completo, incluidas las JSP renderizadas. Requiere el WAR ya generado y las
JAR `tomcat-embed-core`, `tomcat-embed-jasper`, `tomcat-embed-el` y `ecj` (por ejemplo, descargadas con
`mvn dependency:copy-dependencies` en un proyecto aparte):

```powershell
javac -cp "lib\*" -d out pruebas-tomcat\Smoke.java
java -cp "out;lib\*" Smoke target\mifinanzapp-servlets.war
```

Salida esperada:

```text
OK    GET listado 200
OK    listado muestra datos de ejemplo
OK    index redirige a categorias
OK    GET nuevo 200 con formulario
OK    POST guardar redirige (PRG)
OK    redirect lleva mensaje guardada
OK    listado muestra la categoria nueva y el aviso
OK    POST invalido muestra errores
OK    GET editar carga datos
OK    GET editar inexistente 404
OK    accion desconocida 400
OK    POST eliminar redirige
TODAS LAS COMPROBACIONES OK
```
