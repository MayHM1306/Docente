package com.itsoeh.hmmayte.docente.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MEstudiante;

import java.util.ArrayList;

public class AdapterEstudiantesEscaneados extends RecyclerView.Adapter<AdapterEstudiantesEscaneados.ViewHolder> {

    private ArrayList<MEstudiante> lista;
    private final String[] opciones = {"Sin marcar", "Asistencia", "Retardo", "Falta", "Justificado"};

    public AdapterEstudiantesEscaneados(ArrayList<MEstudiante> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estudiante_escaneado, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        MEstudiante e = lista.get(position);

        // ----- Mostrar datos -----
        h.txtMatricula.setText(e.getMatricula());
        h.txtNombre.setText(e.getNombre() + " " + e.getApp() + " " + e.getApm());

        // ----- Cargar opciones del Spinner una sola vez -----
        if (h.spinner.getAdapter() == null) {

            ArrayAdapter<String> adapterSpinner =
                    new ArrayAdapter<>(h.itemView.getContext(),
                            android.R.layout.simple_spinner_item,
                            opciones);

            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            h.spinner.setAdapter(adapterSpinner);
        }

        // ----- Selección segura del spinner -----
        String valor = e.getAsistencia();
        int pos = 0;

        if (valor != null) {
            for (int i = 0; i < opciones.length; i++) {
                if (opciones[i].equals(valor)) {
                    pos = i;
                    break;
                }
            }
        }

        h.spinner.setSelection(pos);

        // ----- Evento del spinner -----
        h.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                e.setAsistencia(opciones[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // 🚀 Método para marcar la asistencia automáticamente cuando el escáner detecta un alumno
    public void marcarAsistencia(String matricula) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getMatricula().equals(matricula)) {

                lista.get(i).setAsistencia("Asistencia");

                notifyItemChanged(i); // refresca SOLO ese item
                break;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMatricula, txtNombre;
        Spinner spinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMatricula = itemView.findViewById(R.id.item_est_escaneado_txtCodigoAlumno);
            txtNombre = itemView.findViewById(R.id.item_est_escaneado_txtNombreEstudiante);
            spinner = itemView.findViewById(R.id.item_est_escaneado_spinnerAsistencia);
        }
    }
    public ArrayList<MEstudiante> getLista() {
        return lista;
    }

}
