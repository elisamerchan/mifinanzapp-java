package com.mifinanzapp.api;

import com.mifinanzapp.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servicio web de consulta del resumen financiero del mes (el mismo que muestra el dashboard).
 *
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/resumen")
@Tag(name = "Resumen", description = "Resumen financiero del usuario")
public class ResumenApiController {

    private final DashboardService dashboardService;
    private final UsuarioActual usuarioActual;

    public ResumenApiController(DashboardService dashboardService, UsuarioActual usuarioActual) {
        this.dashboardService = dashboardService;
        this.usuarioActual = usuarioActual;
    }

    @GetMapping
    @Operation(summary = "Totales del mes, balance general y porcentaje de ahorro")
    public Map<String, Object> resumen(Authentication auth) {
        Long id = usuarioActual.de(auth).getId();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("totalIngresosMes", dashboardService.getTotalIngresosDelMes(id));
        r.put("totalGastosMes", dashboardService.getTotalGastosDelMes(id));
        r.put("balance", dashboardService.getBalance(id));
        r.put("porcentajeAhorro", dashboardService.getPorcentajeAhorro(id));
        r.put("movimientosDelMes", dashboardService.getTotalMovimientosDelMes(id));
        return r;
    }
}
