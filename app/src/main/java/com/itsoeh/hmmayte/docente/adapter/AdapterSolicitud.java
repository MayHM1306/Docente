package com.itsoeh.hmmayte.docente.adapter;


import static com.itsoeh.hmmayte.docente.conexion.VolleySingleton.context;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.modelo.MSolicitud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterSolicitud extends RecyclerView.Adapter<AdapterSolicitud.ViewHolder> {

    ArrayList<MSolicitud> lista;
    private ArrayList<MSolicitud> listaCompleta;

    Context context;

    public AdapterSolicitud(ArrayList<MSolicitud> lista, Context context) {
        this.lista = lista;
        this.listaCompleta = new ArrayList<>(lista); // copia de respaldo
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MSolicitud m = lista.get(position);

        String nombreCompleto = m.getNombre() + " " + m.getApp() + " " + m.getApm();

        holder.txtNombreEstudiante.setText(nombreCompleto);
        holder.txtMatricula.setText("Matrícula: " + m.getMatricula());
        holder.txtGrupo.setText("Grupo solicitado: " + m.getGrupo());

        holder.btnAceptar.setOnClickListener(v -> {
            aceptarSolicitud(m.getIdSolicitud(), holder.getAdapterPosition());
        });

        holder.btnRechazar.setOnClickListener(v -> {
            rechazarSolicitud(m.getIdSolicitud(), holder.getAdapterPosition());
        });

    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombreEstudiante, txtMatricula, txtGrupo;
        Button btnAceptar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombreEstudiante = itemView.findViewById(R.id.txtNombreEstudiante);
            txtMatricula = itemView.findViewById(R.id.txtMatricula);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);

            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }

    private void aceptarSolicitud(int id_inscripcion, int position) {

        StringRequest request = new StringRequest(Request.Method.POST, API.ACEPTAR_SOLICITUD,
                response -> {
                    // Quitar de ambas listas
                    listaCompleta.remove(lista.get(position));
                    lista.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, lista.size());

                    Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(context, "Error al aceptar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_inscripcion", String.valueOf(id_inscripcion));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void rechazarSolicitud(int id_inscripcion, int position) {

        StringRequest request = new StringRequest(Request.Method.POST, API.RECHAZAR_SOLICITUD,
                response -> {
                    listaCompleta.remove(lista.get(position));
                    lista.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, lista.size());

                    Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(context, "Error al rechazar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_inscripcion", String.valueOf(id_inscripcion));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public void filtrar(String texto) {
        lista.clear();

        if (texto.isEmpty()) {
            lista.addAll(listaCompleta);
        } else {
            texto = texto.toLowerCase();
            for (MSolicitud s : listaCompleta) {
                String nombreCompleto = (s.getNombre() + " " + s.getApp() + " " + s.getApm()).toLowerCase();
                if (nombreCompleto.contains(texto) || s.getMatricula().contains(texto)) {
                    lista.add(s);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void actualizarListaOriginal() {
        listaCompleta = new ArrayList<>(lista);
    }


}
