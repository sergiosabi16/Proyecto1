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
    String[] posiciones,ligas,UIDjugadores;
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

            if(txtNombreEquipo.getText().length()==0){
                txtNombreEquipo.setText("Escribe nombre de equipo");
                equipoNuevo=true;
            }else{
                equipoNuevo=false;
            }

            if(usuarioLogged==propietarioEquipo) {
                miEquipo();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

            if(equipoNuevo){

               // guardarEquipo();
                DialogFragment dialog = new DialogBienvenida();
                dialog.show(getFragmentManager(),"bienvenida");
            }
            //Toast.makeText(getActivity(), usuarioVisualizado, Toast.LENGTH_LONG).show();
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
    }
    private void miEquipo() {

        btnNombreEquipo.setVisibility(View.VISIBLE);
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
                        msn.enviarMensaje();
                    }

                }else if(id==R.id.verJugador){
                    Bundle bundle=new Bundle();

                    bundle.putString("usuarioVisualizado",UIDjugadores[posicion]);
                    bundle.putString("usuarioLogged",usuarioLogged);
                    bundle.putBoolean("usuarioNuevo",false);

                    main.verJugador(bundle);

                }else if(id==R.id.asignarme){
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
                    }else{
                        int posicion = getPosicion(v);
                        Boolean bucle = true, seSustituyeJugador = false;
                        String jugadorEliminado = "";
                        Map<String, Object> equipo = new HashMap<>();
                        Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).get();
                        do {
                            if (task.isSuccessful()) {
                                bucle = false;
                                if (task.getResult().getData() != null) ;
                                equipo = task.getResult().getData();
                                if (equipo.get(posiciones[posicion]) != null) {
                                    jugadorEliminado = task.getResult().get(posiciones[posicion]).toString();
                                }
                                if (jugadorEliminado.length() != 0) {
                                    seSustituyeJugador = true;
                                }
                            }
                        } while (bucle);
                        equipo.put(posiciones[posicion], usuarioLogged);
                        FirebaseFirestore.getInstance().collection("equipos").document(usuarioLogged).set(equipo);
                        if (seSustituyeJugador) {
                            MensajeJugadorEliminado(jugadorEliminado, usuarioLogged);
                        }

                        cargarEquipo();
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
                    if (nombre.equals(document.get("nombreEquipo"))) {
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
                bucle=false;
                DocumentSnapshot ds = task.getResult();
                if(ds.getData()!=null) {
                    txtNombreEquipo.setText(ds.get("nombreEquipo").toString());
                    for (int i = 0; i < posiciones.length; i++) {
                        if (ds.get(posiciones[i]) != null) {
                            Boolean aux = true;
                            Task<DocumentSnapshot> jugador = FirebaseFirestore.getInstance().collection("usuarios")
                                    .document(ds.get(posiciones[i]).toString()).get();
                            do {
                                if (jugador.isSuccessful()) {
                                    aux = false;
                                    jugadores[i] = jugador.getResult().get("nombreInvocador").toString();
                                }
                            } while (aux);

                        } else {
                            jugadores[i] = "No existe ningún jugador en esta posición";
                        }
                    }
                    UIDjugadores=jugadores;
                }else{
                    for(int i=0;i<jugadores.length;i++){
                        jugadores[i] = "No existe ningún jugador en esta posición";
                    };
                    UIDjugadores=jugadores;
                }
            }
        }while(bucle);
        jugadorTop.setText(jugadores[0]);
        jugadorJungla.setText(jugadores[1]);
        jugadorMid.setText(jugadores[2]);
        jugadorBot.setText(jugadores[3]);
        jugadorApoyo.setText(jugadores[4]);
        jugadorSuplente.setText(jugadores[5]);
    }

}
