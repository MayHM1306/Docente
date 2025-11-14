package com.itsoeh.hmmayte.docente;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.VideoView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VideoView videoLogin;

    private CardView crvEntrar;
    private NavController controladorDeNavegacion;
    private TextInputEditText txtCorreo;
    private TextInputEditText txtPass;
    private TextView txtRegistro;
    private Bundle paquete;
    private SharedPreferences prefs;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        videoLogin = view.findViewById(R.id.videoLogin);

        // Verifica que el archivo exista en /res/raw/
        Uri uri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.video_fondo_login);
        videoLogin.setVideoURI(uri);

        videoLogin.setAlpha(0.6f);

        videoLogin.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
            videoLogin.setScaleX(1.3f); // 1.0 = normal, >1 = zoom
            videoLogin.setScaleY(1.3f);

            videoLogin.start();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vincularComponentes(view);
        escuchadores(view);
    }

    private void vincularComponentes(View view) {
        crvEntrar = view.findViewById(R.id.login_btnentrar);
        controladorDeNavegacion = Navigation.findNavController(view);
        txtCorreo = view.findViewById(R.id.login_txtEmail);
        txtPass = view.findViewById(R.id.login_txtPassword);
        txtRegistro = view.findViewById(R.id.login_autoregistro);
        txtCorreo.setText("mpb@itsoeh.edu.mx");
        txtPass.setText("123");
    }

    private void escuchadores(View view) {
        crvEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicEntrar(view);
            }
        });
        txtRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicRegistrarse(view);
            }
        });

    }

    private void clicRegistrarse(View view) {
        controladorDeNavegacion.navigate(R.id.action_login_to_autoRegistro);
    }

    private void clicEntrar(View view) {
        String correo = txtCorreo.getText().toString();
        String pass = txtPass.getText().toString();
        this.validaEntrada(correo, pass);
        this.guardarCorreo(view);

    }

    private void validaEntrada(String correo, String pass) {
        Dialogo nuevo = new Dialogo(this.getContext());//vrear un cuadro de dialogo
        nuevo.mostrarDialogoProgress("Por favor espere", "Conectando con el servidor");
        RequestQueue colaDeSolicitudes = VolleySingleton.getInstance(this.getContext()).getRequestQueue();
        StringRequest solicitud = new StringRequest(Request.Method.POST, API.VERIFICA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nuevo.cerrarDialogo();//apaga el cuadro de dialogo
                        nuevo.mostrarDialogoBoton("Aviso", response);
                        try {
                            //LEER AQUI EL CONTENIDO DE LA VARIABLE response
                            JSONObject obj = new JSONObject(response);
                            int op = obj.getInt("msg");
                            if (op == 0) {
                                nuevo.mostrarDialogoBoton("Aviso", "Usuario no registrado");
                                return;
                            } else if (op == 1) {
                                nuevo.mostrarDialogoBoton("Aviso,", "Contraseña incorrecta");
                            } else if (op == 2) {
                                nuevo.mostrarDialogoBoton("Aviso", "Usuario registrado");
                                paquete = new Bundle();//para mandar variables al siguente fragment
                                paquete.putString("correo", txtCorreo.getText().toString());//empaquetamos
                                controladorDeNavegacion.navigate(R.id.action_login_to_menu, paquete);//enviamos
                            }

                        } catch (Exception ex) {
                            //DETECTA ERRORES EN LA LECTURA DEL ARCHIVO JSON
                            nuevo.mostrarDialogoBoton("Error", "Error de formato " + ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nuevo.cerrarDialogo();
                nuevo.mostrarDialogoBoton("No se pudo conectar", "Verifique su conexion a Internet");

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                param.put("correo", correo);
                param.put("pass", pass);
                return param;
            }
        };
        colaDeSolicitudes.add(solicitud);

    }

    private void guardarCorreo(View view) {
        prefs = view.getContext().getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("correo", txtCorreo.getText().toString());
        editor.apply();
    }
}