package com.itsoeh.hmmayte.docente;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Correo {
    public static void enviarCorreo(String destinatario, String asunto, String mensaje,
                                    String remitente, String contrasena,
                                    android.content.Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                // Configuración del servidor SMTP
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                // Session autenticada
                Session session = Session.getInstance(props,
                        new jakarta.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(remitente, contrasena);
                            }
                        });
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(remitente));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(destinatario));
                message.setSubject(asunto);
                message.setText(mensaje);
                // Enviar mensaje
                Transport.send(message);
                handler.post(() -> Toast.makeText(context,
                        "Correo enviado correctamente",
                        Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(context,
                        "Error al enviar correo: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }
}