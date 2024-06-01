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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class agregar_platos extends AppCompatActivity implements onPlatoAddedListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1002;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

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

    private static final String modeloURL = Config.MODELO_URL + "a%c3%b1adir_plato.php";

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

        btnSelectImage.setOnClickListener(view -> openGallery());
        btnSelectCamara.setOnClickListener(view -> openCamera());
        btnEnviar.setOnClickListener(view -> enviarFormulario(view));
    }

    private void checkAndRequestPermissions(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            // Permission already granted
            if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
                openGallery();
            } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                openCamera();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions(Manifest.permission.READ_MEDIA_IMAGES, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            Intent abrir_galeria = new Intent(Intent.ACTION_PICK);
            abrir_galeria.setType("image/*");
            startActivityForResult(abrir_galeria, PICK_IMAGE_REQUEST);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent abrircamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (abrircamara.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(abrircamara, CAMERA_REQUEST);
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
        Log.d("log_anadir_nombre_rest", "Nombre del restaurante: " + nombreRestaurante);
        Log.d("log_anadir_url", "url: " + modeloURL);

        String nombrePlato = editTextNomPlato.getText().toString().trim();
        String descripcionPlato = editTextDescripcion.getText().toString().trim();
        String precioPlato = editTextPrecio.getText().toString().trim();

        if (TextUtils.isEmpty(nombrePlato) || TextUtils.isEmpty(descripcionPlato) || TextUtils.isEmpty(precioPlato) || imageBitmap == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        setFormEnabled(false);

        String restauranteIdString = String.valueOf(restauranteId);
        Log.d("log_anadir_restaurante", "Restaurante ID: " + restauranteIdString);

        guardarImagenEnServidor(nombrePlato, descripcionPlato, precioPlato, restauranteIdString, nombreRestaurante);
    }

    private void setFormEnabled(boolean enabled) {
        btnEnviar.setEnabled(enabled);
        btnSelectImage.setEnabled(enabled);
        btnSelectCamara.setEnabled(enabled);
        editTextNomPlato.setEnabled(enabled);
        editTextDescripcion.setEnabled(enabled);
        editTextPrecio.setEnabled(enabled);
    }

    public void guardarImagenEnServidor(String nombrePlato, String descripcionPlato, String precioPlato, String restauranteIdString, String nombreRestaurante) {
        String imagenBase64 = convertImageToBase64(imageBitmap);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", nombrePlato);
            jsonObject.put("descripcion", descripcionPlato);
            jsonObject.put("precio", precioPlato);
            jsonObject.put("restaurante_id", restauranteIdString);
            jsonObject.put("nombre_restaurante", nombreRestaurante);
            jsonObject.put("imagen", imagenBase64);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    modeloURL,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String responseData = response.toString();
                                Log.d("log_anadir_ResponseData", "Response data: " + responseData); // Agregar registro de depuración
                                if (isJSONValid(responseData)) {
                                    handleServerResponse(responseData); // Llamar a la función para manejar la respuesta del servidor
                                } else {
                                    Toast.makeText(agregar_platos.this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(agregar_platos.this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
                            }
                            setFormEnabled(true); // Rehabilitar el formulario después de recibir la respuesta
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setFormEnabled(true); // Rehabilitar el formulario en caso de error
                            Toast.makeText(agregar_platos.this, "Error al enviar datos al servidor", Toast.LENGTH_SHORT).show();
                            Log.e("Volley Error", error.toString());
                        }
                    }
            );

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            setFormEnabled(true); // Rehabilitar el formulario en caso de excepción
        }
    }

    private String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void handleServerResponse(String responseData) {
        // Maneja la respuesta del servidor aquí
        try {
            JSONObject response = new JSONObject(responseData);
            boolean success = response.getBoolean("success");
            if (success) {
                Toast.makeText(this, "Plato añadido con éxito", Toast.LENGTH_SHORT).show();
                onPlatoAdded();
            } else {
                String errorMessage = response.getString("message");
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}
