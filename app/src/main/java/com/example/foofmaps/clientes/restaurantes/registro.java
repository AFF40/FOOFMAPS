package com.example.foofmaps.clientes.restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.R;

import org.json.JSONException;
import org.json.JSONObject;
public class registro extends AppCompatActivity {

    EditText ed_username, ed_celular, ed_pass1, ed_pass2;
        Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ed_username = findViewById(R.id.ed_user);
        ed_celular = findViewById(R.id.ed_celular);
        ed_pass1 = findViewById(R.id.ed_pass1);
        ed_pass2 = findViewById(R.id.ed_pass2);
        btnRegistrar = findViewById(R.id.btn_registrar);

        btnRegistrar.setOnClickListener(v -> {
            // Obtener los valores ingresados por el usuario
            String username = ed_username.getText().toString();
            String celular = ed_celular.getText().toString();
            String pass1 = ed_pass1.getText().toString();
            String pass2 = ed_pass2.getText().toString();


            // Realizar la solicitud al servidor
            RegisterRequest registerRequest = new RegisterRequest(username, celular, pass1,pass2,
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
                                Intent intent_registro_exitoso = new Intent(registro.this, MainActivity.class);
                                registro.this.startActivity(intent_registro_exitoso);
                                finish();
                            } else {
                                // Error en el registro, mostrar un mensaje al usuario
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                Log.e("info",mensaje);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

            RequestQueue queue = Volley.newRequestQueue(registro.this);
            queue.add(registerRequest);
        });
    }
}