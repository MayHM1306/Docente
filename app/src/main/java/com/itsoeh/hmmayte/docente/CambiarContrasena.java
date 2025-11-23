package com.itsoeh.hmmayte.docente;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CambiarContrasena extends Fragment {

    private TextInputEditText etNuevaContra, etConfirmarContra;
    private Button btnGuardarContra;

    private String correoUsuario;

    public CambiarContrasena() {
        // Required empty public constructor
    }

    public static CambiarContrasena newInstance(String correo) {
        CambiarContrasena fragment = new CambiarContrasena();
        Bundle args = new Bundle();
        args.putString("correo", correo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cambiar_contrasena, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            correoUsuario = getArguments().getString("correo");
        }


        etNuevaContra     = view.findViewById(R.id.txtNuevaContra);
        etConfirmarContra = view.findViewById(R.id.txtConfirmarContra);
        btnGuardarContra  = view.findViewById(R.id.btnGuardarContra);

        btnGuardarContra.setOnClickListener(v -> cambiarContrasena());
    }

    // ---------------------------------------------------
    //  CAMBIAR CONTRASEÑA
    // ---------------------------------------------------
    private void cambiarContrasena() {

        String nueva = etNuevaContra.getText().toString().trim();
        String confirmar = etConfirmarContra.getText().toString().trim();

        Dialogo dialogo = new Dialogo(getContext());

        if (TextUtils.isEmpty(nueva) || TextUtils.isEmpty(confirmar)) {
            dialogo.mostrarDialogoBoton("Campos vacíos", "Ingrese y confirme su nueva contraseña.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            dialogo.mostrarDialogoBoton("No coinciden", "Las contraseñas deben ser iguales.");
            return;
        }

        dialogo.mostrarDialogoProgress("Actualizando", "Guardando nueva contraseña...");

        StringRequest req = new StringRequest(
                Request.Method.POST,
                API.DOC_ACTUALIZAR_CONTRASENA,
                response -> {
                    dialogo.cerrarDialogo();
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("updated")) {
                            dialogo.mostrarDialogoBoton("Contraseña actualizada",
                                    "Tu contraseña ha sido cambiada exitosamente.");

                            etNuevaContra.setText("");
                            etConfirmarContra.setText("");
                            etNuevaContra.requestFocus();

                            if (getView() != null) {
                                NavController navController = Navigation.findNavController(getView());
                                navController.navigate(R.id.action_cambiarContrasena_to_login);
                            }

                        } else {
                            dialogo.mostrarDialogoBoton("Error",
                                    "No fue posible actualizar la contraseña.");
                        }

                    } catch (Exception e) {
                        dialogo.mostrarDialogoBoton("Error",
                                "Ocurrió un problema procesando la respuesta.");
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    dialogo.mostrarDialogoBoton("Error de conexión",
                            "No se pudo actualizar la contraseña.");
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("correo", correoUsuario);
                p.put("pass", nueva);
                return p;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(req);
    }
}
