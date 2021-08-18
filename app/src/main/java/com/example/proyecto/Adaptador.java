package com.example.proyecto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolderAdaptador>{

    ArrayList<String[]> lista;
    int tipo; //tipo 0 jugadores, tipo 1 equipos, tipo 2 mensajes
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
        }else if(tipo==1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_equipo,null,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, null, false);
        }

        return new ViewHolderAdaptador(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptador.ViewHolderAdaptador holder, int position) {

        if(tipo==0) {
            holder.asignarDatos(lista.get(position));
        }else if(tipo==1){
            holder.asignarDatosEquipo(lista.get(position));
        }
        else{
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
                nombreUsuarioLista = itemView.findViewById(R.id.nombreUsuarioLista);
                ligaUsuarioLista = itemView.findViewById(R.id.ligaUsuarioLista);
                posicionUsuarioLista = itemView.findViewById(R.id.posicionUsuarioLista);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuJugador(itemView);
                    }
                });
            }else if(tipo==1){
                nombreUsuarioLista = itemView.findViewById(R.id.nombreEquipoLista);
                ligaUsuarioLista = itemView.findViewById(R.id.ligaEquipoLista);
                posicionUsuarioLista = itemView.findViewById(R.id.propietarioEquipoLista);
            }
            else{
                nombreUsuarioLista = itemView.findViewById(R.id.textoMsg);
            }
        }
        public void asignarDatosMensaje(String [] msn){
            //Mensaje mensaje= new Mensaje(main.getApplicationContext(),msn[0],msn[1],Integer.getInteger(msn[2]),Integer.getInteger(msn[3]));
            nombreUsuarioLista.setText(msn[1]);
        }
        public void asignarDatosEquipo(String [] equipo){
            nombreUsuarioLista.setText(equipo[1]);
            ligaUsuarioLista.setText(equipo[2]);
            posicionUsuarioLista.setText(equipo[3]);
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
                        }else if(id==R.id.invitarJugador){
                            AlertDialog.Builder builder = new AlertDialog.Builder(main);
                            builder.setTitle(R.string.titulo).setItems(R.array.posiciones, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Mensaje msn=new Mensaje(main.getApplicationContext(),UID,main.getUsuarioLogged(),1,which);
                                    msn.enviarMensaje();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    return false;
                }
            });
            popup.show();
        }
    }
}
