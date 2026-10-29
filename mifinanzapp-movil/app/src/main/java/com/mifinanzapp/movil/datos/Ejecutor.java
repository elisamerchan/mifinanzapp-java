package com.mifinanzapp.movil.datos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/** Ejecuta sentencias parametrizadas; ningún valor del usuario se concatena en el SQL. */
final class Ejecutor {

    private Ejecutor() {
    }

    /** Inserta una fila y devuelve su id. */
    static long insertar(SQLiteDatabase db, String sql, Object... argumentos) {
        SQLiteStatement sentencia = db.compileStatement(sql);
        try {
            enlazar(sentencia, argumentos);
            return sentencia.executeInsert();
        } finally {
            sentencia.close();
        }
    }

    /** Ejecuta un UPDATE o DELETE y devuelve cuántas filas cambiaron. */
    static int modificar(SQLiteDatabase db, String sql, Object... argumentos) {
        SQLiteStatement sentencia = db.compileStatement(sql);
        try {
            enlazar(sentencia, argumentos);
            return sentencia.executeUpdateDelete();
        } finally {
            sentencia.close();
        }
    }

    /** Devuelve la primera columna de la primera fila como número (0 si no hay filas). */
    static double numero(SQLiteDatabase db, String sql, String... argumentos) {
        Cursor cursor = db.rawQuery(sql, argumentos);
        try {
            return cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        } finally {
            cursor.close();
        }
    }

    static int cuenta(SQLiteDatabase db, String sql, String... argumentos) {
        return (int) numero(db, sql, argumentos);
    }

    private static void enlazar(SQLiteStatement sentencia, Object[] argumentos) {
        for (int i = 0; i < argumentos.length; i++) {
            Object valor = argumentos[i];
            int posicion = i + 1;
            if (valor == null) {
                sentencia.bindNull(posicion);
            } else if (valor instanceof Long || valor instanceof Integer) {
                sentencia.bindLong(posicion, ((Number) valor).longValue());
            } else if (valor instanceof Double) {
                sentencia.bindDouble(posicion, (Double) valor);
            } else {
                sentencia.bindString(posicion, valor.toString());
            }
        }
    }
}
