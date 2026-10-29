# Mi FinanzApp Móvil (GA8-220501096-AA2-EV02)

Aplicación Android nativa (Java) con los módulos del proyecto Mi FinanzApp: resumen del mes, ingresos, gastos,
categorías, presupuestos con alertas y recordatorios. Guarda los datos en una base SQLite local y no pide permisos.

- Paquete: `com.mifinanzapp.movil` · versión 2.0 (versionCode 2) · Android 7.0+ (API 24), destino API 34
- Solo usa el framework de Android; en pruebas usa JUnit 4 y sqlite-jdbc.

## Abrir y ejecutar
1. Android Studio: **File → Open** y elegir esta carpeta (`mifinanzapp-movil`). Esperar el *Gradle Sync*.
2. Elegir un emulador (API 34) o un celular con depuración USB y pulsar **Run ▶**.

## Pruebas unitarias
- Android Studio: clic derecho en `app/src/test` → *Run Tests*; o en terminal `./gradlew test` (Windows: `gradlew.bat test`).
- 45 pruebas JUnit: cálculo de balance y alertas, validación, formato, fechas y el esquema y las consultas SQL sobre SQLite.

## Estructura
```
app/src/main/java/com/mifinanzapp/movil/
  modelo/    Categoria, Movimiento, Presupuesto, Recordatorio
  negocio/   CalculadoraFinanzas, Validador, Formato, Fechas   (Java puro, sin Android)
  datos/     Sql, BaseDatos, Ejecutor, *Repositorio            (SQLite)
  ui/        MainActivity, ListaBaseActivity, Movimientos/Categorias/Presupuestos/Recordatorios,
             FormularioMovimientoActivity, FilaAdapter, Fila, Dialogos
app/src/main/res/  layouts, strings, colors, themes, ícono
app/src/test/      pruebas JUnit
```
Documentación completa en el PDF de la evidencia GA8-220501096-AA2-EV02.
