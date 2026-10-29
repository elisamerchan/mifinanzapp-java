package com.mifinanzapp.movil.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/** Diálogos y controles de formulario que se repiten en varias pantallas. */
public final class Dialogos {

    /** Acción del botón Guardar de un formulario emergente. */
    public interface AlAceptar {
        /** @return el mensaje de error (el diálogo sigue abierto) o {@code null} si todo salió bien */
        String aceptar();
    }

    private Dialogos() {
    }

    /** Pide confirmación antes de una acción destructiva. */
    public static void confirmar(Context contexto, String mensaje, final Runnable alConfirmar) {
        new AlertDialog.Builder(contexto)
                .setMessage(mensaje)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int boton) {
                        alConfirmar.run();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /** Formulario emergente que valida antes de cerrarse: si hay error, sigue abierto y lo muestra. */
    public static void formulario(final Activity actividad, String titulo, View contenido,
                                  final AlAceptar alAceptar) {
        final AlertDialog dialogo = new AlertDialog.Builder(actividad)
                .setTitle(titulo)
                .setView(contenido)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();
        dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vista) {
                        String error = alAceptar.aceptar();
                        if (error == null) {
                            dialogo.dismiss();
                        } else {
                            Toast.makeText(actividad, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialogo.show();
    }

    /** Abre el selector de fecha y escribe el resultado como AAAA-MM-DD en el campo. */
    public static void elegirFecha(Context contexto, final EditText campo) {
        Calendar calendario = Calendar.getInstance();
        String[] partes = campo.getText().toString().trim().split("-");
        if (partes.length == 3) {
            try {
                calendario.set(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]) - 1,
                        Integer.parseInt(partes[2]));
            } catch (NumberFormatException e) {
                // fecha escrita a medias: el selector se abre en la fecha de hoy
            }
        }
        new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker selector, int anio, int mes, int dia) {
                campo.setText(String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia));
                campo.setError(null);
            }
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static int dp(Context contexto, int valor) {
        return Math.round(valor * contexto.getResources().getDisplayMetrics().density);
    }

    public static EditText campo(Context contexto, String pista, int tipoEntrada) {
        EditText campo = new EditText(contexto);
        campo.setHint(pista);
        campo.setInputType(tipoEntrada);
        return campo;
    }

    public static Spinner spinner(Context contexto, List<String> opciones) {
        Spinner spinner = new Spinner(contexto);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(contexto,
                android.R.layout.simple_spinner_item, opciones);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);
        return spinner;
    }

    public static LinearLayout columna(Context contexto, View... vistas) {
        LinearLayout columna = new LinearLayout(contexto);
        columna.setOrientation(LinearLayout.VERTICAL);
        int margen = dp(contexto, 20);
        columna.setPadding(margen, margen / 2, margen, 0);
        for (View vista : vistas) {
            columna.addView(vista);
        }
        return columna;
    }
}
