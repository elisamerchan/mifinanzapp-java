package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de {@link IngresoJdbcRepository} contra una base H2 en memoria (nunca PostgreSQL).
 * Usuario 1 tiene la categoría 1; usuario 2, la categoría 2.
 */
class IngresoJdbcRepositoryTest {

    private DriverManagerDataSource ds;
    private IngresoJdbcRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        ds = new DriverManagerDataSource("jdbc:h2:mem:ingresos;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1", "sa", "");
        try (var con = ds.getConnection()) {
            new ResourceDatabasePopulator(new ClassPathResource("schema-ingresos.sql")).populate(con);
        }
        repo = new IngresoJdbcRepository(new JdbcTemplate(ds));
    }

    @AfterEach
    void tearDown() {
        new JdbcTemplate(ds).execute("DROP ALL OBJECTS");
    }

    private Ingreso nuevo(long usuarioId, long categoriaId, String monto) {
        Usuario u = new Usuario();
        u.setId(usuarioId);
        Categoria c = new Categoria();
        c.setId(categoriaId);
        Ingreso i = new Ingreso();
        i.setUsuario(u);
        i.setCategoria(c);
        i.setMonto(new BigDecimal(monto));
        i.setFecha(LocalDate.of(2026, 1, 15));
        i.setDescripcion("test");
        return i;
    }

    @Test
    void crearAsignaIdYPermiteBuscarlo() {
        Ingreso creado = repo.crear(nuevo(1, 1, "100.50"));

        assertNotNull(creado.getId());
        Ingreso leido = repo.buscarPorIdYUsuario(creado.getId(), 1L).orElseThrow();
        assertEquals(0, new BigDecimal("100.50").compareTo(leido.getMonto()));
        assertEquals("Salario", leido.getCategoria().getNombre());
        assertEquals(LocalDate.of(2026, 1, 15), leido.getFecha());
    }

    @Test
    void listarPorUsuarioSoloDevuelveLosPropios() {
        repo.crear(nuevo(1, 1, "10"));
        repo.crear(nuevo(2, 2, "20"));

        var lista = repo.listarPorUsuario(1L, 20, 0);

        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).getUsuario().getId());
    }

    @Test
    void paginaYConteoRespetanLimiteYOffset() {
        for (int i = 0; i < 5; i++) repo.crear(nuevo(1, 1, "1"));
        repo.crear(nuevo(2, 2, "1"));

        assertEquals(5, repo.contarPorUsuario(1L));
        assertEquals(1, repo.listarPorUsuario(1L, 2, 4).size()); // última página: queda 1 fila
        assertEquals(2, repo.listarPorUsuario(1L, 2, 0).size());
    }

    @Test
    void listarEntreFechasEsInclusivoYSoloDelUsuario() {
        repo.crear(nuevo(1, 1, "1"));
        repo.crear(nuevo(2, 2, "1"));

        assertEquals(1, repo.listarPorUsuarioEntre(1L, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 15)).size());
        assertTrue(repo.listarPorUsuarioEntre(1L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)).isEmpty());
    }

    @Test
    void buscarInexistenteOAjenoDevuelveVacio() {
        Ingreso creado = repo.crear(nuevo(1, 1, "10"));

        assertTrue(repo.buscarPorIdYUsuario(999L, 1L).isEmpty());
        assertTrue(repo.buscarPorIdYUsuario(creado.getId(), 2L).isEmpty());
    }

    @Test
    void actualizarCambiaLosDatosDelPropietario() {
        Ingreso creado = repo.crear(nuevo(1, 1, "10"));
        creado.setMonto(new BigDecimal("55.00"));
        creado.setDescripcion("editado");

        assertTrue(repo.actualizar(creado));

        Ingreso leido = repo.buscarPorIdYUsuario(creado.getId(), 1L).orElseThrow();
        assertEquals(0, new BigDecimal("55.00").compareTo(leido.getMonto()));
        assertEquals("editado", leido.getDescripcion());
    }

    @Test
    void actualizarIngresoAjenoNoModificaNada() {
        Ingreso ajeno = repo.crear(nuevo(2, 2, "10"));
        Ingreso intento = nuevo(1, 1, "999");
        intento.setId(ajeno.getId());

        assertFalse(repo.actualizar(intento));

        Ingreso leido = repo.buscarPorIdYUsuario(ajeno.getId(), 2L).orElseThrow();
        assertEquals(0, BigDecimal.TEN.compareTo(leido.getMonto()));
    }

    @Test
    void eliminarBorraSoloSiEsDelPropietario() {
        Ingreso creado = repo.crear(nuevo(1, 1, "10"));

        assertFalse(repo.eliminar(creado.getId(), 2L));
        assertTrue(repo.buscarPorIdYUsuario(creado.getId(), 1L).isPresent());

        assertTrue(repo.eliminar(creado.getId(), 1L));
        assertTrue(repo.buscarPorIdYUsuario(creado.getId(), 1L).isEmpty());
        assertFalse(repo.eliminar(creado.getId(), 1L));
    }
}
