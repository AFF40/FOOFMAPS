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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class agregar_bebidas extends AppCompatActivity implements onBebidaAddedListener {
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
    private static final String modeloURL = Config.MODELO_URL + "agregar_bebida.php";

    @Override
    public void onBebidaAdded() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_bebidas);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        editTextNomBebida = findViewById(R.id.editTextNomBebida);
        nomBebida = findViewById(R.id.nom_bebida);
        editTextNomBebida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nomBebida.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextDescripcion = findViewById(R.id.editTextDescripcionBebida);
        descripcion_bebida = findViewById(R.id.desc_bebida);
        editTextDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descripcion_bebida.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextPrecio = findViewById(R.id.editTextPrecioBebida);
        precio_bebida = findViewById(R.id.precio);
        editTextPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                precio_bebida.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectCamara = findViewById(R.id.btnselectcamara);
        imagenBebida = findViewById(R.id.imagenBebida);
        btnEnviar = findViewById(R.id.btnEnviarFormulario);

        btnSelectImage.setOnClickListener(view -> {
            Intent abrir_galeria = new Intent(Intent.ACTION_PICK);
            abrir_galeria.setType("image/*");
            startActivityForResult(abrir_galeria, PICK_IMAGE_REQUEST);
        });

        btnSelectCamara.setOnClickListener(view -> {
            Intent abrircamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (abrircamara.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(abrircamara, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "No se encontró una aplicación de cámara en el dispositivo", Toast.LENGTH_SHORT).show();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarFormulario(view);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imagenBebida.setImageBitmap(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagenBebida.setImageBitmap(imageBitmap);
        }
    }

    public void enviarFormulario(View view) {
        Intent intent = getIntent();
        int restauranteId = intent.getIntExtra("restaurante_id", -1);
        String nombreRestaurante = intent.getStringExtra("nombre_restaurante");
        Log.d("restaurante_id_en_agregar", "restaurante_id: " + restauranteId);
        Log.d("nombre_restaurante_en_agregar", "nombre_restaurante: " + nombreRestaurante);

        String nombreBebida = editTextNomBebida.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String precio = editTextPrecio.getText().toString().trim();
        String restauranteIdString = String.valueOf(restauranteId);

        if (nombreBebida.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos del formulario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenBebida.getDrawable() == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        guardarImagenEnServidor(nombreBebida, descripcion, precio, restauranteIdString, nombreRestaurante);
    }

    public void guardarImagenEnServidor(String nombreBebida, String descripcion, String precio, String restauranteIdString,String nombreRestaurante) {
        Bitmap imageBitmap = ((BitmapDrawable) imagenBebida.getDrawable()).getBitmap();
        String imageBase64 = convertImageToBase64(imageBitmap);
        JSONObject bebidaData = new JSONObject();
        try {
            bebidaData.put("nombre", nombreBebida);
            bebidaData.put("descripcion", descripcion);
            bebidaData.put("precio", precio);
            bebidaData.put("restaurante_nombre", nombreRestaurante);
            bebidaData.put("restaurante_id", restauranteIdString);
            bebidaData.put("imagen", imageBase64);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("log_anadir_bebidadata", "platoData: " + bebidaData);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                modeloURL,
                bebidaData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseData = response.toString();
                            Log.d("log_anadir_ResponseData", "Response data: " + responseData);
                            if (isJSONValid(responseData)) {
                                handleServerResponse(responseData);
                            } else {
                                Toast.makeText(agregar_bebidas.this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(agregar_bebidas.this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error al agregar la bebida: " + error.getMessage();
                        Toast.makeText(agregar_bebidas.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("log_anadir_Error", errorMessage, error);

                        if (error.networkResponse != null) {
                            Log.e("log_anadir_Error_serv", "Respuesta del servidor: " + new String(error.networkResponse.data));
                        } else {
                            Log.e("log_anadir_Error_serv", "No se recibió respuesta del servidor");
                        }

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                JSONObject errorJson = new JSONObject(new String(error.networkResponse.data));
                                String serverErrorMessage = errorJson.optString("error");
                                if (!TextUtils.isEmpty(serverErrorMessage)) {
                                    Toast.makeText(agregar_bebidas.this, serverErrorMessage, Toast.LENGTH_SHORT).show();
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

    private boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private void handleServerResponse(String responseData) {
        try {
            JSONObject response = new JSONObject(responseData);
            String message = response.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            onBebidaAdded();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Respuesta del servidor no válida", Toast.LENGTH_SHORT).show();
        }
    }

    public String convertImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}

