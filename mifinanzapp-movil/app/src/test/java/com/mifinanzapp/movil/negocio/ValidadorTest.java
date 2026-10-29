package com.mifinanzapp.movil.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ValidadorTest {

    @Test
    public void montoValidoNoTieneError() {
        assertNull(Validador.validarMonto("25000"));
        assertNull(Validador.validarMonto("25000.50"));
        assertNull(Validador.validarMonto("  1500,5 "));
    }

    @Test
    public void montoVacioEsObligatorio() {
        assertEquals("El monto es obligatorio", Validador.validarMonto(""));
        assertEquals("El monto es obligatorio", Validador.validarMonto(null));
    }

    @Test
    public void montoCeroNoEsValido() {
        assertEquals("El monto debe ser mayor que cero", Validador.validarMonto("0"));
    }

    @Test
    public void montoNegativoOConLetrasNoEsValido() {
        assertNotNull(Validador.validarMonto("-5"));
        assertNotNull(Validador.validarMonto("abc"));
        assertNotNull(Validador.validarMonto("1.500.000"));
        assertNotNull(Validador.validarMonto("12.345"));
    }

    @Test
    public void montoConMasDeDiezDigitosNoEsValido() {
        assertNotNull(Validador.validarMonto("12345678901"));
    }

    @Test
    public void parsearMontoAceptaComaDecimal() {
        assertEquals(1500.5, Validador.parsearMonto("1500,5"), 0.0001);
        assertEquals(25000.0, Validador.parsearMonto(" 25000 "), 0.0001);
    }

    @Test
    public void fechaValidaNoTieneError() {
        assertNull(Validador.validarFecha("2026-10-28"));
        assertNull(Validador.validarFecha("2028-02-29"));
    }

    @Test
    public void fechaConFormatoIncorrectoSeRechaza() {
        assertEquals("La fecha debe tener el formato AAAA-MM-DD", Validador.validarFecha("28/10/2026"));
        assertEquals("La fecha debe tener el formato AAAA-MM-DD", Validador.validarFecha("2026-1-5"));
    }

    @Test
    public void fechaInexistenteSeRechaza() {
        assertEquals("La fecha no existe en el calendario", Validador.validarFecha("2026-02-30"));
        assertEquals("La fecha no existe en el calendario", Validador.validarFecha("2027-02-29"));
        assertEquals("La fecha no existe en el calendario", Validador.validarFecha("2026-13-01"));
    }

    @Test
    public void fechaVaciaEsObligatoria() {
        assertEquals("La fecha es obligatoria", Validador.validarFecha(" "));
    }

    @Test
    public void textoObligatorioVacioSeRechaza() {
        assertEquals("El campo nombre es obligatorio", Validador.validarTexto("  ", "nombre", 100, true));
    }

    @Test
    public void textoOpcionalVacioEsValido() {
        assertNull(Validador.validarTexto("", "descripción", 255, false));
    }

    @Test
    public void textoMuyLargoSeRechaza() {
        StringBuilder largo = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            largo.append('a');
        }
        assertEquals("El campo nombre admite máximo 100 caracteres",
                Validador.validarTexto(largo.toString(), "nombre", 100, true));
    }
}
