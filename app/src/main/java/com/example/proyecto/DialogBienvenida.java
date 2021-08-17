package com.example.proyecto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DialogBienvenida extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.titulo)
                .setMessage(R.string.bienvenida)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

