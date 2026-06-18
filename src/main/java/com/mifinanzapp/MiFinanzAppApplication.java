package com.mifinanzapp;

import com.mifinanzapp.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Punto de entrada de la aplicación Mi FinanzApp.
 * <p>
 * Aplicación Spring Boot para la gestión de finanzas personales.
 * Inicializa el contexto de Spring, carga las variables de entorno
 * desde el archivo {@code .env} y habilita la caché con Caffeine.
 *
 * @since 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class MiFinanzAppApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MiFinanzAppApplication.class);
        app.addInitializers(new DotenvInitializer());
        app.run(args);
    }
}
