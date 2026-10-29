package com.mifinanzapp.movil.modelo;

/** Recordatorio de un pago o tarea financiera. */
public class Recordatorio {

    private final long id;
    private final String titulo;
    private final String fecha;
    private final boolean completado;

    public Recordatorio(long id, String titulo, String fecha, boolean completado) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.completado = completado;
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isCompletado() {
        return completado;
    }
}
