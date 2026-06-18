package com.mifinanzapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad que representa un presupuesto mensual por categoría.
 * <p>
 * Define un límite de gasto ({@code montoLimite}) para una categoría
 * específica en un mes y año determinados.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "presupuestos", indexes = {
    @Index(name = "idx_presupuesto_usuario_mes_anio", columnList = "usuario_id, mes, anio"),
    @Index(name = "idx_presupuesto_usuario_categoria_mes_anio", columnList = "usuario_id, categoria_id, mes, anio")
})
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 2)
    private Integer mes;

    @Column(nullable = false, length = 4)
    private Integer anio;

    @Column(name = "monto_limite", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoLimite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigDecimal getMontoLimite() {
        return montoLimite;
    }

    public void setMontoLimite(BigDecimal montoLimite) {
        this.montoLimite = montoLimite;
    }
}
