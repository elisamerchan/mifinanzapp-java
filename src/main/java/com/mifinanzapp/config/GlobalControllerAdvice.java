package com.mifinanzapp.config;

import com.mifinanzapp.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Consejo global para todos los controladores MVC.
 * <p>
 * Provee atributos comunes a todas las vistas Thymeleaf,
 * como la URI actual para resaltar la navegación activa en el sidebar,
 * y los datos del usuario autenticado (nombre, email).
 *
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    private final UsuarioService usuarioService;

    public GlobalControllerAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Expone la URI de la petición actual como atributo del modelo.
     * <p>
     * Utilizado por las plantillas Thymeleaf para resaltar la
     * opción activa en el menú de navegación lateral.
     *
     * @param request la petición HTTP actual
     * @return la URI de la petición
     */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Expone el nombre del usuario autenticado como atributo del modelo.
     *
     * @param auth la autenticación actual
     * @return el nombre del usuario, o {@code null} si no hay sesión
     */
    @ModelAttribute("nombre")
    public String globalNombre(Authentication auth) {
        if (auth == null || auth.getName() == null) return null;
        try {
            return usuarioService.buscarPorEmail(auth.getName()).getNombre();
        } catch (Exception e) {
            log.warn("No se pudo obtener nombre para {}", auth.getName());
            return null;
        }
    }

    /**
     * Expone el email del usuario autenticado como atributo del modelo.
     *
     * @param auth la autenticación actual
     * @return el email del usuario, o {@code null} si no hay sesión
     */
    @ModelAttribute("email")
    public String globalEmail(Authentication auth) {
        if (auth == null) return null;
        return auth.getName();
    }
}
