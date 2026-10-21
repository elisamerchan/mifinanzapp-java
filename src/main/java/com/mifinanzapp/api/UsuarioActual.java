package com.mifinanzapp.api;

import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Obtiene el usuario dueño de la petición a partir de las credenciales HTTP Basic.
 *
 * @since 1.2.0
 */
@Component
public class UsuarioActual {

    private final UsuarioService usuarioService;

    public UsuarioActual(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario de(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
