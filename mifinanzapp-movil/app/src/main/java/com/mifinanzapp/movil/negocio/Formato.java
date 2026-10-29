package com.mifinanzapp.movil.negocio;

import java.text.NumberFormat;
import java.util.Locale;

/** Formato de importes y porcentajes para mostrar en pantalla (pesos colombianos). */
public final class Formato {

    private static final Locale COLOMBIA = new Locale("es", "CO");

    private Formato() {
    }

    public static String moneda(double valor) {
        return NumberFormat.getCurrencyInstance(COLOMBIA).format(valor);
    }

    /** Porcentaje con un decimal, por ejemplo {@code 43,3 %}. */
    public static String porcentaje(double valor) {
        return String.format(COLOMBIA, "%.1f %%", valor);
    }

    /** Porcentaje redondeado, por ejemplo {@code 95 %}. */
    public static String porcentajeEntero(double valor) {
        return Math.round(valor) + " %";
    }
}
