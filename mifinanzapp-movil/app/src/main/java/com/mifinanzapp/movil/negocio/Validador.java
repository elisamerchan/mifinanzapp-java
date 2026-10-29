package com.mifinanzapp.movil.negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Validación de los datos escritos por el usuario. Cada método devuelve el mensaje de error
 * para mostrar, o {@code null} si el dato es válido.
 */
public final class Validador {

    public static final int MAX_DESCRIPCION = 255;
    public static final int MAX_NOMBRE = 100;
    public static final int MAX_TITULO = 200;
    /** Máximo de 10 dígitos enteros (columna NUMERIC(12,2) de la aplicación web). */
    private static final String PATRON_MONTO = "\\d{1,10}([.,]\\d{1,2})?";
    private static final String PATRON_FECHA = "\\d{4}-\\d{2}-\\d{2}";

    private Validador() {
    }

    public static String validarMonto(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            return "El monto es obligatorio";
        }
        if (!limpio.matches(PATRON_MONTO)) {
            return "Escribe un monto válido, sin separadores de miles (ej. 25000 o 25000,50)";
        }
        if (parsearMonto(limpio) <= 0) {
            return "El monto debe ser mayor que cero";
        }
        return null;
    }

    /** Convierte un monto ya validado; acepta coma o punto como separador decimal. */
    public static double parsearMonto(String texto) {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    public static String validarFecha(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            return "La fecha es obligatoria";
        }
        if (!limpio.matches(PATRON_FECHA)) {
            return "La fecha debe tener el formato AAAA-MM-DD";
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        formato.setLenient(false);
        try {
            formato.parse(limpio);
        } catch (ParseException e) {
            return "La fecha no existe en el calendario";
        }
        return null;
    }

    public static String validarTexto(String texto, String campo, int maximo, boolean obligatorio) {
        String limpio = texto == null ? "" : texto.trim();
        if (obligatorio && limpio.isEmpty()) {
            return "El campo " + campo + " es obligatorio";
        }
        if (limpio.length() > maximo) {
            return "El campo " + campo + " admite máximo " + maximo + " caracteres";
        }
        return null;
    }
}
