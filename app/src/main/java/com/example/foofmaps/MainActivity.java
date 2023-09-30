package com.example.foofmaps;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
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
                            String mensaje_exito = jsonResponse.getString("msg");
                            String mensaje = jsonResponse.getString("msg");
                            Log.e("info",jsonResponse.toString());
                            if (exito == 1) {
                                // Registro exitoso, manejar el resultado aquí
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                Intent intent_login_exitoso = new Intent(MainActivity.this, MapsActivity.class);
                                MainActivity.this.startActivity(intent_login_exitoso);
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
    }
}
