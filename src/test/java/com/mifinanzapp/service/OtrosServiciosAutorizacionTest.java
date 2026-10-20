package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.PresupuestoRepository;
import com.mifinanzapp.repository.RecordatorioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Autorización por propietario en {@link CategoriaService}, {@link PresupuestoService}
 * y {@link RecordatorioService}.
 */
@ExtendWith(MockitoExtension.class)
class OtrosServiciosAutorizacionTest {

    private static final Long USUARIO = 1L;

    @Mock private CategoriaRepository categoriaRepository;
    @Mock private PresupuestoRepository presupuestoRepository;
    @Mock private GastoRepository gastoRepository;
    @Mock private RecordatorioRepository recordatorioRepository;

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(USUARIO);
        return u;
    }

    // --- Categorías ---

    @Test
    void categoriaAjenaNoSeEliminaNiSeEdita() {
        CategoriaService service = new CategoriaService(categoriaRepository);
        when(categoriaRepository.findByIdAndUsuarioId(3L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(3L, USUARIO));
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(3L, USUARIO));
        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    void guardarCategoriaConIdAjenoLanzaNotFound() {
        CategoriaService service = new CategoriaService(categoriaRepository);
        Categoria c = new Categoria("X", Categoria.TipoCategoria.GASTO);
        c.setId(3L);
        c.setUsuario(usuario());
        when(categoriaRepository.findByIdAndUsuarioId(3L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.guardar(c));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminarCategoriaEnUsoLanzaIllegalArgument() {
        CategoriaService service = new CategoriaService(categoriaRepository);
        Categoria c = new Categoria("X", Categoria.TipoCategoria.GASTO);
        when(categoriaRepository.findByIdAndUsuarioId(3L, USUARIO)).thenReturn(Optional.of(c));
        doThrow(new DataIntegrityViolationException("fk")).when(categoriaRepository).flush();

        assertThrows(IllegalArgumentException.class, () -> service.eliminar(3L, USUARIO));
    }

    // --- Presupuestos ---

    private Presupuesto presupuesto(Long id) {
        Categoria c = new Categoria();
        c.setId(5L);
        Presupuesto p = new Presupuesto();
        p.setId(id);
        p.setUsuario(usuario());
        p.setCategoria(c);
        return p;
    }

    @Test
    void presupuestoAjenoNoSeEliminaNiSeSobrescribe() {
        PresupuestoService service = new PresupuestoService(presupuestoRepository, gastoRepository, categoriaRepository);
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO))
                .thenReturn(Optional.of(new Categoria("Comida", Categoria.TipoCategoria.GASTO)));
        when(presupuestoRepository.findByIdAndUsuarioId(8L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(8L, USUARIO));
        assertThrows(ResourceNotFoundException.class, () -> service.guardar(presupuesto(8L)));
        verify(presupuestoRepository, never()).delete(any());
        verify(presupuestoRepository, never()).save(any());
    }

    @Test
    void presupuestoRechazaCategoriaAjenaOdeIngreso() {
        PresupuestoService service = new PresupuestoService(presupuestoRepository, gastoRepository, categoriaRepository);
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.guardar(presupuesto(null)));

        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO))
                .thenReturn(Optional.of(new Categoria("Salario", Categoria.TipoCategoria.INGRESO)));
        assertThrows(IllegalArgumentException.class, () -> service.guardar(presupuesto(null)));
        verify(presupuestoRepository, never()).save(any());
    }

    // --- Recordatorios ---

    @Test
    void recordatorioAjenoNoSeEliminaNiSeSobrescribe() {
        RecordatorioService service = new RecordatorioService(recordatorioRepository);
        Recordatorio r = new Recordatorio();
        r.setId(4L);
        r.setUsuario(usuario());
        when(recordatorioRepository.findByIdAndUsuarioId(4L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(4L, USUARIO));
        assertThrows(ResourceNotFoundException.class, () -> service.guardar(r));
        verify(recordatorioRepository, never()).delete(any());
        verify(recordatorioRepository, never()).save(any());
    }
}
