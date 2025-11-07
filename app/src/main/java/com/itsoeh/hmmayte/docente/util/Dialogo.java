package com.itsoeh.hmmayte.docente.util;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.itsoeh.hmmayte.docente.R;

public class Dialogo {
    private Dialog dialogo;
    private TextView tvtitulo;
    private TextView tvMessage;
    private CardView btnAccept;
    private ProgressBar progressBar;
    private Context contexto;
    public Dialogo(Context contexto) {
        this.contexto = contexto;
        dialogo = new Dialog(contexto);
        dialogo.setContentView(R.layout.dialogo_msg);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvtitulo = dialogo.findViewById(R.id.msg_titulo);
        tvMessage = dialogo.findViewById(R.id.msg_mensaje);
        btnAccept = dialogo.findViewById(R.id.msg_btnAccept);
        progressBar = dialogo.findViewById(R.id.msg_progres);


    }
    public  void mostrarDialogoBoton(String titulo, String mensaje) {
        tvMessage.setText(mensaje);
        tvtitulo.setText(titulo);
        progressBar.setVisibility(View.GONE);
        btnAccept.setVisibility(View.VISIBLE);
        dialogo.show();
        btnAccept.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             dialogo.dismiss();
                                         }
                                     }
        );

    }
    public  void mostrarDialogoProgress(String titulo, String mensaje) {
        tvMessage.setText(mensaje);
        tvtitulo.setText(titulo);
        progressBar.setVisibility(View.VISIBLE);
        btnAccept.setVisibility(View.GONE);
        dialogo.show();

    }
    public  void cerrarDialogo() {
        dialogo.dismiss();
    }

}