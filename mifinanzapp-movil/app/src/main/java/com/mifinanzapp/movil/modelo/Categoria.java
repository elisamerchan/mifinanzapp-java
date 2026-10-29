package com.mifinanzapp.movil.modelo;

/** Categoría de ingresos o de gastos (misma regla que la aplicación web). */
public class Categoria {

    public static final String INGRESO = "INGRESO";
    public static final String GASTO = "GASTO";

    private final long id;
    private final String nombre;
    private final String tipo;

    public Categoria(long id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /** @return {@link #INGRESO} o {@link #GASTO} */
    public String getTipo() {
        return tipo;
    }
}
