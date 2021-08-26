package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btnRegistrarse,btnEntrar;
    EditText txtCorreo,txtPassword;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtCorreo.getText().toString().isEmpty() && !txtPassword.getText().toString().isEmpty()) {
                    mAuth.signInWithEmailAndPassword(txtCorreo.getText().toString(), txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "Login exitoso", Toast.LENGTH_SHORT);
                                toast1.show();
                                Bundle UID = new Bundle();
                                UID.putString("UID", mAuth.getUid());

                                Intent i = new Intent(v.getContext(), ActivityPrincipal.class);
                                i.putExtras(UID);
                                startActivity(i);

                                txtCorreo.setText("");
                                txtPassword.setText("");
                            } else {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT);
                                toast1.show();

                            }
                        }
                    });
                }else{
                    Toast toast1 = Toast.makeText(v.getContext(), "Rellena los campos", Toast.LENGTH_SHORT);
                    toast1.show();

                }
            }
        });

        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ActivityRegistro.class);
                startActivity(i);
            }
        });
    }
}