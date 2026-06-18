package com.mifinanzapp.service;

import com.mifinanzapp.entity.Categoria;
import com.mifinanzapp.entity.Gasto;
import com.mifinanzapp.entity.Ingreso;
import com.mifinanzapp.repository.GastoRepository;
import com.mifinanzapp.repository.IngresoRepository;
import com.mifinanzapp.repository.PresupuestoRepository;
import com.mifinanzapp.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link DashboardService}.
 * Verifica el cálculo de totales, balance y porcentaje de ahorro.
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private IngresoRepository ingresoRepository;

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                ingresoRepository, gastoRepository,
                presupuestoRepository, categoriaRepository
        );
    }

    @Test
    void getBalanceShouldReturnIngresosMinusGastos() {
        when(ingresoRepository.sumByUsuarioId(1L)).thenReturn(new BigDecimal("1000.00"));
        when(gastoRepository.sumByUsuarioId(1L)).thenReturn(new BigDecimal("400.00"));

        BigDecimal balance = dashboardService.getBalance(1L);

        assertEquals(new BigDecimal("600.00"), balance);
    }

    @Test
    void getPorcentajeAhorroShouldReturnCorrectPercentage() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        when(ingresoRepository.sumByUsuarioAndFechaBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00"));
        when(gastoRepository.sumByUsuarioAndFechaBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("400.00"));

        BigDecimal porcentaje = dashboardService.getPorcentajeAhorro(1L);

        assertEquals(new BigDecimal("60.0"), porcentaje);
    }

    @Test
    void getPorcentajeAhorroShouldReturnZeroWhenNoIngresos() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        when(ingresoRepository.sumByUsuarioAndFechaBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(gastoRepository.sumByUsuarioAndFechaBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        BigDecimal porcentaje = dashboardService.getPorcentajeAhorro(1L);

        assertEquals(BigDecimal.ZERO, porcentaje);
    }

    @Test
    void getConsejoAleatorioShouldReturnNonEmptyString() {
        String consejo = dashboardService.getConsejoAleatorio();
        assertNotNull(consejo);
        assertFalse(consejo.isEmpty());
    }
}
