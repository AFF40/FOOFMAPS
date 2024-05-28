package com.example.foofmaps.clientes.restaurantes;// MainActivity.java

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.ADMIN.Vista_administrador;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.dueño.vista_dueno2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText ed_username, ed_password;
    Button btnLogin;
    TextView btnRegistrar;

    // Agrega una variable de instancia para username
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el valor de sesión y el rol de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        int userRole = sharedPreferences.getInt("userRole", -1); // Obtiene el rol del usuario desde SharedPreferences

        if (isLoggedIn && userRole != -1) {
            // Usuario ya ha iniciado sesión, redirigir según su rol
            if (userRole == 2) {
                // Realizar la consulta para obtener id_rest desde la base de datos
                logout();
            }

            redirectAccordingToRole(userRole);
            Log.d("userRole", String.valueOf(userRole));
        } else {
            // Usuario no ha iniciado sesión, mostrar pantalla de inicio de sesión
            initializeLoginScreen();
        }
    }

    private void redirectAccordingToRole(int userRole) {
        Intent intent;
        switch (userRole) {
            case 1:
                // Usuario con rol 1, redirige a MapsActivity
                intent = new Intent(MainActivity.this, MapsCliActivity.class);
                break;
            case 2:
                // Realizar la consulta para obtener id_rest desde la base de datos
                obtenerIdRestDesdeBaseDeDatos();
                return; // Evita que la actividad se cierre antes de obtener el id_rest
            case 3:
                // Usuario con rol 3, redirige a Vista_administrador y pasa el id_usuario
                intent = new Intent(MainActivity.this, Vista_administrador.class);

                // Obtener el id_usuario de SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int id_usuario = sharedPreferences.getInt("id_usuario", -1);

                // Agregar el id_usuario como un extra en el intent
                intent.putExtra("id_usuario", id_usuario);
                Log.d("id_usuario_admin", String.valueOf(id_usuario));
                break;
            default:
                // Cerrar sesión si el rol no es válido
                logout();
                return;
        }
        startActivity(intent);
        finish(); // Finaliza la actividad actual para que no se pueda volver atrás desde aquí
    }

    private void initializeLoginScreen() {
        ed_username = findViewById(R.id.tv_usuario);
        ed_password = findViewById(R.id.tv_pass1);
        btnLogin = findViewById(R.id.btn_inicio);
        btnRegistrar = findViewById(R.id.tv_reg);

        btnRegistrar.setOnClickListener(v -> {
            Intent intent_registro = new Intent(MainActivity.this, registro.class);
            startActivity(intent_registro);
        });

        btnLogin.setOnClickListener(v -> {
            onPause(); // Pausa la actividad mientras se realiza la solicitud
            // Asigna el valor de ed_username a la variable username
            username = ed_username.getText().toString();
            final String pass1 = ed_password.getText().toString();

            // Realizar la solicitud al servidor
            LoginRequest loginRequest = new LoginRequest(username, pass1,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int exito = jsonResponse.getInt("exito");
                            String mensaje = jsonResponse.getString("msg");
                            Log.e("info_login", jsonResponse.toString());
                            if (exito == 1) {
                                // Registro exitoso, manejar el resultado aquí
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();

                                // Obtener el rol y el id del usuario
                                int rol = jsonResponse.getInt("id_rol");
                                int id_usuario = jsonResponse.getInt("id_usuario"); // Obtener el id_usuario del JSON

                                // Guardar el rol y el id del usuario en SharedPreferences
                                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putInt("userRole", rol); // Guarda el rol del usuario
                                editor.putInt("id_usuario", id_usuario); // Guarda el id_usuario del usuario
                                editor.apply();

                                // Redirigir según el rol
                                redirectAccordingToRole(rol);
                            }
                            else {
                                // Error en el registro, mostrar un mensaje al usuario
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                onResume();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginRequest);
        });
    }

    // Modificamos la función para que obtenga el restaurante_id de forma asíncrona
    private void obtenerIdRestDesdeBaseDeDatos() {
        String modeloURL = Config.MODELO_URL+"/consultar_id_rest.php";
        Log.d("urledit", "apiUrl: " + modeloURL);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, modeloURL, response -> {
            Log.d("usuarioenviado", username);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                Log.d("response_id_rest", jsonResponse.toString());
                int id_rest = jsonResponse.getInt("id_rest");
                Log.d("restaurante_id_rec", String.valueOf(id_rest));
                // Se ha obtenido el restaurante_id, redirige a vista_dueno
                Intent intent_login_dueño = new Intent(MainActivity.this, vista_dueno2.class);
                intent_login_dueño.putExtra("restaurante_id", id_rest);
                Log.d("restaurante_id_enviado", String.valueOf(id_rest));
                startActivity(intent_login_dueño);
                finish(); // Finaliza la actividad actual
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Manejar errores de la solicitud
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    //pausar la actividad
    @Override
    protected void onPause() {
        super.onPause();
        // no permitir que el usuario pueda interactuar con la actividad mientras se realiza la solicitud
        btnLogin.setEnabled(false);
        btnRegistrar.setEnabled(false);
    }

    //reanudar la actividad
    @Override
    protected void onResume() {
        super.onResume();
        // permitir que el usuario pueda interactuar con la actividad
        btnLogin.setEnabled(true);
        btnRegistrar.setEnabled(true);
    }

    private void logout() {
        // Eliminar el valor de sesión en SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual
    }
}

