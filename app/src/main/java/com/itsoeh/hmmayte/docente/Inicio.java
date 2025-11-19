package com.itsoeh.hmmayte.docente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itsoeh.hmmayte.docente.modelo.MDocente;

public class Inicio extends Fragment {

    private TextView txtNombreDocente;

    public Inicio() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNombreDocente = view.findViewById(R.id.inicio_txtNombreDocente);

        cargarDocente();
    }

    private void cargarDocente() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String json = prefs.getString("usuario", null);

        if (json == null) {
            txtNombreDocente.setText("Docente");
            return;
        }

        Gson gson = new Gson();
        MDocente docente = gson.fromJson(json, MDocente.class);

        if (docente != null) {
            txtNombreDocente.setText(docente.getNombre() + " " + docente.getApp() + " " + docente.getApm());
        }
    }
}
