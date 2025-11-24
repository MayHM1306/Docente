package com.itsoeh.hmmayte.docente;

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
import com.itsoeh.hmmayte.docente.adapter.AdapterGrupo;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Grupos extends Fragment {

    private RecyclerView recycler;
    private AdapterGrupo adapter;
    private FloatingActionButton btnMas;
    private ArrayList<MGrupo> listaGrupos;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Grupos() { }

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
        btnMas   = view.findViewById(R.id.grupo_fabAgregar);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        listaGrupos = new ArrayList<>();

        adapter = new AdapterGrupo(listaGrupos, new AdapterGrupo.OnGrupoClickListener() {
            @Override
            public void onEditarClick(MGrupo grupo) {
                // Ir al fragmento ModificarGrupo con el id_grupo
                ModificarGrupo frag = new ModificarGrupo();
                Bundle args = new Bundle();
                args.putInt("id_grupo", grupo.getId_grupo());
                frag.setArguments(args);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.menu_contenedor_interno, frag)
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
        String url = "http://192.168.0.16/wsescuela/apiG.php?api=listargrupos";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        // Para depurar en Logcat
                        Log.d("GRUPOS_API", "Respuesta: " + response);

                        listaGrupos.clear();

                        // Aseguramos que no traiga espacios raros
                        JSONObject root = new JSONObject(response.trim());

                        if (!root.has("msg")) {
                            Toast.makeText(
                                    getContext(),
                                    "Respuesta sin campo 'msg'",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        Object msgObj = root.get("msg");

                        // Caso 1: msg es un arreglo (lo normal)
                        if (msgObj instanceof JSONArray) {
                            JSONArray msgArray = (JSONArray) msgObj;

                            for (int i = 0; i < msgArray.length(); i++) {
                                JSONObject g = msgArray.getJSONObject(i);

                                int idGrupo    = g.optInt("id_grupo", 0);
                                String clave   = g.optString("clave", "");
                                String periodo = g.optString("periodo", "");
                                String carrera = g.optString("carrera", "");

                                // Puede venir "Asignatura" o "asignatura"
                                String asignatura = g.optString(
                                        "Asignatura",
                                        g.optString("asignatura", "")
                                );

                                int estado        = g.optInt("estado", 0);
                                int inscripciones = g.optInt("inscripciones", 0);

                                String horario = g.optString("horarios", "Sin horario");
                                if (horario == null || horario.equals("null")) {
                                    horario = "Sin horario";
                                }

                                int idDocente = 0; // no viene en esta API

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

                            // Caso 2: msg es un String (ejemplo: "No hay grupos")
                        } else if (msgObj instanceof String) {
                            String msgStr = (String) msgObj;
                            Toast.makeText(
                                    getContext(),
                                    msgStr,
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    "Formato de respuesta desconocido",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(
                                getContext(),
                                "Error al procesar información del servidor",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(
                            getContext(),
                            "Error de conexión: " + error.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

}
