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

public class AdapterGrupoPase extends RecyclerView.Adapter<AdapterGrupoPase.ViewHolderGrupo> {

    private List<MGrupo> listaOriginal;
    private List<MGrupo> listaFiltrada;
    private OnGrupoClickListener listener;

    // ✔ Interfaz del listener
    public interface OnGrupoClickListener {
        void onTomarAsistenciaClick(MGrupo grupo);
    }

    // ✔ Constructor correcto
    public AdapterGrupoPase(List<MGrupo> lista, OnGrupoClickListener listener) {
        this.listaOriginal = lista;
        this.listaFiltrada = new ArrayList<>(lista);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderGrupo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo_pase, parent, false);
        return new ViewHolderGrupo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGrupo holder, int position) {
        MGrupo grupo = listaFiltrada.get(position);

        holder.txtAsignatura.setText(grupo.getAsignatura());
        holder.txtClave.setText(grupo.getClave());
        holder.txtPeriodo.setText(grupo.getPeriodo());
        holder.txtHorario.setText(grupo.getHorario());

        // ✔ Clic en el botón de cámara
        holder.btnTomarAsistencia.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTomarAsistenciaClick(grupo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    // ✔ MÉTODO PARA FILTRAR
    public void filtrar(String texto) {
        texto = texto.toLowerCase();
        listaFiltrada.clear();

        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            for (MGrupo g : listaOriginal) {
                if (g.getAsignatura().toLowerCase().contains(texto) ||
                        g.getClave().toLowerCase().contains(texto) ||
                        g.getPeriodo().toLowerCase().contains(texto)) {
                    listaFiltrada.add(g);
                }
            }
        }

        notifyDataSetChanged();
    }

    // ---------- ViewHolder ----------
    public static class ViewHolderGrupo extends RecyclerView.ViewHolder {

        TextView txtAsignatura, txtClave, txtPeriodo, txtHorario;
        ImageButton btnTomarAsistencia;

        public ViewHolderGrupo(@NonNull View itemView) {
            super(itemView);

            txtAsignatura = itemView.findViewById(R.id.item_grupo_pase_txtasignatura);
            txtClave = itemView.findViewById(R.id.item_grupo_pase_txtclave);
            txtPeriodo = itemView.findViewById(R.id.item_grupo_pase_txtperiodo);
            txtHorario = itemView.findViewById(R.id.item_grupo_pase_txthorario);
            btnTomarAsistencia = itemView.findViewById(R.id.item_grupo_pase_btnRegistrar);
        }
    }
}
