package com.example.foofmaps;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuRest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rest);

        // Obtener los extras del Intent
        Intent intent = getIntent();
        int restaurante_id = intent.getIntExtra("restaurant_id", 0);
        String nom_rest = intent.getStringExtra("restaurant_name");
        String telefono = intent.getStringExtra("restaurant_phone");

        // Agregar mensajes de depuración
        Log.d("ID_DEBUG", "restaurante_id: " + restaurante_id);

        // Obtener referencias a los elementos en el diseño
        TextView bannertop = findViewById(R.id.bannertop);
        ImageButton whatsappButton = findViewById(R.id.btnWhatsApp);
        ImageView imageViewRestaurante = findViewById(R.id.imageViewRestaurante);

        // Establecer los valores en los TextView
        bannertop.setText(nom_rest);

        // Configurar el botón para abrir WhatsApp con el número de teléfono
        whatsappButton.setOnClickListener(v -> openWhatsApp(telefono));

        // Construir la URL para obtener la imagen
        String imageUrl = "http://192.168.1.3/modelo/icono_rest.php?id=" + restaurante_id;

        // Agregar mensaje de depuración para verificar la URL
        Log.d("URL_DEBUG", "imageUrl: " + imageUrl);

        // Cargar la imagen desde tu servidor utilizando Picasso
        Picasso.get().load(imageUrl).into(imageViewRestaurante);

        // Realizar una solicitud para obtener la lista de platos en segundo plano
        new GetPlatosTask().execute(restaurante_id);
    }

    private class GetPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            Log.d("idRestaurante" , String.valueOf(idRestaurante));
            List<Plato> platos = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String apiUrl = "http://192.168.1.3/modelo/getPlatos.php?restaurante_id=" + idRestaurante;
                Log.d("apiUrl", apiUrl);
                String jsonResponse = HttpUtils.get(apiUrl);

                Log.d("JSON_RESPONSE", jsonResponse);

                // Procesar el JSON y obtener la lista de platos
                platos = parsePlatosFromJSON(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return platos;
        }

        @Override
        protected void onPostExecute(List<Plato> platos) {

            Log.d("PLATOS_COUNT", String.valueOf(platos.size()));
            // Configurar el RecyclerView con la lista de platos
            RecyclerView recyclerViewPlatos = findViewById(R.id.recyclerViewPlatos);
            PlatoAdapter platoAdapter = new PlatoAdapter(platos);
            recyclerViewPlatos.setLayoutManager(new LinearLayoutManager(MenuRest.this));
            recyclerViewPlatos.setAdapter(platoAdapter);
            Log.d("PLATOS_COUNT", String.valueOf(platos.size()));

        }
    }

    // Método para abrir WhatsApp con el número de teléfono
    private void openWhatsApp(String phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // Método para analizar el JSON y obtener una lista de platos
    private List<Plato> parsePlatosFromJSON(String json) {

        List<Plato> platos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject platoJson = jsonArray.getJSONObject(i);
                String nombre = platoJson.getString("nom_plato");
                String descripcion = platoJson.getString("descripcion");
                float precio = (float) platoJson.getDouble("precio");

                // La imagen en formato Base64
                String imagenBase64 = platoJson.getString("imagen");

                // Decodificar la imagen desde Base64 a bytes
                byte[] imagen = decodeBase64(imagenBase64);

                // Crear un objeto Plato con los datos
                Plato plato = new Plato(nombre, descripcion, precio, imagen);
                platos.add(plato);
                Log.d("PLATO_DEBUG", "nombre: " + nombre);
                Log.d("PLATO_DEBUG", "descripcion: " + descripcion);
                Log.d("PLATO_DEBUG", "precio: " + precio);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return platos;
    }

    // Método para decodificar una cadena Base64 en un arreglo de bytes
    private byte[] decodeBase64(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
    }
}
