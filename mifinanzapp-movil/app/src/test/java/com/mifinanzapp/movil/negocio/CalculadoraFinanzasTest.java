package com.mifinanzapp.movil.negocio;

import static org.junit.Assert.assertEquals;

import com.mifinanzapp.movil.negocio.CalculadoraFinanzas.Nivel;

import org.junit.Test;

public class CalculadoraFinanzasTest {

    @Test
    public void balanceEsIngresosMenosGastos() {
        assertEquals(700000.0, CalculadoraFinanzas.balance(2500000, 1800000), 0.001);
    }

    @Test
    public void balanceNegativoCuandoSeGastaMasDeLoQueEntra() {
        assertEquals(-50.0, CalculadoraFinanzas.balance(100, 150), 0.001);
    }

    @Test
    public void porcentajeAhorroCorrecto() {
        assertEquals(28.0, CalculadoraFinanzas.porcentajeAhorro(2500000, 1800000), 0.001);
    }

    @Test
    public void porcentajeAhorroEsCeroSinIngresos() {
        assertEquals(0.0, CalculadoraFinanzas.porcentajeAhorro(0, 500), 0.001);
    }

    @Test
    public void porcentajeUsoCorrecto() {
        assertEquals(85.0, CalculadoraFinanzas.porcentajeUso(85000, 100000), 0.001);
    }

    @Test
    public void porcentajeUsoEsCeroConLimiteInvalido() {
        assertEquals(0.0, CalculadoraFinanzas.porcentajeUso(500, 0), 0.001);
    }

    @Test
    public void nivelNormalPorDebajoDel80() {
        assertEquals(Nivel.NORMAL, CalculadoraFinanzas.nivel(79.9));
    }

    @Test
    public void nivelAdvertenciaDesdeElLimiteDel80() {
        assertEquals(Nivel.ADVERTENCIA, CalculadoraFinanzas.nivel(80.0));
        assertEquals(Nivel.ADVERTENCIA, CalculadoraFinanzas.nivel(99.9));
    }

    @Test
    public void nivelExcedidoDesdeElCien() {
        assertEquals(Nivel.EXCEDIDO, CalculadoraFinanzas.nivel(100.0));
        assertEquals(Nivel.EXCEDIDO, CalculadoraFinanzas.nivel(250.0));
    }
}
