package com.example.proyecto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentMensajes extends Fragment {

    RecyclerView recycler;

    public static FragmentMensajes newInstance(Bundle arg){
        FragmentMensajes f = new FragmentMensajes();
        if(arg != null){
            f.setArguments(arg);
        }
        return f;
    }

    public FragmentMensajes(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null){

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mensajes, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null) {

            recycler= view.findViewById(R.id.recyclerMensajes);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            EscuchaMensajes escuchaMensajes= new EscuchaMensajes();
            //escuchaMensajes.run();
            ArrayList<String[]> lista = new ArrayList<>();
            Adaptador adaptador= new Adaptador(2,lista,(ActivityPrincipal)getActivity());
            recycler.setAdapter(adaptador);
            recycler.setHasFixedSize(true);

            FirebaseFirestore.getInstance().collection("mensajes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange documentChange: value.getDocumentChanges()){
                        if(documentChange.getType()== DocumentChange.Type.ADDED){
                            String msn[] = new String[4];
                            msn[0]=documentChange.getDocument().get("destinatario").toString();
                            lista.add(msn);
                            adaptador.notifyDataSetChanged();
                            recycler.smoothScrollToPosition(lista.size());
                        }

                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

            /*if(usuarioNuevo){
                DialogFragment dialog = new DialogBienvenida();
                dialog.show(getFragmentManager(),"bienvenida");
            }*/

        }
    }
}
