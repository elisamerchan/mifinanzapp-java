package com.mifinanzapp.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Cuerpo JSON para crear o actualizar un recordatorio.
 *
 * @since 1.2.0
 */
public record RecordatorioRequest(
        @NotBlank(message = "El título es obligatorio") @Size(max = 200, message = "El título admite máximo 200 caracteres") String titulo,
        @Size(max = 500, message = "La descripción admite máximo 500 caracteres") String descripcion,
        @NotNull(message = "La fecha es obligatoria") LocalDate fecha) {
}
