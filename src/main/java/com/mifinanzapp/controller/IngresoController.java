package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import com.mifinanzapp.service.IngresoService;
import com.mifinanzapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de ingresos.
 * <p>
 * Permite listar (paginado), crear, editar y eliminar ingresos,
 * filtrando por categorías de tipo INGRESO. Si el formulario no es válido
 * se vuelve a mostrar con los errores y los datos escritos.
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
    public String listar(@RequestParam(defaultValue = "0") int pagina, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return vista(model, usuario, new Ingreso(), pagina);
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Ingreso ingreso, BindingResult result, Model model,
                          Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        if (result.hasErrors()) {
            return vista(model, usuario, ingreso, 0);
        }
        boolean nuevo = ingreso.getId() == null;
        ingreso.setUsuario(usuario);
        ingresoService.guardar(ingreso);
        flash.addFlashAttribute("mensaje", nuevo ? "Ingreso registrado" : "Ingreso actualizado");
        return "redirect:/ingresos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return vista(model, usuario, ingresoService.buscarPorId(id, usuario.getId()), 0);
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        ingresoService.eliminar(id, usuario.getId());
        flash.addFlashAttribute("mensaje", "Ingreso eliminado");
        return "redirect:/ingresos";
    }

    private String vista(Model model, Usuario usuario, Ingreso ingreso, int pagina) {
        Page<Ingreso> ingresos = ingresoService.listarPagina(usuario.getId(), pagina);
        model.addAttribute("ingreso", ingreso);
        model.addAttribute("ingresos", ingresos.getContent());
        model.addAttribute("pagina", ingresos.getNumber());
        model.addAttribute("totalPaginas", ingresos.getTotalPages());
        model.addAttribute("categorias", categoriaService.listarPorTipo(usuario.getId(), Categoria.TipoCategoria.INGRESO));
        return "ingresos/index";
    }
}
