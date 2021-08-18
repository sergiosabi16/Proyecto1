package com.example.proyecto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentBusqueda extends Fragment {

    ArrayList<String[]> lista;
    RecyclerView recycler;
    Button btnFiltro;
    private String[] posiciones, ligas;
    Boolean tipo; //true jugadores, false equipos

    public static FragmentBusqueda newInstance(Bundle arg){
        FragmentBusqueda f = new FragmentBusqueda();
        if(arg != null){
            f.setArguments(arg);
        }
        return f;
    }
    public FragmentBusqueda(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null){
            tipo=getArguments().getBoolean("tipo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null) {

            posiciones=getActivity().getResources().getStringArray(R.array.posiciones);
            ligas=getActivity().getResources().getStringArray(R.array.ligas);

            btnFiltro = view.findViewById(R.id.btnFiltro);
            btnFiltro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tipo) {
                        menuFiltroJugadores(btnFiltro);
                    }else{

                    }
                }
            });

            recycler= view.findViewById(R.id.recyclerView);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            Adaptador adaptador;
            if(tipo) {
                lista = crearConsultaJugadores(0, "");
                adaptador = new Adaptador(0,lista,(ActivityPrincipal)getActivity());
            }else{
                lista = crearConsultaEquipos(0,"");
                adaptador = new Adaptador(0,lista,(ActivityPrincipal)getActivity());
            }

            recycler.setAdapter(adaptador);
            //Toast.makeText(getActivity(), "onViewCreated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
        }
    }
    public ArrayList<String[]> crearConsultaJugadores(int campo, String valor){
        Boolean bucle;
        ArrayList<String[]> resultados= new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
            Task<QuerySnapshot> task=null;
        if(campo==1){ //busqueda por liga
            task = db.collection("usuarios").whereEqualTo("liga",valor).get();
        }else if(campo==2){ //busqueda por posicion
            task = db.collection("usuarios").whereEqualTo("posicion",valor).get();
        }else if(campo==3){ //busqueda por nombre
            task = db.collection("usuarios").whereEqualTo("nombreInvocador",valor).get();
        }else{
            task = db.collection("usuarios").get();
        }
        do {
            if (task.isSuccessful()) {
                bucle = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Boolean userVacio=false;
                    String[] user = new String[4];
                    user[0]=document.get("UID").toString();
                    user[1]=document.get("nombreInvocador").toString();
                    user[2]=document.get("liga").toString();
                    user[3]=document.get("posicion").toString();
                    for (int i=0;i<user.length;i++) {
                        if(user[i].length()==0){
                            userVacio=true;
                        }
                    }
                    if(!userVacio)
                        resultados.add(user);
                }
            }else{
                bucle=true;
            }
        }while (bucle);
        return resultados;
    }
    public void menuFiltroJugadores(View v){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filtro_jugadores, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id= item.getItemId();

                if(id==R.id.filtroJugadorLiga){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.titulo).setItems(R.array.ligas, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lista= crearConsultaJugadores(1,ligas[which]);
                            Adaptador adaptador = new Adaptador(0,lista,(ActivityPrincipal)getActivity());
                            recycler.setAdapter(adaptador);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(id==R.id.filtroJugadorPosicion){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.titulo).setItems(R.array.posiciones, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lista= crearConsultaJugadores(2,posiciones[which]);
                            Adaptador adaptador = new Adaptador(0,lista,(ActivityPrincipal)getActivity());
                            recycler.setAdapter(adaptador);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(id==R.id.filtroJugadorNombre){

                    final EditText txtDialog = new EditText(getActivity());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.titulo).setView(txtDialog)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    lista= crearConsultaJugadores(3,txtDialog.getText().toString());
                                    Adaptador adaptador = new Adaptador(0,lista,(ActivityPrincipal)getActivity());
                                    recycler.setAdapter(adaptador);
                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(id==R.id.filtroJugadorQuitar){
                    lista= crearConsultaJugadores(0,"");
                    Adaptador adaptador = new Adaptador(0,lista,(ActivityPrincipal)getActivity());
                    recycler.setAdapter(adaptador);
                }
                return false;
            }
        });
        popup.show();
    }
    public ArrayList<String[]> crearConsultaEquipos(int campo, String valor){
        Boolean bucle;
        ArrayList<String[]> resultados= new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task=null;
        if(campo==1){ //busqueda por liga
            task = db.collection("usuarios").whereEqualTo("liga",valor).get();
        }else if(campo==2){ //busqueda por posicion
            task = db.collection("usuarios").whereEqualTo("posicion",valor).get();
        }else if(campo==3){ //busqueda por nombre
            task = db.collection("usuarios").whereEqualTo("nombreInvocador",valor).get();
        }else{
            task = db.collection("equipos").get();
        }
        do {
            if (task.isSuccessful()) {
                bucle = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Boolean userVacio=false;
                    String[] user = new String[4];
                    user[0]=document.get("UID").toString();
                    user[1]=document.get("nombreInvocador").toString();
                    user[2]=document.get("liga").toString();
                    user[3]=document.get("posicion").toString();
                    for (int i=0;i<user.length;i++) {
                        if(user[i].length()==0){
                            userVacio=true;
                        }
                    }
                    if(!userVacio)
                        resultados.add(user);
                }
            }else{
                bucle=true;
            }
        }while (bucle);
        return resultados;
    }


}
