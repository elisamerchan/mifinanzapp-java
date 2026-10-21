package com.mifinanzapp.api;

import com.mifinanzapp.dto.IngresoDTO;
import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.mapper.IngresoMapper;
import com.mifinanzapp.service.IngresoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Servicio web CRUD de ingresos del usuario autenticado.
 *
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/ingresos")
@Tag(name = "Ingresos", description = "Registro, consulta, edición y eliminación de ingresos")
public class IngresoApiController {

    private final IngresoService ingresoService;
    private final UsuarioActual usuarioActual;

    public IngresoApiController(IngresoService ingresoService, UsuarioActual usuarioActual) {
        this.ingresoService = ingresoService;
        this.usuarioActual = usuarioActual;
    }

    @GetMapping
    @Operation(summary = "Lista los ingresos (paginados) o los de un rango de fechas")
    public Object listar(@RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(required = false) LocalDate desde,
                         @RequestParam(required = false) LocalDate hasta,
                         Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        if (desde != null && hasta != null) {
            return IngresoMapper.toDtoList(ingresoService.listarEntre(usuario.getId(), desde, hasta));
        }
        Page<Ingreso> page = ingresoService.listarPagina(usuario.getId(), pagina);
        return PaginaResponse.de(page, IngresoMapper.toDtoList(page.getContent()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un ingreso por id")
    public IngresoDTO obtener(@PathVariable Long id, Authentication auth) {
        return IngresoMapper.toDto(ingresoService.buscarPorId(id, usuarioActual.de(auth).getId()));
    }

    @PostMapping
    @Operation(summary = "Registra un ingreso")
    public ResponseEntity<IngresoDTO> crear(@Valid @RequestBody IngresoRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardar(null, request, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un ingreso")
    public IngresoDTO actualizar(@PathVariable Long id, @Valid @RequestBody IngresoRequest request, Authentication auth) {
        return guardar(id, request, auth);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un ingreso")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        ingresoService.eliminar(id, usuarioActual.de(auth).getId());
        return ResponseEntity.noContent().build();
    }

    private IngresoDTO guardar(Long id, IngresoRequest r, Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        Ingreso ingreso = new Ingreso();
        ingreso.setId(id);
        ingreso.setUsuario(usuario);
        Categoria categoria = new Categoria();
        categoria.setId(r.categoriaId());
        ingreso.setCategoria(categoria);
        ingreso.setMonto(r.monto());
        ingreso.setFecha(r.fecha());
        ingreso.setDescripcion(r.descripcion());
        Ingreso guardado = ingresoService.guardar(ingreso);
        return IngresoMapper.toDto(ingresoService.buscarPorId(guardado.getId(), usuario.getId()));
    }
}
