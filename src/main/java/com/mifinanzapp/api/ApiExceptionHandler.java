package com.mifinanzapp.api;

import com.mifinanzapp.exception.DuplicateResourceException;
import com.mifinanzapp.exception.ResourceNotFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * Convierte las excepciones de los servicios web en respuestas JSON.
 * <p>
 * Tiene máxima precedencia y se limita al paquete {@code api}, para no alterar las vistas de error
 * que devuelve {@code GlobalExceptionHandler} a la aplicación web.
 *
 * @since 1.1.0
 */
@RestControllerAdvice(basePackages = "com.mifinanzapp.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    /** Validación fallida (campos vacíos, email inválido, contraseña corta): 400. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos inválidos", "errores", errores));
    }

    /** Cuerpo ausente o JSON mal formado: 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", "El cuerpo debe ser un JSON válido"));
    }

    /** Recurso inexistente o de otro usuario: 404. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
    }

    /** Regla de negocio incumplida (categoría de otro tipo, categoría con movimientos...): 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleReglaNegocio(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
    }

    /** Parámetro de ruta o de consulta con formato inválido (id no numérico, fecha mal escrita): 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", "Parámetro inválido: " + ex.getName()));
    }

    /** Usuario ya registrado: 409. */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
    }
}
