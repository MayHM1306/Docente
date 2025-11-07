package com.itsoeh.hmmayte.docente.modelo;

public class MGrupo {
    private int id_grupo;
    private String clave;
    private String periodo;
    private String carrera;
    private String asignatura;
    private int estado;
    private int inscripciones;
    private String horario;

    public MGrupo() {
    }

    public MGrupo(int id_grupo, String clave, String periodo, String carrera, String asignatura, int estado, int inscripciones, String horario) {
        this.id_grupo = id_grupo;
        this.clave = clave;
        this.periodo = periodo;
        this.carrera = carrera;
        this.asignatura = asignatura;
        this.estado = estado;
        this.inscripciones = inscripciones;
        this.horario = horario;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public String getClave() {
        return clave;
    }

    public String getPeriodo() {
        return periodo;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public int getEstado() {
        return estado;
    }

    public int getInscripciones() {
        return inscripciones;
    }

    public String getHorario() {
        return horario;
    }

    public void setId_grupo(int id_grupo) {

        this.id_grupo = id_grupo;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setInscripciones(int inscripciones) {
        this.inscripciones = inscripciones;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "MGrupo{" +
                "id_grupo=" + id_grupo +
                ", clave='" + clave + '\'' +
                ", periodo='" + periodo + '\'' +
                ", carrera='" + carrera + '\'' +
                ", asignatura='" + asignatura + '\'' +
                ", estado=" + estado +
                ", inscripciones=" + inscripciones +
                ", horario='" + horario + '\'' +
                '}';
    }
}
