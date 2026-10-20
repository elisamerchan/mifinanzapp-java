package com.mifinanzapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa un gasto registrado por un usuario.
 * <p>
 * Cada gasto pertenece a un usuario y una categoría, e incluye
 * el monto, la fecha, una descripción y el método de pago utilizado.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "gastos", indexes = {
    @Index(name = "idx_gasto_usuario_fecha", columnList = "usuario_id, fecha"),
    @Index(name = "idx_gasto_usuario_categoria_fecha", columnList = "usuario_id, categoria_id, fecha")
})
public class Gasto {

    /**
     * Métodos de pago disponibles para registrar un gasto.
     */
    public enum MetodoPago {
        EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA, NEQUI, DAVIPLATA, OTRO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor que cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @Size(max = 255, message = "La descripción admite máximo 255 caracteres")
    @Column(length = 255)
    private String descripcion;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
}
