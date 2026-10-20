package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de autorización por propietario en {@link GastoService}.
 */
@ExtendWith(MockitoExtension.class)
class GastoServiceTest {

    private static final Long USUARIO = 1L;

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    private GastoService service;

    @BeforeEach
    void setUp() {
        service = new GastoService(gastoRepository, categoriaRepository);
    }

    private Gasto gasto(Long id) {
        Usuario u = new Usuario();
        u.setId(USUARIO);
        Categoria c = new Categoria();
        c.setId(5L);
        Gasto g = new Gasto();
        g.setId(id);
        g.setUsuario(u);
        g.setCategoria(c);
        return g;
    }

    @Test
    void guardarConCategoriaDeOtroUsuarioLanzaNotFound() {
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.guardar(gasto(null)));
        verify(gastoRepository, never()).save(any());
    }

    @Test
    void guardarConCategoriaDeIngresoLanzaIllegalArgument() {
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO))
                .thenReturn(Optional.of(new Categoria("Salario", Categoria.TipoCategoria.INGRESO)));

        assertThrows(IllegalArgumentException.class, () -> service.guardar(gasto(null)));
        verify(gastoRepository, never()).save(any());
    }

    @Test
    void editarGastoAjenoLanzaNotFoundSinGuardar() {
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO))
                .thenReturn(Optional.of(new Categoria("Comida", Categoria.TipoCategoria.GASTO)));
        when(gastoRepository.findByIdAndUsuarioId(7L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.guardar(gasto(7L)));
        verify(gastoRepository, never()).save(any());
    }

    @Test
    void eliminarGastoAjenoLanzaNotFoundSinBorrar() {
        when(gastoRepository.findByIdAndUsuarioId(7L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(7L, USUARIO));
        verify(gastoRepository, never()).delete(any());
    }

    @Test
    void eliminarGastoPropioLoBorra() {
        Gasto g = gasto(7L);
        when(gastoRepository.findByIdAndUsuarioId(7L, USUARIO)).thenReturn(Optional.of(g));

        service.eliminar(7L, USUARIO);

        verify(gastoRepository).delete(g);
    }
}
