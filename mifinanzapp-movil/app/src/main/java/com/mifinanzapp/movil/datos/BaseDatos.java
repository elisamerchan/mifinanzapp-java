package com.mifinanzapp.movil.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Base de datos SQLite local de la aplicación (una sola instancia compartida). */
public class BaseDatos extends SQLiteOpenHelper {

    private static BaseDatos instancia;

    public static synchronized BaseDatos obtener(Context contexto) {
        if (instancia == null) {
            instancia = new BaseDatos(contexto.getApplicationContext());
        }
        return instancia;
    }

    private BaseDatos(Context contexto) {
        super(contexto, Sql.NOMBRE_BD, null, Sql.VERSION_BD);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sentencia : Sql.CREAR) {
            db.execSQL(sentencia);
        }
        for (String[] categoria : Sql.CATEGORIAS_INICIALES) {
            Ejecutor.insertar(db, Sql.INSERTAR_CATEGORIA, categoria[0], categoria[1]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        // La versión 1 no tiene migraciones: aquí irán los ALTER TABLE de versiones futuras.
    }
}
