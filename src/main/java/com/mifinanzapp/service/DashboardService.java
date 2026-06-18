package com.mifinanzapp.service;

import com.mifinanzapp.entity.*;
import com.mifinanzapp.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Servicio de negocio para el dashboard principal.
 * <p>
 * Centraliza todas las consultas de estadísticas y resúmenes
 * financieros que se muestran en la página principal: totales
 * de ingresos/gastos, balance, evolución mensual, presupuestos
 * con progreso, movimientos recientes y consejos financieros.
 * <p>
 * Los resultados se cachean con Caffeine (2 min TTL) para
 * reducir la carga en la base de datos.
 *
 * @since 1.0.0
 */
@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaRepository categoriaRepository;

    private static final List<String> NOMBRES_MESES = List.of(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    );

    public DashboardService(IngresoRepository ingresoRepository, GastoRepository gastoRepository,
                            PresupuestoRepository presupuestoRepository, CategoriaRepository categoriaRepository) {
        this.ingresoRepository = ingresoRepository;
        this.gastoRepository = gastoRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-ingresos'")
    public BigDecimal getTotalIngresosDelMes(Long usuarioId) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return ingresoRepository.sumByUsuarioAndFechaBetween(usuarioId, inicio, fin);
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-gastos'")
    public BigDecimal getTotalGastosDelMes(Long usuarioId) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return gastoRepository.sumByUsuarioAndFechaBetween(usuarioId, inicio, fin);
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-balance'")
    public BigDecimal getBalance(Long usuarioId) {
        BigDecimal ingresos = ingresoRepository.sumByUsuarioId(usuarioId);
        BigDecimal gastos = gastoRepository.sumByUsuarioId(usuarioId);
        return ingresos.subtract(gastos);
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-recientes'")
    public List<Map<String, Object>> getMovimientosRecientes(Long usuarioId, int limit) {
        // Obtiene los últimos gastos e ingresos y los mergea ordenados por fecha (más reciente primero)
        // simulando un feed unificado de movimientos
        List<Gasto> gastos = gastoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, PageRequest.of(0, limit));
        List<Ingreso> ingresos = ingresoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, PageRequest.of(0, limit));

        DecimalFormat df = new DecimalFormat("#,##0.00");
        List<Map<String, Object>> recientes = new ArrayList<>();
        int gIdx = 0, iIdx = 0;
        while (recientes.size() < limit && (gIdx < gastos.size() || iIdx < ingresos.size())) {
            if (iIdx >= ingresos.size() || (gIdx < gastos.size() && gastos.get(gIdx).getFecha().isAfter(ingresos.get(iIdx).getFecha()))) {
                Gasto g = gastos.get(gIdx++);
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("fecha", g.getFecha());
                m.put("tipo", "GASTO");
                m.put("categoria", g.getCategoria());
                m.put("descripcion", g.getDescripcion());
                m.put("monto", g.getMonto());
                m.put("montoStr", "-$" + df.format(g.getMonto()));
                recientes.add(m);
            } else {
                Ingreso i = ingresos.get(iIdx++);
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("fecha", i.getFecha());
                m.put("tipo", "INGRESO");
                m.put("categoria", i.getCategoria());
                m.put("descripcion", i.getDescripcion());
                m.put("monto", i.getMonto());
                m.put("montoStr", "+$" + df.format(i.getMonto()));
                recientes.add(m);
            }
        }
        return recientes;
    }

    public String getCategoriaMayorGasto(Long usuarioId) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<Object[]> results = gastoRepository.sumByCategoriaForUser(usuarioId, inicio, fin);
        if (!results.isEmpty()) {
            Categoria cat = (Categoria) results.get(0)[0];
            return cat.getNombre();
        }
        return "N/A";
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-total-mov'")
    public long getTotalMovimientosDelMes(Long usuarioId) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        long gastos = gastoRepository.countByUsuarioAndFechaBetween(usuarioId, inicio, fin);
        long ingresos = ingresoRepository.countByUsuarioAndFechaBetween(usuarioId, inicio, fin);
        return gastos + ingresos;
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-ahorro'")
    public BigDecimal getPorcentajeAhorro(Long usuarioId) {
        BigDecimal ingresos = getTotalIngresosDelMes(usuarioId);
        BigDecimal gastos = getTotalGastosDelMes(usuarioId);
        if (ingresos.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return ingresos.subtract(gastos).multiply(BigDecimal.valueOf(100))
            .divide(ingresos, 1, RoundingMode.HALF_UP);
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-promedio'")
    public BigDecimal getGastoPromedioDiario(Long usuarioId) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;
        BigDecimal totalGastos = getTotalGastosDelMes(usuarioId);
        return totalGastos.divide(BigDecimal.valueOf(dias), 2, RoundingMode.HALF_UP);
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-mayor-gasto'")
    public Map<String, Object> getDiaMayorGasto(Long usuarioId) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<Object[]> gastosDiarios = gastoRepository.sumByUsuarioAndFechaBetweenGroupByDay(usuarioId, inicio, fin);
        if (!gastosDiarios.isEmpty()) {
            Object[] top = gastosDiarios.get(0);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("dia", top[0].toString());
            result.put("monto", (BigDecimal) top[1]);
            return result;
        }
        return null;
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-evolucion'")
    public Map<String, Object> getEvolucionMensual(Long usuarioId) {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusMonths(6).withDayOfMonth(1);
        List<Object[]> gastosMensuales = gastoRepository.monthlyEvolution(usuarioId, inicio, fin);
        List<Object[]> ingresosMensuales = ingresoRepository.monthlyEvolution(usuarioId, inicio, fin);

        Map<Integer, BigDecimal> gastosMap = new HashMap<>();
        for (Object[] row : gastosMensuales) {
            int key = ((Number) row[0]).intValue() * 100 + ((Number) row[1]).intValue();
            gastosMap.put(key, (BigDecimal) row[2]);
        }
        Map<Integer, BigDecimal> ingresosMap = new HashMap<>();
        for (Object[] row : ingresosMensuales) {
            int key = ((Number) row[0]).intValue() * 100 + ((Number) row[1]).intValue();
            ingresosMap.put(key, (BigDecimal) row[2]);
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> gastosData = new ArrayList<>();
        List<BigDecimal> ingresosData = new ArrayList<>();

        LocalDate cursor = inicio;
        while (!cursor.isAfter(fin)) {
            int key = cursor.getMonthValue() * 100 + cursor.getYear();
            String label = NOMBRES_MESES.get(cursor.getMonthValue() - 1);
            labels.add(label);
            gastosData.add(gastosMap.getOrDefault(key, BigDecimal.ZERO));
            ingresosData.add(ingresosMap.getOrDefault(key, BigDecimal.ZERO));
            cursor = cursor.plusMonths(1);
        }

        Map<String, Object> evolucion = new LinkedHashMap<>();
        evolucion.put("labels", labels);
        evolucion.put("gastos", gastosData);
        evolucion.put("ingresos", ingresosData);
        return evolucion;
    }

    @Cacheable(value = "dashboard", key = "#usuarioId + '-presupuestos'")
    public List<Map<String, Object>> getPresupuestosConProgreso(Long usuarioId) {
        LocalDate now = LocalDate.now();
        List<Presupuesto> presupuestos = presupuestoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, now.getMonthValue(), now.getYear());
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Presupuesto p : presupuestos) {
            BigDecimal gastado = gastoRepository.sumByUsuarioAndCategoriaAndFechaBetween(
                    usuarioId, p.getCategoria().getId(),
                    now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth()));

            Map<String, Object> item = new HashMap<>();
            item.put("categoria", p.getCategoria().getNombre());
            item.put("limite", p.getMontoLimite());
            item.put("gastado", gastado);
            BigDecimal porcentaje = p.getMontoLimite().compareTo(BigDecimal.ZERO) > 0
                    ? gastado.multiply(BigDecimal.valueOf(100)).divide(p.getMontoLimite(), 0, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            item.put("porcentaje", porcentaje);
            item.put("excedido", porcentaje.compareTo(BigDecimal.valueOf(100)) > 0);
            resultado.add(item);
        }
        return resultado;
    }

    public String getConsejoAleatorio() {
        java.util.List<String> consejos = java.util.List.of(
            "Ahorra al menos el 20% de tus ingresos cada mes.",
            "Revisa tus suscripciones mensuales y cancela las que no uses.",
            "Usa la regla 50/30/20: 50% necesidades, 30% deseos, 20% ahorro.",
            "Establece un fondo de emergencia de 3 a 6 meses de gastos.",
            "Paga tus deudas con el interés más alto primero.",
            "Invierte en tu educación financiera, es el mejor rendimiento.",
            "Lleva un registro de cada gasto, por pequeño que sea.",
            "Compara precios antes de hacer compras grandes.",
            "Usa efectivo en lugar de tarjeta para controlar gastos.",
            "Revisa tu presupuesto semanalmente para mantener el rumbo."
        );
        java.util.Random rand = new java.util.Random();
        return consejos.get(rand.nextInt(consejos.size()));
    }
}
