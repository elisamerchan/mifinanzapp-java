package com.mifinanzapp.movil.ui;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.mifinanzapp.movil.datos.BaseDatos;
import com.mifinanzapp.movil.datos.RecordatorioRepositorio;
import com.mifinanzapp.movil.modelo.Recordatorio;
import com.mifinanzapp.movil.negocio.Fechas;
import com.mifinanzapp.movil.negocio.Validador;

import java.util.ArrayList;
import java.util.List;

/** Recordatorios: toca para marcar como hecho, mantén pulsado para eliminar. */
public class RecordatoriosActivity extends ListaBaseActivity {

    private RecordatorioRepositorio repositorio;

    @Override
    protected void configurar() {
        repositorio = new RecordatorioRepositorio(BaseDatos.obtener(this));
        titulo.setText("Recordatorios");
        subtitulo.setVisibility(View.VISIBLE);
        subtitulo.setText("Toca para marcar como hecho. Mantén pulsado para eliminar.");
        botonNuevo.setText("Nuevo recordatorio");
    }

    @Override
    protected void cargar() {
        String hoy = Fechas.hoy();
        List<Fila> filas = new ArrayList<Fila>();
        for (Recordatorio r : repositorio.listar()) {
            String detalle = r.getFecha();
            if (!r.isCompletado() && r.getFecha().compareTo(hoy) < 0) {
                detalle += " · vencido";
            }
            filas.add(new Fila(r.getId(), (r.isCompletado() ? "✔ " : "") + r.getTitulo(), detalle));
        }
        mostrar(filas);
    }

    @Override
    protected void alNuevo() {
        final EditText nuevoTitulo = Dialogos.campo(this, "Título", InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final EditText fecha = Dialogos.campo(this, "Fecha (toca para elegir)", InputType.TYPE_NULL);
        fecha.setFocusable(false);
        fecha.setText(Fechas.hoy());
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                Dialogos.elegirFecha(RecordatoriosActivity.this, fecha);
            }
        });
        Dialogos.formulario(this, "Nuevo recordatorio", Dialogos.columna(this, nuevoTitulo, fecha),
                new Dialogos.AlAceptar() {
            @Override
            public String aceptar() {
                String error = Validador.validarTexto(nuevoTitulo.getText().toString(), "título",
                        Validador.MAX_TITULO, true);
                if (error == null) {
                    error = Validador.validarFecha(fecha.getText().toString());
                }
                if (error != null) {
                    return error;
                }
                repositorio.crear(nuevoTitulo.getText().toString().trim(), fecha.getText().toString().trim());
                cargar();
                return null;
            }
        });
    }

    @Override
    protected void alTocar(long id) {
        repositorio.alternar(id);
        cargar();
    }

    @Override
    protected void alMantener(final long id) {
        Dialogos.confirmar(this, "¿Eliminar este recordatorio?", new Runnable() {
            @Override
            public void run() {
                repositorio.eliminar(id);
                aviso("Recordatorio eliminado");
                cargar();
            }
        });
    }
}
