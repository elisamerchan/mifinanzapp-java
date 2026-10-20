package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.PresupuestoService;
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
 * Controlador para la gestión de presupuestos mensuales.
 * <p>
 * Permite definir, editar y eliminar presupuestos por categoría
 * para cada mes, visualizando el progreso de gasto contra el límite.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    private static final List<String> MESES = List.of(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    );

    public PresupuestoController(PresupuestoService presupuestoService, CategoriaService categoriaService, UsuarioService usuarioService) {
        this.presupuestoService = presupuestoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        Presupuesto nuevo = new Presupuesto();
        nuevo.setMes(LocalDate.now().getMonthValue());
        nuevo.setAnio(LocalDate.now().getYear());
        return vista(model, usuario, nuevo);
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Presupuesto presupuesto, BindingResult result, Model model,
                          Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        if (result.hasErrors()) {
            return vista(model, usuario, presupuesto);
        }
        boolean nuevo = presupuesto.getId() == null;
        presupuesto.setUsuario(usuario);
        presupuestoService.guardar(presupuesto);
        flash.addFlashAttribute("mensaje", nuevo ? "Presupuesto creado" : "Presupuesto actualizado");
        return "redirect:/presupuestos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return vista(model, usuario, presupuestoService.buscarPorId(id, usuario.getId()));
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        presupuestoService.eliminar(id, usuario.getId());
        flash.addFlashAttribute("mensaje", "Presupuesto eliminado");
        return "redirect:/presupuestos";
    }

    private String vista(Model model, Usuario usuario, Presupuesto presupuesto) {
        model.addAttribute("presupuesto", presupuesto);
        model.addAttribute("presupuestos", presupuestoService.listarConProgreso(usuario.getId()));
        model.addAttribute("categoriasGasto", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("meses", MESES);
        return "presupuestos/index";
    }
}
