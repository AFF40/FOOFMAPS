package com.example.foofmaps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.Context;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView textViewWhatsApp = view.findViewById(R.id.btnWhatsApp);
        TextView textViewLogout = view.findViewById(R.id.btnLogout);

        textViewWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir WhatsApp con un número de teléfono específico
                openWhatsApp("+59169474930");
            }
        });

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar la sesión, por ejemplo, eliminando el valor de sesión en SharedPreferences
                logout();
            }
        });

        return view;
    }

    private void openWhatsApp(String phoneNumber) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber+"&text=Hola!!!%20me%20gustaria%20agregar%20mi%20negocio%20a%20la%20aplicacion.";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar errores, por ejemplo, si WhatsApp no está instalado o si el número de teléfono no es válido
        }
    }

    private void logout() {
        // Agrega aquí la lógica para cerrar la sesión, por ejemplo, eliminando el valor de sesión en SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Finalizar la actividad actual (fragment)
    }
}
