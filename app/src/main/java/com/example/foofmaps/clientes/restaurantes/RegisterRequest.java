package com.example.foofmaps.clientes.restaurantes;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.foofmaps.Config;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    //conexion con el archivo php
    private static final String controladorURL = Config.CONTROLADOR_URL+"controladorPersona.php";
    private final Map<String,String>params;
    public RegisterRequest(String username, String email, String pass1, String pass2, Response.Listener<String> listener){
        super(Method.POST, controladorURL,listener,null);
        params=new HashMap<>();
        Log.e("info",username);
        Log.e("info",email);
        params.put("username",username);
        params.put("email",email);
        params.put("pass1",pass1);
        params.put("pass2",pass2);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}