# Casos de uso

## Actores

- **Usuario**: persona que registra y consulta sus finanzas personales.
- **Sistema**: aplicación web Mi FinanzApp (incluye el proceso de alertas al iniciar sesión).

## Diagrama

```mermaid
flowchart LR
    U([Usuario])

    subgraph MiFinanzApp
        CU1[Registrarse]
        CU2[Iniciar sesión]
        CU3[Gestionar categorías]
        CU4[Gestionar ingresos]
        CU5[Gestionar gastos]
        CU6[Registrar movimiento rápido]
        CU7[Consultar historial]
        CU8[Gestionar presupuestos]
        CU9[Gestionar recordatorios]
        CU10[Ver dashboard]
        CU11[Ver reportes]
        CU12[Consultar artículos educativos]
        CU13[Calcular meta de ahorro]
        CU14[Configurar perfil]
    end

    U --> CU1
    U --> CU2
    U --> CU3
    U --> CU4
    U --> CU5
    U --> CU6
    U --> CU7
    U --> CU8
    U --> CU9
    U --> CU10
    U --> CU11
    U --> CU12
    U --> CU13
    U --> CU14

    CU6 -. include .-> CU4
    CU6 -. include .-> CU5
    CU8 -. "alerta al 80 % y 100 %" .-> CU10
    CU9 -. "aviso de vencidos" .-> CU10
```

## Descripción resumida

| ID | Caso de uso | Descripción | Implementado en |
|---|---|---|---|
| CU1 | Registrarse | Crea la cuenta y genera categorías predeterminadas. Rechaza emails repetidos. | `/auth/registro` |
| CU2 | Iniciar sesión | Acceso con email y contraseña (BCrypt). | `/auth/login` |
| CU3 | Gestionar categorías | Crear, editar y eliminar categorías de ingreso o gasto. | `/categorias` |
| CU4 | Gestionar ingresos | CRUD completo; implementado con JDBC explícito. | `/ingresos` |
| CU5 | Gestionar gastos | Crear, editar y eliminar gastos. | `/gastos` |
| CU6 | Registrar movimiento rápido | Formulario único para ingreso o gasto. | `/movimientos/nuevo` |
| CU7 | Consultar historial | Lista paginada con filtros por fecha, tipo, categoría y texto. | `/historial` |
| CU8 | Gestionar presupuestos | Presupuesto mensual por categoría con alertas. | `/presupuestos` |
| CU9 | Gestionar recordatorios | Recordatorios con estado «hecho» y aviso de vencidos. | `/recordatorios` |
| CU10 | Ver dashboard | Resumen del mes y alertas. | `/dashboard` |
| CU11 | Ver reportes | Gráficos de ingresos, gastos y evolución. | `/reportes` |
| CU12 | Consultar artículos educativos | Cinco artículos con consejos según los datos del usuario. | `/educacion` |
| CU13 | Calcular meta de ahorro | Calculadora de ahorro. | `/educacion` |
| CU14 | Configurar perfil | Cambiar nombre y moneda. | `/configuracion` |
