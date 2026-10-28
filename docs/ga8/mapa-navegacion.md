# Mapa de navegación (GA8-220501096-AA1-EV01)

```mermaid
flowchart TB
    inicio(["/ (inicio)"]) --> login["/auth/login<br/>Iniciar sesión"]
    login <--> registro["/auth/registro<br/>Registro"]
    login -->|credenciales válidas| dash["/dashboard<br/>Inicio"]
    dash --> mov["Movimientos"]
    dash --> plan["Planificación"]
    dash --> ana["Análisis"]
    dash --> apr["Aprender"]
    dash --> conf["/configuracion"]
    mov --> nuevo["/movimientos/nuevo"]
    mov --> ing["/ingresos"]
    mov --> gas["/gastos"]
    mov --> his["/historial"]
    mov --> cat["/categorias"]
    ing --> ingE["/ingresos/editar/{id}"]
    gas --> gasE["/gastos/editar/{id}"]
    cat --> catE["/categorias/editar/{id}"]
    plan --> pre["/presupuestos"]
    plan --> rec["/recordatorios"]
    ana --> rep["/reportes"]
    apr --> edu["/educacion"]
    edu --> art["/educacion/{articulo}"]
    dash -->|POST /auth/logout| login
```

- Todas las rutas, salvo `/auth/**`, los recursos estáticos y Swagger, exigen sesión; sin sesión se redirige a `/auth/login`.
- Los formularios envían `POST /<recurso>/guardar` o `POST /<recurso>/eliminar/{id}` y redirigen al listado (patrón Post/Redirect/Get).
- Los errores muestran las páginas 400, 404, 409 y 500 con un botón "Volver al inicio".
