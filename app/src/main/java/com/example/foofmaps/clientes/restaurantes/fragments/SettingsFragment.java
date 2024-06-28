package com.example.foofmaps.clientes.restaurantes.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.MainActivity;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño XML para el fragmento de ajustes
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Obtener referencias a los elementos de la vista
        TextView textViewWhatsApp = view.findViewById(R.id.btnWhatsApp);
        TextView textViewLogout = view.findViewById(R.id.btnLogout);

        // Configurar el clic del botón de WhatsApp
        textViewWhatsApp.setOnClickListener(v -> abrirWhatsApp("+59169474930"));

        // Configurar el clic del botón de Cerrar sesión
        textViewLogout.setOnClickListener(v -> cerrarSesion());

        return view;
    }

    // Método para abrir WhatsApp con un número específico
    private void abrirWhatsApp(String phoneNumber) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber +
                    "&text=Hola!!!%20me%20gustaria%20agregar%20mi%20negocio%20a%20la%20aplicacion.";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para cerrar sesión
    private void cerrarSesion() {
        //limpiar las preferencias compartidas
        SharedPreferences preferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Finalizar la actividad actual (fragment)
    }
}

