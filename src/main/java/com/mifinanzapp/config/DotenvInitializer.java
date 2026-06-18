package com.mifinanzapp.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inicializador del contexto de Spring que carga variables de entorno
 * desde un archivo {@code .env}.
 * <p>
 * Se ejecuta antes de que Spring cree los beans, permitiendo que
 * las propiedades como {@code DB_URL}, {@code DB_USER} y
 * {@code DB_PASSWORD} estén disponibles desde el inicio.
 *
 * @since 1.0.0
 */
public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(DotenvInitializer.class);

    /**
     * Carga las variables del archivo {@code .env} al sistema de propiedades.
     * <p>
     * Ignora valores placeholder (que contengan {@code <} o {@code >})
     * para permitir el uso de archivos {@code .env.example} durante
     * desarrollo sin causar errores.
     *
     * @param context el contexto de la aplicación Spring
     */
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            String value = entry.getValue();
            if (!value.contains("<") && !value.contains(">")) {
                System.setProperty(entry.getKey(), entry.getValue());
                log.debug("loaded_env_var key={}", entry.getKey());
            }
        });
    }
}
