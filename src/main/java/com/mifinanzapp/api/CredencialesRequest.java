package com.mifinanzapp.api;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo JSON del inicio de sesión: usuario (email) y contraseña.
 *
 * @param usuario  email con el que se registró la cuenta
 * @param password contraseña en texto plano (en producción debe viajar por HTTPS)
 * @since 1.1.0
 */
public record CredencialesRequest(
        @NotBlank(message = "El usuario es obligatorio") String usuario,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
