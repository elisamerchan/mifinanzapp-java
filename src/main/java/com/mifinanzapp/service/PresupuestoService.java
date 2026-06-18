package com.mifinanzapp.service;

import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.PresupuestoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * Servicio de negocio para la gestión de presupuestos.
 * <p>
 * Proporciona operaciones CRUD y el cálculo del progreso de gasto
 * contra el límite establecido para cada presupuesto mensual.
 *
 * @since 1.0.0
 */
@Service
public class PresupuestoService {

    private static final Logger log = LoggerFactory.getLogger(PresupuestoService.class);

    private final PresupuestoRepository presupuestoRepository;
    private final GastoRepository gastoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository, GastoRepository gastoRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.gastoRepository = gastoRepository;
    }

    public List<Map<String, Object>> listarConProgreso(Long usuarioId) {
        List<Presupuesto> presupuestos = presupuestoRepository.findByUsuarioId(usuarioId);
        List<Map<String, Object>> resultado = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (Presupuesto p : presupuestos) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("categoria", p.getCategoria());
            item.put("mes", p.getMes());
            item.put("anio", p.getAnio());
            item.put("montoLimite", p.getMontoLimite());

            LocalDate inicio = LocalDate.of(p.getAnio(), p.getMes(), 1);
            LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
            BigDecimal gastado = gastoRepository.sumByUsuarioAndCategoriaAndFechaBetween(
                usuarioId, p.getCategoria().getId(), inicio, fin);

            item.put("gastado", gastado);
            BigDecimal porcentaje = p.getMontoLimite().compareTo(BigDecimal.ZERO) > 0
                ? gastado.multiply(BigDecimal.valueOf(100)).divide(p.getMontoLimite(), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            item.put("porcentaje", porcentaje);
            item.put("excedido", porcentaje.compareTo(BigDecimal.valueOf(100)) > 0);
            resultado.add(item);
        }
        return resultado;
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public Presupuesto guardar(Presupuesto presupuesto) {
        log.info("guardar_presupuesto usuarioId={} categoriaId={} mes={} anio={} limite={}",
                presupuesto.getUsuario().getId(), presupuesto.getCategoria().getId(),
                presupuesto.getMes(), presupuesto.getAnio(), presupuesto.getMontoLimite());
        return presupuestoRepository.save(presupuesto);
    }

    public Presupuesto buscarPorId(Long id) {
        return presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public void eliminar(Long id) {
        log.info("eliminar_presupuesto id={}", id);
        presupuestoRepository.deleteById(id);
    }
}
