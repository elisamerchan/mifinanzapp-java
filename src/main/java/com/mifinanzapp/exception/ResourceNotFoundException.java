package com.mifinanzapp.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 * <p>
 * Utilizada por los servicios cuando una búsqueda por ID o email no encuentra
 * resultados. El {@link GlobalExceptionHandler} la captura y retorna un error 404.
 *
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea una nueva excepción con un mensaje descriptivo.
     *
     * @param resourceName nombre del recurso no encontrado (ej. "Usuario", "Gasto")
     * @param fieldName    nombre del campo por el que se buscó (ej. "id", "email")
     * @param fieldValue   valor del campo que no se encontró
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
    }

    /**
     * Crea una nueva excepción con un mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
