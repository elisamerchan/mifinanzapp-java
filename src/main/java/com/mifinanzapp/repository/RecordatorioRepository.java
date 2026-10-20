package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Recordatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Recordatorio}.
 * <p>
 * Proporciona consultas para obtener recordatorios por usuario
 * ordenados por fecha.
 *
 * @since 1.0.0
 */
@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {
    List<Recordatorio> findByUsuarioIdOrderByFechaAsc(Long usuarioId);
    Optional<Recordatorio> findByIdAndUsuarioId(Long id, Long usuarioId);
}
