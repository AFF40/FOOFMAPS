package com.example.foofmaps;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class pruebacon extends StringRequest {

    //conexion con el archivo php

    private static final String REGISTER_REQUEST_URL="http://192.168.99.217/web2/controlador/controladorPersona.php";
    private final Map<String,String>params;
    public pruebacon(String username, String email, Response.Listener<String> listener){

        super(Method.POST, REGISTER_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("username",username);
        params.put("email",email);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}