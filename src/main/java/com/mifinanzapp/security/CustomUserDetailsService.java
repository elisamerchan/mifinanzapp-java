package com.mifinanzapp.security;

import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado de autenticación de Spring Security.
 * <p>
 * Implementa {@link UserDetailsService} para cargar usuarios
 * desde la base de datos usando el email como nombre de usuario.
 * Las contraseñas se verifican contra el hash BCrypt almacenado.
 *
 * @since 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su email para la autenticación.
     *
     * @param email el email del usuario
     * @return los detalles del usuario para Spring Security
     * @throws UsernameNotFoundException si no se encuentra el email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList()
        );
    }
}
