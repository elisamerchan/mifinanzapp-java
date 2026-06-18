package com.mifinanzapp.mapper;

import com.mifinanzapp.dto.GastoDTO;
import com.mifinanzapp.entity.Gasto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapeador entre la entidad {@link Gasto} y su DTO {@link GastoDTO}.
 * <p>
 * Convierte objetos de negocio a objetos de transferencia de datos
 * para evitar exponer las entidades JPA directamente en la capa de presentación.
 *
 * @since 1.0.0
 */
public class GastoMapper {

    /**
     * Convierte una entidad {@link Gasto} a un DTO {@link GastoDTO}.
     *
     * @param gasto la entidad a convertir
     * @return el DTO con los datos del gasto
     */
    public static GastoDTO toDto(Gasto gasto) {
        if (gasto == null) return null;

        GastoDTO dto = new GastoDTO();
        dto.setId(gasto.getId());
        dto.setMonto(gasto.getMonto());
        dto.setFecha(gasto.getFecha());
        dto.setDescripcion(gasto.getDescripcion());
        dto.setMetodoPago(gasto.getMetodoPago());
        dto.setCategoriaId(gasto.getCategoria() != null ? gasto.getCategoria().getId() : null);
        dto.setCategoriaNombre(gasto.getCategoria() != null ? gasto.getCategoria().getNombre() : null);
        dto.setCategoriaIcono(gasto.getCategoria() != null ? gasto.getCategoria().getIcono() : null);
        return dto;
    }

    /**
     * Convierte una lista de entidades {@link Gasto} a una lista de DTOs.
     *
     * @param gastos lista de entidades a convertir
     * @return lista de DTOs
     */
    public static List<GastoDTO> toDtoList(List<Gasto> gastos) {
        return gastos.stream()
                .map(GastoMapper::toDto)
                .collect(Collectors.toList());
    }
}
