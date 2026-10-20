package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.GastoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de negocio para la gestión de gastos.
 * <p>
 * Proporciona operaciones CRUD para gastos, siempre acotadas al usuario propietario,
 * y evita la caché del dashboard cuando se crean, actualizan o eliminan registros.
 *
 * @since 1.0.0
 */
@Service
public class GastoService {

    private static final Logger log = LoggerFactory.getLogger(GastoService.class);

    public static final int TAMANO_PAGINA = 20;

    private final GastoRepository gastoRepository;
    private final CategoriaRepository categoriaRepository;

    public GastoService(GastoRepository gastoRepository, CategoriaRepository categoriaRepository) {
        this.gastoRepository = gastoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Page<Gasto> listarPagina(Long usuarioId, int pagina) {
        log.debug("listar_gastos usuarioId={} pagina={}", usuarioId, pagina);
        return gastoRepository.findByUsuarioId(usuarioId, PageRequest.of(Math.max(pagina, 0), TAMANO_PAGINA,
                Sort.by(Sort.Order.desc("fecha"), Sort.Order.desc("id"))));
    }

    public List<Gasto> listarEntre(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return gastoRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDescIdDesc(usuarioId, desde, hasta);
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public Gasto guardar(Gasto gasto) {
        Long usuarioId = gasto.getUsuario().getId();
        if (gasto.getCategoria() == null || gasto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(gasto.getCategoria().getId(), usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", gasto.getCategoria().getId()));
        if (categoria.getTipo() != Categoria.TipoCategoria.GASTO) {
            throw new IllegalArgumentException("La categoría seleccionada no es de tipo GASTO");
        }
        if (gasto.getId() != null) {
            buscarPorId(gasto.getId(), usuarioId); // impide sobrescribir gastos ajenos
        }
        log.info("guardar_gasto usuarioId={} categoriaId={} monto={}", usuarioId, categoria.getId(), gasto.getMonto());
        return gastoRepository.save(gasto);
    }

    public Gasto buscarPorId(Long id, Long usuarioId) {
        return gastoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public void eliminar(Long id, Long usuarioId) {
        log.info("eliminar_gasto id={} usuarioId={}", id, usuarioId);
        gastoRepository.delete(buscarPorId(id, usuarioId));
    }
}
