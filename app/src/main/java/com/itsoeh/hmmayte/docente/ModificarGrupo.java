package com.itsoeh.hmmayte.docente;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.modelo.MDocente;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModificarGrupo extends Fragment {

    private TextInputEditText txtClave, txtPeriodo, txtCarrera, txtIdDocente, txtAsignatura;
    private Spinner spEstado;
    private CardView crvGuardar;

    private int idGrupo = -1;
    private int idDocente = -1;

    public ModificarGrupo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modificar_grupo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vinculadores(view);
        configurarSpinners();
        cargarPaquete();
        escucharBoton();
    }

    private void vinculadores(View view) {
        txtClave = view.findViewById(R.id.grupo_txtClave);
        txtPeriodo = view.findViewById(R.id.grupo_txtPeriodo);
        txtCarrera = view.findViewById(R.id.grupo_txtCarrera);
        txtIdDocente = view.findViewById(R.id.grupo_txtIdDocente);
        txtAsignatura = view.findViewById(R.id.grupo_txtAsignatura);

        spEstado = view.findViewById(R.id.sp_Estado);
        crvGuardar = view.findViewById(R.id.login_btnentrar);
    }

    private void configurarSpinners() {
        String[] estados = getResources().getStringArray(R.array.estado);
        ArrayAdapter<String> adapterEstados =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstados);
    }

    private void cargarPaquete() {
        // Leer id_grupo del Bundle
        Bundle paquete = getArguments();
        if (paquete != null) {
            idGrupo = paquete.getInt("id_grupo", -1);
        }

        // Leer id_docente desde SharedPreferences
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        String usuarioJson = prefs.getString("usuario", null);
        if (usuarioJson != null) {
            MDocente doc = new Gson().fromJson(usuarioJson, MDocente.class);
            idDocente = doc.getId_docente();
        }

        if (idDocente != -1) {
            txtIdDocente.setText(String.valueOf(idDocente));
            txtIdDocente.setEnabled(false);
        }

        // Cargar modelo si hay idGrupo
        if (idGrupo != -1) {
            cargarModelo(idGrupo);
        }
    }

    private void escucharBoton() {
        crvGuardar.setOnClickListener(view -> clicActualizar());
    }

    private void cargarModelo(int idGrupo) {
        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Por favor espere", "Cargando grupo...");

        RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, API.GRUPO_ID,
                response -> {
                    dialogo.cerrarDialogo();
                    try {
                        JSONArray array;

                        // Primero intenta parsear como objeto con "msg"
                        try {
                            JSONObject root = new JSONObject(response);
                            array = root.getJSONArray("msg");
                        } catch (Exception e) {
                            // Si falla, prueba parsear como array directo
                            array = new JSONArray(response);
                        }

                        if (array.length() > 0) {
                            JSONObject reg = array.getJSONObject(0);

                            MGrupo grupo = new MGrupo();
                            grupo.setClave(reg.getString("clave"));
                            grupo.setPeriodo(reg.getString("periodo"));
                            grupo.setCarrera(reg.getString("carrera"));
                            grupo.setEstado(reg.getInt("estado"));
                            grupo.setAsignatura(reg.getString("asignatura"));
                            grupo.setIdDocente(idDocente);

                            mostrarDatos(grupo);
                        } else {
                            dialogo.mostrarDialogoBoton("Aviso", "No se encontró el grupo");
                        }

                    } catch (Exception ex) {
                        dialogo.mostrarDialogoBoton("Error", "Formato incorrecto: " + ex.getMessage());
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    dialogo.mostrarDialogoBoton("Error", "No se pudo conectar al servidor");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_grupo", String.valueOf(idGrupo));
                return params;
            }
        };

        queue.add(request);
    }


    private void mostrarDatos(MGrupo grupo) {
        txtClave.setText(grupo.getClave());
        txtPeriodo.setText(grupo.getPeriodo());
        txtCarrera.setText(grupo.getCarrera());
        txtIdDocente.setText(String.valueOf(grupo.getIdDocente()));
        txtAsignatura.setText(grupo.getAsignatura());

        spEstado.setSelection(grupo.getEstado());
    }

    private void clicActualizar() {
        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Por favor espere", "Actualizando datos...");

        RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, API.MOD_GRUPO,
                response -> {
                    dialogo.cerrarDialogo();
                    try {
                        JSONObject obj = new JSONObject(response);
                        String op = obj.getString("msg");

                        if ("true".equals(op)) {
                            dialogo.mostrarDialogoBoton("Aviso", "Datos actualizados");
                        } else if ("duplicado".equals(op)) {
                            dialogo.mostrarDialogoBoton("Error", "Ya existe un grupo con esa clave y periodo");
                        } else {
                            dialogo.mostrarDialogoBoton("Error", "No se pudo actualizar");
                        }
                    } catch (Exception ex) {
                        dialogo.mostrarDialogoBoton("Aviso", "Datos actualizados");
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    dialogo.mostrarDialogoBoton("Error", "Verifique su conexión a Internet");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_grupo", String.valueOf(idGrupo));
                params.put("clave", txtClave.getText().toString().trim());
                params.put("periodo", txtPeriodo.getText().toString().trim());
                params.put("carrera", txtCarrera.getText().toString().trim());
                params.put("id_docente", String.valueOf(idDocente));
                params.put("estado", String.valueOf(spEstado.getSelectedItemPosition()));
                params.put("asignatura", txtAsignatura.getText().toString().trim());

                return params;
            }
        };

        queue.add(request);
    }
}
