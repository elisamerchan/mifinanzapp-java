package com.mifinanzapp.service;

import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.RecordatorioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("guardar_recordatorio usuarioId={} titulo={}", recordatorio.getUsuario().getId(), recordatorio.getTitulo());
        return recordatorioRepository.save(recordatorio);
    }

    public Recordatorio buscarPorId(Long id) {
        return recordatorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recordatorio", "id", id));
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("eliminar_recordatorio id={}", id);
        recordatorioRepository.deleteById(id);
    }
}
