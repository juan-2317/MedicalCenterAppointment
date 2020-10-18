package com.aplavanzadas.medicalcenterappointment;

/* Esta clase nos servirá para añadir los datos de cada registro de lugares como item en el spinner de lugares */
public class LugarItem {

    private String subtitulo;
    private String direccion;

    public LugarItem(String subtitulo, String direccion) {
        this.subtitulo = subtitulo;
        this.direccion = direccion;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public String getDireccion() {
        return direccion;
    }
}
