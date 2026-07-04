package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.IngresoRepository;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.GastoService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;

    public GastoController(GastoService gastoService, CategoriaService categoriaService,
                           UsuarioService usuarioService, IngresoRepository ingresoRepository,
                           GastoRepository gastoRepository) {
        this.gastoService = gastoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.ingresoRepository = ingresoRepository;
        this.gastoRepository = gastoRepository;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        BigDecimal balance = ingresoRepository.sumByUsuarioId(usuario.getId())
                .subtract(gastoRepository.sumByUsuarioId(usuario.getId()));
        model.addAttribute("balance", balance);
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
