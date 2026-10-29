package com.mifinanzapp.movil.datos;

/**
 * Sentencias SQL de la base local (SQLite). Están separadas de las clases de Android para poder
 * probarlas con JUnit sobre SQLite en la JVM. Todas las consultas usan parámetros ({@code ?}).
 */
public final class Sql {

    public static final String NOMBRE_BD = "mifinanzapp.db";
    public static final int VERSION_BD = 1;

    public static final String[] CREAR = {
        "CREATE TABLE categorias ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nombre TEXT NOT NULL, "
            + "tipo TEXT NOT NULL CHECK (tipo IN ('INGRESO','GASTO')), "
            + "UNIQUE (nombre, tipo))",
        "CREATE TABLE movimientos ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "tipo TEXT NOT NULL CHECK (tipo IN ('INGRESO','GASTO')), "
            + "monto REAL NOT NULL CHECK (monto > 0), "
            + "fecha TEXT NOT NULL, "
            + "descripcion TEXT, "
            + "categoria_id INTEGER NOT NULL REFERENCES categorias(id))",
        "CREATE INDEX idx_movimientos_fecha ON movimientos (fecha)",
        "CREATE TABLE presupuestos ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "categoria_id INTEGER NOT NULL REFERENCES categorias(id) ON DELETE CASCADE, "
            + "mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12), "
            + "anio INTEGER NOT NULL, "
            + "monto_limite REAL NOT NULL CHECK (monto_limite > 0), "
            + "UNIQUE (categoria_id, mes, anio))",
        "CREATE TABLE recordatorios ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "titulo TEXT NOT NULL, "
            + "fecha TEXT NOT NULL, "
            + "completado INTEGER NOT NULL DEFAULT 0)"
    };

    /** Categorías que se crean al instalar la aplicación (igual que al registrar un usuario en la web). */
    public static final String[][] CATEGORIAS_INICIALES = {
        {"Salario", "INGRESO"}, {"Freelance", "INGRESO"}, {"Otros ingresos", "INGRESO"},
        {"Alimentación", "GASTO"}, {"Transporte", "GASTO"}, {"Vivienda", "GASTO"}, {"Servicios", "GASTO"},
        {"Entretenimiento", "GASTO"}, {"Salud", "GASTO"}, {"Educación", "GASTO"}
    };

    // Categorías
    public static final String CATEGORIAS_TODAS =
            "SELECT id, nombre, tipo FROM categorias ORDER BY tipo, nombre";
    public static final String CATEGORIAS_POR_TIPO =
            "SELECT id, nombre, tipo FROM categorias WHERE tipo = ? ORDER BY nombre";
    public static final String CATEGORIA_EXISTE =
            "SELECT COUNT(*) FROM categorias WHERE LOWER(nombre) = LOWER(?) AND tipo = ?";
    public static final String CATEGORIA_CUENTA_MOVIMIENTOS =
            "SELECT COUNT(*) FROM movimientos WHERE categoria_id = ?";
    public static final String INSERTAR_CATEGORIA = "INSERT INTO categorias (nombre, tipo) VALUES (?, ?)";
    public static final String ELIMINAR_CATEGORIA = "DELETE FROM categorias WHERE id = ?";

    // Movimientos (ingresos y gastos)
    private static final String SELECCION_MOVIMIENTO =
            "SELECT m.id, m.tipo, m.monto, m.fecha, m.descripcion, m.categoria_id, c.nombre "
            + "FROM movimientos m JOIN categorias c ON c.id = m.categoria_id ";
    public static final String MOVIMIENTO_POR_ID = SELECCION_MOVIMIENTO + "WHERE m.id = ?";
    public static final String MOVIMIENTOS_POR_TIPO =
            SELECCION_MOVIMIENTO + "WHERE m.tipo = ? ORDER BY m.fecha DESC, m.id DESC";
    public static final String INSERTAR_MOVIMIENTO =
            "INSERT INTO movimientos (tipo, monto, fecha, descripcion, categoria_id) VALUES (?, ?, ?, ?, ?)";
    public static final String ACTUALIZAR_MOVIMIENTO =
            "UPDATE movimientos SET monto = ?, fecha = ?, descripcion = ?, categoria_id = ? WHERE id = ?";
    public static final String ELIMINAR_MOVIMIENTO = "DELETE FROM movimientos WHERE id = ?";
    public static final String SUMA_POR_TIPO_ENTRE =
            "SELECT COALESCE(SUM(monto), 0) FROM movimientos WHERE tipo = ? AND fecha BETWEEN ? AND ?";
    public static final String SUMA_POR_CATEGORIA_ENTRE =
            "SELECT COALESCE(SUM(monto), 0) FROM movimientos WHERE categoria_id = ? AND fecha BETWEEN ? AND ?";
    public static final String CONTAR_ENTRE =
            "SELECT COUNT(*) FROM movimientos WHERE fecha BETWEEN ? AND ?";

    // Presupuestos
    public static final String PRESUPUESTOS_DEL_MES =
            "SELECT p.id, p.categoria_id, c.nombre, p.mes, p.anio, p.monto_limite "
            + "FROM presupuestos p JOIN categorias c ON c.id = p.categoria_id "
            + "WHERE p.mes = ? AND p.anio = ? ORDER BY c.nombre";
    public static final String PRESUPUESTO_EXISTE =
            "SELECT COUNT(*) FROM presupuestos WHERE categoria_id = ? AND mes = ? AND anio = ?";
    public static final String INSERTAR_PRESUPUESTO =
            "INSERT INTO presupuestos (categoria_id, mes, anio, monto_limite) VALUES (?, ?, ?, ?)";
    public static final String ELIMINAR_PRESUPUESTO = "DELETE FROM presupuestos WHERE id = ?";

    // Recordatorios
    public static final String RECORDATORIOS =
            "SELECT id, titulo, fecha, completado FROM recordatorios ORDER BY completado, fecha, id";
    public static final String RECORDATORIOS_PENDIENTES_HASTA =
            "SELECT COUNT(*) FROM recordatorios WHERE completado = 0 AND fecha <= ?";
    public static final String INSERTAR_RECORDATORIO =
            "INSERT INTO recordatorios (titulo, fecha, completado) VALUES (?, ?, 0)";
    public static final String ALTERNAR_RECORDATORIO =
            "UPDATE recordatorios SET completado = 1 - completado WHERE id = ?";
    public static final String ELIMINAR_RECORDATORIO = "DELETE FROM recordatorios WHERE id = ?";

    private Sql() {
    }
}
