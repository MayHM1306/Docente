package com.itsoeh.hmmayte.docente;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.itsoeh.hmmayte.docente.adapter.AdapterEstadistica;
import com.itsoeh.hmmayte.docente.conexion.API;
import com.itsoeh.hmmayte.docente.conexion.VolleySingleton;
import com.itsoeh.hmmayte.docente.modelo.MEstadistica;
import com.itsoeh.hmmayte.docente.util.Dialogo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasEstudiantes extends AppCompatActivity {

    private TextInputEditText etFechaInicio, etFechaFin;
    private Button btnFiltrar;
    private RecyclerView rvEstadisticas;

    private AdapterEstadistica adapter;
    private ArrayList<MEstadistica> listaEstudiantes;

    private int idGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_estadisticas_estudiantes); // <-- DEBES CREAR ESTE LAYOUT

        // Recibir id_grupo desde el intent
        idGrupo = getIntent().getIntExtra("id_grupo", -1);

        etFechaInicio = findViewById(R.id.et_fecha_inicio);
        etFechaFin = findViewById(R.id.et_fecha_fin);
        btnFiltrar = findViewById(R.id.btn_filtrar_rango);
        rvEstadisticas = findViewById(R.id.rv_estadisticas);

        listaEstudiantes = new ArrayList<>();

        configurarFecha(etFechaInicio);
        configurarFecha(etFechaFin);

        rvEstadisticas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterEstadistica(this, listaEstudiantes);
        rvEstadisticas.setAdapter(adapter);

        btnFiltrar.setOnClickListener(v -> validarYConsultar());
    }

    private void configurarFecha(TextInputEditText campoFecha) {
        campoFecha.setOnClickListener(v -> mostrarCalendario(campoFecha));
    }

    private void mostrarCalendario(TextInputEditText campo) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    int mes = month + 1;
                    String fecha = year + "-" +
                            (mes < 10 ? "0" + mes : mes) + "-" +
                            (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);

                    campo.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void validarYConsultar() {
        String fechaInicio = etFechaInicio.getText().toString();
        String fechaFin = etFechaFin.getText().toString();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "Seleccione ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        consultarEstadistica(idGrupo, fechaInicio, fechaFin);
    }

    private void consultarEstadistica(int idGrupo, String fechaInicio, String fechaFin) {

        Dialogo dialogo = new Dialogo(this);
        dialogo.mostrarDialogoProgress("Consultando", "Obteniendo estadísticas...");

        RequestQueue cola = VolleySingleton.getInstance(this).getRequestQueue();

        StringRequest solicitud = new StringRequest(Request.Method.POST, API.LISTAR_ASISTENCIAS_POR_GRUPO_FECHA,
                response -> {
                    dialogo.cerrarDialogo();
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.has("data")) {

                            JSONArray array = json.getJSONArray("data");
                            listaEstudiantes.clear();

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject obj = array.getJSONObject(i);

                                MEstadistica est = new MEstadistica(
                                        obj.getString("matricula"),
                                        obj.getString("nombre_completo"),
                                        obj.getInt("total_asistencias"),
                                        obj.getInt("total_retardos"),
                                        obj.getInt("total_faltas"),
                                        obj.getInt("total_justificaciones"),
                                        obj.getDouble("porcentaje_asistencias"),
                                        obj.getDouble("porcentaje_retardos"),
                                        obj.getDouble("porcentaje_faltas"),
                                        obj.getDouble("porcentaje_justificaciones")
                                );

                                listaEstudiantes.add(est);
                            }

                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(this, "Sin datos disponibles", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    dialogo.cerrarDialogo();
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_grupo", String.valueOf(idGrupo));
                params.put("fechainicio", fechaInicio);
                params.put("fechafin", fechaFin);
                return params;
            }
        };

        cola.add(solicitud);
    }

}
