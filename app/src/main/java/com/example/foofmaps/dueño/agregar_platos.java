package com.example.foofmaps.dueño;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.io.IOException;

public class agregar_platos extends AppCompatActivity implements onPlatoAddedListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int READ_MEDIA_IMAGES_PERMISSION_REQUEST_CODE = 1002;
    private EditText editTextNomPlato;
    private EditText editTextDescripcion;
    private EditText editTextPrecio;
    private Button btnSelectImage;
    private Button btnSelectCamara;
    private Button btnEnviar;
    private Bitmap imageBitmap;
    private ImageView imagenPlato;
    private TextView nomPlato;
    private TextView descripcion_plato;
    private TextView precio_plato;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final String modeloURL = Config.MODELO_URL + "agregar_plato.php";

    @Override
    public void onPlatoAdded() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_platos);

        editTextNomPlato = findViewById(R.id.editTextNomPlato);
        nomPlato = findViewById(R.id.nom_plato);
        editTextNomPlato.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nomPlato.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        descripcion_plato = findViewById(R.id.desc_plato);
        editTextDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descripcion_plato.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextPrecio = findViewById(R.id.editTextPrecio);
        precio_plato = findViewById(R.id.precio);
        editTextPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                precio_plato.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectCamara = findViewById(R.id.btnselectcamara);
        imagenPlato = findViewById(R.id.imagenPlato);
        btnEnviar = findViewById(R.id.btnEnviarFormulario);

        btnSelectImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, READ_MEDIA_IMAGES_PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        });

        btnSelectCamara.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        btnEnviar.setOnClickListener(view -> {
            enviarFormulario(view);
            onPause();
        });
    }

    private void openGallery() {
        Intent abrir_galeria = new Intent(Intent.ACTION_PICK);
        abrir_galeria.setType("image/*");
        startActivityForResult(abrir_galeria, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent abrircamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (abrircamara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(abrircamara, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara en el dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_MEDIA_IMAGES_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imagenPlato.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imagenPlato.setImageBitmap(imageBitmap);
        }
    }

    public void enviarFormulario(View view) {
        Intent intent = getIntent();
        int restauranteId = intent.getIntExtra("restaurante_id", -1);
        String nombreRestaurante = intent.getStringExtra("nombre_restaurante");
        Log.d ("log_anadir_nombre_rest", "Nombre del restaurante: " + nombreRestaurante);
        Log.d("log_anadir_url", "url: " + modeloURL);

        String nombrePlato = editTextNomPlato.getText().toString().trim();
        String descripcionPlato = editTextDescripcion.getText().toString().trim();
        String precioPlato = editTextPrecio.getText().toString().trim();
        //enviar el nombre del restaurante como string

        String restauranteIdString = String.valueOf(restauranteId);

        if (nombrePlato.isEmpty() || descripcionPlato.isEmpty() || precioPlato.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos del formulario", Toast.LENGTH_SHORT).show();
            onRestart();
            return;
        }

        if (imageBitmap == null) {
            onRestart();
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        guardarImagenEnServidor(nombrePlato, descripcionPlato, precioPlato, restauranteIdString, nombreRestaurante);
        onRestart();
    }

    public void guardarImagenEnServidor(String nombrePlato, String descripcionPlato, String precioPlato, String restauranteIdString, String nombreRestaurante) {
        String imageBase64 = convertImageToBase64(imageBitmap);

        JSONObject platoData = new JSONObject();
        try {
            platoData.put("nombre", nombrePlato);
            platoData.put("descripcion", descripcionPlato);
            platoData.put("precio", precioPlato);
            platoData.put("restaurante_id", restauranteIdString);
            platoData.put("restaurante_nombre", nombreRestaurante);
            platoData.put("imagen", imageBase64);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("log_anadir_platodata", "platoData: " + platoData);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                modeloURL,
                platoData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseData = response.toString();
                            Log.d("log_anadir_ResponseData", "Response data: " + responseData); // Agregar registro de depuración
                            if (isJSONValid(responseData)) {
                                handleServerResponse(responseData); // Llamar a la función para manejar la respuesta del servidor
                            } else {
                                // La respuesta del servidor no es un JSON válido
                                Toast.makeText(agregar_platos.this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Manejar el error si la respuesta no es un objeto JSON válido
                            Toast.makeText(agregar_platos.this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                // Manejar el error de la solicitud
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMessage = "Error al agregar el plato: " + error.getMessage();
                        Toast.makeText(agregar_platos.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("log_anadir_Error", errorMessage, error);

                        // Imprimir toda la respuesta del servidor en el Logcat
                        if (error.networkResponse != null) {
                            onRestart(); // Llamada a onRestart() en caso de error
                            Log.e("log_anadir_Error_serv", "Respuesta del servidor: " + new String(error.networkResponse.data));
                        } else {
                            Log.e("log_anadir_Error_serv", "No se recibió respuesta del servidor");
                            onRestart(); // Llamada a onRestart() en caso de error
                        }

                        // Verificar si hay un mensaje de error en la respuesta del servidor
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                JSONObject errorJson = new JSONObject(new String(error.networkResponse.data));
                                String serverErrorMessage = errorJson.optString("error");
                                if (!TextUtils.isEmpty(serverErrorMessage)) {
                                    // Mostrar el mensaje de error del servidor
                                    Toast.makeText(agregar_platos.this, serverErrorMessage, Toast.LENGTH_SHORT).show();
                                    onRestart(); // Llamada a onRestart() en caso de error
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

        );
        requestQueue.add(request);
    }

    // Función para verificar si una cadena es un JSON válido
    private boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    // Función para manejar la respuesta del servidor
    private void handleServerResponse(String responseData) {
        try {
            JSONObject response = new JSONObject(responseData);
            String message = response.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            onPlatoAdded();
            finish();
            onRestart(); // Llamada a onRestart() después de procesar la respuesta
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar el error si la respuesta no tiene el formato esperado
            Toast.makeText(this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
            onRestart(); // Llamada a onRestart() en caso de error
        }
    }

    public String convertImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //apagar los botones y los campos de texto
        btnEnviar.setEnabled(false);
        btnSelectImage.setEnabled(false);
        btnSelectCamara.setEnabled(false);
        editTextNomPlato.setEnabled(false);
        editTextDescripcion.setEnabled(false);
        editTextPrecio.setEnabled(false);
    }

    //funcion para quitar la pausa
    @Override
    protected void onRestart() {
        super.onRestart();
        //encender los botones y los campos de texto
        btnEnviar.setEnabled(true);
        btnSelectImage.setEnabled(true);
        btnSelectCamara.setEnabled(true);
        editTextNomPlato.setEnabled(true);
        editTextDescripcion.setEnabled(true);
        editTextPrecio.setEnabled(true);
    }
}
