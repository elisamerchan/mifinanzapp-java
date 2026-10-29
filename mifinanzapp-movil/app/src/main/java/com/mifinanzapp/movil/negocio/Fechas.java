package com.mifinanzapp.movil.negocio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/** Utilidades de fechas en formato AAAA-MM-DD (compatible con Android 7.0, sin java.time). */
public final class Fechas {

    private static final String[] MESES = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
            "agosto", "septiembre", "octubre", "noviembre", "diciembre"};

    private Fechas() {
    }

    public static String hoy() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static int mesActual() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int anioActual() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String inicioMes(int mes, int anio) {
        return String.format(Locale.US, "%04d-%02d-01", anio, mes);
    }

    public static String finMes(int mes, int anio) {
        int ultimo = new GregorianCalendar(anio, mes - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d-%02d-%02d", anio, mes, ultimo);
    }

    public static String nombreMes(int mes) {
        return MESES[mes - 1];
    }
}
