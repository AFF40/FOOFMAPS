package com.example.foofmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class prueba extends AppCompatActivity {

    EditText etUsername, etEmail ;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        etUsername = findViewById(R.id.ed_user);
        etEmail = findViewById(R.id.ed_email);
        btnRegistrar = findViewById(R.id.btn_registrar_prueba);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                // Realizar la solicitud al servidor
                pruebacon registroRequest = new pruebacon(username, email,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    int exito = jsonResponse.getInt("exito");
                                    String mensaje = jsonResponse.getString("msg");

                                    if (exito == 1) {
                                        // Registro exitoso, manejar el resultado aquí
                                    } else {
                                        // Error en el registro, mostrar un mensaje al usuario
                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                RequestQueue queue = Volley.newRequestQueue(prueba.this);
                queue.add(registroRequest);
            }
        });
    }
}