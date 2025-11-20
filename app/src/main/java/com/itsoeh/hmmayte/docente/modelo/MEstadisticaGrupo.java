package com.itsoeh.hmmayte.docente.modelo;

public class MEstadisticaGrupo {

    private int idGrupo;
    private String nombreGrupo;

    private double porcentajeAsistencias;
    private double porcentajeRetardos;
    private double porcentajeFaltas;
    private double porcentajeJustificaciones;

    public MEstadisticaGrupo(int idGrupo, String nombreGrupo,
                             double porcentajeAsistencias, double porcentajeRetardos,
                             double porcentajeFaltas, double porcentajeJustificaciones) {
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
        this.porcentajeAsistencias = porcentajeAsistencias;
        this.porcentajeRetardos = porcentajeRetardos;
        this.porcentajeFaltas = porcentajeFaltas;
        this.porcentajeJustificaciones = porcentajeJustificaciones;
    }

    public int getIdGrupo() { return idGrupo; }
    public String getNombreGrupo() { return nombreGrupo; }
    public double getPorcentajeAsistencias() { return porcentajeAsistencias; }
    public double getPorcentajeRetardos() { return porcentajeRetardos; }
    public double getPorcentajeFaltas() { return porcentajeFaltas; }
    public double getPorcentajeJustificaciones() { return porcentajeJustificaciones; }
}
