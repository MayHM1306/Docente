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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.modelo.MDocente;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModificarPerfil extends Fragment {

    private TextInputEditText txtNombre;
    private TextInputEditText txtNumero;
    private TextInputEditText txtApp;
    private TextInputEditText txtApm;
    private TextInputEditText txtCorreo;
    private Spinner spGenero;
    private Spinner spEstado;
    private Spinner spGrado;
    private CardView crvActualizar;

    private Bundle paquete;
    private int idDocente;

    public ModificarPerfil() { }

    public static ModificarPerfil newInstance(String param1, String param2) {
        ModificarPerfil fragment = new ModificarPerfil();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modificar_perfil, container, false);
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
        txtNumero = view.findViewById(R.id.perfil_docente_txtNumero);
        txtNombre = view.findViewById(R.id.perfil_docente_txtNombre);
        txtApp    = view.findViewById(R.id.perfil_docente_txtApPaterno);
        txtApm    = view.findViewById(R.id.perfil_docente_txtApMaterno);
        txtCorreo = view.findViewById(R.id.perfil_usuario_txtCorreo);

        spGenero = view.findViewById(R.id.sp_Genero);
        spEstado = view.findViewById(R.id.sp_Estado);
        spGrado  = view.findViewById(R.id.sp_Grado);

        // Asegúrate que este ID exista en fragment_modificar_perfil.xml
        crvActualizar = view.findViewById(R.id.login_btnentrar);
    }

    private void configurarSpinners() {
        String[] generos = getResources().getStringArray(R.array.genero);
        ArrayAdapter<String> adapterGeneros =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, generos);
        adapterGeneros.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(adapterGeneros);

        String[] estados = getResources().getStringArray(R.array.estado);
        ArrayAdapter<String> adapterEstados =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstados);

        String[] grados = getResources().getStringArray(R.array.grado);
        ArrayAdapter<String> adapterGrados =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, grados);
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGrado.setAdapter(adapterGrados);
    }

    private void cargarPaquete() {
        paquete = getArguments();
        if (paquete != null) {
            String correo = paquete.getString("correo");
            if (correo != null && !correo.isEmpty()) {
                cargarModelo(correo);
            }
        }
    }

    private void escucharBoton() {
        crvActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicActualizar();
            }
        });
    }

    private void cargarModelo(String correo) {
        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Por favor espere", "Conectando con el servidor");

        RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, API.DOC_BUSCAR_POR_CORREO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogo.cerrarDialogo();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("msg");
                            JSONObject reg0 = array.getJSONObject(0);

                            MDocente modelo = new MDocente();
                            idDocente = reg0.getInt("id_docente");
                            modelo.setId_docente(idDocente);
                            modelo.setNumero(reg0.getString("numero"));
                            modelo.setNombre(reg0.getString("nombre"));
                            modelo.setApp(reg0.getString("app"));
                            modelo.setApm(reg0.getString("apm"));
                            modelo.setCorreo(reg0.getString("correo"));
                            modelo.setEstado(reg0.getInt("estado"));
                            modelo.setGenero(reg0.getInt("genero"));
                            modelo.setGrado(reg0.getInt("grado"));

                            mostrarDatos(modelo);
                        } catch (Exception ex) {
                            dialogo.mostrarDialogoBoton("", "Error de formato: " + ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogo.cerrarDialogo();
                        dialogo.mostrarDialogoBoton("No se pudo conectar", "Verifique su conexión a Internet");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                return params;
            }
        };
        queue.add(request);
    }

    private void mostrarDatos(MDocente modelo) {
        txtNumero.setText(modelo.getNumero());
        txtNombre.setText(modelo.getNombre());
        txtApp.setText(modelo.getApp());
        txtApm.setText(modelo.getApm());
        txtCorreo.setText(modelo.getCorreo());

        // Colocar la selección correcta en los spinners
        spGenero.setSelection(modelo.getGenero());
        spEstado.setSelection(modelo.getEstado());
        spGrado.setSelection(modelo.getGrado());
    }

    private void clicActualizar() {
        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Por favor espere", "Conectando con el servidor");

        RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, API.DOC_ACTUALIZAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogo.cerrarDialogo();
                        try {
                            JSONObject obj = new JSONObject(response);
                            String op = obj.getString("msg");
                            if (op.equals("true")) {
                                dialogo.mostrarDialogoBoton("Aviso", "Datos actualizados");
                            } else {
                                dialogo.mostrarDialogoBoton("Error", "No se ha registrado");
                            }
                        } catch (Exception ex) {
                            // Si el servidor no regresa JSON bien formado pero sí actualiza:
                            dialogo.mostrarDialogoBoton("", "DATOS ACTUALIZADOS");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogo.cerrarDialogo();
                        dialogo.mostrarDialogoBoton("No se pudo conectar", "Verifique su conexión a Internet");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_docente", String.valueOf(idDocente));
                params.put("numero", txtNumero.getText().toString());
                params.put("nombre", txtNombre.getText().toString());
                params.put("app", txtApp.getText().toString());
                params.put("apm", txtApm.getText().toString());
                params.put("correo", txtCorreo.getText().toString());
                params.put("estado", String.valueOf(spEstado.getSelectedItemPosition()));
                params.put("genero", String.valueOf(spGenero.getSelectedItemPosition()));
                params.put("grado", String.valueOf(spGrado.getSelectedItemPosition()));
                return params;
            }
        };
        queue.add(request);
    }
}
