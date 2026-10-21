package com.mifinanzapp.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo JSON del registro: usuario (email), contraseña y nombre opcional.
 *
 * @param usuario  email que se usará como nombre de usuario
 * @param password contraseña de al menos 6 caracteres
 * @param nombre   nombre para mostrar; si falta se usa la parte del email antes de la arroba
 * @since 1.1.0
 */
public record RegistroRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Email(message = "El usuario debe ser un email válido") String usuario,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password,
        String nombre) {
}
