package com.mifinanzapp.movil.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FormatoTest {

    @Test
    public void monedaUsaPuntoComoSeparadorDeMiles() {
        String texto = Formato.moneda(650000);
        assertTrue(texto, texto.contains("650.000"));
        assertTrue(texto, texto.contains("$"));
    }

    @Test
    public void porcentajeTieneUnDecimalConComa() {
        assertEquals("43,3 %", Formato.porcentaje(43.333));
    }

    @Test
    public void porcentajeEnteroRedondea() {
        assertEquals("95 %", Formato.porcentajeEntero(94.6));
        assertEquals("100 %", Formato.porcentajeEntero(100));
    }
}
