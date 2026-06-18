package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.GastoService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la gestión de gastos.
 * <p>
 * Permite listar, crear, editar y eliminar gastos,
 * filtrando por categorías de tipo GASTO.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/gastos")
public class GastoController {

    private final GastoService gastoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public GastoController(GastoService gastoService, CategoriaService categoriaService, UsuarioService usuarioService) {
        this.gastoService = gastoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("gastos", gastoService.listarPorUsuario(usuario.getId()));
        model.addAttribute("gasto", new Gasto());
        model.addAttribute("categorias", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("metodosPago", Gasto.MetodoPago.values());
        return "gastos/index";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Gasto gasto, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        gasto.setUsuario(usuario);
        gastoService.guardar(gasto);
        return "redirect:/gastos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("gasto", gastoService.buscarPorId(id));
        model.addAttribute("gastos", gastoService.listarPorUsuario(usuario.getId()));
        model.addAttribute("categorias", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("metodosPago", Gasto.MetodoPago.values());
        return "gastos/index";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        gastoService.eliminar(id);
        return "redirect:/gastos";
    }
}
