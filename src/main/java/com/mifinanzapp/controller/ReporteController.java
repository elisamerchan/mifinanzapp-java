package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.IngresoRepository;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para la sección de reportes y estadísticas.
 * <p>
 * Genera reportes visuales con gráficos de gastos por categoría,
 * evolución mensual, balance general e indicadores financieros
 * como gasto promedio diario y porcentaje de ahorro.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;
    private final UsuarioService usuarioService;

    private static final List<String> NOMBRES_MESES = List.of(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    );

    public ReporteController(GastoRepository gastoRepository, IngresoRepository ingresoRepository,
                              UsuarioService usuarioService) {
        this.gastoRepository = gastoRepository;
        this.ingresoRepository = ingresoRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String reportes(Model model, Authentication auth,
                            @RequestParam(required = false) String periodo) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());

        LocalDate inicio;
        LocalDate fin = LocalDate.now();
        if ("semanal".equals(periodo)) {
            inicio = fin.minusWeeks(1);
        } else if ("anual".equals(periodo)) {
            inicio = fin.minusYears(1).withDayOfMonth(1);
        } else if ("fecha".equals(periodo)) {
            inicio = fin.minusMonths(1);
        } else {
            inicio = fin.minusMonths(6).withDayOfMonth(1);
        }

        // Gastos por categoría
        List<Object[]> gastosPorCategoria = gastoRepository.sumByCategoriaForUser(usuario.getId(), inicio, fin);
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();
        for (Object[] row : gastosPorCategoria) {
            labels.add(((Categoria) row[0]).getNombre());
            data.add((BigDecimal) row[1]);
        }

        // Evolución mensual
        List<Object[]> evolucion = gastoRepository.monthlyEvolution(usuario.getId(), inicio, fin);
        List<String> meses = new ArrayList<>();
        List<BigDecimal> montos = new ArrayList<>();
        for (Object[] row : evolucion) {
            int mesNum = ((Number) row[0]).intValue();
            String nombreMes = NOMBRES_MESES.get(mesNum - 1);
            int anio = ((Number) row[1]).intValue();
            meses.add(nombreMes + " " + anio);
            montos.add((BigDecimal) row[2]);
        }

        // Balance
        BigDecimal totalIngresos = ingresoRepository.sumByUsuarioAndFechaBetween(usuario.getId(), inicio, fin);
        BigDecimal totalGastos = gastoRepository.sumByUsuarioAndFechaBetween(usuario.getId(), inicio, fin);
        BigDecimal balance = totalIngresos.subtract(totalGastos);

        // Indicadores extras
        long totalMovimientos = 0;
        BigDecimal maxGasto = BigDecimal.ZERO;
        String diaMayorGasto = "N/A";
        BigDecimal gastoPromedioDiario = BigDecimal.ZERO;
        List<Object[]> gastosDiarios = gastoRepository.sumByUsuarioAndFechaBetweenGroupByDay(usuario.getId(), inicio, fin);
        if (!gastosDiarios.isEmpty()) {
            Object[] top = gastosDiarios.get(0);
            maxGasto = (BigDecimal) top[1];
            diaMayorGasto = top[0].toString();
            totalMovimientos = gastosDiarios.stream().mapToLong(r -> 1).sum();
            long dias = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin) + 1;
            gastoPromedioDiario = dias > 0 ? totalGastos.divide(BigDecimal.valueOf(dias), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        BigDecimal porcentajeAhorro = totalIngresos.compareTo(BigDecimal.ZERO) > 0
            ? balance.multiply(BigDecimal.valueOf(100)).divide(totalIngresos, 1, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        model.addAttribute("labels", labels);
        model.addAttribute("data", data);
        model.addAttribute("meses", meses);
        model.addAttribute("montos", montos);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("balance", balance);
        model.addAttribute("totalMovimientos", totalMovimientos);
        model.addAttribute("diaMayorGasto", diaMayorGasto);
        model.addAttribute("maxGasto", maxGasto);
        model.addAttribute("gastoPromedioDiario", gastoPromedioDiario);
        model.addAttribute("porcentajeAhorro", porcentajeAhorro);
        model.addAttribute("periodo", periodo != null ? periodo : "mensual");

        return "reportes/index";
    }
}
