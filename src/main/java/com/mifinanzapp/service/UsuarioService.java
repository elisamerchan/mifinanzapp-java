package com.mifinanzapp.service;

import com.mifinanzapp.dto.UsuarioRegistroDTO;
import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.exception.DuplicateResourceException;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Servicio de negocio para la gestión de usuarios.
 * <p>
 * Proporciona registro de nuevos usuarios con creación automática
 * de categorías predeterminadas, búsqueda por email y
 * actualización de perfil.
 *
 * @since 1.0.0
 */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(UsuarioRegistroDTO dto) {
        log.info("registrar_usuario email={}", dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Usuario", "email", dto.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario = usuarioRepository.save(usuario);

        crearCategoriasPredeterminadas(usuario);

        log.info("usuario_registrado id={} email={}", usuario.getId(), usuario.getEmail());
        return usuario;
    }

    private void crearCategoriasPredeterminadas(Usuario usuario) {
        List<Categoria> gastos = Arrays.asList(
                new Categoria("Alimentación", Categoria.TipoCategoria.GASTO, "🍔"),
                new Categoria("Transporte", Categoria.TipoCategoria.GASTO, "🚗"),
                new Categoria("Salud", Categoria.TipoCategoria.GASTO, "💊"),
                new Categoria("Educación", Categoria.TipoCategoria.GASTO, "📚"),
                new Categoria("Servicios", Categoria.TipoCategoria.GASTO, "💡"),
                new Categoria("Entretenimiento", Categoria.TipoCategoria.GASTO, "🎬")
        );

        List<Categoria> ingresos = Arrays.asList(
                new Categoria("Salario", Categoria.TipoCategoria.INGRESO, "💰"),
                new Categoria("Freelance", Categoria.TipoCategoria.INGRESO, "💻"),
                new Categoria("Ventas", Categoria.TipoCategoria.INGRESO, "📦"),
                new Categoria("Bonificaciones", Categoria.TipoCategoria.INGRESO, "🎁")
        );

        List<Categoria> todas = new java.util.ArrayList<>();
        todas.addAll(gastos);
        todas.addAll(ingresos);

        for (Categoria cat : todas) {
            cat.setUsuario(usuario);
            categoriaRepository.save(cat);
        }
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
    }

    @Transactional
    public Usuario actualizar(Usuario usuario) {
        log.info("actualizar_usuario id={}", usuario.getId());
        return usuarioRepository.save(usuario);
    }
}
