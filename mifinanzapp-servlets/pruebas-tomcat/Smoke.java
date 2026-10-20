// Prueba de humo: arranca Tomcat embebido con el WAR y recorre el CRUD por HTTP.
// Ejecutar con las JAR de tomcat-embed (core, jasper, el) y ecj en el classpath; ver README del módulo.
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class Smoke {
    static HttpClient cliente = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
    static int fallos = 0;

    static HttpResponse<String> get(String url) throws Exception {
        return cliente.send(HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
    static HttpResponse<String> post(String url, String cuerpo) throws Exception {
        return cliente.send(HttpRequest.newBuilder(URI.create(url))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(cuerpo)).build(), HttpResponse.BodyHandlers.ofString());
    }
    static void verificar(String nombre, boolean ok, String detalle) {
        System.out.println((ok ? "OK    " : "FALLO ") + nombre + (ok ? "" : " -> " + detalle));
        if (!ok) fallos++;
    }

    public static void main(String[] args) throws Exception {
        String war = Paths.get(args[0]).toAbsolutePath().toString();
        Path base = Files.createTempDirectory("tomcat-smoke");
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8099);
        tomcat.setBaseDir(base.toString());
        tomcat.getConnector();
        Context ctx = tomcat.addWebapp("/mifinanzapp", war);
        tomcat.start();
        String b = "http://localhost:8099/mifinanzapp";
        try {
            HttpResponse<String> r = get(b + "/categorias");
            verificar("GET listado 200", r.statusCode() == 200, "status " + r.statusCode());
            verificar("listado muestra datos de ejemplo", r.body().contains("Salario") && r.body().contains("Transporte"), "faltan datos");

            r = get(b + "/");
            verificar("index redirige a categorias", r.statusCode() == 302 || r.body().contains("Categorías"), "status " + r.statusCode());

            r = get(b + "/categorias?accion=nuevo");
            verificar("GET nuevo 200 con formulario", r.statusCode() == 200 && r.body().contains("name=\"nombre\""), "status " + r.statusCode());

            r = post(b + "/categorias", "accion=guardar&nombre=Ocio+de+prueba&tipo=GASTO");
            verificar("POST guardar redirige (PRG)", r.statusCode() == 302, "status " + r.statusCode());
            verificar("redirect lleva mensaje guardada", r.headers().firstValue("Location").orElse("").contains("mensaje=guardada"), String.valueOf(r.headers().map()));

            r = get(b + "/categorias?accion=listar&mensaje=guardada");
            verificar("listado muestra la categoria nueva y el aviso", r.body().contains("Ocio de prueba") && r.body().contains("guardada correctamente"), "no aparece");

            r = post(b + "/categorias", "accion=guardar&nombre=&tipo=OTRO");
            verificar("POST invalido muestra errores", r.statusCode() == 200 && r.body().contains("El nombre es obligatorio"), "status " + r.statusCode());

            r = get(b + "/categorias?accion=editar&id=1");
            verificar("GET editar carga datos", r.statusCode() == 200 && r.body().contains("value=\"Salario\""), "status " + r.statusCode());

            r = get(b + "/categorias?accion=editar&id=999");
            verificar("GET editar inexistente 404", r.statusCode() == 404, "status " + r.statusCode());

            r = get(b + "/categorias?accion=hackear");
            verificar("accion desconocida 400", r.statusCode() == 400, "status " + r.statusCode());

            r = post(b + "/categorias", "accion=eliminar&id=1");
            verificar("POST eliminar redirige", r.statusCode() == 302, "status " + r.statusCode());
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
        System.out.println(fallos == 0 ? "\nTODAS LAS COMPROBACIONES OK" : "\nFALLOS: " + fallos);
        System.exit(fallos == 0 ? 0 : 1);
    }
}
