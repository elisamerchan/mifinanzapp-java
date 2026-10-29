package com.mifinanzapp.movil.datos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Ejecuta el esquema y todas las consultas de {@link Sql} sobre SQLite (en la JVM), con las claves
 * foráneas activadas igual que en el dispositivo.
 */
public class SqlTest {

    private Connection conexion;

    @Before
    public void crearBase() throws SQLException {
        conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement s = conexion.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            for (String sentencia : Sql.CREAR) {
                s.execute(sentencia);
            }
        }
        for (String[] c : Sql.CATEGORIAS_INICIALES) {
            ejecutar(Sql.INSERTAR_CATEGORIA, c[0], c[1]);
        }
    }

    @After
    public void cerrar() throws SQLException {
        conexion.close();
    }

    private int ejecutar(String sql, Object... args) throws SQLException {
        try (PreparedStatement p = conexion.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                p.setObject(i + 1, args[i]);
            }
            return p.executeUpdate();
        }
    }

    private double numero(String sql, Object... args) throws SQLException {
        try (PreparedStatement p = conexion.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                p.setObject(i + 1, args[i]);
            }
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getDouble(1) : 0;
            }
        }
    }

    private long idCategoria(String nombre) throws SQLException {
        return (long) numero("SELECT id FROM categorias WHERE nombre = ?", nombre);
    }

    @Test
    public void seCreanLasCategoriasIniciales() throws SQLException {
        assertEquals(10, (int) numero("SELECT COUNT(*) FROM categorias"));
        assertEquals(3, (int) numero("SELECT COUNT(*) FROM categorias WHERE tipo = ?", "INGRESO"));
        assertEquals(7, (int) numero("SELECT COUNT(*) FROM categorias WHERE tipo = ?", "GASTO"));
    }

    @Test
    public void consultasDeCategoriasFuncionan() throws SQLException {
        try (PreparedStatement p = conexion.prepareStatement(Sql.CATEGORIAS_POR_TIPO)) {
            p.setString(1, "GASTO");
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
                assertEquals("Alimentación", r.getString(2));
            }
        }
        assertEquals(1, (int) numero(Sql.CATEGORIA_EXISTE, "salario", "INGRESO"));
        assertEquals(0, (int) numero(Sql.CATEGORIA_EXISTE, "salario", "GASTO"));
    }

    @Test
    public void noSePermiteCategoriaDuplicadaDelMismoTipo() throws SQLException {
        try {
            ejecutar(Sql.INSERTAR_CATEGORIA, "Salario", "INGRESO");
            fail("Debía rechazar el duplicado");
        } catch (SQLException esperado) {
            assertTrue(esperado.getMessage().toUpperCase().contains("UNIQUE"));
        }
    }

    @Test
    public void tipoDeCategoriaInvalidoSeRechaza() {
        try {
            ejecutar(Sql.INSERTAR_CATEGORIA, "Otra", "OTRO");
            fail("Debía rechazar el tipo");
        } catch (SQLException esperado) {
            assertTrue(esperado.getMessage().toUpperCase().contains("CHECK"));
        }
    }

    @Test
    public void sumasPorTipoYPorFecha() throws SQLException {
        long salario = idCategoria("Salario");
        long comida = idCategoria("Alimentación");
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "INGRESO", 2500000.0, "2026-10-01", "Quincena", salario);
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 25000.0, "2026-10-05", "Almuerzo", comida);
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 75000.5, "2026-10-31", "Mercado", comida);
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 99999.0, "2026-11-01", "Otro mes", comida);

        assertEquals(2500000.0, numero(Sql.SUMA_POR_TIPO_ENTRE, "INGRESO", "2026-10-01", "2026-10-31"), 0.001);
        assertEquals(100000.5, numero(Sql.SUMA_POR_TIPO_ENTRE, "GASTO", "2026-10-01", "2026-10-31"), 0.001);
        assertEquals(100000.5, numero(Sql.SUMA_POR_CATEGORIA_ENTRE, comida, "2026-10-01", "2026-10-31"), 0.001);
        assertEquals(3, (int) numero(Sql.CONTAR_ENTRE, "2026-10-01", "2026-10-31"));
        assertEquals(0.0, numero(Sql.SUMA_POR_TIPO_ENTRE, "INGRESO", "2027-01-01", "2027-01-31"), 0.001);
    }

    @Test
    public void movimientosSeListanDelMasRecienteAlMasAntiguo() throws SQLException {
        long comida = idCategoria("Alimentación");
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 10.0, "2026-10-01", "a", comida);
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 20.0, "2026-10-09", "b", comida);
        try (PreparedStatement p = conexion.prepareStatement(Sql.MOVIMIENTOS_POR_TIPO)) {
            p.setString(1, "GASTO");
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
                assertEquals("2026-10-09", r.getString(4));
                assertEquals("Alimentación", r.getString(7));
                assertTrue(r.next());
                assertEquals("2026-10-01", r.getString(4));
                assertFalse(r.next());
            }
        }
    }

    @Test
    public void actualizarYEliminarMovimiento() throws SQLException {
        long comida = idCategoria("Alimentación");
        long transporte = idCategoria("Transporte");
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 10.0, "2026-10-01", "a", comida);
        long id = (long) numero("SELECT id FROM movimientos");
        assertEquals(1, ejecutar(Sql.ACTUALIZAR_MOVIMIENTO, 55.0, "2026-10-02", "bus", transporte, id));
        assertEquals(55.0, numero("SELECT monto FROM movimientos WHERE id = ?", id), 0.001);
        assertEquals(1, ejecutar(Sql.ELIMINAR_MOVIMIENTO, id));
        assertEquals(0, ejecutar(Sql.ELIMINAR_MOVIMIENTO, id));
    }

    @Test
    public void montoCeroOMenorSeRechaza() throws SQLException {
        long comida = idCategoria("Alimentación");
        try {
            ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 0.0, "2026-10-01", "x", comida);
            fail("Debía rechazar el monto");
        } catch (SQLException esperado) {
            assertTrue(esperado.getMessage().toUpperCase().contains("CHECK"));
        }
    }

    @Test
    public void categoriaConMovimientosNoSePuedeBorrarEnLaBase() throws SQLException {
        long comida = idCategoria("Alimentación");
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 10.0, "2026-10-01", "a", comida);
        assertEquals(1, (int) numero(Sql.CATEGORIA_CUENTA_MOVIMIENTOS, comida));
        try {
            ejecutar(Sql.ELIMINAR_CATEGORIA, comida);
            fail("La clave foránea debía impedir el borrado");
        } catch (SQLException esperado) {
            assertTrue(esperado.getMessage().toUpperCase().contains("FOREIGN KEY"));
        }
    }

    @Test
    public void presupuestoUnicoPorCategoriaYMes() throws SQLException {
        long comida = idCategoria("Alimentación");
        ejecutar(Sql.INSERTAR_PRESUPUESTO, comida, 10, 2026, 400000.0);
        assertEquals(1, (int) numero(Sql.PRESUPUESTO_EXISTE, comida, 10, 2026));
        assertEquals(0, (int) numero(Sql.PRESUPUESTO_EXISTE, comida, 11, 2026));
        try {
            ejecutar(Sql.INSERTAR_PRESUPUESTO, comida, 10, 2026, 1.0);
            fail("Debía rechazar el duplicado");
        } catch (SQLException esperado) {
            assertTrue(esperado.getMessage().toUpperCase().contains("UNIQUE"));
        }
    }

    @Test
    public void presupuestosDelMesIncluyenElNombreDeLaCategoria() throws SQLException {
        ejecutar(Sql.INSERTAR_PRESUPUESTO, idCategoria("Servicios"), 10, 2026, 200000.0);
        ejecutar(Sql.INSERTAR_PRESUPUESTO, idCategoria("Alimentación"), 10, 2026, 400000.0);
        ejecutar(Sql.INSERTAR_PRESUPUESTO, idCategoria("Transporte"), 11, 2026, 100000.0);
        try (PreparedStatement p = conexion.prepareStatement(Sql.PRESUPUESTOS_DEL_MES)) {
            p.setInt(1, 10);
            p.setInt(2, 2026);
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
                assertEquals("Alimentación", r.getString(3));
                assertEquals(400000.0, r.getDouble(6), 0.001);
                assertTrue(r.next());
                assertEquals("Servicios", r.getString(3));
                assertFalse(r.next());
            }
        }
    }

    @Test
    public void mesFueraDeRangoSeRechaza() throws SQLException {
        try {
            ejecutar(Sql.INSERTAR_PRESUPUESTO, idCategoria("Salud"), 13, 2026, 1000.0);
            fail("Debía rechazar el mes");
        } catch (SQLException esperado) {
            assertTrue(esperado.getMessage().toUpperCase().contains("CHECK"));
        }
    }

    @Test
    public void borrarCategoriaSinMovimientosBorraSusPresupuestos() throws SQLException {
        long salud = idCategoria("Salud");
        ejecutar(Sql.INSERTAR_PRESUPUESTO, salud, 10, 2026, 1000.0);
        ejecutar(Sql.ELIMINAR_CATEGORIA, salud);
        assertEquals(0, (int) numero("SELECT COUNT(*) FROM presupuestos"));
    }

    @Test
    public void recordatoriosPendientesPrimeroYSeAlternan() throws SQLException {
        ejecutar(Sql.INSERTAR_RECORDATORIO, "Pagar arriendo", "2026-10-05");
        ejecutar(Sql.INSERTAR_RECORDATORIO, "Pagar internet", "2026-10-20");
        long primero = (long) numero("SELECT id FROM recordatorios WHERE titulo = ?", "Pagar arriendo");

        assertEquals(2, (int) numero(Sql.RECORDATORIOS_PENDIENTES_HASTA, "2026-10-28"));
        assertEquals(1, (int) numero(Sql.RECORDATORIOS_PENDIENTES_HASTA, "2026-10-10"));

        assertEquals(1, ejecutar(Sql.ALTERNAR_RECORDATORIO, primero));
        assertEquals(1, (int) numero(Sql.RECORDATORIOS_PENDIENTES_HASTA, "2026-10-28"));
        try (PreparedStatement p = conexion.prepareStatement(Sql.RECORDATORIOS);
             ResultSet r = p.executeQuery()) {
            assertTrue(r.next());
            assertEquals("Pagar internet", r.getString(2));
            assertEquals(0, r.getInt(4));
            assertTrue(r.next());
            assertEquals(1, r.getInt(4));
        }

        assertEquals(1, ejecutar(Sql.ALTERNAR_RECORDATORIO, primero));
        assertEquals(2, (int) numero(Sql.RECORDATORIOS_PENDIENTES_HASTA, "2026-10-28"));
        assertEquals(1, ejecutar(Sql.ELIMINAR_RECORDATORIO, primero));
    }

    /** Android entrega los argumentos de rawQuery como texto: las comparaciones numéricas deben seguir funcionando. */
    @Test
    public void argumentosComoTextoSeComparanBienConColumnasNumericas() throws SQLException {
        long comida = idCategoria("Alimentación");
        ejecutar(Sql.INSERTAR_MOVIMIENTO, "GASTO", 40.0, "2026-10-05", "a", comida);
        ejecutar(Sql.INSERTAR_PRESUPUESTO, comida, 10, 2026, 100.0);
        String id = String.valueOf(comida);
        assertEquals(40.0, numero(Sql.SUMA_POR_CATEGORIA_ENTRE, id, "2026-10-01", "2026-10-31"), 0.001);
        assertEquals(1, (int) numero(Sql.PRESUPUESTO_EXISTE, id, "10", "2026"));
        assertEquals(1, (int) numero(Sql.CATEGORIA_CUENTA_MOVIMIENTOS, id));
        try (PreparedStatement p = conexion.prepareStatement(Sql.PRESUPUESTOS_DEL_MES)) {
            p.setString(1, "10");
            p.setString(2, "2026");
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
            }
        }
        try (PreparedStatement p = conexion.prepareStatement(Sql.MOVIMIENTO_POR_ID)) {
            p.setString(1, String.valueOf((long) numero("SELECT id FROM movimientos")));
            try (ResultSet r = p.executeQuery()) {
                assertTrue(r.next());
            }
        }
    }
}
