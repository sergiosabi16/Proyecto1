package com.example.proyecto;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Mensaje {
    String destinatario, origen;
    int tipo,posicion; // tipo 0 unirse a equipo, tipo 1 invitar a equipo, tipo 2 respuesta, tipo 3 eliminado de equipo, tipo 4 se fue del equipo
    Boolean visto;

    public Mensaje(String destinatario, String origen, int tipo,int posicion) {
        this.destinatario = destinatario;
        this.origen = origen;
        this.tipo = tipo;
        this.posicion = posicion;
        visto=false;
    }

    public void guardarMensaje(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> msn = new HashMap<>();

        msn.put("destinatario",destinatario);
        msn.put("origen",origen);
        msn.put("tipo", String.valueOf(tipo));
        msn.put("posicion", String.valueOf(posicion));
        msn.put("visto",String.valueOf(visto));

        db.collection("mensajes").document().set(msn);
    }

}
