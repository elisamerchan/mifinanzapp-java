package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.IngresoRepository;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.GastoService;
import com.mifinanzapp.service.IngresoService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controlador para el registro rápido de movimientos.
 * <p>
 * Permite registrar ingresos y gastos desde una sola página,
 * seleccionando el tipo de movimiento y la categoría correspondiente.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final IngresoService ingresoService;
    private final GastoService gastoService;
    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;

    public MovimientoController(CategoriaService categoriaService, UsuarioService usuarioService,
                                 IngresoService ingresoService, GastoService gastoService,
                                 IngresoRepository ingresoRepository, GastoRepository gastoRepository) {
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.ingresoService = ingresoService;
        this.gastoService = gastoService;
        this.ingresoRepository = ingresoRepository;
        this.gastoRepository = gastoRepository;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        BigDecimal balance = ingresoRepository.sumByUsuarioId(usuario.getId())
                .subtract(gastoRepository.sumByUsuarioId(usuario.getId()));
        model.addAttribute("balance", balance);
        model.addAttribute("ingreso", new Ingreso());
        model.addAttribute("gasto", new Gasto());
        model.addAttribute("categoriasIngreso", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.INGRESO));
        model.addAttribute("categoriasGasto", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("metodosPago", Gasto.MetodoPago.values());
        return "movimientos/nuevo";
    }

    @PostMapping("/guardar-ingreso")
    public String guardarIngreso(@ModelAttribute Ingreso ingreso, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        ingreso.setUsuario(usuario);
        ingresoService.guardar(ingreso);
        return "redirect:/dashboard";
    }

    @PostMapping("/guardar-gasto")
    public String guardarGasto(@ModelAttribute Gasto gasto, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        gasto.setUsuario(usuario);
        gastoService.guardar(gasto);
        return "redirect:/dashboard";
    }
}
