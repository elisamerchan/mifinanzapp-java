package com.mifinanzapp.controller;

import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.exception.ResourceNotFoundException;
import com.mifinanzapp.service.DashboardService;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la sección de educación financiera.
 * <p>
 * Lista artículos cortos (plantillas en {@code templates/educacion/articulos/}),
 * muestra consejos personalizados según los datos del usuario y una calculadora de ahorro.
 *
 * @since 1.0.0
 */
@Controller
@RequestMapping("/educacion")
public class EducacionController {

    /** Artículo de la lista; el {@code slug} es también el nombre de su plantilla. */
    public record Articulo(String slug, String icono, String titulo, String resumen, int minutos) {}

    // La lista cerrada evita que un slug arbitrario seleccione una plantilla cualquiera.
    private static final List<Articulo> ARTICULOS = List.of(
        new Articulo("regla-50-30-20", "⚖️", "La regla 50/30/20", "Reparte tus ingresos entre necesidades, deseos y ahorro sin complicarte.", 3),
        new Articulo("fondo-emergencia", "🛟", "Fondo de emergencia", "Cuánto necesitas, dónde guardarlo y cómo construirlo paso a paso.", 3),
        new Articulo("presupuesto-mensual", "📊", "Cómo hacer un presupuesto", "Un método de cuatro pasos para planificar el mes y cumplirlo.", 4),
        new Articulo("control-deudas", "💳", "Control de deudas", "Bola de nieve o avalancha: qué deuda pagar primero.", 4),
        new Articulo("interes-compuesto", "📈", "Interés compuesto", "Por qué empezar pronto importa más que ahorrar mucho.", 3)
    );

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;

    public EducacionController(DashboardService dashboardService, UsuarioService usuarioService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String educacion(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("articulos", ARTICULOS);
        model.addAttribute("consejos", consejosPersonalizados(usuario.getId()));
        return "educacion/index";
    }

    @GetMapping("/{slug}")
    public String articulo(@PathVariable String slug, Model model) {
        Articulo articulo = ARTICULOS.stream().filter(a -> a.slug().equals(slug)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "slug", slug));
        model.addAttribute("articulo", articulo);
        model.addAttribute("otros", ARTICULOS.stream().filter(a -> !a.slug().equals(slug)).toList());
        return "educacion/articulo";
    }

    /** Consejos según el mes en curso del usuario: ahorro, presupuestos y balance. */
    List<String> consejosPersonalizados(Long usuarioId) {
        List<String> consejos = new ArrayList<>();
        BigDecimal ingresos = dashboardService.getTotalIngresosDelMes(usuarioId);
        BigDecimal gastos = dashboardService.getTotalGastosDelMes(usuarioId);
        BigDecimal ahorro = dashboardService.getPorcentajeAhorro(usuarioId);

        if (ingresos.signum() == 0 && gastos.signum() == 0) {
            consejos.add("Aún no hay movimientos este mes. Registrar cada ingreso y gasto es el primer paso para entender tu dinero.");
        } else if (gastos.compareTo(ingresos) > 0) {
            consejos.add("Este mes gastas más de lo que ingresas. Revisa la categoría donde más gastas ("
                    + dashboardService.getCategoriaMayorGasto(usuarioId) + ") y busca un recorte realista.");
        } else if (ahorro.compareTo(BigDecimal.valueOf(20)) >= 0) {
            consejos.add("¡Vas muy bien! Estás ahorrando el " + ahorro + "% de tus ingresos, por encima del 20% recomendado.");
        } else {
            consejos.add("Estás ahorrando el " + ahorro + "% de tus ingresos. Intenta acercarte al 20% con un ahorro automático al cobrar.");
        }
        if (dashboardService.getPresupuestosConProgreso(usuarioId).isEmpty()) {
            consejos.add("No tienes presupuestos para este mes. Crea uno para tu categoría de mayor gasto y recibe avisos al acercarte al límite.");
        }
        return consejos;
    }
}
