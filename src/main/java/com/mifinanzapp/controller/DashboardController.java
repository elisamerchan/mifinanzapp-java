package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.DashboardService;
import com.mifinanzapp.service.RecordatorioService;
import com.mifinanzapp.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controlador del dashboard principal.
 * <p>
 * Muestra el resumen financiero del usuario: totales de ingresos/gastos,
 * balance, evolución mensual, presupuestos con progreso, movimientos
 * recientes y consejos financieros.
 * <p>
 * Es la página principal después del inicio de sesión.
 *
 * @since 1.0.0
 */
@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;
    private final RecordatorioService recordatorioService;

    public DashboardController(DashboardService dashboardService, UsuarioService usuarioService,
                               RecordatorioService recordatorioService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
        this.recordatorioService = recordatorioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        log.debug("dashboard_request user={}", auth.getName());
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        Long uid = usuario.getId();

        BigDecimal ingresos = dashboardService.getTotalIngresosDelMes(uid);
        BigDecimal gastos = dashboardService.getTotalGastosDelMes(uid);
        BigDecimal balance = dashboardService.getBalance(uid);

        model.addAttribute("nombre", usuario.getNombre());
        model.addAttribute("totalIngresos", ingresos);
        model.addAttribute("totalGastos", gastos);
        model.addAttribute("balance", balance);
        model.addAttribute("categoriaMayorGasto", dashboardService.getCategoriaMayorGasto(uid));
        model.addAttribute("movimientosRecientes", dashboardService.getMovimientosRecientes(uid, 5));
        model.addAttribute("presupuestos", dashboardService.getPresupuestosConProgreso(uid));
        model.addAttribute("consejo", dashboardService.getConsejoAleatorio());
        model.addAttribute("recordatoriosPendientes", recordatorioService.pendientesHasta(uid, 7));

        model.addAttribute("totalMovimientos", dashboardService.getTotalMovimientosDelMes(uid));
        model.addAttribute("porcentajeAhorro", dashboardService.getPorcentajeAhorro(uid));
        model.addAttribute("gastoPromedioDiario", dashboardService.getGastoPromedioDiario(uid));
        model.addAttribute("diaMayorGasto", dashboardService.getDiaMayorGasto(uid));
        model.addAttribute("evolucionMensual", dashboardService.getEvolucionMensual(uid));

        return "dashboard/index";
    }
}
