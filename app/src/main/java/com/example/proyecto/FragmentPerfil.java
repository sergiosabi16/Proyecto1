package com.example.proyecto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;


public class FragmentPerfil extends Fragment {

    private String usuarioVisualizado,usuarioLogged;
    private TextView nombreUsuario, ligaUsuario, posicionUsuario,descripcion;
    private ImageButton btnNombre, btnLiga, btnPosicion;
    private Button btnGuardarCambios;
    private Boolean usuarioNuevo;
    private String[] posiciones, ligas;
    private EditText txtDescripcion;



    public static FragmentPerfil newInstance(Bundle arg){
        FragmentPerfil f = new FragmentPerfil();
        if(arg != null){
            f.setArguments(arg);
        }
        return f;
    }

    public FragmentPerfil(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null){
            usuarioVisualizado = getArguments().getString("usuarioVisualizado");
            ActivityPrincipal main=(ActivityPrincipal)getActivity();
            usuarioLogged = main.getUsuarioLogged();
            usuarioNuevo = getArguments().getBoolean("usuarioNuevo");
        }

        //Toast.makeText(getActivity(),"onCreate",Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        posiciones=getActivity().getResources().getStringArray(R.array.posiciones);
        ligas=getActivity().getResources().getStringArray(R.array.ligas);

        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {

            nombreUsuario = view.findViewById(R.id.lblNombreInvocador);
            ligaUsuario = view.findViewById(R.id.lblLiga);
            posicionUsuario = view.findViewById(R.id.lblPosicion);
            descripcion = view.findViewById(R.id.lblDescripcion);

            txtDescripcion = view.findViewById(R.id.txtDescripcion);
            btnNombre = view.findViewById(R.id.btnEditarNombre);
            btnLiga = view.findViewById(R.id.btnEditarLiga);
            btnPosicion = view.findViewById(R.id.btnEditarPosicion);

            cargarUsuario();

            if(nombreUsuario.getText().length()==0){
                nombreUsuario.setText("Escribe nombre de usuario");
                ligaUsuario.setText("Elige una liga");
                posicionUsuario.setText("Elige una posición");
            }

            if(usuarioLogged==usuarioVisualizado) {
                usuarioPropio();
            }
            //Toast.makeText(getActivity(), "onViewCreated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

            if(usuarioNuevo){
                DialogFragment dialog = new DialogBienvenida();
                dialog.show(getFragmentManager(),"bienvenida");
            }
            //Toast.makeText(getActivity(), usuarioVisualizado, Toast.LENGTH_LONG).show();
        }
    }

    public void cargarUsuario(){
        Boolean aux=true;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference document = db.collection("usuarios").document(usuarioVisualizado);
        Task<DocumentSnapshot> task=document.get();
        do {
            if(task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                nombreUsuario.setText((CharSequence) ds.get("nombreInvocador"));
                ligaUsuario.setText((CharSequence) ds.get("liga"));
                posicionUsuario.setText((CharSequence) ds.get("posicion"));
                descripcion.setText((CharSequence) ds.get("descripcion"));
                txtDescripcion.setText((CharSequence) ds.get("descripcion"));
                aux=false ;
            }
        }while(aux);
    }
    public void usuarioPropio(){

        descripcion.setVisibility(View.GONE);
        txtDescripcion.setVisibility(View.VISIBLE);
        btnNombre.setVisibility(View.VISIBLE);
        btnLiga.setVisibility(View.VISIBLE);
        btnPosicion.setVisibility(View.VISIBLE);

        txtDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                guardarUsuario(usuarioLogged,nombreUsuario.getText().toString(),ligaUsuario.getText().toString(),posicionUsuario.getText().toString());
            }
        });

        btnNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText txtDialog = new EditText(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.titulo).setView(txtDialog)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(((ActivityPrincipal)getActivity()).nombreDisponible(txtDialog.getText().toString())){
                                    nombreUsuario.setText(txtDialog.getText());
                                    guardarUsuario(usuarioLogged,nombreUsuario.getText().toString(),ligaUsuario.getText().toString(),posicionUsuario.getText().toString());
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Error").setMessage("Lo sentimos, este nombre de usuario ya esta en uso")
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
        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.titulo).setItems(R.array.posiciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        posicionUsuario.setText(posiciones[which]);
                        guardarUsuario(usuarioLogged,nombreUsuario.getText().toString(),ligaUsuario.getText().toString(),posicionUsuario.getText().toString());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        btnLiga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.titulo).setItems(R.array.ligas, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ligaUsuario.setText(ligas[which]);
                        guardarUsuario(usuarioLogged,nombreUsuario.getText().toString(),ligaUsuario.getText().toString(),posicionUsuario.getText().toString());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    public void guardarUsuario (String UID, String nombre,String liga,String posicion){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();

        user.put("UID",UID);
        user.put("nombreInvocador",nombre);
        user.put("liga",liga);
        user.put("posicion",posicion);
        user.put("descripcion",txtDescripcion.getText().toString());

        db.collection("usuarios").document(usuarioLogged).set(user);
    }

}