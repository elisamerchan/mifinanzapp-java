package com.mifinanzapp.movil.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FechasTest {

    @Test
    public void inicioYFinDeMes() {
        assertEquals("2026-10-01", Fechas.inicioMes(10, 2026));
        assertEquals("2026-10-31", Fechas.finMes(10, 2026));
        assertEquals("2026-04-30", Fechas.finMes(4, 2026));
    }

    @Test
    public void febreroEnAnioBisiesto() {
        assertEquals("2028-02-29", Fechas.finMes(2, 2028));
        assertEquals("2027-02-28", Fechas.finMes(2, 2027));
    }

    @Test
    public void nombresDeMes() {
        assertEquals("enero", Fechas.nombreMes(1));
        assertEquals("diciembre", Fechas.nombreMes(12));
    }

    @Test
    public void hoyTieneFormatoValido() {
        assertNull(Validador.validarFecha(Fechas.hoy()));
    }

    @Test
    public void mesYAnioActualEstanEnRango() {
        assertTrue(Fechas.mesActual() >= 1 && Fechas.mesActual() <= 12);
        assertTrue(Fechas.anioActual() >= 2026);
    }
}
