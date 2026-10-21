package com.mifinanzapp.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Cuerpo JSON para crear o actualizar un presupuesto mensual por categoría de gasto.
 *
 * @since 1.2.0
 */
public record PresupuestoRequest(
        @NotNull(message = "La categoría es obligatoria") Long categoriaId,
        @NotNull(message = "El mes es obligatorio") @Min(value = 1, message = "El mes debe estar entre 1 y 12") @Max(value = 12, message = "El mes debe estar entre 1 y 12") Integer mes,
        @NotNull(message = "El año es obligatorio") @Min(value = 2000, message = "Año no válido") @Max(value = 2100, message = "Año no válido") Integer anio,
        @NotNull(message = "El monto límite es obligatorio") @Positive(message = "El monto límite debe ser mayor que cero") BigDecimal montoLimite) {
}
