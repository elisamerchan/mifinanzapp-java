package com.mifinanzapp.servlets.dao;

import com.mifinanzapp.servlets.config.InicializadorBaseDatos;
import com.mifinanzapp.servlets.modelo.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del DAO contra H2 en memoria. Cada prueba usa una base de datos nueva
 * para que no dependan unas de otras.
 */
class CategoriaDaoTest {

    private CategoriaDao dao;

    @BeforeEach
    void prepararBaseDeDatos() {
        // Mismo modo que la aplicación real, para que el esquema se pruebe igual que en producción.
        String url = "jdbc:h2:mem:test_" + UUID.randomUUID().toString().replace("-", "")
                + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        DataSource dataSource = InicializadorBaseDatos.crearDataSource(url);
        InicializadorBaseDatos.crearEsquema(dataSource);
        dao = new CategoriaDao(dataSource);
    }

    @Test
    void listarDevuelveLosDatosDeEjemploOrdenados() {
        List<Categoria> categorias = dao.listar();

        assertEquals(4, categorias.size());
        assertEquals("GASTO", categorias.get(0).getTipo());
    }

    @Test
    void guardarInsertaUnaCategoriaNuevaYAsignaId() {
        Categoria nueva = new Categoria(null, "Ocio", "GASTO");

        Categoria guardada = dao.guardar(nueva);

        assertNotNull(guardada.getId());
        Optional<Categoria> encontrada = dao.buscarPorId(guardada.getId());
        assertTrue(encontrada.isPresent());
        assertEquals("Ocio", encontrada.get().getNombre());
        assertEquals("GASTO", encontrada.get().getTipo());
    }

    @Test
    void guardarConIdActualizaLaCategoriaExistente() {
        Categoria existente = dao.listar().get(0);
        existente.setNombre("Nombre modificado");

        dao.guardar(existente);

        Categoria actualizada = dao.buscarPorId(existente.getId()).orElseThrow();
        assertEquals("Nombre modificado", actualizada.getNombre());
        assertEquals(4, dao.listar().size(), "Actualizar no debe crear filas nuevas");
    }

    @Test
    void buscarPorIdInexistenteDevuelveVacio() {
        assertFalse(dao.buscarPorId(9999L).isPresent());
    }

    @Test
    void eliminarBorraLaCategoriaYDevuelveTrue() {
        long id = dao.listar().get(0).getId();

        assertTrue(dao.eliminar(id));

        assertFalse(dao.buscarPorId(id).isPresent());
        assertEquals(3, dao.listar().size());
    }

    @Test
    void eliminarInexistenteDevuelveFalse() {
        assertFalse(dao.eliminar(9999L));
    }

    @Test
    void crearEsquemaEsIdempotenteYNoDuplicaDatos() {
        DataSource dataSource = InicializadorBaseDatos.crearDataSource(
                "jdbc:h2:mem:idem_" + UUID.randomUUID().toString().replace("-", "")
                        + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL");

        InicializadorBaseDatos.crearEsquema(dataSource);
        InicializadorBaseDatos.crearEsquema(dataSource);

        assertEquals(4, new CategoriaDao(dataSource).listar().size());
    }
}
