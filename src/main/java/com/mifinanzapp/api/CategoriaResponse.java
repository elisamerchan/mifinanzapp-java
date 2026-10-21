package com.mifinanzapp.api;

import com.mifinanzapp.entity.Categoria;

/**
 * Representación JSON de una categoría.
 *
 * @since 1.2.0
 */
public record CategoriaResponse(Long id, String nombre, Categoria.TipoCategoria tipo, String icono) {

    public static CategoriaResponse de(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getTipo(), c.getIcono());
    }
}
