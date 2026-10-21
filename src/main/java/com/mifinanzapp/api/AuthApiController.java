package com.mifinanzapp.api;

import com.mifinanzapp.dto.UsuarioRegistroDTO;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Servicio web de registro e inicio de sesión.
 * <p>
 * Recibe usuario y contraseña en JSON. Si la autenticación es correcta responde
 * {@code 200} con un mensaje de autenticación satisfactoria; en caso contrario
 * responde {@code 401} con un mensaje de error en la autenticación.
 * Las rutas son públicas (ver {@code SecurityConfig}).
 *
 * @since 1.1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UsuarioService usuarioService;

    public AuthApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registra un usuario nuevo (y sus categorías por defecto).
     *
     * @param request usuario (email), contraseña y nombre opcional
     * @return {@code 201} con el usuario creado; {@code 409} si el usuario ya existe
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody RegistroRequest request) {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setEmail(request.usuario());
        dto.setPassword(request.password());
        dto.setConfirmarPassword(request.password());
        dto.setNombre(request.nombre() == null || request.nombre().isBlank()
                ? request.usuario().substring(0, request.usuario().indexOf('@'))
                : request.nombre());

        Usuario usuario = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Usuario registrado correctamente",
                "usuario", usuario.getEmail()));
    }

    /**
     * Verifica las credenciales recibidas.
     *
     * @param request usuario (email) y contraseña
     * @return {@code 200} con mensaje de autenticación satisfactoria, o {@code 401} con
     *         error en la autenticación (el mensaje no distingue usuario inexistente de contraseña
     *         incorrecta, para no revelar qué cuentas existen)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> iniciarSesion(@Valid @RequestBody CredencialesRequest request) {
        if (usuarioService.autenticar(request.usuario(), request.password())) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Autenticación satisfactoria",
                    "usuario", request.usuario()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "mensaje", "Error en la autenticación"));
    }
}
