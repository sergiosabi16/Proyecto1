package com.example.proyecto;

import static com.example.proyecto.Mensaje.MensajeJugadorEliminado;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FragmentEquipo extends Fragment {

    String propietarioEquipo,usuarioLogged;
    Boolean equipoNuevo;
    String[] posiciones,ligas,UIDjugadores=new String[6];
    TextView txtNombreEquipo,txtPropietarioEquipo,txtMediaLigas,jugadorTop,jugadorJungla,jugadorMid,jugadorBot,jugadorApoyo,jugadorSuplente;
    ImageButton btnNombreEquipo;
    ActivityPrincipal main;

    public static FragmentEquipo newInstance(Bundle arg){
        FragmentEquipo f = new FragmentEquipo();
        if(arg != null){
            f.setArguments(arg);
        }
        return f;
    }

    public FragmentEquipo(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null){
            usuarioLogged = getArguments().getString("usuarioLogged");
            propietarioEquipo = getArguments().getString("propietarioEquipo");
            equipoNuevo = getArguments().getBoolean("equipoNuevo");
        }

        //Toast.makeText(getActivity(),"onCreate",Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //posiciones=getActivity().getResources().getStringArray(R.array.posiciones);
        //ligas=getActivity().getResources().getStringArray(R.array.ligas);

        return inflater.inflate(R.layout.fragment_equipo, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {

            main = (ActivityPrincipal) getActivity();
            ligas=getResources().getStringArray(R.array.ligas);
            posiciones=getResources().getStringArray(R.array.posiciones);
            txtNombreEquipo= view.findViewById(R.id.nombreEquipo);
            txtPropietarioEquipo = view.findViewById(R.id.propetarioEquipo);
            txtMediaLigas = view.findViewById(R.id.mediaJugadores);
            jugadorTop = view.findViewById(R.id.jugadorTop);
            jugadorJungla = view.findViewById(R.id.jugadorJungla);
            jugadorMid = view.findViewById(R.id.jugadorMid);
            jugadorBot = view.findViewById(R.id.jugadorBot);
            jugadorApoyo = view.findViewById(R.id.jugadorApoyo);
            jugadorSuplente = view.findViewById(R.id.jugadorSuplente);

            btnNombreEquipo = view.findViewById(R.id.btnEditarNombreEquipo);

            cargarEquipo();

            jugadorTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(jugadorTop,0);
                }
            });
            jugadorJungla.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(jugadorJungla,1);
                }
            });
            jugadorMid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(jugadorMid,2);
                }
            });
            jugadorBot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(jugadorBot,3);
                }
            });
            jugadorApoyo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(jugadorApoyo,4);
                }
            });
            jugadorSuplente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(jugadorSuplente,5);
                }
            });
            if(usuarioLogged==propietarioEquipo) {
                miEquipo();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

        }
    }
    public void guardarEquipo(){
        Boolean bucle=true;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> equipo=new HashMap<>();
        Task<DocumentSnapshot> task = db.collection("equipos").document(usuarioLogged).get();
        do {
            if (task.isSuccessful()) {
                bucle=false;
                if(task.getResult().getData()!=null)
                    equipo = task.getResult().getData();

            }
        }while(bucle);

        equipo.put("nombreEquipo",txtNombreEquipo.getText().toString());
        equipo.put("propietarioEquipo",usuarioLogged);

        db.collection("equipos").document(usuarioLogged).set(equipo);
        equipoNuevo = false;
    }
    private void miEquipo() {
        if(equipoNuevo){
            DialogFragment dialog = new DialogBienvenida();
            dialog.show(getFragmentManager(),"bienvenida");
            txtNombreEquipo.setText("Nombre del equipo");
        }
        btnNombreEquipo.setVisibility(View.VISIBLE);
        txtPropietarioEquipo.setText("Usted es el propietario de este equipo");
        btnNombreEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText txtDialog = new EditText(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.titulo).setView(txtDialog)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(nombreDisponible(txtDialog.getText().toString())){
                                    txtNombreEquipo.setText(txtDialog.getText());
                                    guardarEquipo();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Error").setMessage("Lo sentimos, este nombre de equipo ya esta en uso")
                                            .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                    AlertDialog dialog1 = builder.create();
                                    dialog1.show();
                                }
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    /*private void equipoExterno(){


    }*/
    public void menu(View v,int posicion) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        if(usuarioLogged==propietarioEquipo){
            inflater.inflate(R.menu.jugador_interno,popup.getMenu());
        }else {
            inflater.inflate(R.menu.jugador_externo, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id= item.getItemId();

                if(id==R.id.solicitarUnirme){
                    Boolean jugadorRepetido=false;
                    for(int i=0;i<UIDjugadores.length;i++){
                        if(usuarioLogged==UIDjugadores[i]){
                            jugadorRepetido=true;
                        }
                    }
                    if(jugadorRepetido){
                        AlertDialog.Builder builder = new AlertDialog.Builder(main);
                        builder.setTitle("Ya tienes una posición asignada en este equipo").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog1 = builder.create();
                        dialog1.show();
                    }else {
                        Mensaje msn = new Mensaje(getContext(), propietarioEquipo, main.getUsuarioLogged(), 0, posicion);
                        if(msn.comprobarMensajeEnviado()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(main);
                            builder.setTitle("Ya has solicitado unirte a este equipo").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog dialog1 = builder.create();
                            dialog1.show();
                        }else{
                            msn.enviarMensaje();
                        }
                    }

                }else if(id==R.id.verJugador){
                    if(UIDjugadores[posicion].length()==0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(main);
                        builder.setTitle("No existe ningun jugador en esta posición").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog1 = builder.create();
                        dialog1.show();
                    }else {
                        Bundle bundle = new Bundle();

                        bundle.putString("usuarioVisualizado", UIDjugadores[posicion]);
                        bundle.putString("usuarioLogged", usuarioLogged);
                        bundle.putBoolean("usuarioNuevo", false);

                        main.verJugador(bundle);

                    }
                }else if(id==R.id.asignarme){
                    Boolean jugadorRepetido=false;
                    for(int i=0;i<UIDjugadores.length;i++){
                        if(usuarioLogged.equals(UIDjugadores[i])){
                            jugadorRepetido=true;
                        }
                    }
                    if(jugadorRepetido){
                        AlertDialog.Builder builder = new AlertDialog.Builder(main);
                        builder.setTitle("Ya tienes una posición asignada en este equipo").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog1 = builder.create();
                        dialog1.show();
                    }else{
                        int posicion = getPosicion(v);
                        Boolean bucle = true, seSustituyeJugador = false;
                        String jugadorEliminado = "";
                        Map<String, Object> equipo = new HashMap<>();
                        Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).get();
                        do {
                            if (task.isSuccessful()) {
                                bucle = false;
                                if (task.getResult().getData() != null) {
                                    equipo = task.getResult().getData();
                                }
                                if (equipo.get(posiciones[posicion]) != null) {
                                    jugadorEliminado = task.getResult().get(posiciones[posicion]).toString();
                                }
                                if (jugadorEliminado.length() != 0) {
                                    seSustituyeJugador = true;
                                }
                            }
                        } while (bucle);
                        if(!equipo.isEmpty()) {
                            equipo.put(posiciones[posicion], usuarioLogged);
                            FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).set(equipo);
                            if (seSustituyeJugador) {
                                MensajeJugadorEliminado(jugadorEliminado, usuarioLogged);
                            }

                            cargarEquipo();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(main);
                            builder.setTitle("Antes de nada asigna un nombre al equipo para guardarlo").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog dialog1 = builder.create();
                            dialog1.show();
                        }
                    }
                }else if(id==R.id.eliminarJugador){
                    int posicion=getPosicion(v);
                    Boolean bucle=true;
                    String jugadorEliminado="";
                    Map<String, Object> equipo = new HashMap<>();
                    Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).get();
                    do {
                        if (task.isSuccessful()) {
                            bucle=false;
                            if(task.getResult().getData()!=null);
                            equipo=task.getResult().getData();
                            if(equipo.get(posiciones[posicion])!=null){
                                jugadorEliminado =task.getResult().get(posiciones[posicion]).toString();
                                equipo.remove(posiciones[posicion]);
                            }
                        }
                    } while (bucle);
                    FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).set(equipo);
                    MensajeJugadorEliminado(jugadorEliminado,usuarioLogged);
                    cargarEquipo();
                }else if(id==R.id.abandonarEquipo){
                    int posicion=getPosicion(v);
                    Boolean bucle=true;
                    String jugadorEliminado="";
                    Map<String, Object> equipo = new HashMap<>();
                    Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).get();
                    do {
                        if (task.isSuccessful()) {
                            bucle=false;
                            if(task.getResult().getData()!=null);
                            equipo=task.getResult().getData();
                            equipo.remove(posiciones[posicion]);
                        }
                    } while (bucle);
                    FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).set(equipo);
                    //MensajeJugadorEliminado(jugadorEliminado,usuarioLogged);
                    cargarEquipo();
                }
                return false;
            }

            private int getPosicion(View v) {
                if(v==jugadorTop){
                    return 0;
                }else if(v==jugadorJungla){
                    return 1;
                }else if(v==jugadorMid){
                    return 2;
                }else if(v==jugadorBot){
                    return 3;
                }else if(v==jugadorApoyo){
                    return 4;
                }else{
                    return 5;
                }
            }
        });
        if(UIDjugadores[posicion].equals(main.getUsuarioLogged())&& !main.getUsuarioLogged().equals(propietarioEquipo)){
            popup.getMenu().findItem(R.id.abandonarEquipo).setVisible(true);
        }
        if(UIDjugadores[posicion].length()==0) {
            if(main.getUsuarioLogged().equals(propietarioEquipo)) {
                popup.getMenu().findItem(R.id.eliminarJugador).setVisible(false);
            }else{
                popup.getMenu().findItem(R.id.invitarJugador).setVisible(false);
            }
            popup.getMenu().findItem(R.id.verJugador).setVisible(false);
        }
        popup.show();
    }
    public Boolean nombreDisponible(String nombre){

        Boolean bucle=true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection("equipos").get();
        do {
            if (task.isSuccessful()) {
                bucle = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (nombre.equals(document.get("nombreEquipo").toString())) {
                        return false;
                    }
                }
                return true;
            }
        }while (bucle);
        return false;
    }
    public void cargarEquipo(){
       Boolean bucle=true;
       String jugadores[]=new String[6];
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       DocumentReference document = db.collection("equipos").document(propietarioEquipo);
       Task<DocumentSnapshot> task=document.get();
       do {
            if(task.isSuccessful()) {
                equipoNuevo=false;
                bucle=false;
                DocumentSnapshot ds = task.getResult();
                if(ds.getData()!=null) {
                    txtNombreEquipo.setText(ds.get("nombreEquipo").toString());
                    for (int i = 0; i < posiciones.length; i++) {
                        if (ds.get(posiciones[i]) != null) {
                            Boolean aux = true;
                            UIDjugadores[i] = ds.get(posiciones[i]).toString();
                            Task<DocumentSnapshot> jugador = FirebaseFirestore.getInstance().collection("usuarios")
                                    .document(ds.get(posiciones[i]).toString()).get();
                            do {
                                if (jugador.isSuccessful()) {
                                    aux = false;
                                    jugadores[i] = jugador.getResult().get("nombreInvocador").toString();
                                }
                            } while (aux);

                        } else {
                            UIDjugadores[i]="";
                            jugadores[i] = "No existe ningún jugador en esta posición";
                        }
                    }
                }else{
                    equipoNuevo=true;
                    for(int i=0;i<jugadores.length;i++){
                        UIDjugadores[i]="";
                        jugadores[i] = "No existe ningún jugador en esta posición";
                    };
                }
            }
       }while(bucle);

       bucle=true;
       Task<DocumentSnapshot> task1 = FirebaseFirestore.getInstance().collection("usuarios").document(propietarioEquipo).get();
       do{
           if(task1.isSuccessful()){
               bucle=false;
               txtPropietarioEquipo.setText(task1.getResult().get("nombreInvocador").toString());
           }
       }while(bucle);
       jugadorTop.setText(jugadores[0]);
       jugadorJungla.setText(jugadores[1]);
       jugadorMid.setText(jugadores[2]);
       jugadorBot.setText(jugadores[3]);
       jugadorApoyo.setText(jugadores[4]);
       jugadorSuplente.setText(jugadores[5]);
       txtMediaLigas.setText(calcularLiga());
    }
    public String calcularLiga(){
        int numJugadores=0,puntuacionTotal=0,resultado;
        for(int i=0;i<UIDjugadores.length;i++){
            if(UIDjugadores[i].length()!=0){
                numJugadores++;
                Boolean bucle=true;
                Task<DocumentSnapshot> task= FirebaseFirestore.getInstance().collection("usuarios").document(UIDjugadores[i]).get();
                do{
                    if(task.isSuccessful()){
                        bucle=false;
                        String liga=task.getResult().get("liga").toString();
                        for(int j=0;j<ligas.length;j++){
                            if(liga.equals(ligas[j])){
                                puntuacionTotal+=j;
                                j= ligas.length;
                            }
                        }
                    }
                }while(bucle);
            }
        }
        if(numJugadores!=0){
            resultado=puntuacionTotal/numJugadores;
            return ligas[resultado];
        }else{
            return "No hay jugadores para analizar nivel";
        }

    };
}
