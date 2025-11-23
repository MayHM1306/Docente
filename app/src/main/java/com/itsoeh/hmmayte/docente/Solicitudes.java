package com.itsoeh.hmmayte.docente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.itsoeh.hmmayte.docente.adapter.AdapterSolicitud;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.modelo.MDocente;
import com.itsoeh.hmmayte.docente.modelo.MSolicitud;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solicitudes extends Fragment {

    RecyclerView recycler;
    AdapterSolicitud adapter;
    ArrayList<MSolicitud> lista = new ArrayList<>();

    EditText edtBuscar;

    private SharedPreferences prefs;
    private MDocente objUser;

    private Dialogo dialogo; // <-- agregado

    public Solicitudes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solicitudes, container, false);

        dialogo = new Dialogo(requireContext()); // <-- inicializado

        recycler = view.findViewById(R.id.recyclerInscripciones);
        edtBuscar = view.findViewById(R.id.solicitudes_buscador);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdapterSolicitud(lista, getContext());
        recycler.setAdapter(adapter);

        objUser = cargarDocente();
        cargarSolicitudes(objUser.getId_docente());

        activarBusqueda();

        return view;
    }

    private MDocente cargarDocente() {
        prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String objeto = prefs.getString("usuario", "");
        return new Gson().fromJson(objeto, MDocente.class);
    }

    private void activarBusqueda() {
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarSolicitudes(int idDocente) {

        dialogo.mostrarDialogoProgress("Cargando", "Obteniendo solicitudes...");

        StringRequest request = new StringRequest(Request.Method.POST, API.BUSCAR_SOLICITUDES,
                response -> {

                    dialogo.cerrarDialogo();

                    lista.clear();

                    try {
                        JSONArray arr = new JSONArray(response);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            lista.add(new MSolicitud(
                                    o.getInt("id_inscripcion"),
                                    o.getInt("id_estudiante"),
                                    o.getString("matricula"),
                                    o.getString("nombre"),
                                    o.getString("app"),
                                    o.getString("apm"),
                                    o.getString("grupo"),
                                    o.getString("modalidad"),
                                    o.getString("estado")
                            ));
                        }

                        adapter.actualizarListaOriginal();
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        dialogo.mostrarDialogoBoton("Error", "Error al procesar información.");
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    dialogo.mostrarDialogoBoton("Sin conexión",
                            "No se pudo conectar con el servidor.\nRevisa tu internet.");
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id_docente", String.valueOf(idDocente));
                return param;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
