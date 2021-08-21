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
    ArrayList<String[]> lista = new ArrayList<>();
    Adaptador adaptador;

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
                adaptador= new Adaptador(2,lista,(ActivityPrincipal)getActivity());
                recycler.setAdapter(adaptador);
                recycler.setHasFixedSize(true);

                cargarLista();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

        }
    }
    public void cargarLista(){

        lista.clear();
        FirebaseFirestore.getInstance().collection("mensajes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange: value.getDocumentChanges()){
                    if(documentChange.getDocument().get("destinatario")!=null) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED && documentChange.getDocument().get("destinatario").toString().equals(((ActivityPrincipal) getActivity()).getUsuarioLogged())) {
                            String msn[] = new String[4];
                            msn[0] = documentChange.getDocument().get("destinatario").toString();
                            msn[1] = documentChange.getDocument().get("origen").toString();
                            msn[2] = documentChange.getDocument().get("tipo").toString();
                            msn[3] = documentChange.getDocument().get("posicion").toString();
                            lista.add(msn);
                            adaptador.notifyDataSetChanged();
                            recycler.smoothScrollToPosition(lista.size());
                        }else if(documentChange.getType() == DocumentChange.Type.REMOVED && documentChange.getDocument().get("destinatario").toString().equals(((ActivityPrincipal) getActivity()).getUsuarioLogged())){
                            cargarLista();
                        }

                    }

                }
            }
        });
    };
}
