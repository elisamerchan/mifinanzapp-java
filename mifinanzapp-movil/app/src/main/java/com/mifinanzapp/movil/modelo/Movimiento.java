package com.mifinanzapp.movil.modelo;

/** Ingreso o gasto registrado por el usuario. La fecha usa el formato AAAA-MM-DD. */
public class Movimiento {

    private final long id;
    private final String tipo;
    private final double monto;
    private final String fecha;
    private final String descripcion;
    private final long categoriaId;
    private final String categoriaNombre;

    public Movimiento(long id, String tipo, double monto, String fecha, String descripcion,
                      long categoriaId, String categoriaNombre) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
    }

    public long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public long getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }
}
