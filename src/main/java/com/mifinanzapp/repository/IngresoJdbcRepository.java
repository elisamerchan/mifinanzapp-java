package com.mifinanzapp.repository;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Acceso JDBC explícito (SQL parametrizado con {@link JdbcTemplate}) a la tabla {@code ingresos}.
 * <p>
 * Todas las consultas filtran por {@code usuario_id}. Los errores de base de datos
 * se propagan como {@link org.springframework.dao.DataAccessException}.
 *
 * @since 1.0.0
 */
@Repository
public class IngresoJdbcRepository {

    private static final String SELECT = """
            SELECT i.id, i.monto, i.fecha, i.descripcion, i.usuario_id,
                   c.id AS categoria_id, c.nombre, c.tipo, c.icono
            FROM ingresos i JOIN categorias c ON c.id = i.categoria_id
            """;

    private static final RowMapper<Ingreso> MAPPER = (rs, n) -> {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("usuario_id"));
        Categoria categoria = new Categoria(rs.getString("nombre"),
                Categoria.TipoCategoria.valueOf(rs.getString("tipo")), rs.getString("icono"));
        categoria.setId(rs.getLong("categoria_id"));
        Ingreso ingreso = new Ingreso();
        ingreso.setId(rs.getLong("id"));
        ingreso.setUsuario(usuario);
        ingreso.setCategoria(categoria);
        ingreso.setMonto(rs.getBigDecimal("monto"));
        ingreso.setFecha(rs.getDate("fecha").toLocalDate());
        ingreso.setDescripcion(rs.getString("descripcion"));
        return ingreso;
    };

    private final JdbcTemplate jdbc;

    public IngresoJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Inserta el ingreso y devuelve el mismo objeto con el id generado. */
    public Ingreso crear(Ingreso ingreso) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            var ps = con.prepareStatement(
                    "INSERT INTO ingresos (usuario_id, categoria_id, monto, fecha, descripcion) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setLong(1, ingreso.getUsuario().getId());
            ps.setLong(2, ingreso.getCategoria().getId());
            ps.setBigDecimal(3, ingreso.getMonto());
            ps.setDate(4, Date.valueOf(ingreso.getFecha()));
            ps.setString(5, ingreso.getDescripcion());
            return ps;
        }, keys);
        ingreso.setId(keys.getKey().longValue());
        return ingreso;
    }

    /** Una página de ingresos del usuario, del más reciente al más antiguo. */
    public List<Ingreso> listarPorUsuario(Long usuarioId, int limite, long offset) {
        return jdbc.query(SELECT + " WHERE i.usuario_id = ? ORDER BY i.fecha DESC, i.id DESC LIMIT ? OFFSET ?",
                MAPPER, usuarioId, limite, offset);
    }

    public long contarPorUsuario(Long usuarioId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM ingresos WHERE usuario_id = ?", Long.class, usuarioId);
    }

    /** Ingresos del usuario entre dos fechas (inclusive), del más reciente al más antiguo. */
    public List<Ingreso> listarPorUsuarioEntre(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return jdbc.query(SELECT + " WHERE i.usuario_id = ? AND i.fecha BETWEEN ? AND ? ORDER BY i.fecha DESC, i.id DESC",
                MAPPER, usuarioId, Date.valueOf(desde), Date.valueOf(hasta));
    }

    public Optional<Ingreso> buscarPorIdYUsuario(Long id, Long usuarioId) {
        return jdbc.query(SELECT + " WHERE i.id = ? AND i.usuario_id = ?", MAPPER, id, usuarioId)
                .stream().findFirst();
    }

    /** @return {@code true} si existía un ingreso con ese id del usuario y se actualizó */
    public boolean actualizar(Ingreso ingreso) {
        return jdbc.update("UPDATE ingresos SET categoria_id = ?, monto = ?, fecha = ?, descripcion = ? WHERE id = ? AND usuario_id = ?",
                ingreso.getCategoria().getId(), ingreso.getMonto(), Date.valueOf(ingreso.getFecha()),
                ingreso.getDescripcion(), ingreso.getId(), ingreso.getUsuario().getId()) > 0;
    }

    /** @return {@code true} si existía un ingreso con ese id del usuario y se eliminó */
    public boolean eliminar(Long id, Long usuarioId) {
        return jdbc.update("DELETE FROM ingresos WHERE id = ? AND usuario_id = ?", id, usuarioId) > 0;
    }
}
