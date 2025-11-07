package com.itsoeh.hmmayte.docente;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaseLista#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaseLista extends Fragment {

    private EditText txtBuscar;
    private RecyclerView recycler;
    private AdapterGrupoPase adapter;
    private TextView txtDocente;
    private ArrayList<MGrupo> lista;

    private SharedPreferences prefs;
    private MDocente objUser;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PaseLista() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaseLista.
     */
    // TODO: Rename and change types and number of parameters
    public static PaseLista newInstance(String param1, String param2) {
        PaseLista fragment = new PaseLista();
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
        return inflater.inflate(R.layout.fragment_pase_lista, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vincularcomponentes(view);
        escuchadores(view);
    }

    private void escuchadores(View view) {

    }

    private ArrayList<MGrupo> llenadoDesdeBD(int idGrupo){
        Dialogo nuevo=new Dialogo(this.getContext());
        nuevo.mostrarDialogoProgress("Por favor, espere", "Conectandose con el servidor");
        RequestQueue colaDeSolicitudes= VolleySingleton.getInstance(this.getContext()).getRequestQueue();
        StringRequest solicitud= new StringRequest(Request.Method.POST, API.DOC_LISTAR_GRUPOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nuevo.cerrarDialogo();//apaga el cuadro de dialogo
                        try {
                            //LEER AQUI EL CONTENIDO DE LA VARIABLE response
                            JSONObject contenido=new JSONObject(response);//convierte la respuesta en un objeto JSON
                            JSONArray array=contenido.getJSONArray("msg");//

                            MGrupo obj=new MGrupo();
                            for(int i=0;i<array.length();i++){//recorre el arreglo
                                obj=new MGrupo();
                                JSONObject pos=new JSONObject(array.getString(i));//convierte la posicion en un objeto JSON

                                obj.setId_grupo(pos.getInt("id_grupo"));
                                obj.setClave(pos.getString("clave"));
                                obj.setPeriodo(pos.getString("periodo"));
                                obj.setAsignatura(pos.getString("Asignatura"));
                                obj.setHorario(pos.getString("horarios"));

                                lista.add(obj);
                            }
                            recycler.setHasFixedSize(true);
                            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter=new AdapterGrupoPase(lista);
                            recycler.setAdapter(adapter);//crea el adaptador y pasa la lista

                        }catch (Exception ex){
                            //DETECTA ERRORES EN LA LECTURA DEL ARCHIVO JSON
                            nuevo.mostrarDialogoBoton("Error", "No se pudo leer el archivo JSON\n "+ex.getMessage());

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nuevo.cerrarDialogo();
                nuevo.mostrarDialogoBoton("No se pudo conectar", "Verifique su conexión a internet");

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> param=new HashMap<String,String>();
                //PASA PARAMETROS A LA SOLICITUD
                param.put("id_docente",idGrupo+"");
                return param;
            }
        };
        //ENVIA LA SOLICITUD
        colaDeSolicitudes.add(solicitud);

        return lista;
    }

    private MDocente leerusuario() {
        MDocente modelo=new MDocente();
        prefs = requireActivity().getSharedPreferences("MisPreferencias", getContext().MODE_PRIVATE);
        String objeto=prefs.getString("usuario","");
        Gson gson = new Gson();
        modelo=gson.fromJson(objeto, MDocente.class);
        txtDocente.setText(modelo.getNombre()+" "+modelo.getApp()+" "+modelo.getApm());
        return modelo;
    }

    private void vincularcomponentes(View view) {
        txtDocente=view.findViewById(R.id.listargrupo_txtdocente);
        txtBuscar=view.findViewById(R.id.listargrupo_buscador);
        recycler=view.findViewById(R.id.listargrupo_recyclerview);
        lista= new ArrayList<>();
        objUser = leerusuario();
        this.llenadoDesdeBD(objUser.getId_docente());
    }
}
