package com.itsoeh.hmmayte.docente;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrarGrupo extends Fragment {

    private TextInputEditText txtClave, txtAsignatura, txtPeriodo, txtCarrera;
    private Spinner spEstado;
    private CardView btnGuardar;

    private int idDocente = -1;   // ← AQUÍ SE GUARDARÁ EL ID DEL DOCENTE

    private static final String ARG_ID_DOCENTE = "id_docente";

    public RegistrarGrupo() { }

    public static RegistrarGrupo newInstance(int idDocente) {
        RegistrarGrupo fragment = new RegistrarGrupo();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_DOCENTE, idDocente);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registrar_grupo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener ID del docente desde el Bundle
        if (getArguments() != null) {
            idDocente = getArguments().getInt(ARG_ID_DOCENTE, -1);
        }

        vinculadores(view);
        inicializarSpinners(view);
        escuchadores();
    }

    private void vinculadores(View view) {
        txtClave = view.findViewById(R.id.perfil_docente_txtClave);
        txtAsignatura = view.findViewById(R.id.perfil_usuario_txtAsignatura);
        txtPeriodo = view.findViewById(R.id.perfil_docente_txtPeriodo);
        txtCarrera = view.findViewById(R.id.perfil_docente_txtCarrera);
        spEstado = view.findViewById(R.id.sp_Estado);
        btnGuardar = view.findViewById(R.id.login_btnentrar);
    }

    private void inicializarSpinners(View view) {
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.estado,
                android.R.layout.simple_spinner_item
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstado);
    }

    private void escuchadores() {
        btnGuardar.setOnClickListener(v -> clicGuardar());
    }

    private void clicGuardar() {
        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Por favor espere", "Registrando grupo...");

        RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, API.GUARDAR_GRUPO,
                response -> {
                    dialogo.cerrarDialogo();

                    try {
                        JSONObject obj = new JSONObject(response);
                        String op = obj.getString("msg");
                        if (op.equals("true")) {
                            dialogo.mostrarDialogoBoton("Aviso", "Grupo registrado corectamente");
                        } else {
                            dialogo.mostrarDialogoBoton("Error", "No se ha registrado el grupo");
                        }
                    } catch (Exception ex) {
                        dialogo.mostrarDialogoBoton("", "DATOS ACTUALIZADOS");
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    dialogo.mostrarDialogoBoton("Error", "No se pudo conectar al servidor");
                }) {



            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("clave", txtClave.getText().toString());
                params.put("periodo", txtPeriodo.getText().toString());
                params.put("carrera", txtCarrera.getText().toString());
                params.put("id_docente", String.valueOf(idDocente));
                params.put("estado", "1");
                params.put("asignatura", txtAsignatura.getText().toString());
                return params;
            }
        };

        queue.add(request);
    }

}
