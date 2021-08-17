package com.example.proyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolderAdaptador>{

    ArrayList<String[]> lista;
    int tipo;
    ActivityPrincipal main;

    public Adaptador(int tipo,ArrayList<String[]> lista,ActivityPrincipal main) {
        this.tipo = tipo;
        this.lista = lista;
        this.main = main;

    }

    @NonNull
    @Override
    public ViewHolderAdaptador onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(tipo==0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_perfil, null, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, null, false);
        }

        return new ViewHolderAdaptador(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptador.ViewHolderAdaptador holder, int position) {

        if(tipo==0) {
            holder.asignarDatos(lista.get(position));
        }else{
            holder.asignarDatosMensaje(lista.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolderAdaptador extends RecyclerView.ViewHolder {

        String UID;
        TextView nombreUsuarioLista,ligaUsuarioLista,posicionUsuarioLista;

        public ViewHolderAdaptador(@NonNull View itemView) {
            super(itemView);
            if(tipo==0) {
                nombreUsuarioLista = itemView.findViewById(R.id.nombreUsuarioList);
                ligaUsuarioLista = itemView.findViewById(R.id.ligaUsuarioLista);
                posicionUsuarioLista = itemView.findViewById(R.id.posicionUsuarioLista);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuJugador(itemView);
                    }
                });
            }else{
                nombreUsuarioLista = itemView.findViewById(R.id.destinatarioMsg);
            }
        }
        public void asignarDatosMensaje(String [] msn){
            nombreUsuarioLista.setText(msn[0]);
        }

        public void asignarDatos(String[] usuario) {
            nombreUsuarioLista.setText(usuario[1]);
            ligaUsuarioLista.setText(usuario[2]);
            posicionUsuarioLista.setText(usuario[3]);
            UID = usuario[0];

        }
        public void menuJugador(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.jugador_externo, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                        int id= item.getItemId();
                        if(id==R.id.verJugador){
                            Bundle bundle= new Bundle();
                            bundle.putString("usuarioVisualizado", UID);
                            bundle.putBoolean("usuarioNuevo", false);

                            main.verJugador(bundle);
                        }
                    return false;
                }
            });
            popup.show();
        }
    }
}
