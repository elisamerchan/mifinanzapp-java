package com.mifinanzapp.config;

import com.mifinanzapp.dto.UsuarioRegistroDTO;
import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Gasto.MetodoPago;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.entity.Presupuesto;
import com.mifinanzapp.entity.Recordatorio;
import com.mifinanzapp.entity.Usuario;
import com.mifinanzapp.repository.CategoriaRepository;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.IngresoRepository;
import com.mifinanzapp.repository.PresupuestoRepository;
import com.mifinanzapp.repository.RecordatorioRepository;
import com.mifinanzapp.repository.UsuarioRepository;
import com.mifinanzapp.service.UsuarioService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Crea el usuario demo (ingeniero de software) con 6 meses de movimientos, presupuestos y recordatorios.
 * <p>
 * Solo se registra con {@code app.demo.habilitado=true}; es idempotente (no hace nada si el usuario ya existe).
 *
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty("app.demo.habilitado")
public class DemoDataSeeder implements ApplicationRunner {

    public static final String EMAIL = "demo@mifinanzapp.test";
    public static final String PASSWORD = "demo1234";
    private static final int MESES = 6;

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final RecordatorioRepository recordatorioRepository;

    public DemoDataSeeder(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
                          CategoriaRepository categoriaRepository, IngresoRepository ingresoRepository,
                          GastoRepository gastoRepository, PresupuestoRepository presupuestoRepository,
                          RecordatorioRepository recordatorioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ingresoRepository = ingresoRepository;
        this.gastoRepository = gastoRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.recordatorioRepository = recordatorioRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByEmail(EMAIL)) {
            return;
        }
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setNombre("Alex Demo (Ingeniero de Software)");
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        Usuario usuario = usuarioService.registrar(dto);
        Map<String, Categoria> cat = categoriaRepository.findByUsuarioId(usuario.getId()).stream()
                .collect(Collectors.toMap(Categoria::getNombre, Function.identity()));

        LocalDate hoy = LocalDate.now();
        Random rnd = new Random(42);
        List<Ingreso> ingresos = new ArrayList<>();
        List<Gasto> gastos = new ArrayList<>();
        List<Presupuesto> presupuestos = new ArrayList<>();
        Map<String, Integer> limites = Map.of("Alimentación", 600, "Transporte", 200, "Salud", 150,
                "Educación", 150, "Servicios", 1400, "Entretenimiento", 100);

        for (int i = MESES - 1; i >= 0; i--) {
            YearMonth ym = YearMonth.from(hoy).minusMonths(i);
            Function<Integer, LocalDate> dia = d -> {
                LocalDate f = ym.atDay(Math.min(d, ym.lengthOfMonth()));
                return f.isAfter(hoy) ? hoy : f; // el mes en curso no tiene fechas futuras
            };

            ingresos.add(ingreso(usuario, cat.get("Salario"), 4500, dia.apply(28), "Salario mensual"));
            if (i % 2 == 0) {
                ingresos.add(ingreso(usuario, cat.get("Freelance"), 600 + rnd.nextInt(600), dia.apply(15), "Proyecto freelance"));
            }
            if (i == 3) {
                ingresos.add(ingreso(usuario, cat.get("Bonificaciones"), 1500, dia.apply(20), "Bono de desempeño"));
            }

            gastos.add(gasto(usuario, cat.get("Servicios"), 1200, dia.apply(1), "Alquiler", MetodoPago.TRANSFERENCIA));
            gastos.add(gasto(usuario, cat.get("Servicios"), 45, dia.apply(5), "Internet fibra", MetodoPago.TARJETA_DEBITO));
            gastos.add(gasto(usuario, cat.get("Servicios"), 80 + rnd.nextInt(40), dia.apply(10), "Luz y agua", MetodoPago.TARJETA_DEBITO));
            gastos.add(gasto(usuario, cat.get("Entretenimiento"), 22, dia.apply(8), "Netflix y Spotify", MetodoPago.TARJETA_CREDITO));
            for (int k = 0; k < 1 + rnd.nextInt(2); k++) {
                gastos.add(gasto(usuario, cat.get("Entretenimiento"), 20 + rnd.nextInt(60), dia.apply(1 + rnd.nextInt(28)), "Videojuegos / salidas", aleatorio(rnd)));
            }
            for (int k = 0; k < 8; k++) {
                gastos.add(gasto(usuario, cat.get("Alimentación"), 15 + rnd.nextInt(95), dia.apply(1 + rnd.nextInt(28)), "Supermercado / comidas", aleatorio(rnd)));
            }
            for (int k = 0; k < 4; k++) {
                gastos.add(gasto(usuario, cat.get("Transporte"), 10 + rnd.nextInt(35), dia.apply(1 + rnd.nextInt(28)), "Gasolina / Uber", aleatorio(rnd)));
            }
            if (i % 2 == 1) {
                gastos.add(gasto(usuario, cat.get("Educación"), 49 + rnd.nextInt(70), dia.apply(12), "Curso online / libro técnico", MetodoPago.TARJETA_CREDITO));
            }
            if (i % 3 == 0) {
                gastos.add(gasto(usuario, cat.get("Salud"), 60 + rnd.nextInt(90), dia.apply(18), "Consulta médica", aleatorio(rnd)));
            }

            limites.forEach((nombre, limite) -> {
                Presupuesto p = new Presupuesto();
                p.setUsuario(usuario);
                p.setCategoria(cat.get(nombre));
                p.setMes(ym.getMonthValue());
                p.setAnio(ym.getYear());
                p.setMontoLimite(BigDecimal.valueOf(limite));
                presupuestos.add(p);
            });
        }

        ingresoRepository.saveAll(ingresos);
        gastoRepository.saveAll(gastos);
        presupuestoRepository.saveAll(presupuestos);
        recordatorioRepository.saveAll(List.of(
                recordatorio(usuario, "Pagar alquiler", hoy.plusDays(3), false),
                recordatorio(usuario, "Renovar dominio del portafolio", hoy.plusDays(20), false),
                recordatorio(usuario, "Declaración de impuestos", hoy.plusDays(45), false),
                recordatorio(usuario, "Factura de AWS", hoy.minusDays(5), true),
                recordatorio(usuario, "Renovar licencia del IDE", hoy.minusDays(20), true)));
    }

    private static MetodoPago aleatorio(Random rnd) {
        return MetodoPago.values()[rnd.nextInt(MetodoPago.values().length)];
    }

    private static Ingreso ingreso(Usuario u, Categoria c, int monto, LocalDate fecha, String descripcion) {
        Ingreso i = new Ingreso();
        i.setUsuario(u);
        i.setCategoria(c);
        i.setMonto(BigDecimal.valueOf(monto));
        i.setFecha(fecha);
        i.setDescripcion(descripcion);
        return i;
    }

    private static Gasto gasto(Usuario u, Categoria c, int monto, LocalDate fecha, String descripcion, MetodoPago metodo) {
        Gasto g = new Gasto();
        g.setUsuario(u);
        g.setCategoria(c);
        g.setMonto(BigDecimal.valueOf(monto));
        g.setFecha(fecha);
        g.setDescripcion(descripcion);
        g.setMetodoPago(metodo);
        return g;
    }

    private static Recordatorio recordatorio(Usuario u, String titulo, LocalDate fecha, boolean completado) {
        Recordatorio r = new Recordatorio();
        r.setUsuario(u);
        r.setTitulo(titulo);
        r.setFecha(fecha);
        r.setCompletado(completado);
        return r;
    }
}
