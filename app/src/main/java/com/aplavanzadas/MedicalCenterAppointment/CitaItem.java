package com.aplavanzadas.medicalcenterappointment;

/* Esta clase nos servirá para añadir los datos de cada registro de citas médicas en la base de datos
    como item en el listview de citas */
public class CitaItem {

    private int id;
    private String doctor;
    private String lugar;
    private String direccion;
    private String tipoCita;
    private int consultorio;
    private String fecha;
    private String hora;

    public CitaItem(int id, String doctor, String lugar, String direccion, String tipoCita, int consultorio, String fecha, String hora) {
        this.id = id;
        this.doctor = doctor;
        this.lugar = lugar;
        this.direccion = direccion;
        this.tipoCita = tipoCita;
        this.consultorio = consultorio;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getLugar() {
        return lugar;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTipoCita() {
        return tipoCita;
    }

    public int getConsultorio() {
        return consultorio;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }
}
