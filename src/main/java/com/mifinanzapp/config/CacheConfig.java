package com.mifinanzapp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración del sistema de caché con Caffeine.
 * <p>
 * Define un caché llamado {@code dashboard} con expiración de 2 minutos
 * y un máximo de 500 entradas. Se utiliza para cachear los datos del
 * dashboard y reducir consultas a la base de datos.
 *
 * @since 1.0.0
 */
@Configuration
public class CacheConfig {

    /**
     * Crea el administrador de caché con Caffeine.
     * <p>
     * El caché {@code dashboard} almacena los resultados de consultas
     * del dashboard por usuario (ingresos, gastos, balance, evolución, etc.)
     * con un TTL de 2 minutos para mantener datos actualizados sin
     * sobrecargar la base de datos.
     *
     * @return el administrador de caché configurado
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager("dashboard");
        cm.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .maximumSize(500));
        return cm;
    }
}
