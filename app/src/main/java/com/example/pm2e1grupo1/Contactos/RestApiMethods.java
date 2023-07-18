package com.example.pm2e1grupo1.Contactos;

public class RestApiMethods {

    public static final String ipaddress = "192.168.0.100";
    public static final String webapi = "pm2e1grupo1";
    public static final String separador = "/";

    public static final String puerto = ":8080";

    //Routinh CRUD
    public static final String postRouting = "CrearContacto.php";
    public static final String getRouting = "ConsultarContacto.php";
    public static final String updRouting = "ActualizarContacto";
    public static final String delRouting = "EliminarContacto.php";

    public static final String ApiPost = "http://" + ipaddress + puerto + separador + webapi + separador + postRouting;
    public static final String ApiGet = "http://" + ipaddress + puerto + separador + webapi + separador + getRouting;
    public static final String Apiupd = "http://" + ipaddress + puerto + separador + webapi + separador + updRouting;
    public static final String Apidel = "http://" + ipaddress + puerto + separador + webapi + separador + delRouting;
}
