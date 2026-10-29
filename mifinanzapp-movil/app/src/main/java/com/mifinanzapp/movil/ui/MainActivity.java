package com.mifinanzapp.movil.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mifinanzapp.movil.R;
import com.mifinanzapp.movil.datos.BaseDatos;
import com.mifinanzapp.movil.datos.MovimientoRepositorio;
import com.mifinanzapp.movil.datos.PresupuestoRepositorio;
import com.mifinanzapp.movil.datos.RecordatorioRepositorio;
import com.mifinanzapp.movil.modelo.Categoria;
import com.mifinanzapp.movil.modelo.Presupuesto;
import com.mifinanzapp.movil.negocio.CalculadoraFinanzas;
import com.mifinanzapp.movil.negocio.Fechas;
import com.mifinanzapp.movil.negocio.Formato;

/** Pantalla de inicio: resumen del mes, alertas y acceso a cada módulo. */
public class MainActivity extends Activity {

    private TextView textoMes;
    private TextView textoIngresos;
    private TextView textoGastos;
    private TextView textoBalance;
    private TextView textoAhorro;
    private TextView textoAlertas;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_main);
        textoMes = (TextView) findViewById(R.id.texto_mes);
        textoIngresos = (TextView) findViewById(R.id.texto_ingresos);
        textoGastos = (TextView) findViewById(R.id.texto_gastos);
        textoBalance = (TextView) findViewById(R.id.texto_balance);
        textoAhorro = (TextView) findViewById(R.id.texto_ahorro);
        textoAlertas = (TextView) findViewById(R.id.texto_alertas);

        enlazar(R.id.boton_ingresos, MovimientosActivity.class, Categoria.INGRESO);
        enlazar(R.id.boton_gastos, MovimientosActivity.class, Categoria.GASTO);
        enlazar(R.id.boton_categorias, CategoriasActivity.class, null);
        enlazar(R.id.boton_presupuestos, PresupuestosActivity.class, null);
        enlazar(R.id.boton_recordatorios, RecordatoriosActivity.class, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarResumen();
    }

    private void enlazar(int idBoton, final Class<?> destino, final String tipo) {
        findViewById(idBoton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                Intent intento = new Intent(MainActivity.this, destino);
                if (tipo != null) {
                    intento.putExtra(MovimientosActivity.EXTRA_TIPO, tipo);
                }
                startActivity(intento);
            }
        });
    }

    private void cargarResumen() {
        BaseDatos base = BaseDatos.obtener(this);
        MovimientoRepositorio movimientos = new MovimientoRepositorio(base);
        int mes = Fechas.mesActual();
        int anio = Fechas.anioActual();
        String desde = Fechas.inicioMes(mes, anio);
        String hasta = Fechas.finMes(mes, anio);

        double ingresos = movimientos.sumaPorTipoEntre(Categoria.INGRESO, desde, hasta);
        double gastos = movimientos.sumaPorTipoEntre(Categoria.GASTO, desde, hasta);
        double balance = CalculadoraFinanzas.balance(ingresos, gastos);

        textoMes.setText("Resumen de " + Fechas.nombreMes(mes) + " " + anio + " · "
                + movimientos.contarEntre(desde, hasta) + " movimientos");
        textoIngresos.setText(Formato.moneda(ingresos));
        textoGastos.setText(Formato.moneda(gastos));
        textoBalance.setText(Formato.moneda(balance));
        textoBalance.setTextColor(getResources().getColor(balance >= 0 ? R.color.ok : R.color.peligro, getTheme()));
        textoAhorro.setText("Ahorro: " + Formato.porcentaje(CalculadoraFinanzas.porcentajeAhorro(ingresos, gastos)));

        StringBuilder alertas = new StringBuilder();
        for (Presupuesto p : new PresupuestoRepositorio(base).listarDelMes(mes, anio)) {
            double gastado = movimientos.sumaPorCategoriaEntre(p.getCategoriaId(), desde, hasta);
            double uso = CalculadoraFinanzas.porcentajeUso(gastado, p.getMontoLimite());
            CalculadoraFinanzas.Nivel nivel = CalculadoraFinanzas.nivel(uso);
            if (nivel == CalculadoraFinanzas.Nivel.EXCEDIDO) {
                alertas.append("⚠ Superaste el presupuesto de ").append(p.getCategoriaNombre())
                        .append(" (").append(Formato.porcentajeEntero(uso)).append(").\n");
            } else if (nivel == CalculadoraFinanzas.Nivel.ADVERTENCIA) {
                alertas.append("⚠ Llevas el ").append(Formato.porcentajeEntero(uso))
                        .append(" del presupuesto de ").append(p.getCategoriaNombre()).append(".\n");
            }
        }
        int pendientes = new RecordatorioRepositorio(base).contarPendientesHasta(Fechas.hoy());
        if (pendientes > 0) {
            alertas.append("🔔 Tienes ").append(pendientes)
                    .append(pendientes == 1 ? " recordatorio pendiente." : " recordatorios pendientes.");
        }
        textoAlertas.setText(alertas.toString().trim());
        textoAlertas.setVisibility(alertas.length() == 0 ? View.GONE : View.VISIBLE);
    }
}
