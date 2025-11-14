package com.itsoeh.hmmayte.docente.conexion;

public interface API {
    //CONEXION A LA BASE DE DATOS
    //public String URL = "http://178.6.9.133/wsescuela/"; // IP MAYTE - CASA CAMBIALO
    public String URL = "http://10.61.240.186/wsescuela/"; // IP BETO - CASA

    //AUTENTICACION
    public String VERIFICA = URL + "apiD.php?api=validar";

    // CRUD DOCENTE
    public String DOC_BUSCAR_POR_CORREO = URL + "apiD.php?api=consultarPorCorreo";
    public String DOC_GUARDAR = URL + "apiD.php?api=guardar";
    public String DOC_ACTUALIZAR = URL + "apiD.php?api=actualizar";

    // CRUD GRUPO
    public String DOC_LISTAR_GRUPOS = URL + "apiG.php?api=listargrupos";
}