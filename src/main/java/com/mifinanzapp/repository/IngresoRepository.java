package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Ingreso}.
 * <p>
 * Proporciona métodos de consulta para ingresos por usuario,
 * fechas y estadísticas agregadas para el dashboard y reportes.
 *
 * @since 1.0.0
 */
@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByUsuarioOrderByFechaDesc(Usuario usuario);
    List<Ingreso> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @EntityGraph(attributePaths = "categoria")
    List<Ingreso> findByUsuarioOrderByFechaDesc(Usuario usuario, Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    List<Ingreso> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Ingreso i WHERE i.usuario.id = :usuarioId AND i.fecha BETWEEN :inicio AND :fin")
    long countByUsuarioAndFechaBetween(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.usuario.id = :usuarioId AND i.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumByUsuarioAndFechaBetween(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.usuario.id = :usuarioId")
    BigDecimal sumByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Ingreso> findByUsuarioAndFechaBetweenOrderByFechaDesc(Usuario usuario, LocalDate inicio, LocalDate fin);

    @Query(value = "SELECT EXTRACT(MONTH FROM i.fecha), EXTRACT(YEAR FROM i.fecha), COALESCE(SUM(i.monto), 0) FROM ingresos i WHERE i.usuario_id = :usuarioId AND i.fecha BETWEEN :inicio AND :fin GROUP BY EXTRACT(YEAR FROM i.fecha), EXTRACT(MONTH FROM i.fecha) ORDER BY EXTRACT(YEAR FROM i.fecha), EXTRACT(MONTH FROM i.fecha)", nativeQuery = true)
    List<Object[]> monthlyEvolution(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
