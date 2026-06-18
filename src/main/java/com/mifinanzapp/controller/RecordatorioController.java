package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.RecordatorioService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de recordatorios financieros.
 * <p>
 * Permite crear y eliminar recordatorios, mostrando los próximos
 * eventos y el historial completo de recordatorios del usuario.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/recordatorios")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;
    private final UsuarioService usuarioService;

    public RecordatorioController(RecordatorioService recordatorioService, UsuarioService usuarioService) {
        this.recordatorioService = recordatorioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        List<Recordatorio> todos = recordatorioService.listarPorUsuario(usuario.getId());
        model.addAttribute("recordatorios", todos);
        model.addAttribute("proximos", todos.stream()
            .filter(r -> !r.getFecha().isBefore(java.time.LocalDate.now()))
            .limit(5)
            .toList());
        model.addAttribute("recordatorio", new Recordatorio());
        return "recordatorios/index";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Recordatorio recordatorio, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        recordatorio.setUsuario(usuario);
        recordatorioService.guardar(recordatorio);
        return "redirect:/recordatorios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        recordatorioService.eliminar(id);
        return "redirect:/recordatorios";
    }
}
