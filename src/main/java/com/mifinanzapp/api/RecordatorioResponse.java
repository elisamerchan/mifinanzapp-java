package com.mifinanzapp.api;

import com.mifinanzapp.entity.Recordatorio;

import java.time.LocalDate;

/**
 * Representación JSON de un recordatorio.
 *
 * @since 1.2.0
 */
public record RecordatorioResponse(Long id, String titulo, String descripcion, LocalDate fecha, boolean completado) {

    public static RecordatorioResponse de(Recordatorio r) {
        return new RecordatorioResponse(r.getId(), r.getTitulo(), r.getDescripcion(), r.getFecha(), r.isCompletado());
    }
}
