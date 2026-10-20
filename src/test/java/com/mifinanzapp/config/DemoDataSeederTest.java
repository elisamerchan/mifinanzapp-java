package com.mifinanzapp.config;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.*;
import com.mifinanzapp.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link DemoDataSeeder}.
 */
@ExtendWith(MockitoExtension.class)
class DemoDataSeederTest {

    @Mock private UsuarioService usuarioService;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private IngresoRepository ingresoRepository;
    @Mock private GastoRepository gastoRepository;
    @Mock private PresupuestoRepository presupuestoRepository;
    @Mock private RecordatorioRepository recordatorioRepository;

    private DemoDataSeeder seeder;

    @BeforeEach
    void setUp() {
        seeder = new DemoDataSeeder(usuarioService, usuarioRepository, categoriaRepository,
                ingresoRepository, gastoRepository, presupuestoRepository, recordatorioRepository);
    }

    @Test
    void noHaceNadaSiElUsuarioDemoYaExiste() {
        when(usuarioRepository.existsByEmail(DemoDataSeeder.EMAIL)).thenReturn(true);

        seeder.run(null);

        verifyNoInteractions(usuarioService, ingresoRepository, gastoRepository, presupuestoRepository);
    }

    @Test
    @SuppressWarnings("unchecked")
    void creaHistorialSinPresupuestosDuplicados() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioService.registrar(any())).thenReturn(usuario);
        List<Categoria> todas = Stream.concat(
                Stream.of("Alimentación", "Transporte", "Salud", "Educación", "Servicios", "Entretenimiento")
                        .map(n -> new Categoria(n, Categoria.TipoCategoria.GASTO)),
                Stream.of("Salario", "Freelance", "Ventas", "Bonificaciones")
                        .map(n -> new Categoria(n, Categoria.TipoCategoria.INGRESO)))
                .collect(Collectors.toList());
        when(categoriaRepository.findByUsuarioId(1L)).thenReturn(todas);

        seeder.run(null);

        ArgumentCaptor<List<Presupuesto>> presupuestos = ArgumentCaptor.forClass(List.class);
        verify(presupuestoRepository).saveAll(presupuestos.capture());
        long distintos = presupuestos.getValue().stream()
                .map(p -> p.getCategoria().getNombre() + p.getMes() + "/" + p.getAnio()).distinct().count();
        assertEquals(36, presupuestos.getValue().size());
        assertEquals(36, distintos);

        ArgumentCaptor<List<Gasto>> gastos = ArgumentCaptor.forClass(List.class);
        verify(gastoRepository).saveAll(gastos.capture());
        assertFalse(gastos.getValue().isEmpty());
        verify(ingresoRepository).saveAll(any());
    }
}
