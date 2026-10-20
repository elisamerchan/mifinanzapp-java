package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Categoria}.
 * <p>
 * Proporciona consultas para obtener categorías por usuario,
 * tipo y categorías globales (sin usuario asociado).
 *
 * @since 1.0.0
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByUsuarioOrUsuarioIsNull(Usuario usuario);
    List<Categoria> findByUsuarioId(Long usuarioId);
    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId AND (:tipo IS NULL OR c.tipo = :tipo)")
    List<Categoria> findByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") Categoria.TipoCategoria tipo);
    List<Categoria> findByTipoAndUsuarioIsNull(Categoria.TipoCategoria tipo);
    List<Categoria> findByTipo(Categoria.TipoCategoria tipo);
}
