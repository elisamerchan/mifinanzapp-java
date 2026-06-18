package com.mifinanzapp.dto;

import com.mifinanzapp.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la transferencia de datos de un gasto.
 * <p>
 * Contiene la información de un gasto incluyendo los datos de la categoría
 * asociada, sin exponer la entidad JPA directamente.
 *
 * @since 1.0.0
 */
public class GastoDTO {

    private Long id;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;
    private Gasto.MetodoPago metodoPago;
    private Long categoriaId;
    private String categoriaNombre;
    private String categoriaIcono;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Gasto.MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(Gasto.MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public String getCategoriaIcono() { return categoriaIcono; }
    public void setCategoriaIcono(String categoriaIcono) { this.categoriaIcono = categoriaIcono; }
}
