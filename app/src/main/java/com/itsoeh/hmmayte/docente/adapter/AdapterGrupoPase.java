package com.itsoeh.hmmayte.docente.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;

import java.util.ArrayList;
import java.util.List;

public class AdapterGrupoPase extends RecyclerView.Adapter<AdapterGrupoPase.ViewHolder> {

    private List<MGrupo> listaGrupos;
    private List<MGrupo> listaOriginal;
    private OnGrupoClickListener listener;

    // ---------- INTERFAZ ----------
    public interface OnGrupoClickListener {
        void onTomarAsistenciaClick(MGrupo grupo);
        void onConsultarClick(MGrupo grupo);
    }

    public AdapterGrupoPase(List<MGrupo> lista, OnGrupoClickListener listener) {
        this.listaGrupos = lista;
        this.listener = listener;
        this.listaOriginal = new ArrayList<>();
        this.listaOriginal.addAll(lista);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo_pase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MGrupo grupo = listaGrupos.get(position);

        holder.txtClave.setText(grupo.getClave());
        holder.txtAsignatura.setText(grupo.getAsignatura());
        holder.txtPeriodo.setText(grupo.getPeriodo());
        holder.txtHorario.setText("Horario: " + grupo.getHorario());

        // ---------- BOTÓN TOMAR ASISTENCIA ----------
        holder.btnTomarAsistencia.setOnClickListener(v -> {
            if (listener != null) listener.onTomarAsistenciaClick(grupo);
        });

        // ---------- BOTÓN CONSULTAR ----------
        holder.btnConsultar.setOnClickListener(v -> {
            if (listener != null) listener.onConsultarClick(grupo);
        });
    }

    @Override
    public int getItemCount() {
        return listaGrupos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtClave, txtAsignatura, txtPeriodo, txtInscritos, txtHorario;
        ImageButton btnTomarAsistencia, btnConsultar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtClave = itemView.findViewById(R.id.item_grupo_pase_txtclave);
            txtAsignatura = itemView.findViewById(R.id.item_grupo_pase_txtasignatura);
            txtPeriodo = itemView.findViewById(R.id.item_grupo_pase_txtperiodo);
            txtHorario = itemView.findViewById(R.id.item_grupo_pase_txthorario);

            btnTomarAsistencia = itemView.findViewById(R.id.item_grupo_pase_btnRegistrar);
            btnConsultar = itemView.findViewById(R.id.item_grupo_pase_btnConsultar);
        }
    }

    // ---------- MÉTODO FILTRAR ----------
    public void filtrar(String texto) {
        int longitud = texto.length();
        listaGrupos.clear();
        if (longitud == 0) {
            listaGrupos.addAll(listaOriginal);
        } else {
            for (MGrupo g : listaOriginal) {
                if (g.getClave().toLowerCase().contains(texto.toLowerCase()) ||
                        g.getAsignatura().toLowerCase().contains(texto.toLowerCase())) {
                    listaGrupos.add(g);
                }
            }
        }
        notifyDataSetChanged();
    }
}
