package com.mifinanzapp.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del servicio web de registro e inicio de sesión con contexto completo sobre H2.
 * No envían token CSRF a propósito: la API no debe exigirlo.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthApiControllerTest {

    @Autowired private MockMvc mvc;

    private ResultActions enviar(String ruta, String json) throws Exception {
        return mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private void registrar(String usuario, String password) throws Exception {
        enviar("/api/auth/registro",
                "{\"usuario\":\"" + usuario + "\",\"password\":\"" + password + "\"}")
                .andExpect(status().isCreated());
    }

    @Test
    void registroCreaElUsuario() throws Exception {
        enviar("/api/auth/registro", "{\"usuario\":\"nuevo@test.com\",\"password\":\"secreto1\",\"nombre\":\"Nuevo\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.usuario").value("nuevo@test.com"));
    }

    @Test
    void registroDeUsuarioExistenteDevuelve409() throws Exception {
        registrar("repetido@test.com", "secreto1");
        enviar("/api/auth/registro", "{\"usuario\":\"repetido@test.com\",\"password\":\"secreto1\"}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void registroConDatosInvalidosDevuelve400() throws Exception {
        enviar("/api/auth/registro", "{\"usuario\":\"no-es-email\",\"password\":\"123\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasItem("La contraseña debe tener al menos 6 caracteres")));
    }

    @Test
    void loginCorrectoDevuelveAutenticacionSatisfactoria() throws Exception {
        registrar("ana@test.com", "secreto1");
        enviar("/api/auth/login", "{\"usuario\":\"ana@test.com\",\"password\":\"secreto1\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Autenticación satisfactoria"));
    }

    @Test
    void loginConContrasenaIncorrectaDevuelveError() throws Exception {
        registrar("beto@test.com", "secreto1");
        enviar("/api/auth/login", "{\"usuario\":\"beto@test.com\",\"password\":\"otra-clave\"}")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Error en la autenticación"));
    }

    @Test
    void loginConUsuarioInexistenteDevuelveElMismoError() throws Exception {
        enviar("/api/auth/login", "{\"usuario\":\"nadie@test.com\",\"password\":\"secreto1\"}")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Error en la autenticación"));
    }

    @Test
    void loginSinCamposDevuelve400() throws Exception {
        enviar("/api/auth/login", "{}").andExpect(status().isBadRequest());
    }

    @Test
    void loginConJsonMalFormadoDevuelve400() throws Exception {
        enviar("/api/auth/login", "esto no es json").andExpect(status().isBadRequest());
    }
}
