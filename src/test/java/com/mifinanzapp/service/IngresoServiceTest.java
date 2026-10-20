package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.IngresoJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link IngresoService}: reglas de negocio y autorización por propietario.
 */
@ExtendWith(MockitoExtension.class)
class IngresoServiceTest {

    private static final Long USUARIO = 1L;

    @Mock
    private IngresoJdbcRepository ingresoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    private IngresoService service;

    @BeforeEach
    void setUp() {
        service = new IngresoService(ingresoRepository, categoriaRepository);
    }

    private Ingreso ingreso(Long id) {
        Usuario u = new Usuario();
        u.setId(USUARIO);
        Categoria c = new Categoria();
        c.setId(5L);
        Ingreso i = new Ingreso();
        i.setId(id);
        i.setUsuario(u);
        i.setCategoria(c);
        i.setMonto(new BigDecimal("100"));
        i.setFecha(LocalDate.of(2026, 1, 1));
        return i;
    }

    private void categoriaPropia(Categoria.TipoCategoria tipo) {
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO))
                .thenReturn(Optional.of(new Categoria("Salario", tipo)));
    }

    @Test
    void guardarSinIdCreaElIngreso() {
        categoriaPropia(Categoria.TipoCategoria.INGRESO);
        Ingreso i = ingreso(null);
        when(ingresoRepository.crear(i)).thenReturn(i);

        assertSame(i, service.guardar(i));
        verify(ingresoRepository).crear(i);
        verify(ingresoRepository, never()).actualizar(any());
    }

    @Test
    void guardarConIdActualizaElIngreso() {
        categoriaPropia(Categoria.TipoCategoria.INGRESO);
        Ingreso i = ingreso(7L);
        when(ingresoRepository.actualizar(i)).thenReturn(true);

        service.guardar(i);

        verify(ingresoRepository).actualizar(i);
        verify(ingresoRepository, never()).crear(any());
    }

    @Test
    void actualizarIngresoAjenoLanzaNotFound() {
        categoriaPropia(Categoria.TipoCategoria.INGRESO);
        Ingreso i = ingreso(7L);
        when(ingresoRepository.actualizar(i)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.guardar(i));
    }

    @Test
    void guardarConCategoriaDeOtroUsuarioLanzaNotFound() {
        when(categoriaRepository.findByIdAndUsuarioId(5L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.guardar(ingreso(null)));
        verifyNoInteractions(ingresoRepository);
    }

    @Test
    void guardarConCategoriaDeGastoLanzaIllegalArgument() {
        categoriaPropia(Categoria.TipoCategoria.GASTO);

        assertThrows(IllegalArgumentException.class, () -> service.guardar(ingreso(null)));
        verifyNoInteractions(ingresoRepository);
    }

    @Test
    void guardarValidaDatosObligatorios() {
        Ingreso sinCategoria = ingreso(null);
        sinCategoria.setCategoria(null);
        Ingreso montoCero = ingreso(null);
        montoCero.setMonto(BigDecimal.ZERO);
        Ingreso sinMonto = ingreso(null);
        sinMonto.setMonto(null);
        Ingreso sinFecha = ingreso(null);
        sinFecha.setFecha(null);

        for (Ingreso i : List.of(sinCategoria, montoCero, sinMonto, sinFecha)) {
            assertThrows(IllegalArgumentException.class, () -> service.guardar(i));
        }
        verifyNoInteractions(ingresoRepository);
    }

    @Test
    void listarPaginaDelegaEnElRepositorioJdbc() {
        List<Ingreso> lista = List.of(ingreso(1L));
        when(ingresoRepository.listarPorUsuario(USUARIO, 20, 0L)).thenReturn(lista);
        when(ingresoRepository.contarPorUsuario(USUARIO)).thenReturn(1L);

        var pagina = service.listarPagina(USUARIO, 0);

        assertEquals(lista, pagina.getContent());
        assertEquals(1, pagina.getTotalPages());
    }

    @Test
    void buscarInexistenteLanzaNotFound() {
        when(ingresoRepository.buscarPorIdYUsuario(9L, USUARIO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(9L, USUARIO));
    }

    @Test
    void buscarPropioDevuelveElIngreso() {
        Ingreso i = ingreso(9L);
        when(ingresoRepository.buscarPorIdYUsuario(9L, USUARIO)).thenReturn(Optional.of(i));

        assertSame(i, service.buscarPorId(9L, USUARIO));
    }

    @Test
    void eliminarPropioLlamaAlRepositorio() {
        when(ingresoRepository.eliminar(9L, USUARIO)).thenReturn(true);

        service.eliminar(9L, USUARIO);

        verify(ingresoRepository).eliminar(9L, USUARIO);
    }

    @Test
    void eliminarAjenoOInexistenteLanzaNotFound() {
        when(ingresoRepository.eliminar(9L, USUARIO)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(9L, USUARIO));
    }
}
