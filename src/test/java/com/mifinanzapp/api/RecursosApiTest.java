package com.mifinanzapp.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de los servicios web protegidos (gastos, ingresos, categorías, presupuestos, recordatorios
 * y resumen) con contexto completo sobre H2. No envían token CSRF: la API no debe exigirlo.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecursosApiTest {

    private static final String EMAIL = "api@test.com";
    private static final String CLAVE = "secreto1";

    @Autowired private MockMvc mvc;

    @BeforeEach
    void registrarUsuario() throws Exception {
        registrar(EMAIL);
    }

    private void registrar(String email) throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuario\":\"" + email + "\",\"password\":\"" + CLAVE + "\"}"))
                .andExpect(status().isCreated());
    }

    private MockHttpServletRequestBuilder como(MockHttpServletRequestBuilder req, String email) {
        return req.with(httpBasic(email, CLAVE)).contentType(MediaType.APPLICATION_JSON);
    }

    private MvcResult ejecutar(MockHttpServletRequestBuilder req, String json) throws Exception {
        return mvc.perform(como(req, EMAIL).content(json)).andReturn();
    }

    private long idCategoria(String tipo) throws Exception {
        String cuerpo = mvc.perform(como(get("/api/categorias").param("tipo", tipo), EMAIL))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(cuerpo, "$[0].id")).longValue();
    }

    private long idDe(MvcResult resultado) throws Exception {
        return ((Number) JsonPath.read(resultado.getResponse().getContentAsString(), "$.id")).longValue();
    }

    // ---------- Seguridad ----------

    @Test
    void sinCredencialesDevuelve401() throws Exception {
        mvc.perform(get("/api/gastos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Error en la autenticación"));
    }

    @Test
    void conContrasenaIncorrectaDevuelve401() throws Exception {
        mvc.perform(get("/api/gastos").with(httpBasic(EMAIL, "otra-clave")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void documentacionOpenApiEsPublicaYListaLosServicios() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/gastos']").exists())
                .andExpect(jsonPath("$.paths['/api/presupuestos/{id}']").exists());
    }

    // ---------- Categorías ----------

    @Test
    void categoriaCrudCompleto() throws Exception {
        MvcResult creada = ejecutar(post("/api/categorias"), "{\"nombre\":\"Mascotas\",\"tipo\":\"GASTO\",\"icono\":\"🐶\"}");
        org.junit.jupiter.api.Assertions.assertEquals(201, creada.getResponse().getStatus());
        long id = idDe(creada);

        mvc.perform(como(put("/api/categorias/" + id), EMAIL).content("{\"nombre\":\"Veterinario\",\"tipo\":\"GASTO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Veterinario"));
        mvc.perform(como(get("/api/categorias/" + id), EMAIL)).andExpect(status().isOk());
        mvc.perform(como(delete("/api/categorias/" + id), EMAIL)).andExpect(status().isNoContent());
        mvc.perform(como(get("/api/categorias/" + id), EMAIL)).andExpect(status().isNotFound());
    }

    @Test
    void categoriaSinNombreDevuelve400() throws Exception {
        mvc.perform(como(post("/api/categorias"), EMAIL).content("{\"tipo\":\"GASTO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasItem("El nombre es obligatorio")));
    }

    // ---------- Gastos ----------

    @Test
    void gastoCrudCompleto() throws Exception {
        long categoria = idCategoria("GASTO");
        String json = "{\"monto\":25000,\"fecha\":\"2026-10-15\",\"descripcion\":\"Almuerzo\",\"metodoPago\":\"EFECTIVO\",\"categoriaId\":" + categoria + "}";

        MvcResult creado = ejecutar(post("/api/gastos"), json);
        org.junit.jupiter.api.Assertions.assertEquals(201, creado.getResponse().getStatus());
        long id = idDe(creado);

        mvc.perform(como(get("/api/gastos/" + id), EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Almuerzo"))
                .andExpect(jsonPath("$.categoriaId").value(categoria));
        mvc.perform(como(get("/api/gastos"), EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(1))
                .andExpect(jsonPath("$.contenido[0].id").value(id));
        mvc.perform(como(get("/api/gastos").param("desde", "2026-10-01").param("hasta", "2026-10-31"), EMAIL))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        mvc.perform(como(put("/api/gastos/" + id), EMAIL).content(json.replace("25000", "30000")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.monto").value(30000));

        mvc.perform(como(delete("/api/gastos/" + id), EMAIL)).andExpect(status().isNoContent());
        mvc.perform(como(get("/api/gastos/" + id), EMAIL)).andExpect(status().isNotFound());
    }

    @Test
    void gastoConDatosInvalidosDevuelve400() throws Exception {
        mvc.perform(como(post("/api/gastos"), EMAIL).content("{\"monto\":-5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasItem("El monto debe ser mayor que cero")))
                .andExpect(jsonPath("$.errores", hasItem("La fecha es obligatoria")));
    }

    @Test
    void gastoConCategoriaDeIngresoDevuelve400() throws Exception {
        long categoriaIngreso = idCategoria("INGRESO");
        mvc.perform(como(post("/api/gastos"), EMAIL).content(
                        "{\"monto\":1000,\"fecha\":\"2026-10-15\",\"metodoPago\":\"EFECTIVO\",\"categoriaId\":" + categoriaIngreso + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La categoría seleccionada no es de tipo GASTO"));
    }

    @Test
    void gastoInexistenteDevuelve404() throws Exception {
        mvc.perform(como(get("/api/gastos/999999"), EMAIL)).andExpect(status().isNotFound());
    }

    @Test
    void idNoNumericoDevuelve400() throws Exception {
        mvc.perform(como(get("/api/gastos/abc"), EMAIL)).andExpect(status().isBadRequest());
    }

    @Test
    void gastoDeOtroUsuarioDevuelve404() throws Exception {
        long categoria = idCategoria("GASTO");
        MvcResult creado = ejecutar(post("/api/gastos"),
                "{\"monto\":1000,\"fecha\":\"2026-10-15\",\"metodoPago\":\"EFECTIVO\",\"categoriaId\":" + categoria + "}");
        long id = idDe(creado);

        registrar("otro@test.com");
        mvc.perform(como(get("/api/gastos/" + id), "otro@test.com")).andExpect(status().isNotFound());
        mvc.perform(como(delete("/api/gastos/" + id), "otro@test.com")).andExpect(status().isNotFound());
    }

    // ---------- Ingresos ----------

    @Test
    void ingresoCrudCompleto() throws Exception {
        long categoria = idCategoria("INGRESO");
        String json = "{\"monto\":2000000,\"fecha\":\"2026-10-01\",\"descripcion\":\"Salario\",\"categoriaId\":" + categoria + "}";

        MvcResult creado = ejecutar(post("/api/ingresos"), json);
        org.junit.jupiter.api.Assertions.assertEquals(201, creado.getResponse().getStatus());
        long id = idDe(creado);

        mvc.perform(como(get("/api/ingresos/" + id), EMAIL))
                .andExpect(status().isOk()).andExpect(jsonPath("$.descripcion").value("Salario"));
        mvc.perform(como(get("/api/ingresos"), EMAIL))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElementos").value(1));
        mvc.perform(como(put("/api/ingresos/" + id), EMAIL).content(json.replace("Salario", "Prima")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.descripcion").value("Prima"));
        mvc.perform(como(delete("/api/ingresos/" + id), EMAIL)).andExpect(status().isNoContent());
        mvc.perform(como(get("/api/ingresos/" + id), EMAIL)).andExpect(status().isNotFound());
    }

    @Test
    void ingresoSinMontoDevuelve400() throws Exception {
        mvc.perform(como(post("/api/ingresos"), EMAIL).content("{\"fecha\":\"2026-10-01\",\"categoriaId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasItem("El monto es obligatorio")));
    }

    // ---------- Presupuestos ----------

    @Test
    void presupuestoCrudYDuplicado() throws Exception {
        long categoria = idCategoria("GASTO");
        String json = "{\"categoriaId\":" + categoria + ",\"mes\":10,\"anio\":2026,\"montoLimite\":500000}";

        MvcResult creado = ejecutar(post("/api/presupuestos"), json);
        org.junit.jupiter.api.Assertions.assertEquals(201, creado.getResponse().getStatus());
        long id = idDe(creado);

        mvc.perform(como(post("/api/presupuestos"), EMAIL).content(json)).andExpect(status().isConflict());
        mvc.perform(como(get("/api/presupuestos"), EMAIL))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].excedido").value(false));
        mvc.perform(como(put("/api/presupuestos/" + id), EMAIL).content(json.replace("500000", "600000")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.montoLimite").value(600000));
        mvc.perform(como(delete("/api/presupuestos/" + id), EMAIL)).andExpect(status().isNoContent());
        mvc.perform(como(get("/api/presupuestos/" + id), EMAIL)).andExpect(status().isNotFound());
    }

    @Test
    void presupuestoConMesInvalidoDevuelve400() throws Exception {
        mvc.perform(como(post("/api/presupuestos"), EMAIL).content("{\"categoriaId\":1,\"mes\":13,\"anio\":2026,\"montoLimite\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasItem("El mes debe estar entre 1 y 12")));
    }

    // ---------- Recordatorios ----------

    @Test
    void recordatorioCrudYCompletar() throws Exception {
        MvcResult creado = ejecutar(post("/api/recordatorios"),
                "{\"titulo\":\"Pagar arriendo\",\"descripcion\":\"Día 5\",\"fecha\":\"2026-11-05\"}");
        org.junit.jupiter.api.Assertions.assertEquals(201, creado.getResponse().getStatus());
        long id = idDe(creado);

        mvc.perform(como(patch("/api/recordatorios/" + id + "/completar"), EMAIL))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completado").value(true));
        mvc.perform(como(put("/api/recordatorios/" + id), EMAIL)
                        .content("{\"titulo\":\"Pagar luz\",\"fecha\":\"2026-11-10\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.titulo").value("Pagar luz"));
        mvc.perform(como(get("/api/recordatorios"), EMAIL)).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(como(delete("/api/recordatorios/" + id), EMAIL)).andExpect(status().isNoContent());
        mvc.perform(como(get("/api/recordatorios/" + id), EMAIL)).andExpect(status().isNotFound());
    }

    @Test
    void recordatorioSinTituloDevuelve400() throws Exception {
        mvc.perform(como(post("/api/recordatorios"), EMAIL).content("{\"fecha\":\"2026-11-05\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasItem("El título es obligatorio")));
    }

    // ---------- Resumen ----------

    @Test
    void resumenDevuelveTotales() throws Exception {
        mvc.perform(como(get("/api/resumen"), EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.totalIngresosMes").exists())
                .andExpect(jsonPath("$.totalGastosMes").exists());
    }
}
