# Pruebas de la API con Postman

Archivos de esta carpeta:

| Archivo | Contenido |
|---|---|
| `MiFinanzApp-API-Completa.postman_collection.json` | **AA5-EV04**: 51 peticiones y 76 verificaciones de todos los servicios (auth, categorías, gastos, ingresos, presupuestos, recordatorios, resumen) |
| `MiFinanzApp-API-Auth.postman_collection.json` | AA5-EV02: 8 pruebas del registro y el login |
| `MiFinanzApp-Local.postman_environment.json` | Entorno con la variable `baseUrl` (`http://localhost:8080`) |

## Cómo ejecutarlas

1. Iniciar la aplicación: `mvnw.cmd spring-boot:run` (necesita `.env` con la base de datos, ver `.env.example`).
2. Postman → **Import** → seleccionar la colección y el entorno → elegir el entorno **Mi FinanzApp - Local**.
3. Abrir la colección → **Run** → ejecutar en orden. La petición 1.1 crea un usuario nuevo con un correo único y guarda
   `usuario` y `password`; el resto de peticiones se autentican con HTTP Basic usando esas variables y se pasan los ids entre sí
   (`categoriaGastoId`, `gastoId`, ...). No hay que configurar nada a mano.

Por consola (opcional): `npx newman run MiFinanzApp-API-Completa.postman_collection.json -e MiFinanzApp-Local.postman_environment.json`.
