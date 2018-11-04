package com.example.yuktmitash.whatsthemove;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckCop extends AppCompatActivity {
    private TextView textView;
    private CheckBox yes;
    private CheckBox no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_cop);

        textView = findViewById(R.id.CopStatement);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        textView.setText("Are you now or have you ever been a member of Law Enforcement?");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), registration.class));
            }
        });
    }
}
