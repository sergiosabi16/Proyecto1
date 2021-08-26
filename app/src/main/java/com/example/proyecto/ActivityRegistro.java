package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityRegistro extends AppCompatActivity {

    EditText txtCorreo,txtPassword;
    Button btnAceptar,btnCancelar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        btnAceptar = (Button) findViewById(R.id.btnAceptar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);

        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!txtCorreo.getText().toString().isEmpty() && !txtPassword.getText().toString().isEmpty()) {
                    if(txtPassword.getText().toString().length()<6){
                        Toast toast1 = Toast.makeText(getApplication(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT);
                        toast1.show();
                    }

                    mAuth.createUserWithEmailAndPassword(txtCorreo.getText().toString(), txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast toast1 = Toast.makeText(getApplication(), "La cuenta se ha creado con éxito", Toast.LENGTH_SHORT);
                                toast1.show();
                                Intent i= new Intent(v.getContext(), MainActivity.class);
                                startActivity(i);
                            } else {
                                Toast toast1 = Toast.makeText(getApplication(), "Esta cuenta no se pudo registrar", Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                        }
                    });
                }else{
                    Toast toast1 = Toast.makeText(getApplication(), "Rellena los campos", Toast.LENGTH_SHORT);
                    toast1.show();
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i= new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });

    }


}