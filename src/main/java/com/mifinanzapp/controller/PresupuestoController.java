package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.PresupuestoService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("presupuestos", presupuestoService.listarConProgreso(usuario.getId()));
        model.addAttribute("presupuesto", new Presupuesto());
        model.addAttribute("categoriasGasto", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("meses", MESES);
        return "presupuestos/index";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Presupuesto presupuesto, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        presupuesto.setUsuario(usuario);
        presupuestoService.guardar(presupuesto);
        return "redirect:/presupuestos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("presupuesto", presupuestoService.buscarPorId(id));
        model.addAttribute("presupuestos", presupuestoService.listarConProgreso(usuario.getId()));
        model.addAttribute("categoriasGasto", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("meses", MESES);
        return "presupuestos/index";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        presupuestoService.eliminar(id);
        return "redirect:/presupuestos";
    }
}
