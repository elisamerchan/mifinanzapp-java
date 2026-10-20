package com.mifinanzapp.servlets.dao;

import com.mifinanzapp.servlets.modelo.Categoria;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a la tabla {@code categorias} mediante JDBC explícito.
 * Todas las consultas usan {@link PreparedStatement} con parámetros, nunca concatenación.
 */
public class CategoriaDao {

    private static final String SQL_LISTAR =
            "SELECT id, nombre, tipo FROM categorias ORDER BY tipo, nombre";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id, nombre, tipo FROM categorias WHERE id = ?";
    private static final String SQL_INSERTAR =
            "INSERT INTO categorias (nombre, tipo) VALUES (?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE categorias SET nombre = ?, tipo = ? WHERE id = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM categorias WHERE id = ?";

    private final DataSource dataSource;

    public CategoriaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Devuelve todas las categorías, ordenadas por tipo y nombre. */
    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                categorias.add(mapear(resultado));
            }
            return categorias;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron listar las categorías", e);
        }
    }

    /** Busca una categoría por su identificador. */
    public Optional<Categoria> buscarPorId(long id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar la categoría " + id, e);
        }
    }

    /**
     * Inserta la categoría si no tiene id, o actualiza la existente si lo tiene.
     *
     * @return la categoría con el id asignado por la base de datos
     */
    public Categoria guardar(Categoria categoria) {
        return categoria.getId() == null ? insertar(categoria) : actualizar(categoria);
    }

    /**
     * Elimina una categoría por id.
     *
     * @return {@code true} si existía y se eliminó
     */
    public boolean eliminar(long id) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setLong(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo eliminar la categoría " + id, e);
        }
    }

    private Categoria insertar(Categoria categoria) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, categoria.getNombre());
            sentencia.setString(2, categoria.getTipo());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    categoria.setId(claves.getLong(1));
                }
            }
            return categoria;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo insertar la categoría", e);
        }
    }

    private Categoria actualizar(Categoria categoria) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, categoria.getNombre());
            sentencia.setString(2, categoria.getTipo());
            sentencia.setLong(3, categoria.getId());
            sentencia.executeUpdate();
            return categoria;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar la categoría " + categoria.getId(), e);
        }
    }

    private static Categoria mapear(ResultSet resultado) throws SQLException {
        return new Categoria(
                resultado.getLong("id"),
                resultado.getString("nombre"),
                resultado.getString("tipo"));
    }
}
