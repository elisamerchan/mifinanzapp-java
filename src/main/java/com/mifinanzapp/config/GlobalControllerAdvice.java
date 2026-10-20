package com.mifinanzapp.config;

import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.UsuarioService;
import com.mifinanzapp.util.MonetaryUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Consejo global para todos los controladores MVC.
 * <p>
 * Provee atributos comunes a todas las vistas Thymeleaf,
 * como la URI actual para resaltar la navegación activa en el sidebar,
 * y los datos del usuario autenticado (nombre, email, símbolo de moneda).
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
     * Expone {@code nombre}, {@code email} y {@code simbolo} (moneda preferida) del usuario
     * autenticado, con una sola consulta por petición.
     *
     * @param model el modelo de la vista
     * @param auth  la autenticación actual, {@code null} si no hay sesión
     */
    @ModelAttribute
    public void usuarioActual(Model model, Authentication auth) {
        model.addAttribute("simbolo", "$");
        if (auth == null || auth instanceof AnonymousAuthenticationToken) return;
        model.addAttribute("email", auth.getName());
        try {
            Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("simbolo", MonetaryUtils.simbolo(usuario.getMoneda()));
        } catch (Exception e) {
            log.warn("No se pudo obtener el usuario {}", auth.getName());
        }
    }
}
