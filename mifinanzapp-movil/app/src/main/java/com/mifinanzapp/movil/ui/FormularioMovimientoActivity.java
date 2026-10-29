package com.mifinanzapp.movil.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifinanzapp.movil.R;
import com.mifinanzapp.movil.datos.BaseDatos;
import com.mifinanzapp.movil.datos.CategoriaRepositorio;
import com.mifinanzapp.movil.datos.MovimientoRepositorio;
import com.mifinanzapp.movil.modelo.Categoria;
import com.mifinanzapp.movil.modelo.Movimiento;
import com.mifinanzapp.movil.negocio.Fechas;
import com.mifinanzapp.movil.negocio.Validador;

import java.util.ArrayList;
import java.util.List;

/** Formulario para crear o editar un ingreso o un gasto, con validación por campo. */
public class FormularioMovimientoActivity extends Activity {

    public static final String EXTRA_TIPO = "tipo";
    public static final String EXTRA_ID = "id";

    private MovimientoRepositorio movimientos;
    private List<Categoria> categorias;
    private String tipo;
    private long idEdicion;

    private EditText campoMonto;
    private EditText campoFecha;
    private EditText campoDescripcion;
    private Spinner spinnerCategoria;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_formulario_movimiento);

        BaseDatos base = BaseDatos.obtener(this);
        movimientos = new MovimientoRepositorio(base);
        idEdicion = getIntent().getLongExtra(EXTRA_ID, 0);
        Movimiento existente = idEdicion != 0 ? movimientos.buscar(idEdicion) : null;
        if (idEdicion != 0 && existente == null) {
            Toast.makeText(this, "El movimiento ya no existe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tipo = existente != null ? existente.getTipo() : getIntent().getStringExtra(EXTRA_TIPO);
        if (!Categoria.GASTO.equals(tipo)) {
            tipo = Categoria.INGRESO;
        }

        TextView titulo = (TextView) findViewById(R.id.form_titulo);
        String nombreTipo = Categoria.INGRESO.equals(tipo) ? "ingreso" : "gasto";
        titulo.setText((existente != null ? "Editar " : "Nuevo ") + nombreTipo);

        campoMonto = (EditText) findViewById(R.id.campo_monto);
        campoFecha = (EditText) findViewById(R.id.campo_fecha);
        campoDescripcion = (EditText) findViewById(R.id.campo_descripcion);
        spinnerCategoria = (Spinner) findViewById(R.id.spinner_categoria);

        categorias = new CategoriaRepositorio(base).listarPorTipo(tipo);
        if (categorias.isEmpty()) {
            Toast.makeText(this, "Primero crea una categoría de " + nombreTipo, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        List<String> nombres = new ArrayList<String>();
        for (Categoria c : categorias) {
            nombres.add(c.getNombre());
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nombres);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adaptador);

        campoFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                Dialogos.elegirFecha(FormularioMovimientoActivity.this, campoFecha);
            }
        });
        findViewById(R.id.boton_guardar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                guardar();
            }
        });
        findViewById(R.id.boton_cancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                finish();
            }
        });

        if (existente != null) {
            campoMonto.setText(trimMonto(existente.getMonto()));
            campoFecha.setText(existente.getFecha());
            campoDescripcion.setText(existente.getDescripcion());
            for (int i = 0; i < categorias.size(); i++) {
                if (categorias.get(i).getId() == existente.getCategoriaId()) {
                    spinnerCategoria.setSelection(i);
                }
            }
        } else {
            campoFecha.setText(Fechas.hoy());
        }
    }

    /** Muestra 25000.0 como 25000 y 25000.5 como 25000.5. */
    private static String trimMonto(double monto) {
        return monto == Math.rint(monto) ? String.valueOf((long) monto) : String.valueOf(monto);
    }

    private void guardar() {
        String errorMonto = Validador.validarMonto(campoMonto.getText().toString());
        String errorFecha = Validador.validarFecha(campoFecha.getText().toString());
        String errorDescripcion = Validador.validarTexto(campoDescripcion.getText().toString(),
                "descripción", Validador.MAX_DESCRIPCION, false);
        campoMonto.setError(errorMonto);
        campoFecha.setError(errorFecha);
        campoDescripcion.setError(errorDescripcion);
        if (errorMonto != null || errorFecha != null || errorDescripcion != null) {
            return;
        }

        Categoria categoria = categorias.get(spinnerCategoria.getSelectedItemPosition());
        Movimiento movimiento = new Movimiento(idEdicion, tipo,
                Validador.parsearMonto(campoMonto.getText().toString()),
                campoFecha.getText().toString().trim(), campoDescripcion.getText().toString().trim(),
                categoria.getId(), categoria.getNombre());
        if (idEdicion == 0) {
            movimientos.crear(movimiento);
        } else {
            movimientos.actualizar(movimiento);
        }
        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
        finish();
    }
}
