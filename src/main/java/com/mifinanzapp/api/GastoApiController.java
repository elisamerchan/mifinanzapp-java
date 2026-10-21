package com.mifinanzapp.api;

import com.mifinanzapp.dto.GastoDTO;
import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.mapper.GastoMapper;
import com.mifinanzapp.service.GastoService;
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
 * Servicio web CRUD de gastos del usuario autenticado.
 *
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/gastos")
@Tag(name = "Gastos", description = "Registro, consulta, edición y eliminación de gastos")
public class GastoApiController {

    private final GastoService gastoService;
    private final UsuarioActual usuarioActual;

    public GastoApiController(GastoService gastoService, UsuarioActual usuarioActual) {
        this.gastoService = gastoService;
        this.usuarioActual = usuarioActual;
    }

    @GetMapping
    @Operation(summary = "Lista los gastos (paginados) o los de un rango de fechas")
    public Object listar(@RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(required = false) LocalDate desde,
                         @RequestParam(required = false) LocalDate hasta,
                         Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        if (desde != null && hasta != null) {
            return GastoMapper.toDtoList(gastoService.listarEntre(usuario.getId(), desde, hasta));
        }
        Page<Gasto> page = gastoService.listarPagina(usuario.getId(), pagina);
        return PaginaResponse.de(page, GastoMapper.toDtoList(page.getContent()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un gasto por id")
    public GastoDTO obtener(@PathVariable Long id, Authentication auth) {
        return GastoMapper.toDto(gastoService.buscarPorId(id, usuarioActual.de(auth).getId()));
    }

    @PostMapping
    @Operation(summary = "Registra un gasto")
    public ResponseEntity<GastoDTO> crear(@Valid @RequestBody GastoRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardar(null, request, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un gasto")
    public GastoDTO actualizar(@PathVariable Long id, @Valid @RequestBody GastoRequest request, Authentication auth) {
        return guardar(id, request, auth);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un gasto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        gastoService.eliminar(id, usuarioActual.de(auth).getId());
        return ResponseEntity.noContent().build();
    }

    private GastoDTO guardar(Long id, GastoRequest r, Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        Gasto gasto = new Gasto();
        gasto.setId(id);
        gasto.setUsuario(usuario);
        Categoria categoria = new Categoria();
        categoria.setId(r.categoriaId());
        gasto.setCategoria(categoria);
        gasto.setMonto(r.monto());
        gasto.setFecha(r.fecha());
        gasto.setDescripcion(r.descripcion());
        gasto.setMetodoPago(r.metodoPago());
        Gasto guardado = gastoService.guardar(gasto);
        return GastoMapper.toDto(gastoService.buscarPorId(guardado.getId(), usuario.getId()));
    }
}
