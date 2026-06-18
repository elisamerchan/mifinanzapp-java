package com.mifinanzapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para la sección de educación financiera.
 * <p>
 * Muestra contenido educativo y consejos sobre finanzas personales.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/educacion")
public class EducacionController {

    @GetMapping
    public String educacion() {
        return "educacion/index";
    }
}
