package com.itsoeh.hmmayte.docente.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MEstadisticaGrupo;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;

import java.util.ArrayList;
import java.util.List;

public class AdapterEstadisticasGrupos extends RecyclerView.Adapter<AdapterEstadisticasGrupos.ViewHolder> {

    public interface OnGrupoClickListener {
        void onGrupoClick(MEstadisticaGrupo grupo);
    }

    private Context context;
    private List<MEstadisticaGrupo> lista;
    private List<MEstadisticaGrupo> listaOriginal;
    private OnGrupoClickListener listener;

    public AdapterEstadisticasGrupos(Context context, List<MEstadisticaGrupo> lista, OnGrupoClickListener listener) {
        this.context = context;
        this.lista = new ArrayList<>(lista); // lista "visible"
        this.listaOriginal = new ArrayList<>(lista); // copia original para filtrar
        this.listener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_estadistica_grupo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        MEstadisticaGrupo m = lista.get(pos);
        h.tvGrupo.setText("Grupo: " + m.getNombreGrupo());

        // Gráfica
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float)m.getPorcentajeAsistencias(), "Asistencias"));
        entries.add(new PieEntry((float)m.getPorcentajeRetardos(), "Retardos"));
        entries.add(new PieEntry((float)m.getPorcentajeFaltas(), "Faltas"));
        entries.add(new PieEntry((float)m.getPorcentajeJustificaciones(), "Justificaciones"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#FFC107"),
                Color.parseColor("#F44336"), Color.parseColor("#2196F3"));
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(h.pieChart));
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12f);

        h.pieChart.setUsePercentValues(true);
        h.pieChart.setData(data);
        h.pieChart.getDescription().setEnabled(false);
        h.pieChart.getLegend().setEnabled(true);
        h.pieChart.animateY(1000, Easing.EaseInOutQuad);
        h.pieChart.invalidate();

        // Click listener
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGrupoClick(m);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGrupo;
        PieChart pieChart;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvGrupo = v.findViewById(R.id.tvGrupo);
            pieChart = v.findViewById(R.id.pieChartGrupo);
        }
    }
    public void filtrar(String texto) {
        texto = texto.toLowerCase();
        lista.clear(); // limpiar lista visible

        if (texto.isEmpty()) {
            lista.addAll(listaOriginal); // restaurar todos
        } else {
            for (MEstadisticaGrupo g : listaOriginal) {
                // Filtrar por el nombre del grupo (o cualquier otro campo que tengas)
                if (g.getNombreGrupo().toLowerCase().contains(texto)) {
                    lista.add(g);
                }
            }
        }
        notifyDataSetChanged();
    }
}
