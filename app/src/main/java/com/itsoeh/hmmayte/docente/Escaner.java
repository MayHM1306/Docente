package com.itsoeh.hmmayte.docente;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.itsoeh.hmmayte.docente.adapter.AdapterEstudiantesEscaneados;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.modelo.MEstudiante;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Escaner extends AppCompatActivity {

    private CompoundBarcodeView barcodeView;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;

    private RecyclerView recyclerEstudiantes;
    private AdapterEstudiantesEscaneados adapter;
    private ArrayList<MEstudiante> listaEstudiantes = new ArrayList<>();
    private Button btnGuardar;


    private int idGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_escaner);
        btnGuardar = findViewById(R.id.btnGuardarAsistencia);

        btnGuardar.setOnClickListener(v -> guardarAsistencias());


        idGrupo = getIntent().getIntExtra("id_grupo", -1);

        barcodeView = findViewById(R.id.barcodeView);
        recyclerEstudiantes = findViewById(R.id.recyclerEstudiantesEscaneados);
        recyclerEstudiantes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdapterEstudiantesEscaneados(listaEstudiantes);
        recyclerEstudiantes.setAdapter(adapter);

        // PEDIR PERMISO SI FALTA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );

        } else {
            // SI YA TIENE PERMISO → CARGAR NORMAL
            cargarEstudiantesInscritos(idGrupo);
            iniciarEscaneo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // DEBE INICIALIZAR TODO ESTO TAMBIÉN
                cargarEstudiantesInscritos(idGrupo);
                iniciarEscaneo();

            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void iniciarEscaneo() {

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {

                if (result != null) {

                    String codigo = result.getText();

                    adapter.marcarAsistencia(codigo);

                    Toast.makeText(Escaner.this,
                            "Registrado: " + codigo, Toast.LENGTH_SHORT).show();
                }
            }
        });

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    private void cargarEstudiantesInscritos(int idGrupo) {

        StringRequest request = new StringRequest(Request.Method.POST, API.LISTAR_INSCRITOS,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray array = json.getJSONArray("msg");

                        listaEstudiantes.clear();

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject obj = array.getJSONObject(i);

                            MEstudiante e = new MEstudiante();
                            e.setMatricula(obj.getString("matricula"));
                            e.setNombre(obj.getString("nombre"));
                            e.setApp(obj.getString("app"));
                            e.setApm(obj.getString("apm"));

                            listaEstudiantes.add(e);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error JSON: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_grupo", String.valueOf(idGrupo));
                return p;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private int convertirEstado(String estado) {
        switch (estado) {
            case "Asistencia":
                return 1;
            case "Retardo":
                return 2;
            case "Falta":
                return 3;
            case "Justificado":
                return 4;
            default:
                return 0; // Sin marcar
        }
    }
    private void guardarAsistencias() {

        ArrayList<MEstudiante> lista = adapter.getLista();

        for (MEstudiante est : lista) {

            String matricula = est.getMatricula();
            int valor = convertirEstado(est.getAsistencia()); // 0–3

            StringRequest req = new StringRequest(
                    Request.Method.POST,
                    API.ASISTENCIA_GUARDAR,
                    response -> {
                        // Puedes manejar respuesta por alumno
                    },
                    error -> {
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> p = new HashMap<>();
                    p.put("matricula", String.valueOf(matricula));
                    p.put("id_grupo", String.valueOf(idGrupo));
                    p.put("valor", String.valueOf(valor));

                    return p;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(req);
        }

        Toast.makeText(this, "Asistencias guardadas correctamente", Toast.LENGTH_LONG).show();
    }



}
