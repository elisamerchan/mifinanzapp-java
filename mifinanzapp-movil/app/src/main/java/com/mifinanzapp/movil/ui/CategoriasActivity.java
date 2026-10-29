package com.mifinanzapp.movil.ui;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.mifinanzapp.movil.datos.BaseDatos;
import com.mifinanzapp.movil.datos.CategoriaRepositorio;
import com.mifinanzapp.movil.modelo.Categoria;
import com.mifinanzapp.movil.negocio.Validador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Lista de categorías de ingreso y de gasto; permite crear y eliminar. */
public class CategoriasActivity extends ListaBaseActivity {

    private CategoriaRepositorio repositorio;

    @Override
    protected void configurar() {
        repositorio = new CategoriaRepositorio(BaseDatos.obtener(this));
        titulo.setText("Categorías");
        subtitulo.setVisibility(View.VISIBLE);
        subtitulo.setText("Mantén pulsada una categoría para eliminarla.");
        botonNuevo.setText("Nueva categoría");
    }

    @Override
    protected void cargar() {
        List<Fila> filas = new ArrayList<Fila>();
        for (Categoria c : repositorio.listar()) {
            filas.add(new Fila(c.getId(), c.getNombre(),
                    Categoria.INGRESO.equals(c.getTipo()) ? "Ingreso" : "Gasto"));
        }
        mostrar(filas);
    }

    @Override
    protected void alNuevo() {
        final EditText nombre = Dialogos.campo(this, "Nombre", InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final Spinner tipo = Dialogos.spinner(this, Arrays.asList("Gasto", "Ingreso"));
        Dialogos.formulario(this, "Nueva categoría", Dialogos.columna(this, nombre, tipo), new Dialogos.AlAceptar() {
            @Override
            public String aceptar() {
                String error = Validador.validarTexto(nombre.getText().toString(), "nombre",
                        Validador.MAX_NOMBRE, true);
                if (error != null) {
                    return error;
                }
                String tipoElegido = tipo.getSelectedItemPosition() == 0 ? Categoria.GASTO : Categoria.INGRESO;
                try {
                    repositorio.crear(nombre.getText().toString().trim(), tipoElegido);
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
        Dialogos.confirmar(this, "¿Eliminar esta categoría?", new Runnable() {
            @Override
            public void run() {
                try {
                    repositorio.eliminar(id);
                    aviso("Categoría eliminada");
                } catch (IllegalStateException e) {
                    aviso(e.getMessage());
                }
                cargar();
            }
        });
    }
}
