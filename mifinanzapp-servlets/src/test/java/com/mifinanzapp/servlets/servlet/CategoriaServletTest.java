package com.mifinanzapp.servlets.servlet;

import com.mifinanzapp.servlets.dao.CategoriaDao;
import com.mifinanzapp.servlets.modelo.Categoria;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de los métodos GET y POST del servlet con peticiones y DAO simulados.
 * No necesitan Tomcat ni base de datos.
 */
class CategoriaServletTest {

    private CategoriaDao dao;
    private CategoriaServlet servlet;
    private HttpServletRequest peticion;
    private HttpServletResponse respuesta;
    private RequestDispatcher despachador;

    @BeforeEach
    void prepararServlet() {
        dao = mock(CategoriaDao.class);
        servlet = new CategoriaServlet(dao);
        peticion = mock(HttpServletRequest.class);
        respuesta = mock(HttpServletResponse.class);
        despachador = mock(RequestDispatcher.class);
        when(peticion.getContextPath()).thenReturn("/mifinanzapp");
        when(peticion.getRequestDispatcher(any())).thenReturn(despachador);
    }

    @Test
    void getSinAccionListaLasCategoriasYReenviaAlListado() throws Exception {
        List<Categoria> lista = List.of(new Categoria(1L, "Salario", "INGRESO"));
        when(dao.listar()).thenReturn(lista);

        servlet.doGet(peticion, respuesta);

        verify(peticion).setAttribute("categorias", lista);
        verify(peticion).getRequestDispatcher("/WEB-INF/jsp/categorias/listado.jsp");
        verify(despachador).forward(peticion, respuesta);
    }

    @Test
    void getNuevoMuestraFormularioVacio() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("nuevo");

        servlet.doGet(peticion, respuesta);

        ArgumentCaptor<Object> captura = ArgumentCaptor.forClass(Object.class);
        verify(peticion).setAttribute(eq("categoria"), captura.capture());
        assertEquals(null, ((Categoria) captura.getValue()).getId());
        verify(peticion).getRequestDispatcher("/WEB-INF/jsp/categorias/formulario.jsp");
    }

    @Test
    void getEditarCargaLaCategoriaExistente() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("editar");
        when(peticion.getParameter("id")).thenReturn("3");
        when(dao.buscarPorId(3L)).thenReturn(Optional.of(new Categoria(3L, "Ventas", "INGRESO")));

        servlet.doGet(peticion, respuesta);

        verify(peticion).setAttribute(eq("categoria"), any(Categoria.class));
        verify(despachador).forward(peticion, respuesta);
    }

    @Test
    void getEditarInexistenteDevuelve404() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("editar");
        when(peticion.getParameter("id")).thenReturn("99");
        when(dao.buscarPorId(anyLong())).thenReturn(Optional.empty());

        servlet.doGet(peticion, respuesta);

        verify(respuesta).sendError(HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
    }

    @Test
    void getEditarSinIdDevuelve400() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("editar");
        when(peticion.getParameter("id")).thenReturn("abc");

        servlet.doGet(peticion, respuesta);

        verify(respuesta).sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador no válido");
    }

    @Test
    void getAccionDesconocidaDevuelve400() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("hackear");

        servlet.doGet(peticion, respuesta);

        verify(respuesta).sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
    }

    @Test
    void postGuardarNuevaCategoriaLaInsertaYRedirigeConPRG() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("guardar");
        when(peticion.getParameter("nombre")).thenReturn("  Ocio  ");
        when(peticion.getParameter("tipo")).thenReturn("GASTO");

        servlet.doPost(peticion, respuesta);

        ArgumentCaptor<Categoria> captura = ArgumentCaptor.forClass(Categoria.class);
        verify(dao).guardar(captura.capture());
        assertEquals("Ocio", captura.getValue().getNombre(), "El nombre debe llegar sin espacios");
        verify(respuesta).sendRedirect("/mifinanzapp/categorias?accion=listar&mensaje=guardada");
    }

    @Test
    void postGuardarConDatosInvalidosVuelveAlFormularioConErrores() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("guardar");
        when(peticion.getParameter("nombre")).thenReturn("   ");
        when(peticion.getParameter("tipo")).thenReturn("OTRO");

        servlet.doPost(peticion, respuesta);

        verify(dao, never()).guardar(any());
        verify(peticion).setAttribute(eq("errores"), any());
        verify(peticion).getRequestDispatcher("/WEB-INF/jsp/categorias/formulario.jsp");
        verify(despachador).forward(peticion, respuesta);
    }

    @Test
    void postEliminarExistenteRedirigeConMensaje() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("eliminar");
        when(peticion.getParameter("id")).thenReturn("5");
        when(dao.eliminar(5L)).thenReturn(true);

        servlet.doPost(peticion, respuesta);

        verify(respuesta).sendRedirect("/mifinanzapp/categorias?accion=listar&mensaje=eliminada");
    }

    @Test
    void postEliminarInexistenteDevuelve404() throws Exception {
        when(peticion.getParameter("accion")).thenReturn("eliminar");
        when(peticion.getParameter("id")).thenReturn("5");
        when(dao.eliminar(5L)).thenReturn(false);

        servlet.doPost(peticion, respuesta);

        verify(respuesta).sendError(HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
    }

    @Test
    void postSinAccionDevuelve400() throws Exception {
        servlet.doPost(peticion, respuesta);

        verify(respuesta).sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
        verify(dao, never()).guardar(any());
    }

    @Test
    void validarRechazaNombreDemasiadoLargo() {
        Categoria larga = new Categoria(null, "x".repeat(Categoria.LONGITUD_MAXIMA_NOMBRE + 1), "GASTO");

        assertEquals(1, larga.validar().size());
    }
}
