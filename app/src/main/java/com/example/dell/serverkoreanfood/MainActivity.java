package com.example.dell.serverkoreanfood;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    Button btnSignin;
    TextView txtSologan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnSignin = findViewById(R.id.btnSignIn);
        txtSologan = findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSologan.setTypeface(face);


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn  = new Intent(MainActivity.this,SignIn.class);
                startActivity(signIn);

            }
        });

    }
}
