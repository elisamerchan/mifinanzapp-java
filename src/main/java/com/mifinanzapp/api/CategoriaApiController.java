package com.mifinanzapp.api;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio web CRUD de categorías del usuario autenticado.
 *
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Categorías de ingresos y gastos")
public class CategoriaApiController {

    private final CategoriaService categoriaService;
    private final UsuarioActual usuarioActual;

    public CategoriaApiController(CategoriaService categoriaService, UsuarioActual usuarioActual) {
        this.categoriaService = categoriaService;
        this.usuarioActual = usuarioActual;
    }

    @GetMapping
    @Operation(summary = "Lista las categorías, opcionalmente filtradas por tipo (INGRESO o GASTO)")
    public List<CategoriaResponse> listar(@RequestParam(required = false) Categoria.TipoCategoria tipo, Authentication auth) {
        Long usuarioId = usuarioActual.de(auth).getId();
        List<Categoria> categorias = tipo == null
                ? categoriaService.listarPorUsuario(usuarioId)
                : categoriaService.listarPorTipo(usuarioId, tipo);
        return categorias.stream().map(CategoriaResponse::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta una categoría por id")
    public CategoriaResponse obtener(@PathVariable Long id, Authentication auth) {
        return CategoriaResponse.de(categoriaService.buscarPorId(id, usuarioActual.de(auth).getId()));
    }

    @PostMapping
    @Operation(summary = "Crea una categoría")
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardar(null, request, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una categoría")
    public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request, Authentication auth) {
        return guardar(id, request, auth);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una categoría sin movimientos asociados")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        categoriaService.eliminar(id, usuarioActual.de(auth).getId());
        return ResponseEntity.noContent().build();
    }

    private CategoriaResponse guardar(Long id, CategoriaRequest r, Authentication auth) {
        Usuario usuario = usuarioActual.de(auth);
        Categoria categoria = new Categoria(r.nombre(), r.tipo(), r.icono());
        categoria.setId(id);
        categoria.setUsuario(usuario);
        return CategoriaResponse.de(categoriaService.guardar(categoria));
    }
}
