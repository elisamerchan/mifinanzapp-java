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
 * Muestra todos los ingresos y gastos del usuario en una vista
 * unificada, con filtros opcionales por rango de fechas y categoría.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/historial")
public class HistorialController {

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
                            @RequestParam(required = false) Long categoriaId) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        List<Map<String, Object>> movimientos = new ArrayList<>();

        LocalDate inicio = desde != null && !desde.isEmpty() ? LocalDate.parse(desde) : LocalDate.now().minusMonths(3);
        LocalDate fin = hasta != null && !hasta.isEmpty() ? LocalDate.parse(hasta) : LocalDate.now();

        List<Gasto> gastos = gastoService.listarPorUsuario(usuario.getId());
        List<Ingreso> ingresos = ingresoService.listarPorUsuario(usuario.getId());

        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");

        for (Gasto g : gastos) {
            if (categoriaId != null && !g.getCategoria().getId().equals(categoriaId)) continue;
            if (g.getFecha().isBefore(inicio) || g.getFecha().isAfter(fin)) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("fecha", g.getFecha());
            m.put("tipo", "GASTO");
            m.put("categoria", g.getCategoria().getNombre());
            m.put("descripcion", g.getDescripcion());
            m.put("monto", g.getMonto());
            m.put("montoStr", "-$" + df.format(g.getMonto()));
            m.put("id", g.getId());
            movimientos.add(m);
        }

        for (Ingreso i : ingresos) {
            if (categoriaId != null && !i.getCategoria().getId().equals(categoriaId)) continue;
            if (i.getFecha().isBefore(inicio) || i.getFecha().isAfter(fin)) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("fecha", i.getFecha());
            m.put("tipo", "INGRESO");
            m.put("categoria", i.getCategoria().getNombre());
            m.put("descripcion", i.getDescripcion());
            m.put("monto", i.getMonto());
            m.put("montoStr", "+$" + df.format(i.getMonto()));
            m.put("id", i.getId());
            movimientos.add(m);
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
        BigDecimal balance = totalIngresos.subtract(totalGastos);

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("categorias", categoriaService.listarTodas(usuario.getId()));
        model.addAttribute("desde", inicio);
        model.addAttribute("hasta", fin);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("balance", balance);
        model.addAttribute("totalIngresosStr", "+$" + df.format(totalIngresos));
        model.addAttribute("totalGastosStr", "-$" + df.format(totalGastos));
        model.addAttribute("balanceStr", "$" + df.format(balance));
        return "historial/index";
    }
}
