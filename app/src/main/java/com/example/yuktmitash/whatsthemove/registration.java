package com.example.yuktmitash.whatsthemove;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class registration extends AppCompatActivity {

    private static final String TAG = "registration";
    private TextView displayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private EditText username;
    private EditText email;
    private EditText password;
    private Button regButton;


    private int Year = 0;
    private int Month = 0;
    private int Day = 0;
    private String wholeDate;

    private User user;
    private ProgressDialog progressDialog;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Firebase mRootRef;
    private DatabaseReference ref;



    public BackEnd backEnd;
    private Spinner gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        username = findViewById(R.id.nameregistration);
        email = findViewById(R.id.mail);
        password = findViewById(R.id.passwordregistration);
        gender = findViewById(R.id.spinnerGender);
        regButton = (Button) findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();


        mRootRef = new Firebase("https://whatsthemove-42660.firebaseio.com/users");
        ref = FirebaseDatabase.getInstance().getReference();
        //if (firebaseAuth.getCurrentUser() != null) {
          //  finish();
          //  startActivity(new Intent(getApplicationContext(), MainScreen.class));
       // }
        String [] arr = new String[] {"Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(registration.this, android.R.layout.simple_spinner_item, arr);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(stringArrayAdapter);





        displayDate = findViewById(R.id.DOB);
        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(registration.this,
                        android.R.style.Theme_DeviceDefault, dateSetListener, year, month, day);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT ));
                dialog.show();


            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day ) {
                month++;
                //Log.d(TAG, month +"//"+ day+ year );
                wholeDate = month+"/"+day+"/"+year;
                displayDate.setText(wholeDate);
                Year = year;
                Month = month;
                Day = day;


            }
        };

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid userName.", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid e-mail address", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid password.", Toast.LENGTH_SHORT).show();
                } else if (wholeDate == null) {
                    Toast.makeText(getApplicationContext(), "Please enter a date of birth.", Toast.LENGTH_SHORT).show();
                } else if (gender.getSelectedItem().toString().equals("Gender")) {
                    Toast.makeText(getApplicationContext(), "Please enter a gender.", Toast.LENGTH_SHORT).show();

                } else {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    int years = year - Year;
                    int months = month - Month;
                    months++;
                    int age = months == 0 && Day <= day ? years : years - 1;
                    user = new User( username.getText().toString(), age,
                           password.getText().toString(), email.getText().toString(), Month, Year,
                            gender.getSelectedItem().toString());
                    registerUser();


                }

            }
        });


    }

    private void registerUser() {
        String email = user.getEmail();
        String password = user.getPassword();
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(registration.this, "Registered User!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    firebaseAuth = FirebaseAuth.getInstance();
                    DatabaseReference usersRef = ref.child("users");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    usersRef.child(firebaseUser.getUid()).setValue(user);
                    Intent regInt = new Intent(getApplicationContext(), MainScreen.class);
                    regInt.putExtra("Username", user.getUserName());
                    startActivity(regInt);
                } else {
                    Toast.makeText(registration.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

        });


        //String usernum = firebaseUser.getUid();
        //Firebase child = mRootRef.child(usernum);
        //child.setValue(user);





    }

}
