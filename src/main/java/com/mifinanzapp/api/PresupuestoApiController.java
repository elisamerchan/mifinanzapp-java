package com.mifinanzapp.api;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.PresupuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio web CRUD de presupuestos mensuales por categoría de gasto.
 *
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/presupuestos")
@Tag(name = "Presupuestos", description = "Límites mensuales de gasto por categoría, con su progreso")
public class PresupuestoApiController {

    private final PresupuestoService presupuestoService;
    private final UsuarioActual usuarioActual;

    public PresupuestoApiController(PresupuestoService presupuestoService, UsuarioActual usuarioActual) {
        this.presupuestoService = presupuestoService;
        this.usuarioActual = usuarioActual;
    }

    @GetMapping
    @Operation(summary = "Lista los presupuestos con lo gastado, el porcentaje y si se excedieron")
    public List<Map<String, Object>> listar(Authentication auth) {
        return presupuestoService.listarConProgreso(usuarioActual.de(auth).getId()).stream()
                .map(this::aplanar).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un presupuesto por id")
    public Map<String, Object> obtener(@PathVariable Long id, Authentication auth) {
        Long usuarioId = usuarioActual.de(auth).getId();
        presupuestoService.buscarPorId(id, usuarioId); // 404 si no existe o es de otro usuario
        return presupuestoService.listarConProgreso(usuarioId).stream()
                .filter(p -> id.equals(p.get("id"))).findFirst().map(this::aplanar).orElseThrow();
    }

    @PostMapping
    @Operation(summary = "Crea un presupuesto")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody PresupuestoRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardar(null, request, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un presupuesto")
    public Map<String, Object> actualizar(@PathVariable Long id, @Valid @RequestBody PresupuestoRequest request, Authentication auth) {
        return guardar(id, request, auth);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un presupuesto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        presupuestoService.eliminar(id, usuarioActual.de(auth).getId());
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> guardar(Long id, PresupuestoRequest r, Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(id);
        presupuesto.setUsuario(usuario);
        Categoria categoria = new Categoria();
        categoria.setId(r.categoriaId());
        presupuesto.setCategoria(categoria);
        presupuesto.setMes(r.mes());
        presupuesto.setAnio(r.anio());
        presupuesto.setMontoLimite(r.montoLimite());
        return obtener(presupuestoService.guardar(presupuesto).getId(), auth);
    }

    /** Sustituye la entidad categoría por su id y nombre para devolver un JSON simple. */
    private Map<String, Object> aplanar(Map<String, Object> item) {
        Map<String, Object> salida = new LinkedHashMap<>(item);
        Categoria c = (Categoria) salida.remove("categoria");
        salida.put("categoriaId", c.getId());
        salida.put("categoriaNombre", c.getNombre());
        return salida;
    }
}
