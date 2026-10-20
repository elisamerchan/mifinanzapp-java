package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de categorías de ingresos y gastos.
 * <p>
 * Permite listar, crear, editar y eliminar categorías.
 * Cada categoría está asociada a un usuario y tiene un tipo (INGRESO/GASTO).
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public CategoriaController(CategoriaService categoriaService, UsuarioService usuarioService) {
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    private static final java.util.List<String> ICONOS = java.util.List.of(
        "🍔", "🚗", "💊", "📚", "💡", "🎬", "💰", "💻", "📦", "🎁",
        "🏠", "👕", "✈️", "🐱", "🎮", "🏋️", "☕", "🍕", "🎵", "📱",
        "💼", "🏦", "📈", "🛒", "🔧", "💄", "🍷", "🎂", "🏥", "🎓"
    );

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("categorias", categoriaService.listarTodas(usuario.getId()));
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("iconos", ICONOS);
        return "categorias/index";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        categoria.setUsuario(usuario);
        categoriaService.guardar(categoria);
        flash.addFlashAttribute("mensaje", "Categoría guardada");
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("categorias", categoriaService.listarTodas(usuario.getId()));
        model.addAttribute("categoria", categoriaService.buscarPorId(id, usuario.getId()));
        model.addAttribute("iconos", ICONOS);
        return "categorias/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        categoriaService.eliminar(id, usuario.getId());
        flash.addFlashAttribute("mensaje", "Categoría eliminada");
        return "redirect:/categorias";
    }
}
