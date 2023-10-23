package com.example.foofmaps.dueño;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import com.android.volley.Request;


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


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class agregar_platos extends AppCompatActivity  implements onPlatoAddedListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private EditText editTextNomPlato;
    private EditText editTextDescripcion;
    private EditText editTextPrecio;
    private Button btnSelectImage;
    private Button btnSelectCamara;
    private Button btnEnviar;

    private ImageView imagenPlato;
    private TextView nomPlato;
    private TextView descripcion_plato;
    private TextView precio_plato;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    public void onPlatoAdded() {
        // Notifica al fragment dueno_menu que se ha agregado un plato
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish(); // Cierra la actividad actual
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_platos);
        // Verifica si el permiso de la cámara está otorgado
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Si no se otorgó el permiso, solicítalo al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }



        // Vincula las vistas desde el diseño XML
        editTextNomPlato = findViewById(R.id.editTextNomPlato);
        nomPlato = findViewById(R.id.nom_plato);
        // Configura un TextWatcher para el EditText
        editTextNomPlato.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama cuando el texto cambia.
                // Actualiza el TextView "nom_plato" con el texto del EditText
                nomPlato.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto cambie.
            }
        });

        // Vincula las vistas desde el diseño XML
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        descripcion_plato = findViewById(R.id.desc_plato);
        editTextDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama cuando el texto cambia.
                // Actualiza el TextView "nom_plato" con el texto del EditText
                descripcion_plato.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto cambie.

            }
        });

        // Vincula las vistas desde el diseño XML
        editTextPrecio = findViewById(R.id.editTextPrecio);
        precio_plato = findViewById(R.id.precio);
        editTextPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama cuando el texto cambia.
                // Actualiza el TextView "nom_plato" con el texto del EditText
                precio_plato.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Este método se llama después de que el texto cambie.
            }
        });





        // Vincula las vistas desde el diseño XML
        editTextNomPlato = findViewById(R.id.editTextNomPlato);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectCamara = findViewById(R.id.btnselectcamara);
        imagenPlato = findViewById(R.id.imagenPlato);
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
            imagenPlato.setImageURI(selectedImageUri);
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // La foto se ha tomado exitosamente y se encuentra en 'data' (generalmente como un bitmap)
            // Puedes procesar y mostrar la foto aquí, o guardarla en el almacenamiento de tu aplicación.

            // Ejemplo de cómo obtener la imagen como un bitmap (puede variar según la cámara)
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Configura el bitmap en el ImageView "imagenPlato" o realiza otras acciones según tus necesidades
            imagenPlato.setImageBitmap(imageBitmap);
        }



    }


    public void enviarFormulario(View view) {
        // Recupera el valor del restaurante_id del Intent
        Intent intent = getIntent();
        int restauranteId = intent.getIntExtra("restaurante_id", -1);
        Log.d("restaurante_id_en_agregar", "restaurante_id: " + restauranteId);
        String url = "http://192.168.1.3/modelo/a%c3%b1adir_plato.php";

        // Obtener los valores de los campos del formulario
        String nombrePlato = editTextNomPlato.getText().toString().trim();
        String descripcionPlato = editTextDescripcion.getText().toString().trim();
        String precioPlato = editTextPrecio.getText().toString().trim();
        String restauranteIdString = String.valueOf(restauranteId);

        // Verificar que todos los campos estén llenos
        if (nombrePlato.isEmpty() || descripcionPlato.isEmpty() || precioPlato.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos del formulario", Toast.LENGTH_SHORT).show();
            return; // No se envía la solicitud si falta algún campo
        }

        // Verificar que se haya seleccionado una imagen
        if (imagenPlato.getDrawable() == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return; // No se envía la solicitud si no hay imagen seleccionada
        }

        try {
            // Crear un objeto JSON para enviar al servidor
            JSONObject platoData = new JSONObject();
            platoData.put("nom_plato", nombrePlato);
            platoData.put("descripcion", descripcionPlato);
            platoData.put("precio", precioPlato);
            platoData.put("restaurante_id", restauranteIdString);

            // Obtener la imagen como Bitmap (por ejemplo, desde un ImageView)
            Bitmap imageBitmap = ((BitmapDrawable) imagenPlato.getDrawable()).getBitmap();

            // Convertir la imagen a Base64
            String imageBase64 = convertImageToBase64(imageBitmap);

            // Agregar la imagen al objeto JSON
            platoData.put("imagen", imageBase64);

            // Crear una solicitud HTTP POST con Volley
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    platoData,
                    response -> {
                        // Maneja la respuesta del servidor aquí
                        Toast.makeText(this, "¡Plato agregado con éxito!", Toast.LENGTH_SHORT).show();
                        Log.d("MiApp", "Respuesta del servidor: " + response.toString());

                        // Puedes realizar otras acciones, como volver a la actividad anterior o limpiar los campos del formulario.

                        // En el éxito al agregar el plato, llama al método onPlatoAdded
                        onPlatoAdded();


                    },
                    error -> {
                        // Maneja errores de la solicitud aquí
                        Toast.makeText(this, "Error al agregar el plato", Toast.LENGTH_SHORT).show();
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