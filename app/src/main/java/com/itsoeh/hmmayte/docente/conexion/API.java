package com.itsoeh.hmmayte.docente.conexion;

public interface API {
    //CONEXION A LA BASE DE DATOS
    //public String URL = "http://178.6.9.133/wsescuela/"; // IP MAYTE - CASA CAMBIALO
    public String URL = "http://192.168.1.55/wsescuela/"; // IP BETO - CASA

    //AUTENTICACION
    public String VERIFICA = URL + "apiD.php?api=validar";
    // CRUD DOCENTE
    public String DOC_BUSCAR_POR_CORREO = URL + "apiD.php?api=consultarPorCorreo";
    public String DOC_GUARDAR = URL + "apiD.php?api=guardar";
    public String DOC_ACTUALIZAR = URL + "apiD.php?api=actualizar";

    // CRUD GRUPO
    public String GUARDAR_GRUPO = URL + "apiG.php?api=guardar";
    public String DOC_LISTAR_GRUPOS = URL + "apiG.php?api=listargrupos";
    public String BUSCAR_SOLICITUDES = URL + "apiG.php?api=consultar_solicitudes";

    // ASISTENCIAS
    public String ASISTENCIA_GUARDAR = URL + "apiPase.php?api=guardar";

    public String BUSCAR_ASISTENCIAS_POR_GRUPO = URL + "apiPase.php?api=consultar_estadisticas_grupo";

    public String LISTAR_INSCRITOS = URL + "apiG.php?api=listarestudiantesporgrupo";
    public String LISTAR_ASISTENCIAS_POR_GRUPO_FECHA = URL + "apiPase.php?api=consultar_por_grupo_fecha";


}