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

import androidx.annotation.NonNull;
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

public class Editarestabebida extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    private EditText nom_bebida;
    private EditText descripcion;
    private EditText precio;
    private ImageView imagen;
    private Button btnGuardar;
    private String nombreRestaurante;
    private int id_rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarestabebida);

        // Recibir el Bundle con los datos
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int bebidaId = bundle.getInt("id_mebeb");
            String nombre_bebida = bundle.getString("nombre_bebida");
            String descripcion_bebida = bundle.getString("descripcion_bebida");
            double precio_bebida = bundle.getDouble("precio_bebida");
            String imagen_bebida = bundle.getString("imagen_bebida");
            id_rest = bundle.getInt("restaurante_id");
            nombreRestaurante = bundle.getString("nombre_restaurante");

            Log.d("Log_editarestabebida", "bebida_id: " + bebidaId);
            Log.d("Log_editarestabebida", "nombre_bebida: " + nombre_bebida);
            Log.d("Log_editarestabebida", "descripcion_bebida: " + descripcion_bebida);
            Log.d("Log_editarestabebida", "precio_bebida: " + precio_bebida);
            Log.d("Log_editarestabebida", "imagen_bebida: " + imagen_bebida);
            Log.d("Log_editarestabebida", "restaurante_id: " + id_rest);
            Log.d("Log_editarestabebida", "nombre_restaurante: " + nombreRestaurante);

            // Obtener referencias a los elementos de la vista
            nom_bebida = findViewById(R.id.nom_bebida);
            descripcion = findViewById(R.id.desc_bebida);
            precio = findViewById(R.id.precio_bebida);
            imagen = findViewById(R.id.imageViewBebida);
            btnGuardar = findViewById(R.id.btnGuardarBebida);

            // Asignar los valores a los elementos de la vista
            nom_bebida.setText(nombre_bebida);
            descripcion.setText(descripcion_bebida);
            precio.setText("" + precio_bebida);

            // Cargar la imagen desde la URL
            if (imagen_bebida != null && !imagen_bebida.isEmpty()) {
                loadImageFromUrl(imagen_bebida);
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
                    enviarDatosAlServidor(bebidaId);
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
        builder.setTitle("Cambiar imagen de la bebida");

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

    private void enviarDatosAlServidor(int bebidaId) {
        String modeloURL = Config.MODELO_URL + "update_bebida.php";
        // Obtener los valores de los campos del formulario
        String nombreBebida = nom_bebida.getText().toString().trim();
        String descripcionBebida = descripcion.getText().toString().trim();
        String precioBebida = precio.getText().toString().trim();

        // Verificar que todos los campos estén llenos
        if (nombreBebida.isEmpty() || descripcionBebida.isEmpty() || precioBebida.isEmpty()) {
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

        // Crear un objeto JSON con los datos de la bebida
        JSONObject bebidaData = new JSONObject();
        try {
            bebidaData.put("id_mebeb", bebidaId);
            bebidaData.put("nombre_bebida", nombreBebida);
            bebidaData.put("descripcion_bebida", descripcionBebida);
            bebidaData.put("precio_bebida", precioBebida);
            bebidaData.put("imagen_bebida", imagenBase64);
            bebidaData.put("nombre_restaurante", nombreRestaurante); // Usar el nombre del restaurante recibido
            bebidaData.put("restaurante_id", id_rest); // Usar el ID del restaurante recibido
            Log.d("Log_editarestabebida", "bebidaData: " + bebidaData);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear los datos de la bebida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar los datos al servidor mediante una solicitud POST
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, modeloURL, bebidaData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(Editarestabebida.this, "Bebida actualizada exitosamente", Toast.LENGTH_SHORT).show();
                                finish(); // Finalizar la actividad y regresar a la anterior
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(Editarestabebida.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Editarestabebida.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Editarestabebida.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    private String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
