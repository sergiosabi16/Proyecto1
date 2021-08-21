package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

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
    String[] posiciones;
    Context context;

    public Mensaje(Context context,String destinatario, String origen, int tipo, int posicion) {
        this.context=context;
        posiciones=context.getResources().getStringArray(R.array.posiciones);
        this.destinatario = destinatario;
        this.origen = origen;
        this.tipo = tipo;
        this.posicion = posicion;
    }
    public void enviarMensaje(String destinatario,String origen,int tipo,int posicion){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> msn = new HashMap<>();

        msn.put("destinatario",destinatario);
        msn.put("origen",origen);
        msn.put("tipo", String.valueOf(tipo));
        msn.put("posicion", String.valueOf(posicion));


        db.collection("mensajes").document().set(msn);
    }
    public void enviarMensaje(){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> msn = new HashMap<>();

            msn.put("destinatario", destinatario);
            msn.put("origen", origen);
            msn.put("tipo", String.valueOf(tipo));
            msn.put("posicion", String.valueOf(posicion));

            Task task = db.collection("mensajes").document().set(msn);
            do {
                //wait
            } while (!task.isSuccessful());

    }
    public String montarMensaje(){

        Boolean bucle=true;
        String msn="";
        try{
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
        }catch (Exception e){
            msn="ERROR FATAL AL LEER EL MENSAJE";
        }
        return msn;
    }
    public void cambiarJugador(){
        Boolean bucle=true,seSustituyeJugador=false;
        String jugadorEliminado="";
        Map<String, Object> equipo = new HashMap<>();
        if(tipo==1){
            Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(origen).get();
            do {
                if (task.isSuccessful()) {
                    bucle=false;
                    if(task.getResult().getData()!=null);
                        equipo=task.getResult().getData();
                    if(equipo.get(posiciones[posicion])!=null){
                        jugadorEliminado =task.getResult().get(posiciones[posicion]).toString();
                    }
                    if(jugadorEliminado.length()!=0){
                        seSustituyeJugador=true;
                    }
                }
            } while (bucle);
            equipo.put(posiciones[posicion],destinatario);
            FirebaseFirestore.getInstance().collection("equipos").document(origen).set(equipo);
        }
        if(seSustituyeJugador)
            MensajeJugadorEliminado(jugadorEliminado,origen);
    };
    public static void MensajeJugadorEliminado(String jugadorEliminado,String origen){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> msn = new HashMap<>();

        msn.put("destinatario",jugadorEliminado);
        msn.put("origen",origen);
        msn.put("tipo", String.valueOf(3));
        msn.put("posicion",String.valueOf(0));

        Task task= db.collection("mensajes").document().set(msn);
        do{
            //wait
        }while (!task.isSuccessful());
    };
    public void eliminarMensaje(){


    };
    public Boolean comprobarMensajeEnviado(){
        Boolean bucle=true;
        Task<QuerySnapshot> task = FirebaseFirestore.getInstance().collection("mensajes").whereEqualTo("destinatario",destinatario).whereEqualTo("origen",origen)
                .whereEqualTo("tipo",String.valueOf(tipo)).get();
        do{
            if(task.isSuccessful()){
                bucle=false;
                for(DocumentSnapshot doc : task.getResult()){
                    if(doc.exists()){
                        return true;
                    }
                }
                return false;
            }
        }while(bucle);

        return true;
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
