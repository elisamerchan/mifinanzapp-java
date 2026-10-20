package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.RecordatorioService;
import com.mifinanzapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de recordatorios financieros.
 * <p>
 * Permite crear, marcar como hechos y eliminar recordatorios, mostrando
 * los próximos eventos y el historial completo del usuario.
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
        return vista(model, usuario, new Recordatorio());
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Recordatorio recordatorio, BindingResult result, Model model,
                          Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        if (result.hasErrors()) {
            return vista(model, usuario, recordatorio);
        }
        recordatorio.setUsuario(usuario);
        recordatorioService.guardar(recordatorio);
        flash.addFlashAttribute("mensaje", "Recordatorio guardado");
        return "redirect:/recordatorios";
    }

    @PostMapping("/completar/{id}")
    public String completar(@PathVariable Long id, Authentication auth,
                            @RequestHeader(value = "Referer", required = false) String referer) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        recordatorioService.alternarCompletado(id, usuario.getId());
        // Se puede marcar desde el dashboard o desde esta página
        return referer != null && referer.contains("/dashboard") ? "redirect:/dashboard" : "redirect:/recordatorios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        recordatorioService.eliminar(id, usuario.getId());
        flash.addFlashAttribute("mensaje", "Recordatorio eliminado");
        return "redirect:/recordatorios";
    }

    private String vista(Model model, Usuario usuario, Recordatorio recordatorio) {
        List<Recordatorio> todos = recordatorioService.listarPorUsuario(usuario.getId());
        model.addAttribute("recordatorios", todos);
        model.addAttribute("proximos", todos.stream()
            .filter(r -> !r.isCompletado() && !r.getFecha().isBefore(LocalDate.now()))
            .limit(5)
            .toList());
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("recordatorio", recordatorio);
        return "recordatorios/index";
    }
}
