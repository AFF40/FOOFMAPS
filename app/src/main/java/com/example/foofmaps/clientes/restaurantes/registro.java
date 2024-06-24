package com.example.foofmaps.clientes.restaurantes;

import android.app.ProgressDialog;
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

    private ProgressDialog progressDialog; // Cuadro de diálogo para mostrar el progreso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar el cuadro de diálogo de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando...");
        progressDialog.setCancelable(false);

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

            // Mostrar el cuadro de diálogo de progreso
            progressDialog.show();

            // Realizar la solicitud al servidor
            RegisterRequest registerRequest = new RegisterRequest(username, celular, pass1, pass2,
                    response -> {
                        // Ocultar el cuadro de diálogo de progreso
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int exito = jsonResponse.getInt("exito");
                            String mensaje = jsonResponse.getString("msg");
                            Log.e("info", jsonResponse.toString());
                            if (exito == 1) {
                                // Registro exitoso, manejar el resultado aquí
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                Intent intent_registro_exitoso = new Intent(registro.this, MainActivity.class);
                                registro.this.startActivity(intent_registro_exitoso);
                                finish();
                            } else {
                                // Error en el registro, mostrar un mensaje al usuario
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                Log.e("info", mensaje);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

            RequestQueue queue = Volley.newRequestQueue(registro.this);
            queue.add(registerRequest);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausa la actividad mientras se realiza la solicitud
        progressDialog.dismiss(); // Asegurarse de que se oculte el diálogo si la actividad se pausa
        btnRegistrar.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Habilita el botón de registro
        btnRegistrar.setEnabled(true);
    }
}
