package com.mifinanzapp.service;

import com.mifinanzapp.dto.UsuarioRegistroDTO;
import com.mifinanzapp.exception.DuplicateResourceException;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link UsuarioService}.
 * Verifica el registro de usuarios y manejo de duplicados.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, categoriaRepository, passwordEncoder);
    }

    @Test
    void registrarShouldThrowExceptionWhenEmailExists() {
        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(true);

        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setEmail("test@test.com");
        dto.setNombre("Test");
        dto.setPassword("password123");

        assertThrows(DuplicateResourceException.class, () -> usuarioService.registrar(dto));
        verify(usuarioRepository, never()).save(any());
    }
}
