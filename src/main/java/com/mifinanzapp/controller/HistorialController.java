package com.mifinanzapp.controller;

import com.mifinanzapp.entity.*;
import com.mifinanzapp.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Controlador para el historial de movimientos.
 * <p>
 * Muestra los ingresos y gastos del usuario en una vista unificada y paginada,
 * con filtros opcionales por rango de fechas, categoría, tipo y texto de la descripción.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/historial")
public class HistorialController {

    static final int TAMANO_PAGINA = 20;

    private final IngresoService ingresoService;
    private final GastoService gastoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public HistorialController(IngresoService ingresoService, GastoService gastoService,
                                CategoriaService categoriaService, UsuarioService usuarioService) {
        this.ingresoService = ingresoService;
        this.gastoService = gastoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String historial(Model model, Authentication auth,
                            @RequestParam(required = false) String desde,
                            @RequestParam(required = false) String hasta,
                            @RequestParam(required = false) Long categoriaId,
                            @RequestParam(required = false) String tipo,
                            @RequestParam(required = false) String q,
                            @RequestParam(defaultValue = "0") int pagina) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        Long uid = usuario.getId();

        LocalDate inicio = parsearFecha(desde, LocalDate.now().minusMonths(3));
        LocalDate fin = parsearFecha(hasta, LocalDate.now());
        String texto = q == null ? "" : q.trim().toLowerCase();
        boolean verGastos = !"INGRESO".equals(tipo);
        boolean verIngresos = !"GASTO".equals(tipo);

        List<Map<String, Object>> movimientos = new ArrayList<>();
        if (verGastos) {
            for (Gasto g : gastoService.listarEntre(uid, inicio, fin)) {
                if (coincide(g.getCategoria(), g.getDescripcion(), categoriaId, texto)) {
                    movimientos.add(movimiento("GASTO", g.getId(), g.getFecha(), g.getCategoria(), g.getDescripcion(), g.getMonto()));
                }
            }
        }
        if (verIngresos) {
            for (Ingreso i : ingresoService.listarEntre(uid, inicio, fin)) {
                if (coincide(i.getCategoria(), i.getDescripcion(), categoriaId, texto)) {
                    movimientos.add(movimiento("INGRESO", i.getId(), i.getFecha(), i.getCategoria(), i.getDescripcion(), i.getMonto()));
                }
            }
        }
        movimientos.sort((a, b) -> ((LocalDate) b.get("fecha")).compareTo((LocalDate) a.get("fecha")));

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;
        for (Map<String, Object> m : movimientos) {
            if ("INGRESO".equals(m.get("tipo"))) {
                totalIngresos = totalIngresos.add((BigDecimal) m.get("monto"));
            } else {
                totalGastos = totalGastos.add((BigDecimal) m.get("monto"));
            }
        }

        int totalPaginas = Math.max(1, (movimientos.size() + TAMANO_PAGINA - 1) / TAMANO_PAGINA);
        int paginaActual = Math.min(Math.max(pagina, 0), totalPaginas - 1);
        int desdeIdx = paginaActual * TAMANO_PAGINA;
        List<Map<String, Object>> visibles = movimientos.subList(desdeIdx, Math.min(desdeIdx + TAMANO_PAGINA, movimientos.size()));

        model.addAttribute("movimientos", visibles);
        model.addAttribute("pagina", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("categorias", categoriaService.listarTodas(uid));
        model.addAttribute("desde", inicio);
        model.addAttribute("hasta", fin);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("tipo", tipo);
        model.addAttribute("q", q);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("balance", totalIngresos.subtract(totalGastos));
        return "historial/index";
    }

    private static LocalDate parsearFecha(String valor, LocalDate porDefecto) {
        if (valor == null || valor.isBlank()) return porDefecto;
        try {
            return LocalDate.parse(valor);
        } catch (java.time.format.DateTimeParseException e) {
            return porDefecto;
        }
    }

    private static boolean coincide(Categoria categoria, String descripcion, Long categoriaId, String texto) {
        if (categoriaId != null && !categoria.getId().equals(categoriaId)) return false;
        return texto.isEmpty() || (descripcion != null && descripcion.toLowerCase().contains(texto));
    }

    private static Map<String, Object> movimiento(String tipo, Long id, LocalDate fecha, Categoria categoria,
                                                  String descripcion, BigDecimal monto) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("tipo", tipo);
        m.put("fecha", fecha);
        m.put("categoria", categoria.getNombre());
        m.put("descripcion", descripcion);
        m.put("monto", monto);
        return m;
    }
}
