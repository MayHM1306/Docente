package com.itsoeh.hmmayte.docente.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.itsoeh.hmmayte.docente.R;
import com.itsoeh.hmmayte.docente.modelo.MEstudiante;

import java.util.List;

public class AdapterAsistencia extends RecyclerView.Adapter<AdapterAsistencia.ViewHolder> {

    private List<MEstudiante> lista;
    private Context context;

    public AdapterAsistencia(List<MEstudiante> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_estudiante_escaneado, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MEstudiante estudiante = lista.get(position);

        holder.nombre.setText(estudiante.getNombre());
        holder.matricula.setText(estudiante.getMatricula());

        // Adaptador del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.lista_asistencia,  // ["PRESENTE","FALTA"] en strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spEstado.setAdapter(adapter);

        String[] opciones = {"Sin marcar", "Asistencia", "Retardo", "Falta", "Justificado"};

        String valor = estudiante.getAsistencia();
        int pos = 0; // default

        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(valor)) {
                pos = i;
                break;
            }
        }

        holder.spEstado.setSelection(pos);

    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, matricula;
        Spinner spEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.item_est_escaneado_txtNombreEstudiante);
            matricula = itemView.findViewById(R.id.item_est_escaneado_txtCodigoAlumno);
            spEstado = itemView.findViewById(R.id.item_est_escaneado_spinnerAsistencia);
        }
    }

}