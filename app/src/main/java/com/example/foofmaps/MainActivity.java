package com.example.foofmaps;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.dueño.vista_dueno;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText ed_username, ed_password;
    Button btnLogin;
    TextView btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el valor de sesión y el rol de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        int userRole = sharedPreferences.getInt("userRole", -1); // Obtiene el rol del usuario desde SharedPreferences

        ed_username = findViewById(R.id.tv_usuario);
        ed_password = findViewById(R.id.tv_pass1);
        btnLogin = findViewById(R.id.btn_inicio);
        btnRegistrar = findViewById(R.id.tv_reg);

        btnRegistrar.setOnClickListener(v -> {
            Intent intent_registro = new Intent(MainActivity.this, registro.class);
            MainActivity.this.startActivity(intent_registro);
        });

        btnLogin.setOnClickListener(v -> {
            final String username = ed_username.getText().toString();
            final String pass1 = ed_password.getText().toString();

            // Realizar la solicitud al servidor
            LoginRequest loginRequest = new LoginRequest(username, pass1,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int exito = jsonResponse.getInt("exito");
                            String mensaje = jsonResponse.getString("msg");
                            Log.e("info", jsonResponse.toString());
                            if (exito == 1) {
                                // Registro exitoso, manejar el resultado aquí
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();

                                // Obtener el rol del usuario
                                int rol = jsonResponse.getInt("rol");

                                // Guardar el rol del usuario en SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putInt("userRole", rol); // Guarda el rol del usuario
                                editor.apply();

                                // Redirigir según el rol
                                if (rol == 1) {
                                    // Usuario con rol 1, redirige a MapsActivity
                                    Intent intent_login_exitoso = new Intent(MainActivity.this, MapsActivity.class);
                                    MainActivity.this.startActivity(intent_login_exitoso);
                                    finish(); // Finaliza la actividad actual para que no se pueda volver atrás desde aquí
                                } else if (rol == 2) {
                                    // Usuario con rol 2, redirige a vista_dueno
                                    Intent intent_login_dueño = new Intent(MainActivity.this, vista_dueno.class);
                                    MainActivity.this.startActivity(intent_login_dueño);
                                    finish(); // Finaliza la actividad actual
                                }
                            } else {
                                // Error en el registro, mostrar un mensaje al usuario
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginRequest);
        });

        // Verificar si el usuario ha iniciado sesión previamente
        if (isLoggedIn) {
            if (userRole == 1) {
                // Usuario con rol 1, redirige a MapsActivity
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para que no se pueda volver atrás desde aquí
            } else if (userRole == 2) {
                // Usuario con rol 2, redirige a vista_dueno
                Intent intent = new Intent(MainActivity.this, vista_dueno.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        }
    }
}
