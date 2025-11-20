package com.itsoeh.hmmayte.docente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.itsoeh.hmmayte.docente.adapter.AdapterGrupoPase;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.modelo.MDocente;
import com.itsoeh.hmmayte.docente.modelo.MGrupo;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaseLista extends Fragment {

    private RecyclerView recyclerViewGrupos;
    private AdapterGrupoPase adaptador;
    private List<MGrupo> listaGrupos;
    private EditText txtBuscar;

    private SharedPreferences prefs;
    private MDocente objUser;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pase_lista, container, false);

        vincularComponentes(view);

        adaptador = new AdapterGrupoPase(listaGrupos, new AdapterGrupoPase.OnGrupoClickListener() {

            @Override
            public void onTomarAsistenciaClick(MGrupo grupo) {
                Bundle args = new Bundle();
                args.putInt("id_grupo", grupo.getId_grupo());

                Intent intent = new Intent(getContext(), Escaner.class);
                intent.putExtra("id_grupo", grupo.getId_grupo());
                startActivity(intent);
            }

            @Override
            public void onConsultarClick(MGrupo grupo) {
                Bundle args = new Bundle();
                args.putInt("id_grupo", grupo.getId_grupo());

                Intent intent = new Intent(getContext(), EstadisticasEstudiantes.class);
                intent.putExtra("id_grupo", grupo.getId_grupo());
                startActivity(intent);
            }


        });

        recyclerViewGrupos.setAdapter(adaptador);

        return view;
    }

    private void vincularComponentes(View view) {
        txtBuscar = view.findViewById(R.id.listargrupo_buscador);
        recyclerViewGrupos = view.findViewById(R.id.listargrupo_recyclerview);

        recyclerViewGrupos.setLayoutManager(new LinearLayoutManager(getContext()));
        listaGrupos = new ArrayList<>();
        objUser = cargarDocente();
        cargarGruposDesdeBD(objUser.getId_docente());
    }

    private MDocente cargarDocente() {
        prefs = requireActivity().getSharedPreferences("MisPreferencias", getContext().MODE_PRIVATE);
        String objeto = prefs.getString("usuario", "");
        Gson gson = new Gson();
        MDocente modelo = gson.fromJson(objeto, MDocente.class);
        return modelo;
    }

    private void cargarGruposDesdeBD(int idDocente) {
        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Cargando grupos", "Espere un momento...");

        RequestQueue cola = VolleySingleton.getInstance(getContext()).getRequestQueue();

        StringRequest solicitud = new StringRequest(Request.Method.POST, API.DOC_LISTAR_GRUPOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogo.cerrarDialogo();
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray array = json.getJSONArray("msg");

                            listaGrupos.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject pos = array.getJSONObject(i);

                                MGrupo g = new MGrupo();
                                g.setId_grupo(pos.getInt("id_grupo"));
                                g.setClave(pos.getString("clave"));
                                g.setPeriodo(pos.getString("periodo"));
                                g.setCarrera(pos.getString("carrera"));
                                g.setAsignatura(pos.getString("Asignatura"));
                                g.setEstado(pos.getInt("estado"));
                                g.setInscripciones(pos.getInt("inscripciones"));
                                g.setHorario(pos.getString("horarios"));

                                listaGrupos.add(g);
                            }

                            configurarAdapter();

                        } catch (Exception e) {
                            dialogo.mostrarDialogoBoton("Error", "Interpretación JSON\n" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogo.cerrarDialogo();
                        dialogo.mostrarDialogoBoton("Error", "Fallo al conectar con el servidor.");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id_docente", idDocente + "");
                return param;
            }
        };

        cola.add(solicitud);
    }

    private void configurarAdapter() {

        adaptador = new AdapterGrupoPase(listaGrupos, new AdapterGrupoPase.OnGrupoClickListener() {
            @Override
            public void onTomarAsistenciaClick(MGrupo grupo) {
                Intent intent = new Intent(getContext(), Escaner.class);
                intent.putExtra("id_grupo", grupo.getId_grupo());
                startActivity(intent);
            }
            @Override
            public void onConsultarClick(MGrupo grupo) {
                Intent intent = new Intent(getContext(), EstadisticasEstudiantes.class);
                intent.putExtra("id_grupo", grupo.getId_grupo());
                startActivity(intent);
            }

        });

        recyclerViewGrupos.setAdapter(adaptador);

        txtBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptador.filtrar(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
