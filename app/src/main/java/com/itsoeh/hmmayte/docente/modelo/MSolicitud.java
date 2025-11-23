package com.itsoeh.hmmayte.docente.modelo;
public class MSolicitud {

    private int idSolicitud;
    private int idEstudiante;
    private String matricula;
    private String nombre;
    private String app;
    private String apm;
    private String grupo;
    private String modalidad;
    private String estado;

    public MSolicitud(int idSolicitud, int idEstudiante, String matricula, String nombre,
                      String app, String apm, String grupo, String modalidad, String estado) {

        this.idSolicitud = idSolicitud;
        this.idEstudiante = idEstudiante;
        this.matricula = matricula;
        this.nombre = nombre;
        this.app = app;
        this.apm = apm;
        this.grupo = grupo;
        this.modalidad = modalidad;
        this.estado = estado;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public int getIdEstudiante() { return idEstudiante; }
    public String getMatricula() { return matricula; }
    public String getNombre() { return nombre; }
    public String getApp() { return app; }
    public String getApm() { return apm; }
    public String getGrupo() { return grupo; }
    public String getModalidad() { return modalidad; }
    public String getEstado() { return estado; }
}
