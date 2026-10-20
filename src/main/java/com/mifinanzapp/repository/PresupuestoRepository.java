package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Presupuesto}.
 * <p>
 * Proporciona consultas para obtener presupuestos por usuario,
 * categoría, mes y año.
 *
 * @since 1.0.0
 */
@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    List<Presupuesto> findByUsuarioId(Long usuarioId);
    Optional<Presupuesto> findByIdAndUsuarioId(Long id, Long usuarioId);
    Optional<Presupuesto> findByUsuarioIdAndCategoriaIdAndMesAndAnio(Long usuarioId, Long categoriaId, Integer mes, Integer anio);
    List<Presupuesto> findByUsuarioIdAndMesAndAnio(Long usuarioId, Integer mes, Integer anio);
}
