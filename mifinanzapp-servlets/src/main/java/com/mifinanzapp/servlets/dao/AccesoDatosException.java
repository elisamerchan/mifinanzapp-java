package com.mifinanzapp.servlets.dao;

/**
 * Error de acceso a datos. Envuelve las {@code SQLException} para que las capas superiores
 * no dependan de JDBC y el contenedor responda con un error 500 controlado.
 */
public class AccesoDatosException extends RuntimeException {

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
