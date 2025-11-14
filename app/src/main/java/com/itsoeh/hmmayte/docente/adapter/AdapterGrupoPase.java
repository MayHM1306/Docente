package com.itsoeh.hmmayte.docente.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;

import java.util.ArrayList;

public class AdapterGrupoPase extends RecyclerView.Adapter<AdapterGrupoPase.ViewHolderGrupo> {
    private ArrayList <MGrupo> lista;
    public AdapterGrupoPase(ArrayList lista){
        this.lista=lista;
    }

    @NonNull
    @Override
    public ViewHolderGrupo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grupo_pase, null, false);
        return new ViewHolderGrupo(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderGrupo holder, int position) {
        MGrupo grupo = this.lista.get(position);
        holder.txtAsignatura.setText(grupo.getAsignatura());
        holder.txtClave.setText(grupo.getClave());
        holder.txtPeriodo.setText(grupo.getPeriodo());
        holder.txtHorario.setText(grupo.getHorario());
    }

    @Override
    public int getItemCount() {
        return this.lista.size();
    }

    public class ViewHolderGrupo extends RecyclerView.ViewHolder{
        TextView txtAsignatura, txtClave, txtPeriodo, txtHorario;
        private ImageButton imgCamara;
        private ImageButton imgCamar;
        public ViewHolderGrupo(@NonNull View itemView) {
            super(itemView);
            txtAsignatura = itemView.findViewById(R.id.item_grupo_pase_txtasignatura);
            txtClave = itemView.findViewById(R.id.item_grupo_pase_txtclave);
            txtPeriodo = itemView.findViewById(R.id.item_grupo_pase_txtperiodo);
            txtHorario = itemView.findViewById(R.id.item_grupo_pase_txthorario);
            imgCamara = itemView.findViewById(R.id.item_grupo_pase_btnRegistrar);
        }
    }
}