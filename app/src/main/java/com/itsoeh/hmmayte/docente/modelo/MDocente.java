package com.itsoeh.hmmayte.docente.modelo;

import java.io.Serializable;

public class MDocente implements Serializable {
    private int id_docente;
    private String numero;
    private String nombre;
    private String app;
    private String apm;
    private String correo;
    private int estado;
    private int genero;
    private int grado;

    public MDocente() {
    }

    public MDocente(int id_docente, String numero, String nombre, String app, String apm, String correo, int estado, int genero, int grado) {
        this.id_docente = id_docente;
        this.numero = numero;
        this.nombre = nombre;
        this.app = app;
        this.apm = apm;
        this.correo = correo;
        this.estado = estado;
        this.genero = genero;
        this.grado = grado;
    }

    public int getId_docente() {
        return id_docente;
    }

    public void setId_docente(int id_docente) {
        this.id_docente = id_docente;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getApm() {
        return apm;
    }

    public void setApm(String apm) {
        this.apm = apm;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getGenero() {
        return genero;
    }

    public void setGenero(int genero) {
        this.genero = genero;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    @Override
    public String toString() {
        return "MDocente{" +
                "id_docente=" + id_docente +
                ", numero='" + numero + '\'' +
                ", nombre='" + nombre + '\'' +
                ", app='" + app + '\'' +
                ", apm='" + apm + '\'' +
                ", correo='" + correo + '\'' +
                ", estado=" + estado +
                ", genero=" + genero +
                ", grado=" + grado +
                '}';
    }
}
