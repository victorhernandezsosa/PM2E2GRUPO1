package com.example.pm2e1grupo1.Contactos;

public class Contactos {

    private int id;
    private String nombres;
    private String telefono;
    private String imagen;
    private String latitud;
    private String longitud;

    public Contactos(int id, String nombres, String telefono, String imagen, String latitud, String longitud) {
        this.id = id;
        this.nombres = nombres;
        this.telefono = telefono;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Contactos() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
