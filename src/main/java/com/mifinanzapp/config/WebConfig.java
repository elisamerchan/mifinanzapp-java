package com.mifinanzapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de recursos estáticos de la aplicación.
 * <p>
 * Mapea las rutas URL de recursos estáticos (CSS, JS, vendor, imágenes)
 * a sus ubicaciones en el classpath bajo {@code /static/}.
 *
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registra los manejadores de recursos estáticos.
     * <p>
     * Las rutas disponibles son:
     * <ul>
     *   <li>{@code /css/**} → classpath:/static/css/</li>
     *   <li>{@code /js/**} → classpath:/static/js/</li>
     *   <li>{@code /vendor/**} → classpath:/static/vendor/</li>
     *   <li>{@code /img/**} → classpath:/static/img/</li>
     * </ul>
     *
     * @param registry el registro de manejadores de recursos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/vendor/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)); // librerías versionadas que casi nunca cambian
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
    }
}
