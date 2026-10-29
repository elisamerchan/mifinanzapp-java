package com.mifinanzapp.movil.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifinanzapp.movil.R;

import java.util.ArrayList;
import java.util.List;

/** Adaptador reutilizable para todas las listas de la aplicación. */
public class FilaAdapter extends BaseAdapter {

    private final LayoutInflater inflador;
    private List<Fila> filas = new ArrayList<Fila>();

    public FilaAdapter(Context contexto) {
        this.inflador = LayoutInflater.from(contexto);
    }

    public void setFilas(List<Fila> nuevas) {
        this.filas = nuevas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filas.size();
    }

    @Override
    public Object getItem(int posicion) {
        return filas.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return filas.get(posicion).id;
    }

    @Override
    public View getView(int posicion, View vista, ViewGroup padre) {
        if (vista == null) {
            vista = inflador.inflate(R.layout.item_fila, padre, false);
        }
        Fila fila = filas.get(posicion);
        ((TextView) vista.findViewById(R.id.fila_titulo)).setText(fila.titulo);
        ((TextView) vista.findViewById(R.id.fila_detalle)).setText(fila.detalle);
        ProgressBar barra = (ProgressBar) vista.findViewById(R.id.fila_progreso);
        if (fila.progreso == Fila.SIN_PROGRESO) {
            barra.setVisibility(View.GONE);
        } else {
            barra.setVisibility(View.VISIBLE);
            barra.setProgress(fila.progreso);
            barra.setProgressTintList(ColorStateList.valueOf(fila.colorProgreso));
        }
        return vista;
    }
}
