package com.mifinanzapp.movil.ui;

/** Datos de una fila de lista: título, detalle y, opcionalmente, una barra de progreso. */
public class Fila {

    /** Valor de {@link #progreso} cuando la fila no muestra barra. */
    public static final int SIN_PROGRESO = -1;

    public final long id;
    public final String titulo;
    public final String detalle;
    public final int progreso;
    public final int colorProgreso;

    public Fila(long id, String titulo, String detalle) {
        this(id, titulo, detalle, SIN_PROGRESO, 0);
    }

    public Fila(long id, String titulo, String detalle, int progreso, int colorProgreso) {
        this.id = id;
        this.titulo = titulo;
        this.detalle = detalle;
        this.progreso = progreso;
        this.colorProgreso = colorProgreso;
    }
}
