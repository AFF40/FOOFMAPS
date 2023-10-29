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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Editaresteplato extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    private EditText nom_plato;
    private EditText descripcion;
    private EditText precio;
    private ImageView imagen;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaresteplato);

        Intent intent = getIntent();
        int platoId = intent.getIntExtra("id_comida", 0);
        String nombre_plato = intent.getStringExtra("nombre_plato");
        String descripcion_plato = intent.getStringExtra("descripcion_plato");
        double precio_plato = intent.getDoubleExtra("precio_plato", 0);
        byte[] imagen_plato = intent.getByteArrayExtra("imagen_plato");

        // Obtener referencias a los elementos de la vista
        nom_plato = findViewById(R.id.nom_plato);
        descripcion = findViewById(R.id.desc_plato);
        precio = findViewById(R.id.precio);
        imagen= findViewById(R.id.imageViewPlato);
        btnGuardar=findViewById(R.id.btnGuardar);

        // Asignar los valores a los elementos de la vista
        nom_plato.setText(nombre_plato);
        descripcion.setText(descripcion_plato);
        precio.setText("" + precio_plato);

        // Si tienes la imagen en formato byte[], puedes convertirla a un Bitmap y establecerla en el ImageView
        if (imagen_plato != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagen_plato, 0, imagen_plato.length);
            imagen.setImageBitmap(bitmap);
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
                enviarDatosAlServidor( platoId);
            }
        });
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
        // URL del archivo PHP en tu servidor
        String modeloURL = Config.MODELO_URL +"update_plato.php";

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

        // Crear una solicitud POST utilizando Volley (puedes usar otras bibliotecas también)
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, modeloURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Después de realizar la actualización con éxito
                        if (response.equals("Actualización exitosa")) {
                            Toast.makeText(Editaresteplato.this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show();

                            // Crear un intent para pasar los datos actualizados
                            Intent intent = new Intent();
                            intent.putExtra("plato_id", platoId);
                            intent.putExtra("nombre_plato", nombrePlato);
                            intent.putExtra("descripcion_plato", descripcionPlato);
                            intent.putExtra("precio_plato", Double.parseDouble(precioPlato));

                            // Puedes también enviar la imagen actualizada si es necesario
                            // intent.putExtra("imagen_plato", imagenBase64);

                            setResult(RESULT_OK, intent); // Establecer el resultado OK
                            finish(); // Cierra la actividad actual
                        }
                        else {
                            Toast.makeText(Editaresteplato.this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de la solicitud
                        Toast.makeText(Editaresteplato.this, "Error al enviar datos", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("plato_id", String.valueOf(platoId)); // Agregar plato_id
                params.put("nombre_plato", nombrePlato);
                params.put("descripcion_plato", descripcionPlato);
                params.put("precio_plato", precioPlato);
                params.put("imagen", imagenBase64);
                Log.d("imagenenviadaalupdate", "imagen: " +imagenBase64 );
                Log.d("imagenenviadaalupdate", "plato_id: " +platoId );
                Log.d("imagenenviadaalupdate", "nombre_plato: " +nombrePlato );
                Log.d("imagenenviadaalupdate", "descripcion_plato: " +descripcionPlato );
                Log.d("imagenenviadaalupdate", "precio_plato: " +precioPlato );
                return params;
            }
        };

        // Agregar la solicitud a la cola
        requestQueue.add(stringRequest);
    }

    private String convertImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}