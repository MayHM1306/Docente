package com.itsoeh.hmmayte.docente;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import java.util.Random;

public class RecuperarContrasena extends Fragment {

    private TextInputEditText txtCorreo, txtToken;
    private Button btnEnviar, btnConfirmar;
    private LinearLayout lyToken;
    private String tokenGenerado;
    private NavController controladorDeNavegacion;

    public RecuperarContrasena() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controladorDeNavegacion = Navigation.findNavController(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recuperar_contrasena, container, false);

        vinculadores(view);
        escuchadores();
        return view;
    }

    private void vinculadores(View view) {
        txtCorreo = view.findViewById(R.id.rec_txtcorreo);  // correo
        txtToken = view.findViewById(R.id.rec_txttoken);         // token
        btnEnviar = view.findViewById(R.id.rec_btnEnviar);       // botón enviar token
        btnConfirmar = view.findViewById(R.id.rec_btnconfirmar); // botón confirmar token
        lyToken = view.findViewById(R.id.rec_lyToken);           // layout oculto del token
    }

    private void escuchadores() {
        btnEnviar.setOnClickListener(v -> clicEnviar());
        btnConfirmar.setOnClickListener(v -> clicConfirmar());
    }

    private void clicEnviar() {
        String correo = txtCorreo.getText().toString().trim();

        if (correo.isEmpty()) {
            new Dialogo(getContext()).mostrarDialogoBoton(
                    "Campo vacío",
                    "Por favor ingresa tu correo."
            );
            return;
        }

        validarCorreoEnServidor(correo);
    }

    private void validarCorreoEnServidor(String correo) {

        Dialogo dialogo = new Dialogo(getContext());
        dialogo.mostrarDialogoProgress("Validando", "Verificando información...");

        StringRequest request = new StringRequest(
                com.android.volley.Request.Method.POST,
                API.DOC_BUSCAR_CORREO,
                response -> {

                    dialogo.cerrarDialogo();

                    if (response.contains("existe")) {

                        // Generar token
                        tokenGenerado = generarToken(6);

                        // Enviar correo
                        Correo.enviarCorreo(
                                correo,
                                "Recuperación de contraseña Eduverse",
                                "Tu token de recuperación es: " + tokenGenerado,
                                "albertojimrey1610@gmail.com",
                                "ikux anap wuzg strd",
                                getContext()
                        );

                        lyToken.setVisibility(View.VISIBLE);

                        new Dialogo(getContext()).mostrarDialogoBoton(
                                "Token enviado",
                                "El código de recuperación se envió correctamente a tu correo."
                        );

                    } else {
                        new Dialogo(getContext()).mostrarDialogoBoton(
                                "Correo no válido",
                                "El correo no está registrado en el sistema."
                        );
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    new Dialogo(getContext()).mostrarDialogoBoton(
                            "Error de conexión",
                            "No se pudo conectar con el servidor.\n" + error.getMessage()
                    );
                }
        ) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("correo", correo);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void clicConfirmar() {
        String tokenIngresado = txtToken.getText().toString().trim();

        if (tokenIngresado.isEmpty()) {
            new Dialogo(getContext()).mostrarDialogoBoton(
                    "Campo vacío",
                    "Por favor ingresa el token."
            );
            return;
        }

        if (tokenIngresado.equals(tokenGenerado)) {

            new Dialogo(getContext()).mostrarDialogoBoton(
                    "Correcto",
                    "Token validado correctamente."
            );

            // --- PASAR EL CORREO AL FRAGMENT CAMBIAR CONTRASENA ---
            Bundle bundle = new Bundle();
            bundle.putString("correo", txtCorreo.getText().toString().trim());

            controladorDeNavegacion.navigate(
                    R.id.action_recuperarContrasena_to_cambiarContrasena,
                    bundle
            );

        } else {
            new Dialogo(getContext()).mostrarDialogoBoton(
                    "Token incorrecto",
                    "El token ingresado no coincide con el enviado."
            );
        }
    }


    private String generarToken(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < longitud; i++) {
            token.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return token.toString();
    }
}
