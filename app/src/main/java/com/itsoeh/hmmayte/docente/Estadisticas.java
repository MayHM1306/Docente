package com.itsoeh.hmmayte.docente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.itsoeh.hmmayte.docente.adapter.AdapterEstadisticasGrupos;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.modelo.MDocente;
import com.itsoeh.hmmayte.docente.modelo.MEstadisticaGrupo;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Estadisticas extends Fragment {

    private static final String TAG = "ESTADISTICAS_JSON";

    private RecyclerView recyclerViewGrupos;
    private AdapterEstadisticasGrupos adaptador;
    private ArrayList<MEstadisticaGrupo> listaGrupos;
    private EditText txtBuscar;
    private SharedPreferences prefs;
    private MDocente objUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        vincularComponentes(view);
        return view;
    }

    private void vincularComponentes(View view) {
        txtBuscar = view.findViewById(R.id.buscar_grupo_estadisticas);
        recyclerViewGrupos = view.findViewById(R.id.rv_estadisticas_grupos);
        recyclerViewGrupos.setLayoutManager(new LinearLayoutManager(requireContext()));

        listaGrupos = new ArrayList<>();
        objUser = cargarDocente();
        cargarGruposDesdeBD(objUser.getId_docente());
    }

    private MDocente cargarDocente() {
        prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String objeto = prefs.getString("usuario", "");
        return new Gson().fromJson(objeto, MDocente.class);
    }

    private void cargarGruposDesdeBD(int idDocente) {

        Dialogo dialogo = new Dialogo(requireContext());
        dialogo.mostrarDialogoProgress("Cargando grupos", "Espere un momento...");

        RequestQueue cola = VolleySingleton.getInstance(requireContext()).getRequestQueue();

        StringRequest solicitud = new StringRequest(Request.Method.POST, API.BUSCAR_ASISTENCIAS_POR_GRUPO,
                response -> {
                    dialogo.cerrarDialogo();
                    try {
                        // DEBUG: ver la respuesta cruda
                        System.out.println("RESPUESTA CRUDA: " + response);

                        JSONObject json = new JSONObject(response);

                        if (!json.has("data")) {
                            dialogo.mostrarDialogoBoton("Error", "No se encontró el campo 'data' en la respuesta");
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        // Obtener datos del grupo
                        int id = data.getInt("id_grupo");
                        String nombre = data.getString("clave");
                        float porcentajeAsistencias = Float.parseFloat(data.getString("porcentaje_asistencias"));
                        float porcentajeRetardos = Float.parseFloat(data.getString("porcentaje_retardos"));
                        float porcentajeFaltas = Float.parseFloat(data.getString("porcentaje_faltas"));
                        float porcentajeJustificaciones = Float.parseFloat(data.getString("porcentaje_justificaciones"));

                        // Limpiar lista y agregar nuevo objeto
                        listaGrupos.clear();
                        MEstadisticaGrupo est = new MEstadisticaGrupo(id, nombre,
                                porcentajeAsistencias, porcentajeRetardos, porcentajeFaltas, porcentajeJustificaciones);
                        listaGrupos.add(est);

                        // Configurar adaptador
                        configurarAdapter();

                    } catch (Exception e) {
                        dialogo.mostrarDialogoBoton("Error", "Error al interpretar JSON:\n" + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    dialogo.mostrarDialogoBoton("Error", "Error de conexión:\n" + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id_docente", String.valueOf(idDocente));
                return param;
            }
        };

        cola.add(solicitud);
    }


    private void configurarAdapter() {
        adaptador = new AdapterEstadisticasGrupos(requireContext(), listaGrupos, grupo -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id_grupo", grupo.getIdGrupo());
            bundle.putString("nombre_grupo", grupo.getNombreGrupo());

            Estadisticas fragmentDetalle = new Estadisticas();
            fragmentDetalle.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_contenedor_interno, fragmentDetalle)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerViewGrupos.setAdapter(adaptador);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptador.filtrar(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
