package com.mifinanzapp.movil.ui;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.mifinanzapp.movil.R;
import com.mifinanzapp.movil.datos.BaseDatos;
import com.mifinanzapp.movil.datos.CategoriaRepositorio;
import com.mifinanzapp.movil.datos.MovimientoRepositorio;
import com.mifinanzapp.movil.datos.PresupuestoRepositorio;
import com.mifinanzapp.movil.modelo.Categoria;
import com.mifinanzapp.movil.modelo.Presupuesto;
import com.mifinanzapp.movil.negocio.CalculadoraFinanzas;
import com.mifinanzapp.movil.negocio.Fechas;
import com.mifinanzapp.movil.negocio.Formato;
import com.mifinanzapp.movil.negocio.Validador;

import java.util.ArrayList;
import java.util.List;

/** Presupuestos del mes actual con barra de progreso y alerta al 80 % y al 100 %. */
public class PresupuestosActivity extends ListaBaseActivity {

    private PresupuestoRepositorio presupuestos;
    private MovimientoRepositorio movimientos;
    private CategoriaRepositorio categorias;
    private int mes;
    private int anio;

    @Override
    protected void configurar() {
        BaseDatos base = BaseDatos.obtener(this);
        presupuestos = new PresupuestoRepositorio(base);
        movimientos = new MovimientoRepositorio(base);
        categorias = new CategoriaRepositorio(base);
        mes = Fechas.mesActual();
        anio = Fechas.anioActual();
        titulo.setText("Presupuestos");
        subtitulo.setVisibility(View.VISIBLE);
        subtitulo.setText(Fechas.nombreMes(mes) + " " + anio + " · mantén pulsado para eliminar");
        botonNuevo.setText("Nuevo presupuesto");
    }

    @Override
    protected void cargar() {
        String desde = Fechas.inicioMes(mes, anio);
        String hasta = Fechas.finMes(mes, anio);
        List<Fila> filas = new ArrayList<Fila>();
        for (Presupuesto p : presupuestos.listarDelMes(mes, anio)) {
            double gastado = movimientos.sumaPorCategoriaEntre(p.getCategoriaId(), desde, hasta);
            double uso = CalculadoraFinanzas.porcentajeUso(gastado, p.getMontoLimite());
            CalculadoraFinanzas.Nivel nivel = CalculadoraFinanzas.nivel(uso);
            String aviso = "";
            int color = getResources().getColor(R.color.ok, getTheme());
            if (nivel == CalculadoraFinanzas.Nivel.EXCEDIDO) {
                aviso = " · límite superado";
                color = getResources().getColor(R.color.peligro, getTheme());
            } else if (nivel == CalculadoraFinanzas.Nivel.ADVERTENCIA) {
                aviso = " · cerca del límite";
                color = getResources().getColor(R.color.advertencia, getTheme());
            }
            String detalle = Formato.moneda(gastado) + " de " + Formato.moneda(p.getMontoLimite())
                    + " (" + Formato.porcentajeEntero(uso) + ")" + aviso;
            filas.add(new Fila(p.getId(), p.getCategoriaNombre(), detalle,
                    (int) Math.min(100, Math.round(uso)), color));
        }
        mostrar(filas);
    }

    @Override
    protected void alNuevo() {
        final List<Categoria> gastos = categorias.listarPorTipo(Categoria.GASTO);
        if (gastos.isEmpty()) {
            aviso("Primero crea una categoría de gasto");
            return;
        }
        List<String> nombres = new ArrayList<String>();
        for (Categoria c : gastos) {
            nombres.add(c.getNombre());
        }
        final Spinner spinner = Dialogos.spinner(this, nombres);
        final EditText limite = Dialogos.campo(this, "Límite del mes", InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Dialogos.formulario(this, "Nuevo presupuesto", Dialogos.columna(this, spinner, limite),
                new Dialogos.AlAceptar() {
            @Override
            public String aceptar() {
                String error = Validador.validarMonto(limite.getText().toString());
                if (error != null) {
                    return error;
                }
                Categoria elegida = gastos.get(spinner.getSelectedItemPosition());
                try {
                    presupuestos.crear(elegida.getId(), mes, anio, Validador.parsearMonto(limite.getText().toString()));
                } catch (IllegalArgumentException e) {
                    return e.getMessage();
                }
                cargar();
                return null;
            }
        });
    }

    @Override
    protected void alMantener(final long id) {
        Dialogos.confirmar(this, "¿Eliminar este presupuesto?", new Runnable() {
            @Override
            public void run() {
                presupuestos.eliminar(id);
                aviso("Presupuesto eliminado");
                cargar();
            }
        });
    }
}
