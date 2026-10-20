<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Categorías" />
<%@ include file="/WEB-INF/jsp/fragmentos/cabecera.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h1 class="h3 mb-0">Categorías</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/categorias?accion=nuevo">Nueva categoría</a>
</div>

<c:if test="${param.mensaje == 'guardada'}">
    <div class="alert alert-success" role="alert">Categoría guardada correctamente.</div>
</c:if>
<c:if test="${param.mensaje == 'eliminada'}">
    <div class="alert alert-success" role="alert">Categoría eliminada correctamente.</div>
</c:if>

<div class="card shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover mb-0 align-middle">
            <thead class="table-light">
            <tr>
                <th scope="col">Nombre</th>
                <th scope="col">Tipo</th>
                <th scope="col" class="text-end">Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty categorias}">
                    <tr>
                        <td colspan="3" class="text-center text-muted py-4">No hay categorías registradas.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="cat" items="${categorias}">
                        <tr>
                            <td><c:out value="${cat.nombre}" /></td>
                            <td>
                                <span class="badge ${cat.tipo == 'INGRESO' ? 'text-bg-success' : 'text-bg-danger'}">
                                    <c:out value="${cat.tipo}" />
                                </span>
                            </td>
                            <td class="text-end">
                                <a class="btn btn-sm btn-outline-secondary"
                                   href="${pageContext.request.contextPath}/categorias?accion=editar&id=${cat.id}">Editar</a>
                                <form class="d-inline" method="post"
                                      action="${pageContext.request.contextPath}/categorias"
                                      onsubmit="return confirm('¿Eliminar la categoría?');">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="id" value="${cat.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragmentos/pie.jspf" %>
