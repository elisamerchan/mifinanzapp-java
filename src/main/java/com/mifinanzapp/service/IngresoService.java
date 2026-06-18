package com.mifinanzapp.service;

import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.IngresoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión de ingresos.
 * <p>
 * Proporciona operaciones CRUD para ingresos y evita la caché del
 * dashboard cuando se crean, actualizan o eliminan registros.
 *
 * @since 1.0.0
 */
@Service
public class IngresoService {

    private static final Logger log = LoggerFactory.getLogger(IngresoService.class);

    private final IngresoRepository ingresoRepository;

    public IngresoService(IngresoRepository ingresoRepository) {
        this.ingresoRepository = ingresoRepository;
    }

    public List<Ingreso> listarPorUsuario(Long usuarioId) {
        log.debug("listar_ingresos usuarioId={}", usuarioId);
        return ingresoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public Ingreso guardar(Ingreso ingreso) {
        log.info("guardar_ingreso usuarioId={} categoriaId={} monto={}",
                ingreso.getUsuario().getId(), ingreso.getCategoria().getId(), ingreso.getMonto());
        return ingresoRepository.save(ingreso);
    }

    public Ingreso buscarPorId(Long id) {
        return ingresoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso", "id", id));
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public void eliminar(Long id) {
        log.info("eliminar_ingreso id={}", id);
        ingresoRepository.deleteById(id);
    }
}
