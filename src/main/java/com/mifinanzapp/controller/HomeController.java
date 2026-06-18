package com.mifinanzapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de la página de inicio.
 * <p>
 * Redirige la raíz de la aplicación a la página de inicio de sesión.
 * Las rutas autenticadas como /dashboard son manejadas por
 * {@link DashboardController}.
 *
 * @since 1.0.0
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}
