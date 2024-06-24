package com.example.foofmaps.clientes.restaurantes;

import android.app.ProgressDialog;
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
import com.android.volley.Response;
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

    private String username; // Variable para almacenar el nombre de usuario
    private ProgressDialog progressDialog; // Cuadro de diálogo para mostrar el progreso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el cuadro de diálogo de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);

        // Obtener el valor de sesión y el rol de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        int userRole = sharedPreferences.getInt("userRole", -1); // Obtiene el rol del usuario desde SharedPreferences
        int id_rest = sharedPreferences.getInt("restaurante_id", -1); // Obtiene el id_rest del usuario desde SharedPreferences
        boolean mantenersesion = sharedPreferences.getBoolean("mantenersesion", false); // Obtiene el valor de mantener sesión

        // Log para el estado de mantenimiento de sesión
        Log.d("sharedpref_estado", String.valueOf(mantenersesion));
        Log.d("sharedpref_islogged", String.valueOf(isLoggedIn));
        Log.d("sharedpref_userRole", String.valueOf(userRole));
        Log.d("sharedpref_id_rest", String.valueOf(id_rest));

        if (isLoggedIn && userRole != -1 && id_rest != -1) {

            // Usuario ya ha iniciado sesión, redirigir según su rol
            if (userRole == 2 && mantenersesion) {
                // Usuario con rol 2 y mantener sesión activado, redirige a vista_dueno2
                Intent intent = new Intent(MainActivity.this, vista_dueno2.class);
                intent.putExtra("restaurante_id", id_rest);
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            } else {
                // Cualquier otro caso (por ejemplo, si el usuario no tiene sesión activa), cerrar sesión
                Toast.makeText(getApplicationContext(), "Se ha cerrado la sesión", Toast.LENGTH_SHORT).show();
                logout();
            }

            if (userRole == 3) {
                // Realizar la consulta para obtener id_rest desde la base de datos si es usuario con rol 3
                obtenerIdRestDesdeBaseDeDatos();
            }

            // Redirigir según el rol del usuario
            redirectAccordingToRole(userRole);
            Log.d("userRole", String.valueOf(userRole));
        } else {
            // Usuario no ha iniciado sesión, mostrar pantalla de inicio de sesión
            initializeLoginScreen();
        }
        if (userRole == 1 ){
            Intent intent = new Intent(MainActivity.this, MapsCliActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void redirectAccordingToRole(int userRole) {
        Intent intent;
        switch (userRole) {
            case 1:
                // Usuario con rol 1, redirige a MapsCliActivity
                intent = new Intent(MainActivity.this, MapsCliActivity.class);
                break;
            case 2:
                // Usuario con rol 2 y con id_rest obtenido, redirige a vista_dueno2
                int id_rest = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("restaurante_id", -1);
                if (id_rest != -1) {
                    intent = new Intent(MainActivity.this, vista_dueno2.class);
                    intent.putExtra("restaurante_id", id_rest);
                    Log.d("restaurante_id_enviado_a_vista", String.valueOf(id_rest));
                } else {
                    // Si no se encuentra id_rest en SharedPreferences, obtenerlo desde la base de datos
                    obtenerIdRestDesdeBaseDeDatos();
                    return; // Evita que la actividad se cierre antes de obtener el id_rest
                }
                break;
            case 3:
                // Usuario con rol 3, redirige a Vista_administrador
                intent = new Intent(MainActivity.this, Vista_administrador.class);
                // Obtener el id_usuario de SharedPreferences
                int id_usuario = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("id_usuario", -1);
                // Agregar el id_usuario como extra en el intent
                intent.putExtra("id_usuario", id_usuario);
                Log.d("id_usuario_admin", String.valueOf(id_usuario));
                break;
            default:
                // Cerrar sesión si el rol no es válido
                logout();
                return;
        }
        startActivity(intent);
        finish(); // Finaliza la actividad actual para evitar que el usuario regrese presionando Atrás
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
            // Asignar el valor de ed_username a la variable username
            username = ed_username.getText().toString();
            final String pass1 = ed_password.getText().toString();

            // Mostrar el cuadro de diálogo de progreso
            progressDialog.show();

            // Realizar la solicitud al servidor
            LoginRequest loginRequest = new LoginRequest(username, pass1,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Ocultar el cuadro de diálogo de progreso
                            progressDialog.dismiss();
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
                                    int id_usuario = jsonResponse.getInt("id_usuario");

                                    // Guardar el rol y el id del usuario en SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putInt("userRole", rol);
                                    editor.putInt("id_usuario", id_usuario);
                                    editor.apply();

                                    // Redirigir según el rol
                                    redirectAccordingToRole(rol);
                                } else {
                                    // Error en el registro, mostrar mensaje de error
                                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginRequest);
        });
    }

    // Método para obtener el id_rest desde la base de datos
    private void obtenerIdRestDesdeBaseDeDatos() {
        String modeloURL = Config.MODELO_URL + "/consultar_id_rest.php";
        Log.d("urledit", "apiUrl: " + modeloURL);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, modeloURL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        Log.d("response_id_rest", jsonResponse.toString());
                        int id_rest = jsonResponse.getInt("id_rest");

                        // Guardar id_rest en SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                        editor.putInt("restaurante_id", id_rest);
                        editor.apply();
                        Log.d("restaurante_id_rec", String.valueOf(id_rest));

                        // Redirigir a vista_dueno2 y pasar id_rest como extra
                        Intent intent_login_dueño = new Intent(MainActivity.this, vista_dueno2.class);
                        intent_login_dueño.putExtra("restaurante_id", id_rest);
                        Log.d("restaurante_id_enviado", String.valueOf(id_rest));
                        startActivity(intent_login_dueño);
                        finish(); // Finaliza la actividad actual
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Ocultar el cuadro de diálogo de progreso en caso de error
                    progressDialog.dismiss();
                    // Manejar errores de la solicitud
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username); // Envía el nombre de usuario al servidor
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Deshabilitar los botones mientras se realiza la solicitud
        btnLogin.setEnabled(false);
        btnRegistrar.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Habilitar los botones cuando la actividad se reanuda
        btnLogin.setEnabled(true);
        btnRegistrar.setEnabled(true);
    }

    private void logout() {
        // Eliminar la sesión en SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual
    }
}
