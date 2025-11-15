package com.itsoeh.hmmayte.docente.modelo;

import java.io.Serializable;

public class MEstudiante implements Serializable {

    private int id_estudiante;
    private String matricula;
    private String nombre;
    private String app;
    private String apm;
    private String correo;
    private int estado;
    private int genero;
    private String carrera;
    // Campo necesario para el escaneo
    private String asistencia = "Sin marcar";


    public MEstudiante() {
    }

    public MEstudiante(int id_estudiante, String matricula, String nombre, String app, String apm, String correo, int estado, int genero, String carrera) {
        this.id_estudiante = id_estudiante;
        this.matricula = matricula;
        this.nombre = nombre;
        this.app = app;
        this.apm = apm;
        this.correo = correo;
        this.estado = estado;
        this.genero = genero;
        this.carrera = carrera;
    }

    public int getId_alumno() {
        return id_estudiante;
    }

    public void setId_alumno(int id_alumno) {
        this.id_estudiante = id_alumno;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
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

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    @Override
    public String toString() {
        return "MAlumno{" +
                "id_alumno=" + id_estudiante +
                ", matricula='" + matricula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", app='" + app + '\'' +
                ", apm='" + apm + '\'' +
                ", correo='" + correo + '\'' +
                ", estado=" + estado +
                ", genero=" + genero +
                ", carrera='" + carrera + '\'' +
                '}';
    }
    public String getAsistencia() {
        return asistencia;
    }
    public void setAsistencia(String asistencia) {
        this.asistencia = asistencia;
    }
}
