package com.itsoeh.hmmayte.docente.modelo;

public class MEstadistica {

    private String matricula;
    private String nombreCompleto;

    private int totalAsistencias;
    private int totalRetardos;
    private int totalFaltas;
    private int totalJustificaciones;

    private double porcentajeAsistencias;
    private double porcentajeRetardos;
    private double porcentajeFaltas;
    private double porcentajeJustificaciones;

    public MEstadistica(String matricula, String nombreCompleto,
                             int totalAsistencias, int totalRetardos,
                             int totalFaltas, int totalJustificaciones,
                             double porcentajeAsistencias, double porcentajeRetardos,
                             double porcentajeFaltas, double porcentajeJustificaciones) {

        this.matricula = matricula;
        this.nombreCompleto = nombreCompleto;

        this.totalAsistencias = totalAsistencias;
        this.totalRetardos = totalRetardos;
        this.totalFaltas = totalFaltas;
        this.totalJustificaciones = totalJustificaciones;

        this.porcentajeAsistencias = porcentajeAsistencias;
        this.porcentajeRetardos = porcentajeRetardos;
        this.porcentajeFaltas = porcentajeFaltas;
        this.porcentajeJustificaciones = porcentajeJustificaciones;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public int getTotalAsistencias() {
        return totalAsistencias;
    }

    public int getTotalRetardos() {
        return totalRetardos;
    }

    public int getTotalFaltas() {
        return totalFaltas;
    }

    public int getTotalJustificaciones() {
        return totalJustificaciones;
    }

    public double getPorcentajeAsistencias() {
        return porcentajeAsistencias;
    }

    public double getPorcentajeRetardos() {
        return porcentajeRetardos;
    }

    public double getPorcentajeFaltas() {
        return porcentajeFaltas;
    }

    public double getPorcentajeJustificaciones() {
        return porcentajeJustificaciones;
    }
}
