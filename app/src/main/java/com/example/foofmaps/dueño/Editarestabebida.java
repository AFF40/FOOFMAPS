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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarestabebida);

        // Recibir el Bundle con los datos
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int bebidaId = bundle.getInt("id_bebida");
            String nombre_bebida = bundle.getString("nombre");
            String descripcion_bebida = bundle.getString("descripcion");
            double precio_bebida = bundle.getDouble("precio");
            String imagen_bebida = bundle.getString("imagen");
            int id_rest = bundle.getInt("restaurante_id");
            String nombreRestaurante = bundle.getString("nombre_restaurante");

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
                requestCameraPermission();
            }
        });

        builder.show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Llama al método de la clase base
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
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
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
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
            bebidaData.put("bebida_id", bebidaId);
            bebidaData.put("nombre_bebida", nombreBebida);
            bebidaData.put("descripcion_bebida", descripcionBebida);
            bebidaData.put("precio_bebida", precioBebida);
            bebidaData.put("imagen_bebida", imagenBase64);
            bebidaData.put("nombre_restaurante", "NombreDelRestaurante"); // Cambiar a la lógica adecuada para obtener el nombre del restaurante
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear datos JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear una solicitud POST utilizando Volley (JsonObjectRequest)
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, modeloURL, bebidaData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(Editarestabebida.this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show();
                                //regresar a la actividad anterior
                                finish();
                            } else {
                                String errorMessage = response.getString("error_message");
                                Toast.makeText(Editarestabebida.this, "Error al actualizar los datos: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Editarestabebida.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Editarestabebida.this, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
