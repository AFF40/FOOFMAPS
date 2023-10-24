package com.example.foofmaps.dueño;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class agregar_bebidas extends AppCompatActivity  implements onBebidaAddedListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private EditText editTextNomBebida;
    private EditText editTextDescripcion;
    private EditText editTextPrecio;
    private Button btnSelectImage;
    private Button btnSelectCamara;
    private Button btnEnviar;

    private ImageView imagenBebida;
    private TextView nomBebida;
    private TextView descripcion_bebida;
    private TextView precio_bebida;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    public void onBebidaAdded() {
        // Notifica al fragment dueno_menu que se ha agregado un plato
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish(); // Cierra la actividad actual
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_bebidas);
        // Verifica si el permiso de la cámara está otorgado
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Si no se otorgó el permiso, solicítalo al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
        // Vincula las vistas desde el diseño XML
        editTextNomBebida = findViewById(R.id.editTextNomBebida);
        nomBebida = findViewById(R.id.nom_bebida);
        // Configura un TextWatcher para el EditText
        editTextNomBebida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama antes de que el texto cambie.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama cuando el texto cambia.
                // Actualiza el TextView "nom_plato" con el texto del EditText
                nomBebida.setText(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto cambie.
            }
        });
        // Vincula las vistas desde el diseño XML
        editTextDescripcion = findViewById(R.id.editTextDescripcionBebida);
        descripcion_bebida = findViewById(R.id.desc_bebida);
        editTextDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama antes de que el texto cambie.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama cuando el texto cambia.
                // Actualiza el TextView "nom_plato" con el texto del EditText
                descripcion_bebida.setText(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto cambie.

            }
        });
        // Vincula las vistas desde el diseño XML
        editTextPrecio = findViewById(R.id.editTextPrecioBebida);
        precio_bebida = findViewById(R.id.precio);
        editTextPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama cuando el texto cambia.
                // Actualiza el TextView "nom_plato" con el texto del EditText
                precio_bebida.setText(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto cambie.
            }
        });
        // Vincula las vistas desde el diseño XML
        editTextNomBebida = findViewById(R.id.editTextNomBebida);
        editTextDescripcion = findViewById(R.id.editTextDescripcionBebida);
        editTextPrecio = findViewById(R.id.editTextPrecioBebida);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectCamara = findViewById(R.id.btnselectcamara);
        imagenBebida = findViewById(R.id.imagenBebida);
        btnEnviar = findViewById(R.id.btnEnviarFormulario);

        // Configura clics de botones o lógica adicional según sea necesario
        btnSelectImage.setOnClickListener(view -> {
            // Aquí puedes implementar la lógica para cargar una imagen desde la galería
            // Crea una intención para seleccionar una imagen desde la galería
            Intent abrircamara = new Intent(Intent.ACTION_PICK);
            abrircamara.setType("image/*");

            // Inicia la actividad de selección de imagen
            startActivityForResult(abrircamara, PICK_IMAGE_REQUEST);
        });

        btnSelectCamara.setOnClickListener(view -> {
            // Aquí puedes implementar la lógica para tomar una foto

            // Crea una intención para abrir la aplicación de cámara
            Intent abrircamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Verifica si hay una aplicación de cámara disponible en el dispositivo
            if (abrircamara.resolveActivity(getPackageManager()) != null) {
                // Inicia la aplicación de cámara
                startActivityForResult(abrircamara, CAMERA_REQUEST); // Usando el valor de CAMERA_REQUEST
            } else {
                // Si no hay una aplicación de cámara disponible, muestra un mensaje de error o proporciona una alternativa
                Toast.makeText(this, "No se encontró una aplicación de cámara en el dispositivo", Toast.LENGTH_SHORT).show();
            }
        });
        // Configura un listener de clics para el botón
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarFormulario(view); // Llama al método enviarFormulario cuando se hace clic en el botón
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // La imagen ha sido seleccionada exitosamente, y la ubicación de la imagen se encuentra en 'data.getData()'
            // Puedes utilizar 'data.getData()' para cargar la imagen en el ImageView.
            Uri selectedImageUri = data.getData();

            // Configura la imagen seleccionada en el ImageView "imagenPlato"
            imagenBebida.setImageURI(selectedImageUri);
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // La foto se ha tomado exitosamente y se encuentra en 'data' (generalmente como un bitmap)
            // Puedes procesar y mostrar la foto aquí, o guardarla en el almacenamiento de tu aplicación.

            // Ejemplo de cómo obtener la imagen como un bitmap (puede variar según la cámara)
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Configura el bitmap en el ImageView "imagenPlato" o realiza otras acciones según tus necesidades
            imagenBebida.setImageBitmap(imageBitmap);
        }
    }
    public void enviarFormulario(View view) {
        // Recupera el valor del restaurante_id del Intent
        Intent intent = getIntent();
        int restauranteId = intent.getIntExtra("restaurante_id", -1);
        Log.d("restaurante_id_en_agregar", "restaurante_id: " + restauranteId);
        String url = "http://192.168.1.3/modelo/a%c3%b1adir_bebida.php";

        // Obtener los valores de los campos del formulario
        String nombreBebida = editTextNomBebida.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String precio = editTextPrecio.getText().toString().trim();
        String restauranteIdString = String.valueOf(restauranteId);

        // Verificar que todos los campos estén llenos
        if (nombreBebida.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos del formulario", Toast.LENGTH_SHORT).show();
            return; // No se envía la solicitud si falta algún campo
        }

        // Verificar que se haya seleccionado una imagen
        if (imagenBebida.getDrawable() == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return; // No se envía la solicitud si no hay imagen seleccionada
        }

        try {
            // Crear un objeto JSON para enviar al servidor
            JSONObject bebidaData = new JSONObject();
            bebidaData.put("nom_bebida", nombreBebida);
            bebidaData.put("descripcion", descripcion);
            bebidaData.put("precio", precio);
            bebidaData.put("restaurante_id", restauranteIdString);

            // Obtener la imagen como Bitmap (por ejemplo, desde un ImageView)
            Bitmap imageBitmap = ((BitmapDrawable) imagenBebida.getDrawable()).getBitmap();

            // Convertir la imagen a Base64
            String imageBase64 = convertImageToBase64(imageBitmap);

            // Agregar la imagen al objeto JSON
            bebidaData.put("imagen", imageBase64);

            // Crear una solicitud HTTP POST con Volley
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    bebidaData,
                    response -> {
                        // Maneja la respuesta del servidor aquí
                        Toast.makeText(this, "¡Bebida agregada con éxito!", Toast.LENGTH_SHORT).show();
                        Log.d("MiApp", "Respuesta del servidor: " + response.toString());

                        // Puedes realizar otras acciones, como volver a la actividad anterior o limpiar los campos del formulario.

                        // En el éxito al agregar el plato, llama al método onPlatoAdded
                        onBebidaAdded();


                    },
                    error -> {
                        // Maneja errores de la solicitud aquí
                        Toast.makeText(this, "Error al agregar la bebida", Toast.LENGTH_SHORT).show();
                        Log.d("erroralenviar", "error: " + error);
                    }
            );

            // Agregar la solicitud a la cola
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String convertImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


}