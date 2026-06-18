package com.mifinanzapp.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utilidades para el manejo de fechas.
 * <p>
 * Proporciona métodos comunes para calcular rangos de fechas,
 * obtener el primer/último día del mes, etc.
 *
 * @since 1.0.0
 */
public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    /**
     * Obtiene el primer día del mes actual.
     *
     * @return primer día del mes en curso
     */
    public static LocalDate inicioMesActual() {
        return LocalDate.now().withDayOfMonth(1);
    }

    /**
     * Obtiene el último día del mes actual.
     *
     * @return último día del mes en curso
     */
    public static LocalDate finMesActual() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(now.lengthOfMonth());
    }

    /**
     * Obtiene el primer día de un mes y año específicos.
     *
     * @param mes  número del mes (1-12)
     * @param anio año
     * @return primer día del mes
     */
    public static LocalDate inicioMes(int mes, int anio) {
        return LocalDate.of(anio, mes, 1);
    }

    /**
     * Obtiene el último día de un mes y año específicos.
     *
     * @param mes  número del mes (1-12)
     * @param anio año
     * @return último día del mes
     */
    public static LocalDate finMes(int mes, int anio) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        return inicio.withDayOfMonth(inicio.lengthOfMonth());
    }

    /**
     * Calcula el número de días transcurridos entre dos fechas, inclusive.
     *
     * @param inicio fecha de inicio
     * @param fin    fecha de fin
     * @return número de días
     */
    public static long diasTranscurridos(LocalDate inicio, LocalDate fin) {
        return ChronoUnit.DAYS.between(inicio, fin) + 1;
    }
}
