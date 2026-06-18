package com.mifinanzapp.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 * <p>
 * Por ejemplo, al registrar un usuario con un email que ya está en uso.
 * El {@link GlobalExceptionHandler} la captura y retorna un error 409.
 *
 * @since 1.0.0
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Crea una nueva excepción con un mensaje descriptivo.
     *
     * @param resourceName nombre del recurso duplicado
     * @param fieldName    nombre del campo que causa la duplicación
     * @param fieldValue   valor duplicado
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s ya existe con %s: '%s'", resourceName, fieldName, fieldValue));
    }

    /**
     * Crea una nueva excepción con un mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
