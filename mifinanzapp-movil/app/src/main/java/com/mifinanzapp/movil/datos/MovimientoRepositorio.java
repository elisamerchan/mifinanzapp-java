package com.mifinanzapp.movil.datos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mifinanzapp.movil.modelo.Movimiento;

import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de ingresos y gastos (tabla {@code movimientos}). */
public class MovimientoRepositorio {

    private final BaseDatos base;

    public MovimientoRepositorio(BaseDatos base) {
        this.base = base;
    }

    public long crear(Movimiento movimiento) {
        return Ejecutor.insertar(base.getWritableDatabase(), Sql.INSERTAR_MOVIMIENTO,
                movimiento.getTipo(), movimiento.getMonto(), movimiento.getFecha(),
                movimiento.getDescripcion(), movimiento.getCategoriaId());
    }

    /** @return {@code true} si el movimiento existía y se actualizó */
    public boolean actualizar(Movimiento movimiento) {
        return Ejecutor.modificar(base.getWritableDatabase(), Sql.ACTUALIZAR_MOVIMIENTO,
                movimiento.getMonto(), movimiento.getFecha(), movimiento.getDescripcion(),
                movimiento.getCategoriaId(), movimiento.getId()) > 0;
    }

    public boolean eliminar(long id) {
        return Ejecutor.modificar(base.getWritableDatabase(), Sql.ELIMINAR_MOVIMIENTO, id) > 0;
    }

    /** @return el movimiento, o {@code null} si no existe */
    public Movimiento buscar(long id) {
        List<Movimiento> encontrados = leer(Sql.MOVIMIENTO_POR_ID, String.valueOf(id));
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    public List<Movimiento> listarPorTipo(String tipo) {
        return leer(Sql.MOVIMIENTOS_POR_TIPO, tipo);
    }

    /** Suma de los movimientos de un tipo entre dos fechas (AAAA-MM-DD, inclusive). */
    public double sumaPorTipoEntre(String tipo, String desde, String hasta) {
        return Ejecutor.numero(base.getReadableDatabase(), Sql.SUMA_POR_TIPO_ENTRE, tipo, desde, hasta);
    }

    /** Suma de los movimientos de una categoría entre dos fechas. */
    public double sumaPorCategoriaEntre(long categoriaId, String desde, String hasta) {
        return Ejecutor.numero(base.getReadableDatabase(), Sql.SUMA_POR_CATEGORIA_ENTRE,
                String.valueOf(categoriaId), desde, hasta);
    }

    public int contarEntre(String desde, String hasta) {
        return Ejecutor.cuenta(base.getReadableDatabase(), Sql.CONTAR_ENTRE, desde, hasta);
    }

    private List<Movimiento> leer(String sql, String... argumentos) {
        List<Movimiento> resultado = new ArrayList<Movimiento>();
        SQLiteDatabase db = base.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, argumentos);
        try {
            while (cursor.moveToNext()) {
                resultado.add(new Movimiento(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2),
                        cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getString(6)));
            }
        } finally {
            cursor.close();
        }
        return resultado;
    }
}
