package com.mifinanzapp.service;

import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.GastoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión de gastos.
 * <p>
 * Proporciona operaciones CRUD para gastos y evita la caché del
 * dashboard cuando se crean, actualizan o eliminan registros.
 *
 * @since 1.0.0
 */
@Service
public class GastoService {

    private static final Logger log = LoggerFactory.getLogger(GastoService.class);

    private final GastoRepository gastoRepository;

    public GastoService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    public List<Gasto> listarPorUsuario(Long usuarioId) {
        log.debug("listar_gastos usuarioId={}", usuarioId);
        return gastoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public Gasto guardar(Gasto gasto) {
        log.info("guardar_gasto usuarioId={} categoriaId={} monto={}",
                gasto.getUsuario().getId(), gasto.getCategoria().getId(), gasto.getMonto());
        return gastoRepository.save(gasto);
    }

    public Gasto buscarPorId(Long id) {
        return gastoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public void eliminar(Long id) {
        log.info("eliminar_gasto id={}", id);
        gastoRepository.deleteById(id);
    }
}
