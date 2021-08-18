 package com.example.proyecto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

 public class ActivityPrincipal extends AppCompatActivity {

    private static final String TAG = "Cosicas";
    private FirebaseAuth mAuth;
    private String usuarioLogged;
    private Boolean usuarioNuevo,notificaion=false;
    private MenuItem iconoNotificacion;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mAuth = FirebaseAuth.getInstance();
        usuarioLogged = mAuth.getUid();

        if(!existeUsuario(usuarioLogged)) {
            usuarioNuevo=true;
        }else{
            usuarioNuevo=false;
        }

        //iconoNotificacion = findViewById(R.id.mensajes);

        //notificaciones();

        Bundle bundle = new Bundle();

        bundle.putString("usuarioVisualizado", usuarioLogged);
        bundle.putString("usuarioLogged",usuarioLogged);
        bundle.putBoolean("usuarioNuevo",usuarioNuevo);

        FragmentPerfil fragment = FragmentPerfil.newInstance(bundle);

        fragmentManager.beginTransaction().replace(R.id.pantalla,fragment).commit();

    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buscarJugadores) {

            Bundle bundle = new Bundle();

            bundle.putString("usuarioVisualizado", usuarioLogged);
            bundle.putBoolean("usuarioNuevo", usuarioNuevo);
            bundle.putBoolean("tipo",true);

            FragmentBusqueda fragment = FragmentBusqueda.newInstance(bundle);

            fragmentManager.beginTransaction().replace(R.id.pantalla, fragment).commit();

        } else if (id == R.id.miEquipo) {

            Bundle bundle = new Bundle();
            bundle.putString("usuarioLogged", usuarioLogged);
            bundle.putString("equipoVisualizado", "miEquipo");
            bundle.putString("propietarioEquipo", usuarioLogged);
            bundle.putBoolean("equipoNuevo", true);

            FragmentEquipo fragmentEquipo = FragmentEquipo.newInstance(bundle);

            fragmentManager.beginTransaction().replace(R.id.pantalla, fragmentEquipo).commit();

            Toast.makeText(getApplicationContext(), "item 2", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.mensajes){

            Bundle bundle = new Bundle();
            bundle.putString("usuarioLogged", usuarioLogged);

            FragmentMensajes fragmentMensajes = FragmentMensajes.newInstance(bundle);

            fragmentManager.beginTransaction().replace(R.id.pantalla, fragmentMensajes).commit();
            notificaion=false;
        }else if(id == R.id.buscarEquipos){

            Bundle bundle = new Bundle();

            bundle.putString("usuarioVisualizado", usuarioLogged);
            bundle.putBoolean("usuarioNuevo", usuarioNuevo);
            bundle.putBoolean("tipo",false);

            FragmentBusqueda fragment = FragmentBusqueda.newInstance(bundle);

            fragmentManager.beginTransaction().replace(R.id.pantalla, fragment).commit();


        }else if(id == R.id.cerrarSesion){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            this.onDestroy();
        }
        return super.onOptionsItemSelected(item);
    }
    public void notificaciones(){

        FirebaseFirestore.getInstance().collection("mensajes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange: value.getDocumentChanges()){
                    if(documentChange.getDocument().get("destinatario").toString().equals(usuarioLogged) && documentChange.getType()== DocumentChange.Type.ADDED){
                        notificaion=true;
                    }
                    if(notificaion){
                        iconoNotificacion.setIcon(R.drawable.ic_baseline_notification_important_24);

                    }else{
                        iconoNotificacion.setIcon(R.drawable.ic_baseline_notifications_none_24);
                    }
                }
            }
        });
    }

    public Boolean existeUsuario(String UID){

        Boolean bucle=true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection("usuarios").get();
        do {
            if (task.isSuccessful()) {
                bucle = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (UID.equals(document.get("UID"))) {
                        return true;
                    }
                }
                return false;
            }
        }while (bucle);
        return false;
    }
    public Boolean nombreDisponible(String nombre){

        Boolean bucle=true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection("usuarios").get();
        do {
            if (task.isSuccessful()) {
                bucle = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (nombre.equals(document.get("nombreInvocador"))) {
                        return false;
                    }
                }
                return true;
            }
        }while (bucle);
        return false;
    }public String getUsuarioLogged(){
        return usuarioLogged;
    }

    public void verJugador(Bundle bundle) {
        FragmentPerfil fragmentEquipo = FragmentPerfil.newInstance(bundle);
        fragmentManager.beginTransaction().replace(R.id.pantalla, fragmentEquipo).commit();
    }
}