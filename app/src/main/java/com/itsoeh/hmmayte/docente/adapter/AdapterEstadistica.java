package com.itsoeh.hmmayte.docente.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MEstadistica;

import java.util.ArrayList;
import java.util.List;

public class AdapterEstadistica extends RecyclerView.Adapter<AdapterEstadistica.ViewHolder> {

    private Context context;
    private List<MEstadistica> lista;

    public AdapterEstadistica(Context context, List<MEstadistica> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_estudiante_estadistica, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MEstadistica m = lista.get(position);

        holder.txtNombre.setText(m.getNombreCompleto());
        holder.txtMatricula.setText(m.getMatricula());

        // Generar la gráfica automáticamente
        generarGrafica(holder.chart, m);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtMatricula;
        PieChart chart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.item_est_esta_txt_nombre);
            txtMatricula = itemView.findViewById(R.id.item_est_esta_txt_matricula);


            chart = itemView.findViewById(R.id.item_est_esta_chart);
        }
    }

    // ----------------------------------------------------------
    // MÉTODO PARA GENERAR LA GRÁFICA
    // ----------------------------------------------------------
    private void generarGrafica(PieChart chart, MEstadistica m) {

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) m.getPorcentajeAsistencias(), "A"));
        entries.add(new PieEntry((float) m.getPorcentajeRetardos(), "R"));
        entries.add(new PieEntry((float) m.getPorcentajeFaltas(), "F"));
        entries.add(new PieEntry((float) m.getPorcentajeJustificaciones(), "J"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(2f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.setDrawEntryLabels(false);

        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(45f);

        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Casteamos Entry a PieEntry para acceder a getLabel() si hace falta
                if (e instanceof com.github.mikephil.charting.data.PieEntry) {
                    com.github.mikephil.charting.data.PieEntry pe =
                            (com.github.mikephil.charting.data.PieEntry) e;

                    String tipo = pe.getLabel();
                    float valor = pe.getValue();

                    switch (tipo) {
                        case "A":
                            Toast.makeText(context, "Asistencias: " + valor + "%", Toast.LENGTH_SHORT).show();
                            break;
                        case "R":
                            Toast.makeText(context, "Retardos: " + valor + "%", Toast.LENGTH_SHORT).show();
                            break;
                        case "F":
                            Toast.makeText(context, "Faltas: " + valor + "%", Toast.LENGTH_SHORT).show();
                            break;
                        case "J":
                            Toast.makeText(context, "Justificaciones: " + valor + "%", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, pe.getLabel() + ": " + valor, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    // En caso de que no sea PieEntry — manejo genérico
                    float valor = e.getY();
                    Toast.makeText(context, "Valor: " + valor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Opcional: hacer algo cuando no hay selección
            }
        });

    }
}
