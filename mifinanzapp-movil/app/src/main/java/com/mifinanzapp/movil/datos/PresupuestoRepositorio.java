package com.mifinanzapp.movil.datos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mifinanzapp.movil.modelo.Presupuesto;

import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de los presupuestos mensuales. */
public class PresupuestoRepositorio {

    private final BaseDatos base;

    public PresupuestoRepositorio(BaseDatos base) {
        this.base = base;
    }

    public List<Presupuesto> listarDelMes(int mes, int anio) {
        List<Presupuesto> resultado = new ArrayList<Presupuesto>();
        Cursor cursor = base.getReadableDatabase().rawQuery(Sql.PRESUPUESTOS_DEL_MES,
                new String[] {String.valueOf(mes), String.valueOf(anio)});
        try {
            while (cursor.moveToNext()) {
                resultado.add(new Presupuesto(cursor.getLong(0), cursor.getLong(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5)));
            }
        } finally {
            cursor.close();
        }
        return resultado;
    }

    /**
     * Crea un presupuesto.
     *
     * @throws IllegalArgumentException si la categoría ya tiene presupuesto ese mes
     */
    public long crear(long categoriaId, int mes, int anio, double montoLimite) {
        SQLiteDatabase db = base.getWritableDatabase();
        if (Ejecutor.cuenta(db, Sql.PRESUPUESTO_EXISTE, String.valueOf(categoriaId),
                String.valueOf(mes), String.valueOf(anio)) > 0) {
            throw new IllegalArgumentException("Esa categoría ya tiene presupuesto este mes");
        }
        return Ejecutor.insertar(db, Sql.INSERTAR_PRESUPUESTO, categoriaId, mes, anio, montoLimite);
    }

    public boolean eliminar(long id) {
        return Ejecutor.modificar(base.getWritableDatabase(), Sql.ELIMINAR_PRESUPUESTO, id) > 0;
    }
}
