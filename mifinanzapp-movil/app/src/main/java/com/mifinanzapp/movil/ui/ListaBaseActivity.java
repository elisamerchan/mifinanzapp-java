package com.mifinanzapp.movil.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifinanzapp.movil.R;

import java.util.List;

/**
 * Pantalla base de las listas (patrón Template Method): arma el diseño común y llama a los pasos que
 * cada pantalla concreta define: {@link #configurar()}, {@link #cargar()} y las acciones.
 */
public abstract class ListaBaseActivity extends Activity {

    protected TextView titulo;
    protected TextView subtitulo;
    protected Button botonNuevo;
    protected Button botonAlterno;
    private FilaAdapter adaptador;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_lista);
        titulo = (TextView) findViewById(R.id.lista_titulo);
        subtitulo = (TextView) findViewById(R.id.lista_subtitulo);
        botonNuevo = (Button) findViewById(R.id.lista_boton_nuevo);
        botonAlterno = (Button) findViewById(R.id.lista_boton_alterno);
        ListView lista = (ListView) findViewById(R.id.lista);

        adaptador = new FilaAdapter(this);
        lista.setAdapter(adaptador);
        lista.setEmptyView(findViewById(R.id.lista_vacio));
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> padre, View vista, int posicion, long id) {
                alTocar(id);
            }
        });
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> padre, View vista, int posicion, long id) {
                alMantener(id);
                return true;
            }
        });
        botonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                alNuevo();
            }
        });
        botonAlterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                alAlterno();
            }
        });
        configurar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    /** Define el texto de los botones y qué botones se ven. Se llama una vez. */
    protected abstract void configurar();

    /** Lee los datos y los muestra con {@link #mostrar(List)}. Se llama cada vez que la pantalla vuelve a verse. */
    protected abstract void cargar();

    protected abstract void alNuevo();

    protected void alTocar(long id) {
    }

    protected void alMantener(long id) {
    }

    protected void alAlterno() {
    }

    protected void mostrar(List<Fila> filas) {
        adaptador.setFilas(filas);
    }

    protected void aviso(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
