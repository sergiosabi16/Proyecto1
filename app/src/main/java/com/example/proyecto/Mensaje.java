package com.example.proyecto;

public class Mensaje {
    String destinatario, origen;
    int tipo; // tipo 0 unirse a equipo, tipo 1 invitar a equipo, tipo 2 respuesta, tipo 3 eliminado de equipo, tipo 4 se fue del equipo
    Boolean visto;

    public Mensaje(String destinatario, String origen, int tipo) {
        this.destinatario = destinatario;
        this.origen = origen;
        this.tipo = tipo;
        visto=false;
    }
}
