package com.itsoeh.hmmayte.docente;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Foto {

    private Fragment fragment;
    private int idUsuario;
    private ImageView destino;

    private ActivityResultLauncher<Intent> launcher;

    public Foto(Fragment fragment, int idUsuario, ImageView destino) {
        this.fragment = fragment;
        this.idUsuario = idUsuario;
        this.destino = destino;

        launcher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        procesarFoto(result.getData());
                    }
                });
    }

    private String obtenerRutaLocal() {
        return new File(fragment.requireActivity().getFilesDir(),
                "perfil_" + idUsuario + ".jpg").getAbsolutePath();
    }

    public void cargarFotoLocal() {
        File f = new File(obtenerRutaLocal());
        if (f.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
            destino.setImageBitmap(bmp);
        }
    }

    public void seleccionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    private void procesarFoto(Intent data) {
        try {
            Uri imageUri = data.getData();
            InputStream inputStream = fragment.requireActivity()
                    .getContentResolver().openInputStream(imageUri);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Guardar local
            File f = new File(obtenerRutaLocal());
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            // Mostrar en pantalla
            destino.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
