package com.mifinanzapp.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuerpo JSON para crear o actualizar un ingreso.
 *
 * @since 1.2.0
 */
public record IngresoRequest(
        @NotNull(message = "El monto es obligatorio") @Positive(message = "El monto debe ser mayor que cero") BigDecimal monto,
        @NotNull(message = "La fecha es obligatoria") LocalDate fecha,
        @Size(max = 255, message = "La descripción admite máximo 255 caracteres") String descripcion,
        @NotNull(message = "La categoría es obligatoria") Long categoriaId) {
}
