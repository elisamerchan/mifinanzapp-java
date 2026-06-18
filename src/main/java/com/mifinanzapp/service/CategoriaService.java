package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión de categorías.
 * <p>
 * Proporciona operaciones CRUD y consultas filtradas por
 * usuario y tipo (INGRESO/GASTO).
 *
 * @since 1.0.0
 */
@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarPorUsuario(Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    public List<Categoria> listarPorTipo(Long usuarioId, Categoria.TipoCategoria tipo) {
        return categoriaRepository.findByUsuarioIdAndTipo(usuarioId, tipo);
    }

    @Transactional
    public Categoria guardar(Categoria categoria) {
        log.info("guardar_categoria usuarioId={} nombre={} tipo={}",
                categoria.getUsuario().getId(), categoria.getNombre(), categoria.getTipo());
        return categoriaRepository.save(categoria);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("eliminar_categoria id={}", id);
        categoriaRepository.deleteById(id);
    }

    public List<Categoria> listarTodas(Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }
}
