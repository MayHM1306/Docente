package com.itsoeh.hmmayte.docente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

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
 * Use the {@link Menu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Menu extends Fragment {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private NavController controlDeNavegacion;
    private Bundle paquete;
    private SharedPreferences prefs;
    private TextView txtDocente;
    private TextView txtCorreo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Menu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Menuu.
     */
    // TODO: Rename and change types and number of parameters
    public static Menu newInstance(String param1, String param2) {
        Menu fragment = new Menu();
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
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        this.cargaMenuLateral(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vincularComponentes(view);
        escuchadores(view);
        String correo = this.leerCorreoDesdeShareP(view);//aqui
        this.cargarUsuarioDesdeBD(correo);
    }

    private void cargarUsuarioDesdeBD(String correo) {
        Dialogo nuevo = new Dialogo(this.getContext());//crear un cuadro de dialogo
        nuevo.mostrarDialogoProgress("Por favor espere", "Conectando con el servidor");
        RequestQueue colaDeSolicitudes = VolleySingleton.getInstance(this.getContext()).getRequestQueue();
        StringRequest solicitud = new StringRequest(Request.Method.POST, API.DOC_BUSCAR_POR_CORREO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nuevo.cerrarDialogo();//apaga el cuadro de dialogo
                        //nuevo.mostrarDialogoBoton("Aviso", response);
                        try {
                            //LEER AQUI EL CONTENIDO DE LA VARIABLE response
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("msg");//
                            JSONObject reg0 = new JSONObject(array.getString(0));
                            MDocente modelo = new MDocente();
                            modelo.setId_docente(reg0.getInt("id_docente"));
                            modelo.setNumero(reg0.getString("numero"));
                            modelo.setNombre(reg0.getString("nombre"));
                            modelo.setApp(reg0.getString("app"));
                            modelo.setApm(reg0.getString("apm"));
                            modelo.setCorreo(reg0.getString("correo"));
                            modelo.setEstado(reg0.getInt("estado"));
                            modelo.setGenero(reg0.getInt("genero"));
                            modelo.setGrado(reg0.getInt("grado"));
                            txtDocente.setText(modelo.getNombre() + " " + modelo.getApp() + " " + modelo.getApm());
                            txtCorreo.setText(modelo.getCorreo());
                            guardaObjetoEnSharedP(modelo);
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
                return param;
            }
        };
        colaDeSolicitudes.add(solicitud);
    }

    private void guardaObjetoEnSharedP(MDocente modelo) {
        // Guardar usuario con SharedPreferences
        prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String usuario = gson.toJson(modelo);
        editor.putString("usuario", usuario);
        editor.apply();

    }

    private String leerCorreoDesdeShareP(View view) {
        prefs = requireActivity().getSharedPreferences("MisPreferencias", view.getContext().MODE_PRIVATE);
        String correo = prefs.getString("correo", "");
        return correo;
    }

    private void vincularComponentes(View view) {
        controlDeNavegacion = Navigation.findNavController(view);
        paquete = getArguments();//lee la informacion enviada del fragment anterior
    }

    private void escuchadores(View view) {

    }

    private void cargaMenuLateral(View view) {
        // Inflate the layout for this fragment
        drawerLayout = view.findViewById(R.id.menu);
        navigationView = view.findViewById(R.id.menu_navview);
        toolbar = view.findViewById(R.id.menu_toolbar);
        View encabezado = navigationView.getHeaderView(0);
        txtDocente = encabezado.findViewById(R.id.encabezado_txtnombreusuario);
        txtCorreo = encabezado.findViewById(R.id.encabezado_txtcorreousuario);
        // Usamos la actividad para configurar la Toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);

            drawerToggle = new ActionBarDrawerToggle(
                    activity, drawerLayout, toolbar,
                    R.string.abrir_men,
                    R.string.cerrar_men
            );

            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }

        // Cargar el fragmento por defecto
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_contenedor_interno, new Grupos())
                .commit();

        // Listener de opciones del drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.menu_perfil) {
                fragment = new ModificarPerfil();
                fragment.setArguments(paquete);//pasar correo al fragment
                toolbar.setTitle("Perfil");
            } else if (id == R.id.menu_grupos) {
                fragment = new Grupos();
                toolbar.setTitle("Grupos");
            } else if (id == R.id.menu_paseLista) {
                fragment = new PaseLista();
                toolbar.setTitle("Pase de Lista");
            } else if (id == R.id.menu_graficas) {
                fragment = new Estadisticas();
                toolbar.setTitle("Graficas");
            } else if (id == R.id.menu_acercade) {
                fragment = new AcercaDe();
                toolbar.setTitle("Acerca de");
            } else if (id == R.id.lateral_salir) {
            controlDeNavegacion.navigate(R.id.action_menu_to_login);
        }

            if (fragment != null) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.menu_contenedor_interno, fragment)
                        .commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }
}