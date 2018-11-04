package com.example.yuktmitash.whatsthemove;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;

    private TextView mTextMessage;
    private FirebaseAuth firebaseAuth;
//on data change listener rather than single event


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        //if (firebaseAuth.getCurrentUser() != null) {
         //   finish();
          //  startActivity(new Intent(getApplicationContext(), MainScreen.class));
      //  }

        mTextMessage = (TextView) findViewById(R.id.message);



        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);

        int color = Color.parseColor("#99cc00");
        loginButton.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        registerButton.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), login.class);

                startActivity(loginIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrationIntent = new Intent(getApplicationContext(), CheckCop.class);

                startActivity(registrationIntent);
            }
        });


    }

}
