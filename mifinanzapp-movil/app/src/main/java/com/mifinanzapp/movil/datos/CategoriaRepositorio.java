package com.mifinanzapp.movil.datos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mifinanzapp.movil.modelo.Categoria;

import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de las categorías. */
public class CategoriaRepositorio {

    private final BaseDatos base;

    public CategoriaRepositorio(BaseDatos base) {
        this.base = base;
    }

    public List<Categoria> listar() {
        return leer(Sql.CATEGORIAS_TODAS);
    }

    public List<Categoria> listarPorTipo(String tipo) {
        return leer(Sql.CATEGORIAS_POR_TIPO, tipo);
    }

    /**
     * Crea una categoría.
     *
     * @throws IllegalArgumentException si ya existe una del mismo tipo con ese nombre
     */
    public long crear(String nombre, String tipo) {
        SQLiteDatabase db = base.getWritableDatabase();
        if (Ejecutor.cuenta(db, Sql.CATEGORIA_EXISTE, nombre, tipo) > 0) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        return Ejecutor.insertar(db, Sql.INSERTAR_CATEGORIA, nombre, tipo);
    }

    /**
     * Elimina una categoría (y sus presupuestos).
     *
     * @throws IllegalStateException si la categoría tiene movimientos
     */
    public void eliminar(long id) {
        SQLiteDatabase db = base.getWritableDatabase();
        if (Ejecutor.cuenta(db, Sql.CATEGORIA_CUENTA_MOVIMIENTOS, String.valueOf(id)) > 0) {
            throw new IllegalStateException("No se puede eliminar: la categoría tiene movimientos");
        }
        Ejecutor.modificar(db, Sql.ELIMINAR_CATEGORIA, id);
    }

    private List<Categoria> leer(String sql, String... argumentos) {
        List<Categoria> resultado = new ArrayList<Categoria>();
        Cursor cursor = base.getReadableDatabase().rawQuery(sql, argumentos);
        try {
            while (cursor.moveToNext()) {
                resultado.add(new Categoria(cursor.getLong(0), cursor.getString(1), cursor.getString(2)));
            }
        } finally {
            cursor.close();
        }
        return resultado;
    }
}
