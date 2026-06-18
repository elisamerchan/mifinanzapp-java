package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * <p>
 * Proporciona métodos para buscar usuarios por email
 * y verificar la existencia de un email.
 *
 * @since 1.0.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
