package com.mifinanzapp.movil.modelo;

/** Límite de gasto mensual para una categoría. */
public class Presupuesto {

    private final long id;
    private final long categoriaId;
    private final String categoriaNombre;
    private final int mes;
    private final int anio;
    private final double montoLimite;

    public Presupuesto(long id, long categoriaId, String categoriaNombre, int mes, int anio, double montoLimite) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.mes = mes;
        this.anio = anio;
        this.montoLimite = montoLimite;
    }

    public long getId() {
        return id;
    }

    public long getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public int getMes() {
        return mes;
    }

    public int getAnio() {
        return anio;
    }

    public double getMontoLimite() {
        return montoLimite;
    }
}
