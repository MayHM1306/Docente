package com.itsoeh.hmmayte.docente;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AutoRegistro extends Fragment {

    private NavController controladorDeNavegacion;

    private TextInputEditText txtNumero, txtNombre, txtApp, txtApm, txtCorreo, txtPass;
    private Spinner spEstado, spGenero, spGrado;

    private CardView crvGuardar;

    public AutoRegistro() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auto_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vincularComponentes(view);
        configurarListasDesplegables();
        escuchadores();
    }

    private void vincularComponentes(View view) {

        controladorDeNavegacion = Navigation.findNavController(view);

        txtNumero = view.findViewById(R.id.reg_numero);
        txtNombre = view.findViewById(R.id.reg_nombre);
        txtApp    = view.findViewById(R.id.reg_app);
        txtApm    = view.findViewById(R.id.reg_apm);
        txtCorreo = view.findViewById(R.id.reg_correo);
        txtPass   = view.findViewById(R.id.reg_pass);

        // ❗ AHORA sí se vinculan las variables GLOBALES (¡antes estabas creando nuevas!)
        spEstado = view.findViewById(R.id.sp_Estado);
        spGenero = view.findViewById(R.id.sp_Genero);
        spGrado  = view.findViewById(R.id.sp_Grado);

        crvGuardar = view.findViewById(R.id.reg_btnguardar);
    }

    // 🔽 CONFIGURACIÓN DE ADAPTADORES PARA SPINNERS
    private void configurarListasDesplegables() {

        ArrayAdapter<CharSequence> adapterEstado =
                ArrayAdapter.createFromResource(requireContext(),
                        R.array.estado, android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstado);

        ArrayAdapter<CharSequence> adapterGenero =
                ArrayAdapter.createFromResource(requireContext(),
                        R.array.genero, android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(adapterGenero);

        ArrayAdapter<CharSequence> adapterGrado =
                ArrayAdapter.createFromResource(requireContext(),
                        R.array.grado, android.R.layout.simple_spinner_dropdown_item);
        spGrado.setAdapter(adapterGrado);
    }

    private void escuchadores() {
        crvGuardar.setOnClickListener(v -> clicGuardar());
    }

    private void clicGuardar() {

        Dialogo nuevo = new Dialogo(requireContext());
        nuevo.mostrarDialogoProgress("Por favor espere", "Conectando con el servidor");

        RequestQueue colaDeSolicitudes =
                VolleySingleton.getInstance(requireContext()).getRequestQueue();

        StringRequest solicitud = new StringRequest(Request.Method.POST, API.DOC_GUARDAR,
                response -> {
                    nuevo.cerrarDialogo();
                    try {
                        JSONObject obj = new JSONObject(response);
                        String op = obj.getString("msg");

                        if (op.equals("true")) {
                            nuevo.mostrarDialogoBoton("Aviso", "Docente registrado");
                        } else {
                            nuevo.mostrarDialogoBoton("Aviso", "Docente NO registrado");
                        }

                    } catch (Exception ex) {
                        nuevo.mostrarDialogoBoton("Error", "Error JSON: " + ex.getMessage());
                    }
                },
                error -> {
                    nuevo.cerrarDialogo();
                    nuevo.mostrarDialogoBoton("No se pudo conectar",
                            "Verifique su conexión a Internet");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();

                param.put("numero", txtNumero.getText().toString());
                param.put("nombre", txtNombre.getText().toString());
                param.put("app", txtApp.getText().toString());
                param.put("apm", txtApm.getText().toString());
                param.put("correo", txtCorreo.getText().toString());
                param.put("pass", txtPass.getText().toString());
                param.put("estado", spEstado.getSelectedItem().toString());
                param.put("genero", spGenero.getSelectedItem().toString());
                param.put("grado", spGrado.getSelectedItem().toString());

                return param;
            }
        };

        colaDeSolicitudes.add(solicitud);
    }
}
