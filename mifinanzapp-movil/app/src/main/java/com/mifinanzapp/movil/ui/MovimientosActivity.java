package com.mifinanzapp.movil.ui;

import android.content.Intent;

import com.mifinanzapp.movil.datos.BaseDatos;
import com.mifinanzapp.movil.datos.MovimientoRepositorio;
import com.mifinanzapp.movil.modelo.Categoria;
import com.mifinanzapp.movil.modelo.Movimiento;
import com.mifinanzapp.movil.negocio.Formato;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/** Lista de ingresos o de gastos, con acceso al formulario para crear, editar y eliminar. */
public class MovimientosActivity extends ListaBaseActivity {

    public static final String EXTRA_TIPO = "tipo";

    private MovimientoRepositorio repositorio;
    private String tipo;

    @Override
    protected void configurar() {
        repositorio = new MovimientoRepositorio(BaseDatos.obtener(this));
        tipo = Categoria.GASTO.equals(getIntent().getStringExtra(EXTRA_TIPO)) ? Categoria.GASTO : Categoria.INGRESO;
        botonAlterno.setVisibility(View.VISIBLE);
        subtitulo.setVisibility(View.VISIBLE);
        subtitulo.setText("Toca para editar. Mantén pulsado para eliminar.");
        actualizarTextos();
    }

    private boolean esIngreso() {
        return Categoria.INGRESO.equals(tipo);
    }

    private void actualizarTextos() {
        titulo.setText(esIngreso() ? "Ingresos" : "Gastos");
        botonNuevo.setText(esIngreso() ? "Nuevo ingreso" : "Nuevo gasto");
        botonAlterno.setText(esIngreso() ? "Ver gastos" : "Ver ingresos");
    }

    @Override
    protected void cargar() {
        List<Fila> filas = new ArrayList<Fila>();
        for (Movimiento m : repositorio.listarPorTipo(tipo)) {
            String detalle = m.getFecha();
            if (m.getDescripcion() != null && m.getDescripcion().length() > 0) {
                detalle += " · " + m.getDescripcion();
            }
            filas.add(new Fila(m.getId(), m.getCategoriaNombre() + " · " + Formato.moneda(m.getMonto()), detalle));
        }
        mostrar(filas);
    }

    @Override
    protected void alNuevo() {
        Intent intento = new Intent(this, FormularioMovimientoActivity.class);
        intento.putExtra(FormularioMovimientoActivity.EXTRA_TIPO, tipo);
        startActivity(intento);
    }

    @Override
    protected void alTocar(long id) {
        Intent intento = new Intent(this, FormularioMovimientoActivity.class);
        intento.putExtra(FormularioMovimientoActivity.EXTRA_ID, id);
        startActivity(intento);
    }

    @Override
    protected void alMantener(final long id) {
        Dialogos.confirmar(this, "¿Eliminar este movimiento?", new Runnable() {
            @Override
            public void run() {
                repositorio.eliminar(id);
                aviso("Movimiento eliminado");
                cargar();
            }
        });
    }

    @Override
    protected void alAlterno() {
        tipo = esIngreso() ? Categoria.GASTO : Categoria.INGRESO;
        actualizarTextos();
        cargar();
    }
}
