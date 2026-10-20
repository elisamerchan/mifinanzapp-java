package com.mifinanzapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;

/**
 * Manejador global de excepciones para controladores MVC.
 * <p>
 * Captura las excepciones lanzadas por los controladores y servicios,
 * registra el error y retorna una vista de error apropiada. Los errores de negocio
 * (400 y 409) de un envío de formulario POST vuelven a la página de origen con un
 * mensaje flash {@code error} en lugar de mostrar una página de error.
 *
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja recursos no encontrados (404).
     *
     * @param ex excepción lanzada
     * @param model modelo de la vista
     * @return nombre de la plantilla de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("resource_not_found message={}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 404);
        return "error/404";
    }

    /**
     * Maneja recursos duplicados (409).
     *
     * @param ex excepción lanzada
     * @param model modelo de la vista
     * @return nombre de la plantilla de error
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResource(DuplicateResourceException ex, Model model,
                                          HttpServletRequest request, HttpServletResponse response) {
        log.warn("duplicate_resource message={}", ex.getMessage());
        String volver = volverConError(request, ex.getMessage());
        if (volver != null) return volver;
        response.setStatus(HttpStatus.CONFLICT.value());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 409);
        return "error/409";
    }

    /**
     * Maneja errores de validación y argumentos ilegales (400).
     *
     * @param ex excepción lanzada
     * @param model modelo de la vista
     * @return nombre de la plantilla de error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, Model model,
                                   HttpServletRequest request, HttpServletResponse response) {
        log.warn("bad_request message={}", ex.getMessage());
        String volver = volverConError(request, ex.getMessage());
        if (volver != null) return volver;
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 400);
        return "error/400";
    }

    /**
     * Maneja cualquier otra excepción no capturada (500).
     *
     * @param ex excepción lanzada
     * @param model modelo de la vista
     * @return nombre de la plantilla de error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralError(Exception ex, Model model) {
        log.error("unexpected_error message={}", ex.getMessage(), ex);
        model.addAttribute("error", "Ha ocurrido un error inesperado. Intenta nuevamente.");
        model.addAttribute("status", 500);
        return "error/500";
    }

    /**
     * Si la petición es un POST con Referer, prepara un redirect a la misma ruta (solo el path,
     * para no permitir redirecciones a otros dominios) con el mensaje como flash {@code error}.
     *
     * @return la vista {@code redirect:...}, o {@code null} si no aplica
     */
    private String volverConError(HttpServletRequest request, String mensaje) {
        String referer = request.getHeader("Referer");
        if (!"POST".equals(request.getMethod()) || referer == null) return null;
        try {
            String path = URI.create(referer).getPath();
            if (path == null || !path.startsWith("/") || path.startsWith("//")) return null;
            RequestContextUtils.getOutputFlashMap(request).put("error", mensaje);
            return "redirect:" + path;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
