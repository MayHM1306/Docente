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
import android.widget.AutoCompleteTextView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutoRegistro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoRegistro extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private NavController controladorDeNavegacion;
    private TextInputEditText txtNumero;
    private TextInputEditText txtNombre;
    private TextInputEditText txtApp;
    private TextInputEditText txtApm;
    private TextInputEditText txtCorreo;
    private TextInputEditText txtPass;
    private AutoCompleteTextView txtEstado;
    private AutoCompleteTextView txtGenero;
    private AutoCompleteTextView txtGrado;
    private CardView crvGuardar;
    private Bundle paquete;
    private int idDocente;

    public AutoRegistro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutoRegistroo.
     */
    // TODO: Rename and change types and number of parameters
    public static AutoRegistro newInstance(String param1, String param2) {
        AutoRegistro fragment = new AutoRegistro();
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
        return inflater.inflate(R.layout.fragment_auto_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vincularComponentes(view);
        escuchadores(view);

    }

    private void vincularComponentes(View view) {
        controladorDeNavegacion = Navigation.findNavController(view);
        txtNumero=view.findViewById(R.id.reg_numero);
        txtNombre=view.findViewById(R.id.reg_nombre);
        txtApp=view.findViewById(R.id.reg_app);
        txtApm=view.findViewById(R.id.reg_apm);
        txtCorreo=view.findViewById(R.id.reg_correo);
        txtPass=view.findViewById(R.id.reg_pass);
        txtEstado=view.findViewById(R.id.reg_estado);
        txtGenero=view.findViewById(R.id.reg_genero);
        txtGrado=view.findViewById(R.id.reg_grado);
        crvGuardar=view.findViewById(R.id.reg_btnguardar);
    }

    private void escuchadores(View view) {
        crvGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicGuardar(view);
            }
        });

       String[] generos = getResources().getStringArray(R.array.genero);
        ArrayAdapter<String> adapterGeneros = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.select_dialog_item,
                generos
        );
        txtGenero.setAdapter(adapterGeneros);
        String[] grados = getResources().getStringArray(R.array.grado);
        ArrayAdapter<String> adapterGrados = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.select_dialog_item,
                grados
        );
        txtGrado.setAdapter(adapterGrados);
        String[] estados = getResources().getStringArray(R.array.estado);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.select_dialog_item,
                estados
        );
        txtEstado.setAdapter(adapterEstados);
    }

    private void clicGuardar(View view) {
        //validarVacios();
        Dialogo nuevo=new Dialogo(this.getContext());//crear un cuadro de dialogo
        nuevo.mostrarDialogoProgress("Por favor espere","Conectando con el servidor");
        RequestQueue colaDeSolicitudes= VolleySingleton.getInstance(this.getContext()).getRequestQueue();
        StringRequest solicitud= new StringRequest(Request.Method.POST, API.DOC_GUARDAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nuevo.cerrarDialogo();//apaga el cuadro de dialogo
                        nuevo.mostrarDialogoBoton("Aviso", response);
                        try {
                            //LEER AQUI EL CONTENIDO DE LA VARIABLE response
                            JSONObject obj=new JSONObject(response);
                            String op=obj.getString("msg");
                            if(op.equals("true")){
                                nuevo.mostrarDialogoBoton("Aviso","Docente registrado");
                                controladorDeNavegacion.navigate(R.id.action_autoregistro_to_login);
                                return;
                            }else
                                nuevo.mostrarDialogoBoton("Error","No se ha registrado");

                        }catch (Exception ex){
                            //DETECTA ERRORES EN LA LECTURA DEL ARCHIVO JSON
                            nuevo.mostrarDialogoBoton("Error","Error de formato "+ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nuevo.cerrarDialogo();
                nuevo.mostrarDialogoBoton("No se puedo conectar","Verifique su conexion a Internet");

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> param=new HashMap<String,String>();
                param.put("numero", txtNumero.getText().toString());
                param.put("nombre", txtNombre.getText().toString());
                param.put("app", txtApp.getText().toString());
                param.put("apm", txtApm.getText().toString());
                param.put("correo", txtCorreo.getText().toString());
                param.put("pass", txtPass.getText().toString());
                param.put("estado", txtEstado.getText().toString());
                param.put("genero", txtGenero.getText().toString());
                param.put("grado", txtGrado.getText().toString());
                return param;
            }
        };
        colaDeSolicitudes.add(solicitud);


    }
}