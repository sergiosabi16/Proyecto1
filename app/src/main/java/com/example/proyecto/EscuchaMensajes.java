package com.example.proyecto;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EscuchaMensajes extends Thread{

    String usuarioLogged;
    ArrayList<String[]> lista;

    @Override
    public void run() {
        while(true){
            lista=escuchar();
        }
    }

    public ArrayList<String[]> escuchar(){

        Boolean bucle=true;
        ArrayList<String[]> resultados= new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection("mensajes").whereEqualTo("destinatario",usuarioLogged).get();
        do {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document:task.getResult()){
                    String[] mensaje = new String[3];

                    mensaje[0]=document.get("destinatario").toString();
                    mensaje[1]=document.get("origen").toString();
                    resultados.add(mensaje);
                }
                bucle=false;
            }
        }while (bucle);
        return resultados;
    }

}
