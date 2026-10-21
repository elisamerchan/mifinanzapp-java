package com.mifinanzapp.api;

import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.RecordatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio web de recordatorios de pago del usuario autenticado.
 *
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/recordatorios")
@Tag(name = "Recordatorios", description = "Recordatorios de pagos pendientes")
public class RecordatorioApiController {

    private final RecordatorioService recordatorioService;
    private final UsuarioActual usuarioActual;

    public RecordatorioApiController(RecordatorioService recordatorioService, UsuarioActual usuarioActual) {
        this.recordatorioService = recordatorioService;
        this.usuarioActual = usuarioActual;
    }

    @GetMapping
    @Operation(summary = "Lista los recordatorios")
    public List<RecordatorioResponse> listar(Authentication auth) {
        return recordatorioService.listarPorUsuario(usuarioActual.de(auth).getId()).stream()
                .map(RecordatorioResponse::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un recordatorio por id")
    public RecordatorioResponse obtener(@PathVariable Long id, Authentication auth) {
        return RecordatorioResponse.de(recordatorioService.buscarPorId(id, usuarioActual.de(auth).getId()));
    }

    @PostMapping
    @Operation(summary = "Crea un recordatorio")
    public ResponseEntity<RecordatorioResponse> crear(@Valid @RequestBody RecordatorioRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardar(null, request, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un recordatorio")
    public RecordatorioResponse actualizar(@PathVariable Long id, @Valid @RequestBody RecordatorioRequest request, Authentication auth) {
        return guardar(id, request, auth);
    }

    @PatchMapping("/{id}/completar")
    @Operation(summary = "Alterna el estado completado de un recordatorio")
    public RecordatorioResponse completar(@PathVariable Long id, Authentication auth) {
        Long usuarioId = usuarioActual.de(auth).getId();
        recordatorioService.alternarCompletado(id, usuarioId);
        return RecordatorioResponse.de(recordatorioService.buscarPorId(id, usuarioId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un recordatorio")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        recordatorioService.eliminar(id, usuarioActual.de(auth).getId());
        return ResponseEntity.noContent().build();
    }

    private RecordatorioResponse guardar(Long id, RecordatorioRequest r, Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        Recordatorio recordatorio = id == null ? new Recordatorio() : recordatorioService.buscarPorId(id, usuario.getId());
        recordatorio.setUsuario(usuario);
        recordatorio.setTitulo(r.titulo());
        recordatorio.setDescripcion(r.descripcion());
        recordatorio.setFecha(r.fecha());
        return RecordatorioResponse.de(recordatorioService.guardar(recordatorio));
    }
}
