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

import java.util.List;

public class AdapterGrupo extends RecyclerView.Adapter<AdapterGrupo.ViewHolderGrupo> {

    private List<MGrupo> lista;
    private OnGrupoClickListener listener;

    // Listener para manejar clics desde el Fragment
    public interface OnGrupoClickListener {
        void onEditarClick(MGrupo grupo);
        void onItemClick(MGrupo grupo);   // opcional: clic en toda la tarjeta
    }

    public AdapterGrupo(List<MGrupo> lista, OnGrupoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderGrupo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo, parent, false);
        return new ViewHolderGrupo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGrupo holder, int position) {
        MGrupo grupo = lista.get(position);

        holder.txtAsignatura.setText(grupo.getAsignatura());
        holder.txtClave.setText(grupo.getClave());
        holder.txtPeriodo.setText(grupo.getPeriodo());
        holder.txtHorario.setText(grupo.getHorario());
        holder.txtCarrera.setText(grupo.getCarrera());
        holder.txtInscripciones.setText("Total de estudiantes: " + grupo.getInscripciones());

        // Clic en toda la tarjeta (si lo quieres usar para tomar asistencia o ver detalle)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(grupo);
        });

        // Clic en botón Modificar
        holder.btnModificar.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(grupo);
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    // Si más adelante quieres actualizar la lista desde el Fragment
    public void setLista(List<MGrupo> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class ViewHolderGrupo extends RecyclerView.ViewHolder {

        TextView txtAsignatura, txtClave, txtPeriodo, txtHorario, txtCarrera, txtInscripciones;
        ImageButton btnModificar;

        public ViewHolderGrupo(@NonNull View itemView) {
            super(itemView);
            txtAsignatura = itemView.findViewById(R.id.grupo_txtNombre);
            txtClave = itemView.findViewById(R.id.grupo_txtCodigo);
            txtPeriodo = itemView.findViewById(R.id.grupo_txtPeriodo);
            txtHorario = itemView.findViewById(R.id.grupo_txtHorario);
            txtCarrera = itemView.findViewById(R.id.grupo_txtCarrera);
            txtInscripciones = itemView.findViewById(R.id.grupo_txtTotalEstudiantes);
            btnModificar = itemView.findViewById(R.id.btnModificar);
        }
    }
}
