package com.itsoeh.hmmayte.docente.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;

import java.util.ArrayList;
import java.util.List;

public class AdapterGrupo extends RecyclerView.Adapter<AdapterGrupo.ViewHolderGrupo> {

    private List<MGrupo> lista;
    private OnGrupoClickListener listener;

    public interface OnGrupoClickListener {
        void onTomarAsistenciaClick(MGrupo grupo);
        void onEditarClick(MGrupo grupo);
        void onEliminarClick(MGrupo grupo);
    }

    public AdapterGrupo(List<MGrupo> lista, OnGrupoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderGrupo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo, null, false); // ✅ CORRECTO
        return new ViewHolderGrupo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGrupo.ViewHolderGrupo holder, int position) {
        MGrupo grupo = this.lista.get(position);
        holder.txtAsignatura.setText(grupo.getAsignatura());
        holder.txtClave.setText(grupo.getClave());
        holder.txtPeriodo.setText(grupo.getPeriodo());
        holder.txtHorario.setText(grupo.getHorario());
        holder.txtCarrera.setText(grupo.getCarrera());
        holder.txtInscripciones.setText("Total de estudiantes = " + grupo.getInscripciones());

        // Click en el item para tomar asistencia
        holder.itemView.setOnClickListener(v -> listener.onTomarAsistenciaClick(grupo));

        // Click en los íconos
        holder.imgEditar.setOnClickListener(v -> listener.onEditarClick(grupo));
        holder.imgEliminar.setOnClickListener(v -> listener.onEliminarClick(grupo));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder interno
    public static class ViewHolderGrupo extends RecyclerView.ViewHolder {

        TextView txtAsignatura, txtClave, txtPeriodo, txtHorario, txtCarrera, txtInscripciones;
        ImageView imgEditar, imgEliminar;

        public ViewHolderGrupo(@NonNull View itemView) {
            super(itemView);
            txtAsignatura = itemView.findViewById(R.id.grupo_txtNombre);
            txtClave = itemView.findViewById(R.id.grupo_txtCodigo);
            txtPeriodo = itemView.findViewById(R.id.grupo_txtPeriodo);
            txtHorario = itemView.findViewById(R.id.grupo_txtHorario);
            txtCarrera = itemView.findViewById(R.id.grupo_txtCarrera);
            txtInscripciones = itemView.findViewById(R.id.grupo_txtTotalEstudiantes);
            imgEditar = itemView.findViewById(R.id.btnModificar);
        }
    }
}
