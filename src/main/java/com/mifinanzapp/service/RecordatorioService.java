package com.mifinanzapp.service;

import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.RecordatorioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de negocio para la gestión de recordatorios.
 * <p>
 * Proporciona operaciones CRUD para recordatorios financieros
 * ordenados por fecha ascendente.
 *
 * @since 1.0.0
 */
@Service
public class RecordatorioService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioService.class);

    private final RecordatorioRepository recordatorioRepository;

    public RecordatorioService(RecordatorioRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public List<Recordatorio> listarPorUsuario(Long usuarioId) {
        return recordatorioRepository.findByUsuarioIdOrderByFechaAsc(usuarioId);
    }

    @Transactional
    public Recordatorio guardar(Recordatorio recordatorio) {
        if (recordatorio.getId() != null) {
            buscarPorId(recordatorio.getId(), recordatorio.getUsuario().getId()); // impide sobrescribir recordatorios ajenos
        }
        log.info("guardar_recordatorio usuarioId={} titulo={}", recordatorio.getUsuario().getId(), recordatorio.getTitulo());
        return recordatorioRepository.save(recordatorio);
    }

    public Recordatorio buscarPorId(Long id, Long usuarioId) {
        return recordatorioRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Recordatorio", "id", id));
    }

    /** Alterna el estado completado/pendiente de un recordatorio del usuario. */
    @Transactional
    public void alternarCompletado(Long id, Long usuarioId) {
        Recordatorio recordatorio = buscarPorId(id, usuarioId);
        recordatorio.setCompletado(!recordatorio.isCompletado());
        log.info("alternar_recordatorio id={} completado={}", id, recordatorio.isCompletado());
    }

    /** Recordatorios pendientes vencidos o que vencen en los próximos {@code dias} días. */
    public List<Recordatorio> pendientesHasta(Long usuarioId, int dias) {
        LocalDate limite = LocalDate.now().plusDays(dias);
        return recordatorioRepository.findByUsuarioIdOrderByFechaAsc(usuarioId).stream()
                .filter(r -> !r.isCompletado() && !r.getFecha().isAfter(limite))
                .toList();
    }

    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        log.info("eliminar_recordatorio id={} usuarioId={}", id, usuarioId);
        recordatorioRepository.delete(buscarPorId(id, usuarioId));
    }
}
