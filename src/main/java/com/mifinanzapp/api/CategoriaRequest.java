package com.mifinanzapp.api;

import com.mifinanzapp.entity.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo JSON para crear o actualizar una categoría.
 *
 * @since 1.2.0
 */
public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre admite máximo 100 caracteres") String nombre,
        @NotNull(message = "El tipo es obligatorio (INGRESO o GASTO)") Categoria.TipoCategoria tipo,
        @Size(max = 10, message = "El icono admite máximo 10 caracteres") String icono) {
}
