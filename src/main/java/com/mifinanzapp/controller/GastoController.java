package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.IngresoRepository;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.GastoService;
import com.mifinanzapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Controlador para la gestión de gastos.
 * <p>
 * Permite listar (paginado), crear, editar y eliminar gastos,
 * filtrando por categorías de tipo GASTO. Si el formulario no es válido
 * se vuelve a mostrar con los errores y los datos escritos.
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
    public String listar(@RequestParam(defaultValue = "0") int pagina, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return vista(model, usuario, new Gasto(), pagina);
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Gasto gasto, BindingResult result, Model model,
                          Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        if (result.hasErrors()) {
            return vista(model, usuario, gasto, 0);
        }
        boolean nuevo = gasto.getId() == null;
        gasto.setUsuario(usuario);
        gastoService.guardar(gasto);
        flash.addFlashAttribute("mensaje", nuevo ? "Gasto registrado" : "Gasto actualizado");
        return "redirect:/gastos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return vista(model, usuario, gastoService.buscarPorId(id, usuario.getId()), 0);
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        gastoService.eliminar(id, usuario.getId());
        flash.addFlashAttribute("mensaje", "Gasto eliminado");
        return "redirect:/gastos";
    }

    private String vista(Model model, Usuario usuario, Gasto gasto, int pagina) {
        BigDecimal balance = ingresoRepository.sumByUsuarioId(usuario.getId())
                .subtract(gastoRepository.sumByUsuarioId(usuario.getId()));
        Page<Gasto> gastos = gastoService.listarPagina(usuario.getId(), pagina);
        model.addAttribute("balance", balance);
        model.addAttribute("gasto", gasto);
        model.addAttribute("gastos", gastos.getContent());
        model.addAttribute("pagina", gastos.getNumber());
        model.addAttribute("totalPaginas", gastos.getTotalPages());
        model.addAttribute("categorias", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.GASTO));
        model.addAttribute("metodosPago", Gasto.MetodoPago.values());
        return "gastos/index";
    }
}
