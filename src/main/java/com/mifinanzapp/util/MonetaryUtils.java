package com.mifinanzapp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Utilidades para el formateo y cálculos monetarios.
 * <p>
 * Proporciona métodos para formatear montos con separadores de miles
 * y decimales, y operaciones comunes con BigDecimal.
 *
 * @since 1.0.0
 */
public final class MonetaryUtils {

    private static final DecimalFormat FORMATO_MONTO = new DecimalFormat("#,##0.00");

    private MonetaryUtils() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    /**
     * Formatea un monto BigDecimal a string con separadores de miles y 2 decimales.
     *
     * @param monto el monto a formatear
     * @return string formateado (ej. "1.234,56")
     */
    public static String formatear(BigDecimal monto) {
        if (monto == null) return "0.00";
        return FORMATO_MONTO.format(monto);
    }

    /**
     * Calcula el porcentaje que representa una parte respecto a un total.
     *
     * @param parte  valor parcial
     * @param total  valor total
     * @param escala número de decimales
     * @return porcentaje calculado, 0 si el total es 0
     */
    public static BigDecimal calcularPorcentaje(BigDecimal parte, BigDecimal total, int escala) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        if (parte == null) return BigDecimal.ZERO;
        return parte.multiply(BigDecimal.valueOf(100))
                .divide(total, escala, RoundingMode.HALF_UP);
    }
}
