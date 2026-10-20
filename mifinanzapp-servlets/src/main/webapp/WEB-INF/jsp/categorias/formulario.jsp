<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="${empty categoria.id ? 'Nueva categoría' : 'Editar categoría'}" />
<%@ include file="/WEB-INF/jsp/fragmentos/cabecera.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-7 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-4"><c:out value="${titulo}" /></h1>

                <c:if test="${not empty errores}">
                    <div class="alert alert-danger" role="alert">
                        <ul class="mb-0">
                            <c:forEach var="error" items="${errores}">
                                <li><c:out value="${error}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/categorias">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="id" value="${categoria.id}">

                    <div class="mb-3">
                        <label for="nombre" class="form-label">Nombre</label>
                        <input type="text" class="form-control" id="nombre" name="nombre"
                               maxlength="60" required value="${categoria.nombre}">
                    </div>

                    <div class="mb-4">
                        <label for="tipo" class="form-label">Tipo</label>
                        <select class="form-select" id="tipo" name="tipo" required>
                            <option value="INGRESO" ${categoria.tipo == 'INGRESO' ? 'selected' : ''}>Ingreso</option>
                            <option value="GASTO" ${categoria.tipo == 'GASTO' ? 'selected' : ''}>Gasto</option>
                        </select>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">Guardar</button>
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/categorias">Cancelar</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragmentos/pie.jspf" %>
