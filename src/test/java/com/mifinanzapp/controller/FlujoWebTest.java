package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.UsuarioRepository;
import com.mifinanzapp.service.IngresoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del flujo web con contexto completo sobre H2 (ver {@code src/test/resources/application.properties}).
 * Cada prueba se revierte al terminar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlujoWebTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private IngresoService ingresoService;

    private Usuario ana;
    private Usuario beto;
    private Categoria catAna;
    private Categoria catBeto;

    @BeforeEach
    void datos() {
        ana = usuario("ana@test.com");
        beto = usuario("beto@test.com");
        catAna = categoria(ana, "Salario");
        catBeto = categoria(beto, "Freelance");
    }

    private Usuario usuario(String email) {
        Usuario u = new Usuario();
        u.setNombre(email);
        u.setEmail(email);
        u.setPassword("x");
        return usuarioRepository.save(u);
    }

    private Categoria categoria(Usuario u, String nombre) {
        Categoria c = new Categoria(nombre, Categoria.TipoCategoria.INGRESO);
        c.setUsuario(u);
        return categoriaRepository.save(c);
    }

    private Ingreso ingresoDe(Usuario u, Categoria c) {
        Ingreso i = new Ingreso();
        i.setUsuario(u);
        i.setCategoria(c);
        i.setMonto(new BigDecimal("50"));
        i.setFecha(LocalDate.now());
        return ingresoService.guardar(i);
    }

    @Test
    void sinSesionRedirigeAlLogin() throws Exception {
        mvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void postSinTokenCsrfEsRechazado() throws Exception {
        mvc.perform(post("/ingresos/eliminar/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void eliminarIngresoAjenoDevuelve404() throws Exception {
        Ingreso deBeto = ingresoDe(beto, catBeto);

        mvc.perform(post("/ingresos/eliminar/" + deBeto.getId()).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void editarIngresoAjenoDevuelve404() throws Exception {
        Ingreso deBeto = ingresoDe(beto, catBeto);

        mvc.perform(get("/ingresos/editar/" + deBeto.getId())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void formularioInvalidoVuelveAMostrarseConErrores() throws Exception {
        mvc.perform(post("/ingresos/guardar").with(csrf())
                        .param("monto", "-5")
                        .param("fecha", LocalDate.now().toString())
                        .param("categoria.id", catAna.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("ingresos/index"))
                .andExpect(model().attributeHasFieldErrors("ingreso", "monto"));
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void crearIngresoValidoRedirigeConMensaje() throws Exception {
        mvc.perform(post("/ingresos/guardar").with(csrf())
                        .param("monto", "120.50")
                        .param("fecha", LocalDate.now().toString())
                        .param("categoria.id", catAna.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingresos"))
                .andExpect(flash().attribute("mensaje", "Ingreso registrado"));
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void categoriaDeOtroUsuarioEsRechazada() throws Exception {
        mvc.perform(post("/ingresos/guardar").with(csrf())
                        .param("monto", "10")
                        .param("fecha", LocalDate.now().toString())
                        .param("categoria.id", catBeto.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void paginasPrincipalesCargan() throws Exception {
        for (String ruta : new String[]{"/dashboard", "/ingresos", "/gastos", "/historial", "/categorias",
                "/presupuestos", "/recordatorios", "/reportes", "/educacion", "/educacion/regla-50-30-20",
                "/movimientos/nuevo", "/configuracion"}) {
            mvc.perform(get(ruta)).andExpect(status().isOk());
        }
    }

    @Test
    @WithMockUser(username = "ana@test.com")
    void articuloInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/educacion/../../etc")).andExpect(status().is4xxClientError());
        mvc.perform(get("/educacion/no-existe")).andExpect(status().isNotFound());
    }
}
