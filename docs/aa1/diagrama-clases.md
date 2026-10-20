# Diagrama de clases — modelo de dominio

Entidades JPA de `com.mifinanzapp.entity`. Todas las entidades de movimientos pertenecen a un `Usuario`
(multi-tenancy por usuario).

```mermaid
classDiagram
    class Usuario {
        +Long id
        +String nombre
        +String email
        +String password
        +String moneda
        +String idioma
        +LocalDateTime fechaRegistro
    }

    class Categoria {
        +Long id
        +String nombre
        +TipoCategoria tipo
        +String icono
    }

    class TipoCategoria {
        <<enumeration>>
        INGRESO
        GASTO
    }

    class Ingreso {
        +Long id
        +BigDecimal monto
        +LocalDate fecha
        +String descripcion
    }

    class Gasto {
        +Long id
        +BigDecimal monto
        +LocalDate fecha
        +String descripcion
        +MetodoPago metodoPago
    }

    class MetodoPago {
        <<enumeration>>
    }

    class Presupuesto {
        +Long id
        +Integer mes
        +Integer anio
        +BigDecimal montoLimite
    }

    class Recordatorio {
        +Long id
        +String titulo
        +String descripcion
        +LocalDate fecha
        +boolean completado
    }

    Usuario "1" --> "0..*" Categoria : posee
    Usuario "1" --> "0..*" Ingreso : registra
    Usuario "1" --> "0..*" Gasto : registra
    Usuario "1" --> "0..*" Presupuesto : define
    Usuario "1" --> "0..*" Recordatorio : crea
    Categoria "1" --> "0..*" Ingreso : clasifica
    Categoria "1" --> "0..*" Gasto : clasifica
    Categoria "1" --> "0..*" Presupuesto : limita
    Categoria --> TipoCategoria
    Gasto --> MetodoPago
```

## Reglas de negocio reflejadas en el modelo

- Una categoría es de tipo `INGRESO` o `GASTO`; al guardar un movimiento se valida que la categoría sea del usuario y del tipo correcto.
- No puede haber dos presupuestos con la misma combinación categoría + mes + año.
- Los importes son `BigDecimal` para evitar errores de redondeo con dinero.
- Cada consulta filtra por `usuario_id`; un registro ajeno se trata como inexistente (404).
