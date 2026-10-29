package com.mifinanzapp.movil.negocio;

/**
 * Reglas de cálculo del resumen y de los presupuestos. No depende de Android,
 * por lo que se prueba con JUnit en la JVM.
 */
public final class CalculadoraFinanzas {

    /** Porcentaje del presupuesto desde el que se avisa (HU-08). */
    public static final double UMBRAL_ADVERTENCIA = 80.0;
    /** Porcentaje del presupuesto en el que el límite se considera superado. */
    public static final double UMBRAL_EXCEDIDO = 100.0;

    /** Estado de un presupuesto según lo gastado. */
    public enum Nivel { NORMAL, ADVERTENCIA, EXCEDIDO }

    private CalculadoraFinanzas() {
    }

    public static double balance(double ingresos, double gastos) {
        return ingresos - gastos;
    }

    /** Porcentaje de los ingresos que no se gastó; 0 si no hay ingresos. */
    public static double porcentajeAhorro(double ingresos, double gastos) {
        if (ingresos <= 0) {
            return 0;
        }
        return (ingresos - gastos) * 100.0 / ingresos;
    }

    /** Porcentaje del límite que ya se gastó; 0 si el límite no es válido. */
    public static double porcentajeUso(double gastado, double limite) {
        if (limite <= 0) {
            return 0;
        }
        return gastado * 100.0 / limite;
    }

    public static Nivel nivel(double porcentajeUso) {
        if (porcentajeUso >= UMBRAL_EXCEDIDO) {
            return Nivel.EXCEDIDO;
        }
        if (porcentajeUso >= UMBRAL_ADVERTENCIA) {
            return Nivel.ADVERTENCIA;
        }
        return Nivel.NORMAL;
    }
}
