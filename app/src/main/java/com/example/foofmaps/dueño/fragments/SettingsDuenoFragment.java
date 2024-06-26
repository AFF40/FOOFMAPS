package com.example.foofmaps.dueño.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.MainActivity;
import com.example.foofmaps.dueño.cliente.MapsDueCliActivity;

public class SettingsDuenoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_dueno, container, false);

        // Configurar los botones de la interfaz de usuario
        TextView textViewWhatsApp = view.findViewById(R.id.btnWhatsApp);
        TextView textViewLogout = view.findViewById(R.id.btnLogout);
        TextView textViewCambiarRol = view.findViewById(R.id.btn_cambiar_rol);
        Switch switchSesion = view.findViewById(R.id.switch_sesion);
        //setear el texto del boton
        textViewCambiarRol.setText("Cambiar a Modo Cliente");
        // Obtener el id del restaurante
        int id_rest = requireActivity().getIntent().getIntExtra("restaurante_id", -1);
        Bundle bundle = new Bundle();
        bundle.putInt("restaurante_id", id_rest);
        Log.d("id_rest_en_settings_due", String.valueOf(id_rest));

        // Configurar los eventos de clic de los botones
        // si el usuario hace clic en el botón de WhatsApp
        textViewWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir WhatsApp con un número de teléfono específico
                Informar_Error("+59169474930");
            }
        });

        textViewCambiarRol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el id del restaurante
                int id_rest = requireActivity().getIntent().getIntExtra("restaurante_id", -1);

                // Crear un nuevo Intent para la actividad MapsDueCliActivity
                Intent intent = new Intent(requireActivity(), MapsDueCliActivity.class);
                // Agregar el id del restaurante como un dato extra en el Intent
                intent.putExtra("restaurante_id", id_rest);
                Log.d("id_rest_enviando_a_duecli", String.valueOf(id_rest));

                // Finalizar la actividad principal
                requireActivity().finish();

                // Iniciar la actividad MapsDueCliActivity
                startActivity(intent);
            }
        });


        Log.d("sharedprefs_settings_mansesion", String.valueOf(requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getBoolean("mantenersesion", true)));
        if (requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getBoolean("mantenersesion", true)) {
            switchSesion.setChecked(true);
            //cambiar el color del boton del switch a verde
            switchSesion.getThumbDrawable().setTint(getResources().getColor(R.color.verde));
            Log.d("switch_sesion", "true");
        }
        else {
            switchSesion.setChecked(false);
            //cambiar el color del switch a rojo
            switchSesion.getThumbDrawable().setTint(getResources().getColor(R.color.rojo));
            Log.d("switch_sesion", "false");
        }
        switchSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchSesion.isChecked()) {
                    // cambiar valor de shared preferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences
                            ("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("mantenersesion", true);
                    editor.apply();

                    // Cambiar el color del thumb a verde
                    switchSesion.getThumbDrawable().setTint(getResources().getColor(R.color.verde));
                } else {
                    // cambiar valor de shared preferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences
                            ("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("mantenersesion", false);
                    editor.apply();

                    // Cambiar el color del thumb a rojo
                    switchSesion.getThumbDrawable().setTint(getResources().getColor(R.color.rojo));
                }
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

    private void Informar_Error(String phoneNumber) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber+
                    "&text=" + "Hola, tengo un problema con la aplicacion. ¿Puedes ayudarme?";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
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
