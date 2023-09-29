package com.example.foofmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class usuario extends AppCompatActivity {




    TextView textVusername,textVemail,textVpass1,textVpass2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);


        textVusername=findViewById(R.id.textVusername);
        textVemail=findViewById(R.id.textVemail);
        textVpass1=findViewById(R.id.textVpass1);
        textVpass2=findViewById(R.id.textVpass2);


        Intent intent = getIntent();

        String username=intent.getStringExtra("username");
        String email=intent.getStringExtra("email");
        String pass1=intent.getStringExtra("pass1");
        String pass2=intent.getStringExtra("pass2");


        textVusername.setText(username);
        textVemail.setText(email);
        textVpass1.setText(pass1);
        textVpass2.setText(pass2);

    }

}