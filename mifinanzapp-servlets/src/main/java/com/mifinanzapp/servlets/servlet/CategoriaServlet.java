package com.mifinanzapp.servlets.servlet;

import com.mifinanzapp.servlets.config.InicializadorBaseDatos;
import com.mifinanzapp.servlets.dao.CategoriaDao;
import com.mifinanzapp.servlets.modelo.Categoria;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * CRUD de categorías.
 *
 * <ul>
 *   <li>GET {@code /categorias?accion=listar} (por defecto): tabla de categorías.</li>
 *   <li>GET {@code /categorias?accion=nuevo}: formulario vacío.</li>
 *   <li>GET {@code /categorias?accion=editar&id=N}: formulario con los datos de la categoría.</li>
 *   <li>POST {@code accion=guardar}: crea o actualiza según llegue o no el campo {@code id}.</li>
 *   <li>POST {@code accion=eliminar&id=N}: borra la categoría.</li>
 * </ul>
 *
 * Los POST redirigen con PRG (Post/Redirect/Get) para que recargar la página no repita la operación.
 */
@WebServlet(name = "CategoriaServlet", urlPatterns = "/categorias")
public class CategoriaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VISTA_LISTADO = "/WEB-INF/jsp/categorias/listado.jsp";
    private static final String VISTA_FORMULARIO = "/WEB-INF/jsp/categorias/formulario.jsp";

    private static final String ACCION_LISTAR = "listar";
    private static final String ACCION_NUEVO = "nuevo";
    private static final String ACCION_EDITAR = "editar";
    private static final String ACCION_GUARDAR = "guardar";
    private static final String ACCION_ELIMINAR = "eliminar";

    private transient CategoriaDao categoriaDao;

    /** Constructor usado por el contenedor. El DAO se obtiene del contexto en {@link #init()}. */
    public CategoriaServlet() {
    }

    /** Constructor para pruebas: permite inyectar un DAO simulado. */
    public CategoriaServlet(CategoriaDao categoriaDao) {
        this.categoriaDao = categoriaDao;
    }

    @Override
    public void init() {
        if (categoriaDao == null) {
            categoriaDao = (CategoriaDao) getServletContext().getAttribute(InicializadorBaseDatos.ATRIBUTO_DAO);
        }
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String accion = parametro(peticion, "accion", ACCION_LISTAR);

        switch (accion) {
            case ACCION_LISTAR -> mostrarListado(peticion, respuesta);
            case ACCION_NUEVO -> mostrarFormulario(peticion, respuesta, new Categoria());
            case ACCION_EDITAR -> mostrarEdicion(peticion, respuesta);
            default -> respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
        }
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.setCharacterEncoding("UTF-8");
        String accion = parametro(peticion, "accion", "");

        switch (accion) {
            case ACCION_GUARDAR -> guardar(peticion, respuesta);
            case ACCION_ELIMINAR -> eliminar(peticion, respuesta);
            default -> respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
        }
    }

    private void mostrarListado(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        List<Categoria> categorias = categoriaDao.listar();
        peticion.setAttribute("categorias", categorias);
        reenviar(peticion, respuesta, VISTA_LISTADO);
    }

    private void mostrarEdicion(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Optional<Long> id = idOpcional(peticion);
        if (id.isEmpty()) {
            respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador no válido");
            return;
        }

        Optional<Categoria> categoria = categoriaDao.buscarPorId(id.get());
        if (categoria.isEmpty()) {
            respuesta.sendError(HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
            return;
        }
        mostrarFormulario(peticion, respuesta, categoria.get());
    }

    private void mostrarFormulario(HttpServletRequest peticion, HttpServletResponse respuesta,
                                   Categoria categoria) throws ServletException, IOException {
        peticion.setAttribute("categoria", categoria);
        reenviar(peticion, respuesta, VISTA_FORMULARIO);
    }

    private void guardar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Categoria categoria = new Categoria(
                idOpcional(peticion).orElse(null),
                parametro(peticion, "nombre", "").trim(),
                parametro(peticion, "tipo", ""));

        List<String> errores = categoria.validar();
        if (!errores.isEmpty()) {
            // Se vuelve al formulario con los datos escritos y los errores, sin perder lo introducido.
            peticion.setAttribute("errores", errores);
            mostrarFormulario(peticion, respuesta, categoria);
            return;
        }

        categoriaDao.guardar(categoria);
        redirigirAListado(peticion, respuesta, "guardada");
    }

    private void eliminar(HttpServletRequest peticion, HttpServletResponse respuesta) throws IOException {
        Optional<Long> id = idOpcional(peticion);
        if (id.isEmpty()) {
            respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador no válido");
            return;
        }

        if (!categoriaDao.eliminar(id.get())) {
            respuesta.sendError(HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
            return;
        }
        redirigirAListado(peticion, respuesta, "eliminada");
    }

    private void redirigirAListado(HttpServletRequest peticion, HttpServletResponse respuesta, String mensaje)
            throws IOException {
        respuesta.sendRedirect(peticion.getContextPath() + "/categorias?accion=listar&mensaje=" + mensaje);
    }

    private static void reenviar(HttpServletRequest peticion, HttpServletResponse respuesta, String vista)
            throws ServletException, IOException {
        RequestDispatcher despachador = peticion.getRequestDispatcher(vista);
        despachador.forward(peticion, respuesta);
    }

    private static String parametro(HttpServletRequest peticion, String nombre, String porDefecto) {
        String valor = peticion.getParameter(nombre);
        return valor == null ? porDefecto : valor;
    }

    /** Lee el parámetro {@code id}; devuelve vacío si falta o no es un número. */
    private static Optional<Long> idOpcional(HttpServletRequest peticion) {
        String valor = peticion.getParameter("id");
        if (valor == null || valor.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(valor.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
