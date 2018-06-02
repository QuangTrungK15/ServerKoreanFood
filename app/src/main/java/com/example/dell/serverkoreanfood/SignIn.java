package com.example.dell.serverkoreanfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.serverkoreanfood.Common.Common;
import com.example.dell.serverkoreanfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    EditText editPhone,editPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        editPassword = findViewById(R.id.editPass);
        editPhone = findViewById(R.id.editPhone);

        btnSignIn = findViewById(R.id.btnSignIn);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
                progressDialog.setMessage("Please waiting... ");
                progressDialog.show();

                // Attach a listener to read the data at our User reference
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.child(editPhone.getText().toString()).exists()) {
                            //get user information
                            progressDialog.dismiss();
                            User user = dataSnapshot.child(editPhone.getText().toString()).getValue(User.class);

                            if (Boolean.parseBoolean(user.getIsStaff())) {

                                if (user.getPassword().equals(editPassword.getText().toString())) {
                                    Intent homeIntent = new Intent(SignIn.this,Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();
                                    //Toast.makeText(SignIn.this, "Sign In successfull !!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignIn.this, "Wrong password !!!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignIn.this, "Please login staff account !!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SignIn.this, "User not exits in database !!!", Toast.LENGTH_SHORT).show();
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






            }
        });

    }
}
