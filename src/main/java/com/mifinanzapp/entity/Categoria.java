package com.mifinanzapp.entity;

import jakarta.persistence.*;

/**
 * Entidad que representa una categoría de ingresos o gastos.
 * <p>
 * Cada categoría pertenece a un usuario y tiene un tipo
 * ({@link TipoCategoria#INGRESO} o {@link TipoCategoria#GASTO})
 * y un icono emoji asociado.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    /**
     * Tipo de categoría: ingreso o gasto.
     */
    public enum TipoCategoria {
        INGRESO, GASTO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCategoria tipo;

    @Column(length = 10)
    private String icono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Categoria() {}

    public Categoria(String nombre, TipoCategoria tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public Categoria(String nombre, TipoCategoria tipo, String icono) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.icono = icono;
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

    public TipoCategoria getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
