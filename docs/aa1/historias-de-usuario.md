# Historias de usuario

Formato: **Como** [actor] **quiero** [acción] **para** [beneficio], seguido de criterios de aceptación.

## HU-01 · Registro de usuario
**Como** persona nueva **quiero** crear una cuenta **para** empezar a registrar mis finanzas.

- [ ] El formulario pide nombre, email y contraseña.
- [ ] Si el email ya existe, se muestra un error y no se crea la cuenta (HTTP 409).
- [ ] La contraseña se guarda cifrada con BCrypt.
- [ ] Al registrarse se crean categorías predeterminadas.

## HU-02 · Inicio de sesión
**Como** usuario registrado **quiero** iniciar sesión **para** ver mis datos.

- [ ] Con credenciales válidas se llega al dashboard.
- [ ] Con credenciales inválidas se muestra un mensaje sin revelar cuál dato es incorrecto.
- [ ] Las páginas privadas redirigen al login si no hay sesión.

## HU-03 · Gestión de categorías
**Como** usuario **quiero** crear, editar y eliminar categorías **para** organizar mis movimientos.

- [ ] Cada categoría es de tipo INGRESO o GASTO.
- [ ] Los formularios de movimientos solo muestran categorías del tipo correspondiente.
- [ ] Editar o borrar una categoría ajena responde 404.

## HU-04 · Registrar un ingreso
**Como** usuario **quiero** registrar un ingreso **para** saber cuánto recibo.

- [ ] Campos obligatorios: monto (mayor que cero), fecha y categoría.
- [ ] Si hay errores de validación, el formulario conserva lo escrito y muestra el error junto al campo.
- [ ] Tras guardar se muestra un mensaje de confirmación y el ingreso aparece en el listado.
- [ ] El dashboard se actualiza.

## HU-05 · Editar y eliminar un ingreso
**Como** usuario **quiero** corregir o borrar un ingreso **para** mantener mis datos exactos.

- [ ] La edición carga el formulario con los datos actuales.
- [ ] El borrado pide confirmación.
- [ ] Un ingreso de otro usuario no se puede editar ni borrar (404).

## HU-06 · Registrar y gestionar gastos
**Como** usuario **quiero** registrar, editar y eliminar gastos **para** controlar en qué gasto.

- [ ] Un gasto incluye monto, fecha, categoría de tipo GASTO y método de pago.
- [ ] Los mismos criterios de validación y propiedad que HU-04 y HU-05.

## HU-07 · Historial con filtros
**Como** usuario **quiero** buscar movimientos **para** revisar periodos o categorías concretas.

- [ ] Filtros por fecha, tipo, categoría y texto.
- [ ] Resultados paginados de 20 en 20.

## HU-08 · Presupuesto mensual
**Como** usuario **quiero** fijar un límite mensual por categoría **para** no gastar de más.

- [ ] No se permite un segundo presupuesto para la misma categoría en el mismo mes y año.
- [ ] Al llegar al 80 % del límite aparece una alerta en el inicio.
- [ ] Al llegar al 100 % la alerta es de límite superado.

## HU-09 · Recordatorios
**Como** usuario **quiero** recordatorios de pagos **para** no olvidar fechas importantes.

- [ ] Un recordatorio tiene título, fecha y estado «hecho».
- [ ] Los recordatorios pendientes vencidos aparecen como aviso en el inicio.

## HU-10 · Dashboard y reportes
**Como** usuario **quiero** ver un resumen y gráficos **para** entender mi situación financiera.

- [ ] El dashboard muestra un resumen financiero del mes (ingresos, gastos y alertas).
- [ ] Los reportes muestran gráficos con Chart.js.

## HU-11 · Contenido educativo
**Como** usuario **quiero** leer artículos y usar una calculadora de ahorro **para** mejorar mis hábitos.

- [ ] Existen cinco artículos accesibles desde el menú Aprender.
- [ ] La calculadora de meta de ahorro devuelve el importe mensual necesario.
