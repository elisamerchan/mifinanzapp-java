package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Controlador para la configuración del perfil de usuario.
 * <p>
 * Permite al usuario cambiar su nombre, moneda preferida
 * e idioma de la aplicación.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final UsuarioService usuarioService;

    private static final Map<String, String> MONEDAS = Map.of(
        "USD", "$ USD",
        "EUR", "€ EUR",
        "COP", "$ COP",
        "MXN", "$ MXN",
        "ARS", "$ ARS",
        "PEN", "S/ PEN"
    );

    private static final Map<String, String> IDIOMAS = Map.of(
        "es", "Español",
        "en", "English"
    );

    public ConfiguracionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String configuracion(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("usuario", usuario);
        model.addAttribute("monedas", MONEDAS);
        model.addAttribute("idiomas", IDIOMAS);
        return "configuracion/index";
    }

    @PostMapping("/guardar")
    public String guardar(Authentication auth,
                           @RequestParam String nombre,
                           @RequestParam String moneda,
                           @RequestParam String idioma) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        usuario.setNombre(nombre);
        usuario.setMoneda(moneda);
        usuario.setIdioma(idioma);
        usuarioService.actualizar(usuario);
        return "redirect:/configuracion?guardado";
    }
}