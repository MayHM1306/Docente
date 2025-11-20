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
    import android.widget.TextView;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.google.android.material.textfield.TextInputEditText;
    import com.itsoeh.hmmayte.docente.conexion.API;
    import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
    import com.itsoeh.hmmayte.docente.util.Dialogo;

    import java.util.HashMap;
    import java.util.Map;

    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link RegistrarGrupo#newInstance} factory method to
     * create an instance of this fragment.
     */
    public class RegistrarGrupo extends Fragment {

        private TextInputEditText txtClave, txtAsignatura, txtPeriodo, txtCarrera, txtIdDocente, txtIdGrupo;
        private Spinner spEstado;
        private CardView btnGuardar;

        // TODO: Rename parameter arguments, choose names that match
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        private String mParam1;
        private String mParam2;

        public RegistrarGrupo() {
            // Required empty public constructor
        }

        public static RegistrarGrupo newInstance(String param1, String param2) {
            RegistrarGrupo fragment = new RegistrarGrupo();
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
            return inflater.inflate(R.layout.fragment_registrar_grupo, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            vinculadores(view);
            inicializarSpinners(view);
            escuchadores(view);
        }

        private void vinculadores(View view) {
            txtIdGrupo = view.findViewById(R.id.perfil_docente_txtIdGrupo);
            txtClave = view.findViewById(R.id.perfil_docente_txtClave);
            txtAsignatura = view.findViewById(R.id.perfil_usuario_txtAsignatura);
            txtPeriodo = view.findViewById(R.id.perfil_docente_txtPeriodo);
            txtCarrera = view.findViewById(R.id.perfil_docente_txtCarrera);
            txtIdDocente = view.findViewById(R.id.perfil_docente_txtIdDocente);
            spEstado = view.findViewById(R.id.sp_Estado);
            btnGuardar = view.findViewById(R.id.login_btnentrar);
        }

        private void inicializarSpinners(View view) {
            ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                    view.getContext(),
                    R.array.estado,
                    android.R.layout.simple_spinner_item
            );
            adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEstado.setAdapter(adapterEstado);
        }

        private void escuchadores(View view) {
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicGuardar();
                }
            });
        }

        private void clicGuardar() {
            // Mostrar cuadro de diálogo de carga
            Dialogo dialogo = new Dialogo(getContext());
            dialogo.mostrarDialogoProgress("Por favor espere", "Registrando grupo");

            // Preparar la solicitud POST
            RequestQueue queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
            StringRequest request = new StringRequest(Request.Method.POST, API.GUARDAR_GRUPO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialogo.cerrarDialogo();
                            dialogo.mostrarDialogoBoton("Aviso", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialogo.cerrarDialogo();
                            dialogo.mostrarDialogoBoton("Error", "No se pudo conectar al servidor");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_grupo",txtIdGrupo.getText().toString());
                    params.put("clave", txtClave.getText().toString());
                    params.put("periodo", txtPeriodo.getText().toString());
                    params.put("carrera", txtCarrera.getText().toString());
                    params.put("id_docente", txtIdDocente.getText().toString());
                    params.put("estado", String.valueOf(spEstado.getSelectedItemPosition()));
                    params.put("asignatura", txtAsignatura.getText().toString());
                    return params;
                }
            };

            queue.add(request);
        }
    }
