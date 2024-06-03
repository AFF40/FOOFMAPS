package com.example.foofmaps.dueño;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Editaresteplato extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    private EditText nom_plato;
    private EditText descripcion;
    private EditText precio;
    private ImageView imagen;
    private Button btnGuardar;
    private String nombreRestaurante;
private int id_rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaresteplato);

        // Recibir el Bundle con los datos
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int platoId = bundle.getInt("id_meplat");
            String nombre_plato = bundle.getString("nombre_plato");
            String descripcion_plato = bundle.getString("descripcion_plato");
            double precio_plato = bundle.getDouble("precio_plato");
            String imagen_plato = bundle.getString("imagen_plato");
            id_rest = bundle.getInt("restaurante_id");
            nombreRestaurante = bundle.getString("nombre_restaurante");  // Asignar el nombre del restaurante

            Log.d("Log_editaresteplato_rec", "plato_id: " + platoId);
            Log.d("Log_editaresteplato_rec", "nombre_plato: " + nombre_plato);
            Log.d("Log_editaresteplato_rec", "descripcion_plato: " + descripcion_plato);
            Log.d("Log_editaresteplato_rec", "precio_plato: " + precio_plato);
            Log.d("Log_editaresteplato_rec", "restaurante_id: " + id_rest);
            Log.d("Log_editaresteplato_rec", "nombre_restaurante: " + nombreRestaurante);
            Log.d("Log_editaresteplato_rec", "imagen_plato: " + imagen_plato);

            // Obtener referencias a los elementos de la vista
            nom_plato = findViewById(R.id.nom_plato);
            descripcion = findViewById(R.id.desc_plato);
            precio = findViewById(R.id.precio);
            imagen = findViewById(R.id.imageViewPlato);
            btnGuardar = findViewById(R.id.btnGuardar);

            // Asignar los valores a los elementos de la vista
            nom_plato.setText(nombre_plato);
            descripcion.setText(descripcion_plato);
            precio.setText("" + precio_plato);

            // Cargar la imagen desde la URL
            if (imagen_plato != null && !imagen_plato.isEmpty()) {
                loadImageFromUrl(imagen_plato);
            }

            // Configura un listener de clics para el ImageView para mostrar el diálogo de selección de imagen
            imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageSelectionDialog();
                }
            });

            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enviarDatosAlServidor(platoId);
                }
            });
        }
    }

    private void loadImageFromUrl(String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = new java.net.URL(imageUrl).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imagen.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar imagen del plato");

        // Opción para seleccionar una imagen de la galería
        builder.setPositiveButton("Seleccionar de la galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectImageFromGallery();
            }
        });

        // Opción para tomar una foto
        builder.setNegativeButton("Tomar una foto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                captureImageFromCamera();
            }
        });

        builder.show();
    }

    private void selectImageFromGallery() {
        Intent abrirGaleria = new Intent(Intent.ACTION_PICK);
        abrirGaleria.setType("image/*");
        startActivityForResult(abrirGaleria, PICK_IMAGE_REQUEST);
    }

    private void captureImageFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent abrirCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (abrirCamara.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(abrirCamara, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "No se encontró una aplicación de cámara en el dispositivo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imagen.setImageURI(selectedImageUri);
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagen.setImageBitmap(imageBitmap);
        }
    }

    private void enviarDatosAlServidor(int platoId) {
        String modeloURL = Config.MODELO_URL + "update_plato.php";
        // Obtener los valores de los campos del formulario
        String nombrePlato = nom_plato.getText().toString().trim();
        String descripcionPlato = descripcion.getText().toString().trim();
        String precioPlato = precio.getText().toString().trim();

        // Verificar que todos los campos estén llenos
        if (nombrePlato.isEmpty() || descripcionPlato.isEmpty() || precioPlato.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos del formulario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que se haya seleccionado una imagen
        if (imagen.getDrawable() == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la imagen en una representación de bytes (Base64)
        String imagenBase64 = convertImageToBase64(((BitmapDrawable) imagen.getDrawable()).getBitmap());
        // Crear un objeto JSON con los datos del plato
        JSONObject platoData = new JSONObject();
        try {

            platoData.put("id_meplat", platoId);
            platoData.put("nombre_plato", nombrePlato);
            platoData.put("descripcion_plato", descripcionPlato);
            platoData.put("precio_plato", precioPlato);
            platoData.put("imagen_plato", imagenBase64);
            platoData.put("nombre_restaurante",nombreRestaurante ); // Cambiar a la lógica adecuada para obtener el nombre del restaurante
            platoData.put("restaurante_id", id_rest); // Cambiar a la lógica adecuada para obtener el ID del restaurante
            Log.d("Log_editaresteplato", "platoData: " + platoData);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear datos JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear una solicitud POST utilizando Volley (JsonObjectRequest)
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, modeloURL, platoData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(Editaresteplato.this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String errorMessage = response.getString("error_message");
                                Toast.makeText(Editaresteplato.this, "Error al actualizar los datos: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Editaresteplato.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Editaresteplato.this, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Añadir la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    private String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }



}
