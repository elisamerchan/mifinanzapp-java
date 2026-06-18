package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.IngresoService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la gestión de ingresos.
 * <p>
 * Permite listar, crear, editar y eliminar ingresos,
 * filtrando por categorías de tipo INGRESO.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public IngresoController(IngresoService ingresoService, CategoriaService categoriaService, UsuarioService usuarioService) {
        this.ingresoService = ingresoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("ingresos", ingresoService.listarPorUsuario(usuario.getId()));
        model.addAttribute("ingreso", new Ingreso());
        model.addAttribute("categorias", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.INGRESO));
        return "ingresos/index";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Ingreso ingreso, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        ingreso.setUsuario(usuario);
        ingresoService.guardar(ingreso);
        return "redirect:/ingresos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("ingreso", ingresoService.buscarPorId(id));
        model.addAttribute("ingresos", ingresoService.listarPorUsuario(usuario.getId()));
        model.addAttribute("categorias", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.INGRESO));
        return "ingresos/index";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        ingresoService.eliminar(id);
        return "redirect:/ingresos";
    }
}
