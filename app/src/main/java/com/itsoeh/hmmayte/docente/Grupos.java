package com.itsoeh.hmmayte.docente;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.itsoeh.hmmayte.docente.adapter.AdapterGrupo;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.modelo.MDocente;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Grupos extends Fragment {

    private RecyclerView recycler;
    private AdapterGrupo adapter;
    private FloatingActionButton btnMas;
    private ArrayList<MGrupo> listaGrupos;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Grupos() {
    }

    public static Grupos newInstance(String param1, String param2) {
        Grupos fragment = new Grupos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grupos, container, false);

        recycler = view.findViewById(R.id.grupo_recyclerView);
        btnMas = view.findViewById(R.id.grupo_fabAgregar);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        listaGrupos = new ArrayList<>();

        adapter = new AdapterGrupo(listaGrupos, new AdapterGrupo.OnGrupoClickListener() {
            @Override
            public void onEditarClick(MGrupo grupo) {
                Bundle bundle = new Bundle();
                bundle.putInt("id_grupo", grupo.getId_grupo());
                ModificarGrupo fragment = new ModificarGrupo();
                fragment.setArguments(bundle);


                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.menu_contenedor_interno, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            @Override
            public void onItemClick(MGrupo grupo) {
                // Aquí puedes abrir detalle, pase de lista, etc.
                Toast.makeText(
                        getContext(),
                        "Grupo: " + grupo.getAsignatura(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        recycler.setAdapter(adapter);

        // FAB: ir a RegistrarGrupo
        btnMas.setOnClickListener(v -> {
            RegistrarGrupo frag = new RegistrarGrupo();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_contenedor_interno, frag)
                    .addToBackStack(null)
                    .commit();
        });

        cargarGruposDesdeApi();

        return view;
    }

    private void cargarGruposDesdeApi() {

        Toast.makeText(getContext(), "Cargando grupos...", Toast.LENGTH_SHORT).show();

        // Obtener id_docente desde SharedPreferences, como en PaseLista
        SharedPreferences prefs = requireActivity().getSharedPreferences("MisPreferencias", getContext().MODE_PRIVATE);
        String objeto = prefs.getString("usuario", "");
        MDocente objUser = new Gson().fromJson(objeto, MDocente.class);
        int idDocente = objUser.getId_docente();

        StringRequest solicitud = new StringRequest(
                Request.Method.POST,
                API.DOC_LISTAR_GRUPOS,
                response -> {
                    Log.d("GRUPOS_API", "Respuesta: " + response); // debug
                    try {
                        listaGrupos.clear();

                        JSONObject root = new JSONObject(response.trim());
                        if (!root.has("msg")) {
                            Toast.makeText(getContext(), "Campo 'msg' no encontrado", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONArray array = root.getJSONArray("msg");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject g = array.getJSONObject(i);

                            int idGrupo = g.optInt("id_grupo", 0);
                            String clave = g.optString("clave", "");
                            String periodo = g.optString("periodo", "");
                            String carrera = g.optString("carrera", "");
                            String asignatura = g.optString("Asignatura", g.optString("asignatura", ""));
                            int estado = g.optInt("estado", 0);
                            int inscripciones = g.optInt("inscripciones", 0);
                            String horario = g.optString("horarios", "Sin horario");

                            MGrupo grupo = new MGrupo(
                                    idGrupo,
                                    idDocente,
                                    clave,
                                    periodo,
                                    carrera,
                                    asignatura,
                                    estado,
                                    inscripciones,
                                    horario
                            );

                            listaGrupos.add(grupo);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id_docente", String.valueOf(idDocente));
                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(solicitud);
    }

}
