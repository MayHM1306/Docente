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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

    public Menu() { }

    public static Menu newInstance(String param1, String param2) {
        Menu fragment = new Menu();
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
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        cargaMenuLateral(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vincularComponentes(view);

        // Header del menú lateral
        View header = navigationView.getHeaderView(0);
        txtDocente = header.findViewById(R.id.encabezado_txtnombreusuario);
        txtCorreo  = header.findViewById(R.id.encabezado_txtcorreousuario);

        String correo = leerCorreoDesdeShareP();
        cargarUsuarioDesdeBD(correo);
    }

    private void cargaMenuLateral(View view) {
        drawerLayout   = view.findViewById(R.id.menu);
        navigationView = view.findViewById(R.id.menu_navview);
        toolbar        = view.findViewById(R.id.menu_toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);

            drawerToggle = new ActionBarDrawerToggle(
                    activity,
                    drawerLayout,
                    toolbar,
                    R.string.abrir_men,
                    R.string.cerrar_men
            );

            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();

            // Cambiar color del icono hamburguesa
            drawerToggle.getDrawerArrowDrawable().setColor(
                    getResources().getColor(R.color.blanco)
            );
        }

        // Fragment inicial
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_contenedor_interno, new Inicio())
                .commit();

        // Listener del menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();
            Fragment fragment = null;

            if (id == R.id.menu_grupos) {
                fragment = new Grupos();
                toolbar.setTitle("Mis Grupos");
            } else if (id == R.id.solicitudes) {
                fragment = new Solicitudes();
                toolbar.setTitle("Solicitudes");
            } else if (id == R.id.menu_paseLista) {
                fragment = new PaseLista();
                toolbar.setTitle("Pase de Lista");
            } else if (id == R.id.menu_graficas) {
                fragment = new Estadisticas();
                toolbar.setTitle("Estadísticas");
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

        // Listener del icono EDITAR PERFIL (en header) – fuera del listener del menú
        View header = navigationView.getHeaderView(0);
        ImageView iconEditar = header.findViewById(R.id.encabezado_icono_modificar_perfil_usuario);

        iconEditar.setOnClickListener(v2 -> {
            Fragment frag = new ModificarPerfil();

            // Crear Bundle con el correo para ModificarPerfil
            Bundle args = new Bundle();
            String correo = leerCorreoDesdeShareP();
            args.putString("correo", correo);
            frag.setArguments(args);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_contenedor_interno, frag)
                    .addToBackStack(null)   // opcional
                    .commit();

            drawerLayout.closeDrawer(GravityCompat.START);
            toolbar.setTitle("Modificar Perfil");
        });
    }

    private void vincularComponentes(View view) {
        controlDeNavegacion = Navigation.findNavController(view);
        paquete = getArguments();
    }

    private String leerCorreoDesdeShareP() {
        prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        return prefs.getString("correo", "");
    }

    private void cargarUsuarioDesdeBD(String correo) {

        Dialogo dlg = new Dialogo(getContext());
        dlg.mostrarDialogoProgress("Espere", "Cargando usuario...");

        RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();

        StringRequest req = new StringRequest(Request.Method.POST,
                API.DOC_BUSCAR_POR_CORREO,
                response -> {
                    dlg.cerrarDialogo();
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("msg");
                        JSONObject reg0 = arr.getJSONObject(0);

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

                        txtDocente.setText(
                                modelo.getNombre() + " " +
                                        modelo.getApp() + " " +
                                        modelo.getApm()
                        );
                        txtCorreo.setText(modelo.getCorreo());

                        guardarEnShared(modelo);

                    } catch (Exception ex) {
                        dlg.mostrarDialogoBoton("Error", ex.getMessage());
                    }
                },
                error -> {
                    dlg.cerrarDialogo();
                    dlg.mostrarDialogoBoton("Error", "No se pudo conectar con el servidor");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("correo", correo);
                return p;
            }
        };

        queue.add(req);
    }

    private void guardarEnShared(MDocente modelo) {
        prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        editor.putString("usuario", gson.toJson(modelo));
        editor.apply();
    }
}
