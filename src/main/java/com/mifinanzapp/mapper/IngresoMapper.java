package com.mifinanzapp.mapper;

import com.mifinanzapp.dto.IngresoDTO;
import com.mifinanzapp.entity.Ingreso;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapeador entre la entidad {@link Ingreso} y su DTO {@link IngresoDTO}.
 * <p>
 * Convierte objetos de negocio a objetos de transferencia de datos
 * para evitar exponer las entidades JPA directamente en la capa de presentación.
 *
 * @since 1.0.0
 */
public class IngresoMapper {

    /**
     * Convierte una entidad {@link Ingreso} a un DTO {@link IngresoDTO}.
     *
     * @param ingreso la entidad a convertir
     * @return el DTO con los datos del ingreso
     */
    public static IngresoDTO toDto(Ingreso ingreso) {
        if (ingreso == null) return null;

        IngresoDTO dto = new IngresoDTO();
        dto.setId(ingreso.getId());
        dto.setMonto(ingreso.getMonto());
        dto.setFecha(ingreso.getFecha());
        dto.setDescripcion(ingreso.getDescripcion());
        dto.setCategoriaId(ingreso.getCategoria() != null ? ingreso.getCategoria().getId() : null);
        dto.setCategoriaNombre(ingreso.getCategoria() != null ? ingreso.getCategoria().getNombre() : null);
        dto.setCategoriaIcono(ingreso.getCategoria() != null ? ingreso.getCategoria().getIcono() : null);
        return dto;
    }

    /**
     * Convierte una lista de entidades {@link Ingreso} a una lista de DTOs.
     *
     * @param ingresos lista de entidades a convertir
     * @return lista de DTOs
     */
    public static List<IngresoDTO> toDtoList(List<Ingreso> ingresos) {
        return ingresos.stream()
                .map(IngresoMapper::toDto)
                .collect(Collectors.toList());
    }
}
