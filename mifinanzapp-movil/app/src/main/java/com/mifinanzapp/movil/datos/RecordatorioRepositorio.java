package com.mifinanzapp.movil.datos;

import android.database.Cursor;

import com.mifinanzapp.movil.modelo.Recordatorio;

import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de los recordatorios. */
public class RecordatorioRepositorio {

    private final BaseDatos base;

    public RecordatorioRepositorio(BaseDatos base) {
        this.base = base;
    }

    /** Lista primero los pendientes, ordenados por fecha. */
    public List<Recordatorio> listar() {
        List<Recordatorio> resultado = new ArrayList<Recordatorio>();
        Cursor cursor = base.getReadableDatabase().rawQuery(Sql.RECORDATORIOS, new String[0]);
        try {
            while (cursor.moveToNext()) {
                resultado.add(new Recordatorio(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3) == 1));
            }
        } finally {
            cursor.close();
        }
        return resultado;
    }

    public long crear(String titulo, String fecha) {
        return Ejecutor.insertar(base.getWritableDatabase(), Sql.INSERTAR_RECORDATORIO, titulo, fecha);
    }

    /** Cambia entre pendiente y hecho. */
    public void alternar(long id) {
        Ejecutor.modificar(base.getWritableDatabase(), Sql.ALTERNAR_RECORDATORIO, id);
    }

    public boolean eliminar(long id) {
        return Ejecutor.modificar(base.getWritableDatabase(), Sql.ELIMINAR_RECORDATORIO, id) > 0;
    }

    /** Cantidad de recordatorios sin completar cuya fecha ya llegó. */
    public int contarPendientesHasta(String fecha) {
        return Ejecutor.cuenta(base.getReadableDatabase(), Sql.RECORDATORIOS_PENDIENTES_HASTA, fecha);
    }
}
