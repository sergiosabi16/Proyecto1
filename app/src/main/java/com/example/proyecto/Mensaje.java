package com.example.proyecto;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class Mensaje {
    String destinatario, origen;
    int tipo,posicion; // tipo 0 unirse a equipo, tipo 1 invitar a equipo, tipo 2 respuesta, tipo 3 eliminado de equipo, tipo 4 se fue del equipo
    Boolean visto;
    String[] posiciones;
    Context context;

    public Mensaje(Context context,String destinatario, String origen, int tipo, int posicion) {
        this.context=context;
        posiciones=context.getResources().getStringArray(R.array.posiciones);
        this.destinatario = destinatario;
        this.origen = origen;
        this.tipo = tipo;
        this.posicion = posicion;
        visto=false;
    }
    public void enviarMensaje(String destinatario,String origen,int tipo,int posicion){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> msn = new HashMap<>();

        msn.put("destinatario",destinatario);
        msn.put("origen",origen);
        msn.put("tipo", String.valueOf(tipo));
        msn.put("posicion", String.valueOf(posicion));
        msn.put("visto",String.valueOf(visto));

        db.collection("mensajes").document().set(msn);
    }
    public void enviarMensaje(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> msn = new HashMap<>();

        msn.put("destinatario",destinatario);
        msn.put("origen",origen);
        msn.put("tipo", String.valueOf(tipo));
        msn.put("posicion", String.valueOf(posicion));
        msn.put("visto",String.valueOf(visto));

        db.collection("mensajes").document().set(msn);
    }

    public String montarMensaje(){
        Boolean bucle=true;
        String msn="";
        if(tipo==1) {
            String nombreEquipo="";
            Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(origen).get();
            do {
                if (task.isSuccessful()) {
                    bucle=false;
                    DocumentSnapshot doc = task.getResult();
                    nombreEquipo=doc.get("nombreEquipo").toString();
                }
            } while (bucle);
            msn="El equipo '"+nombreEquipo+"' esta interesado en que juegues en la posición de "+posiciones[posicion]+".";
        }else if(tipo==3){
            String nombreEquipo="";
            Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(origen).get();
            do {
                if (task.isSuccessful()) {
                    bucle=false;
                    DocumentSnapshot doc = task.getResult();
                    nombreEquipo=doc.get("nombreEquipo").toString();
                }
            } while (bucle);
            msn="Lo sentimos, pero el equipo '"+nombreEquipo+"' ha decidido no contar más contigo.";
        }
        return msn;
    }
    public void cambiarJugador(){
        Boolean bucle=true,seSustituyeJugador=false;
        String jugadorEliminado="";
        Map<String, Object> jugador = new HashMap<>();
        if(tipo==1){
            Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(origen).get();
            do {
                if (task.isSuccessful()) {
                    bucle=false;
                    if(task.getResult().get(posiciones[posicion])!=null)
                        jugadorEliminado =task.getResult().get(posiciones[posicion]).toString();
                    if(jugadorEliminado.length()!=0){
                        seSustituyeJugador=true;
                    }
                }
            } while (bucle);
            jugador.put(posiciones[posicion],destinatario);
            FirebaseFirestore.getInstance().collection("equipos").document(origen).set(jugador);
        }
        if(seSustituyeJugador)
            MensajeJugadorEliminado(jugadorEliminado);
    };
    public void MensajeJugadorEliminado(String jugadorEliminado){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> msn = new HashMap<>();

        msn.put("destinatario",jugadorEliminado);
        msn.put("origen",origen);
        msn.put("tipo", String.valueOf(3));
        msn.put("visto",String.valueOf(visto));

        db.collection("mensajes").document().set(msn);
    };
//region Getters y Setters
    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    //endregion

}
