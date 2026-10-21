package com.mifinanzapp.api;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Respuesta paginada de los listados.
 *
 * @since 1.2.0
 */
public record PaginaResponse<T>(List<T> contenido, int pagina, int totalPaginas, long totalElementos) {

    public static <T> PaginaResponse<T> de(Page<?> page, List<T> contenido) {
        return new PaginaResponse<>(contenido, page.getNumber(), page.getTotalPages(), page.getTotalElements());
    }
}
