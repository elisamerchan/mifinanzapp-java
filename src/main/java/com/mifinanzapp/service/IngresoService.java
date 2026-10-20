package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.IngresoJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de negocio para la gestión de ingresos.
 * <p>
 * Las operaciones CRUD usan JDBC explícito ({@link IngresoJdbcRepository}) y siempre
 * se acotan al usuario propietario. Evita la caché del dashboard en cada escritura.
 *
 * @since 1.0.0
 */
@Service
public class IngresoService {

    private static final Logger log = LoggerFactory.getLogger(IngresoService.class);

    public static final int TAMANO_PAGINA = 20;

    private final IngresoJdbcRepository ingresoRepository;
    private final CategoriaRepository categoriaRepository;

    public IngresoService(IngresoJdbcRepository ingresoRepository, CategoriaRepository categoriaRepository) {
        this.ingresoRepository = ingresoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Page<Ingreso> listarPagina(Long usuarioId, int pagina) {
        log.debug("listar_ingresos usuarioId={} pagina={}", usuarioId, pagina);
        PageRequest pageable = PageRequest.of(Math.max(pagina, 0), TAMANO_PAGINA);
        return new PageImpl<>(
                ingresoRepository.listarPorUsuario(usuarioId, pageable.getPageSize(), pageable.getOffset()),
                pageable, ingresoRepository.contarPorUsuario(usuarioId));
    }

    public List<Ingreso> listarEntre(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return ingresoRepository.listarPorUsuarioEntre(usuarioId, desde, hasta);
    }

    /**
     * Crea el ingreso (id nulo) o actualiza uno existente del mismo usuario.
     *
     * @throws IllegalArgumentException  si faltan datos o la categoría no es de tipo INGRESO
     * @throws ResourceNotFoundException si la categoría no es del usuario o el ingreso no existe para él
     */
    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public Ingreso guardar(Ingreso ingreso) {
        Long usuarioId = ingreso.getUsuario().getId();
        if (ingreso.getCategoria() == null || ingreso.getCategoria().getId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        if (ingreso.getMonto() == null || ingreso.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (ingreso.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(ingreso.getCategoria().getId(), usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", ingreso.getCategoria().getId()));
        if (categoria.getTipo() != Categoria.TipoCategoria.INGRESO) {
            throw new IllegalArgumentException("La categoría seleccionada no es de tipo INGRESO");
        }
        log.info("guardar_ingreso usuarioId={} categoriaId={} monto={}", usuarioId, categoria.getId(), ingreso.getMonto());
        if (ingreso.getId() == null) {
            return ingresoRepository.crear(ingreso);
        }
        if (!ingresoRepository.actualizar(ingreso)) {
            throw new ResourceNotFoundException("Ingreso", "id", ingreso.getId());
        }
        return ingreso;
    }

    public Ingreso buscarPorId(Long id, Long usuarioId) {
        return ingresoRepository.buscarPorIdYUsuario(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso", "id", id));
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public void eliminar(Long id, Long usuarioId) {
        log.info("eliminar_ingreso id={} usuarioId={}", id, usuarioId);
        if (!ingresoRepository.eliminar(id, usuarioId)) {
            throw new ResourceNotFoundException("Ingreso", "id", id);
        }
    }
}
