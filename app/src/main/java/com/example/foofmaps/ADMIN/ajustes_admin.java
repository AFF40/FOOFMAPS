package com.example.foofmaps.ADMIN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ajustes_admin extends Fragment { @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_ajustes_admin, container, false);

    TextView textViewLogout = view.findViewById(R.id.btnLogout);



    textViewLogout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Cerrar la sesión, por ejemplo, eliminando el valor de sesión en SharedPreferences
            logout();
        }
    });

    return view;
}

    private void logout() {
        // eliminando el valor de sesión en SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences
                ("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Finalizar la actividad actual (fragment)
    }
}