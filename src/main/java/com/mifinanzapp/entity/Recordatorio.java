package com.mifinanzapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Entidad que representa un recordatorio financiero.
 * <p>
 * Los recordatorios permiten al usuario establecer alertas
 * para fechas importantes relacionadas con sus finanzas,
 * como pagos de facturas o vencimientos.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "recordatorios")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título admite máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Size(max = 500, message = "La descripción admite máximo 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean completado;

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
