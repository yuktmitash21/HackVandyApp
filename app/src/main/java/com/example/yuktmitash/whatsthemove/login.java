package com.example.yuktmitash.whatsthemove;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView failed;
    private Button login;
    private Button makeAccount;

    private String Email = null;
    private String Password = null;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);

        email = findViewById(R.id.logemail);
        password = findViewById(R.id.passwordlogin);
        failed = findViewById(R.id.failedlogin);
        login = findViewById(R.id.loginInlog);
        makeAccount = findViewById(R.id.makeAnAccount);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        //if (firebaseAuth.getCurrentUser() != null) {
         //   finish();
         //   startActivity(new Intent(getApplicationContext(), MainScreen.class));
        //}


        makeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(getApplicationContext(), registration.class);
                startActivity(register);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   signInUser();




            }
        });


    }

    public void signInUser() {
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
     //   if (TextUtils.isEmpty(Email)) {
       //     failed.setText("Please enter a valid e-mail");
       // } else if (TextUtils.isEmpty(Password)) {
        //    failed.setText("Please enter a valid password");
       // } else {
            progressDialog.setMessage("Signing in....");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                       // Toast.makeText(login.this, "Success.", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(login.this, "Logged In!", Toast.LENGTH_SHORT).show();
                        //finish();
                        Intent mainScreen = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(mainScreen);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(login.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    //on data change listener rather than single event


    }

