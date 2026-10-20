package com.mifinanzapp.servlets.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Categoría de movimientos (ingreso o gasto).
 * Es el modelo que el CRUD de servlets muestra, crea, actualiza y elimina.
 */
public class Categoria {

    /** Longitud máxima del nombre, igual que la columna de la base de datos. */
    public static final int LONGITUD_MAXIMA_NOMBRE = 60;

    /** Tipos permitidos. Coinciden con el CHECK de la tabla. */
    public static final String TIPO_INGRESO = "INGRESO";
    public static final String TIPO_GASTO = "GASTO";

    private Long id;
    private String nombre;
    private String tipo;

    /** Constructor vacío requerido para crear el objeto antes de rellenarlo desde el formulario. */
    public Categoria() {
    }

    public Categoria(Long id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    /**
     * Valida los datos de la categoría.
     *
     * @return lista de mensajes de error; vacía si los datos son válidos
     */
    public List<String> validar() {
        List<String> errores = new ArrayList<>();

        if (nombre == null || nombre.isBlank()) {
            errores.add("El nombre es obligatorio.");
        } else if (nombre.length() > LONGITUD_MAXIMA_NOMBRE) {
            errores.add("El nombre no puede tener más de " + LONGITUD_MAXIMA_NOMBRE + " caracteres.");
        }

        if (!TIPO_INGRESO.equals(tipo) && !TIPO_GASTO.equals(tipo)) {
            errores.add("El tipo debe ser INGRESO o GASTO.");
        }

        return errores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
