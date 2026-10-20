package com.mifinanzapp.controller;

import com.mifinanzapp.dto.UsuarioRegistroDTO;
import com.mifinanzapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador de autenticación de usuarios.
 * <p>
 * Maneja el registro de nuevos usuarios y el inicio de sesión.
 * Las rutas de este controlador son públicas (accesibles sin autenticación).
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    private final boolean demoHabilitado;

    public AuthController(UsuarioService usuarioService, @Value("${app.demo.habilitado:false}") boolean demoHabilitado) {
        this.usuarioService = usuarioService;
        this.demoHabilitado = demoHabilitado;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("demoHabilitado", demoHabilitado);
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO dto,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/registro";
        }

        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "auth/registro";
        }

        try {
            usuarioService.registrar(dto);
            return "redirect:/auth/login?registro=exitoso";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/registro";
        }
    }
}
