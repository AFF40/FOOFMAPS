package com.example.foofmaps.clientes.restaurantes.fragments;

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


    private void cerrarSesion() {
        //limpiar las preferencias guardadas en SharedPreferences y redirigir al usuario a la actividad de inicio de sesión|// Obtener el valor de sesión y el rol de SharedPreferences
        //        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        //        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        //        int userRole = sharedPreferences.getInt("userRole", -1); // Obtiene el rol del usuario desde SharedPreferences
        //        int id_rest = sharedPreferences.getInt("restaurante_id", -1); // Obtiene el id_rest del usuario desde SharedPreferences
        //        boolean mantenersesion = sharedPreferences.getBoolean("mantenersesion", false); // Obtiene el valor de mantener sesión

        //limpiar las preferencias guardadas una por una en SharedPreferences y redirigir al usuario a la actividad de inicio de sesión
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn");
        editor.remove("userRole");
        editor.remove("restaurante_id");
        editor.remove("mantenersesion");
        editor.apply();
        //redirigir al usuario a la actividad de inicio de sesión
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        //que no se pueda volver atras
        requireActivity().finish();

    }
}

