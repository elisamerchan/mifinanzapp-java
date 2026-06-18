package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
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
 * Repositorio JPA para la entidad {@link Gasto}.
 * <p>
 * Proporciona métodos de consulta para gastos por usuario,
 * fechas, categorías, así como consultas agregadas para
 * totales, evolución mensual y estadísticas del dashboard.
 *
 * @since 1.0.0
 */
@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByUsuarioOrderByFechaDesc(Usuario usuario);
    List<Gasto> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @EntityGraph(attributePaths = "categoria")
    List<Gasto> findByUsuarioOrderByFechaDesc(Usuario usuario, Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    List<Gasto> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.fecha BETWEEN :inicio AND :fin")
    long countByUsuarioAndFechaBetween(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumByUsuarioAndFechaBetween(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId")
    BigDecimal sumByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Gasto> findByUsuarioAndFechaBetweenOrderByFechaDesc(Usuario usuario, LocalDate inicio, LocalDate fin);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.categoria.id = :categoriaId AND g.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumByUsuarioAndCategoriaAndFechaBetween(@Param("usuarioId") Long usuarioId, @Param("categoriaId") Long categoriaId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT g.categoria, COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.fecha BETWEEN :inicio AND :fin GROUP BY g.categoria ORDER BY SUM(g.monto) DESC")
    List<Object[]> sumByCategoriaForUser(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query(value = "SELECT EXTRACT(MONTH FROM g.fecha), EXTRACT(YEAR FROM g.fecha), COALESCE(SUM(g.monto), 0) FROM gastos g WHERE g.usuario_id = :usuarioId AND g.fecha BETWEEN :inicio AND :fin GROUP BY EXTRACT(YEAR FROM g.fecha), EXTRACT(MONTH FROM g.fecha) ORDER BY EXTRACT(YEAR FROM g.fecha), EXTRACT(MONTH FROM g.fecha)", nativeQuery = true)
    List<Object[]> monthlyEvolution(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT g.fecha, COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.fecha BETWEEN :inicio AND :fin GROUP BY g.fecha ORDER BY SUM(g.monto) DESC")
    List<Object[]> sumByUsuarioAndFechaBetweenGroupByDay(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
