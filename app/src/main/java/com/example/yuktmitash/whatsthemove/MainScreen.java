package com.example.yuktmitash.whatsthemove;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainScreen extends AppCompatActivity {
    private Button logout;
    private TextView intro;

    private Button Promote;
    private Button find;
    private Button partyStarter;
    private Button myParties;

    private String username;
    private Button profile;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference reference;

    private double lattitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        logout = findViewById(R.id.logoutmain);
        intro = findViewById(R.id.intro);
        myParties = findViewById(R.id.MyParties);
        find = findViewById(R.id.Finder);
        profile = findViewById(R.id.MyProfile);

        int color = Color.parseColor("#99cc00");
        logout.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
       // intro.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        myParties.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        find.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        profile.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));



        firebaseAuth = FirebaseAuth.getInstance();



        firebaseUser = firebaseAuth.getCurrentUser();
        final String userid = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("users").child(userid).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               username = (String) dataSnapshot.getValue();
                Log.d("username", (String) dataSnapshot.getValue());
                String x = "Hello, " + username;
                intro.setText(x);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        Promote = findViewById(R.id.Promoter);
        find = findViewById(R.id.Finder);
        partyStarter = findViewById(R.id.starter);

        partyStarter.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        Promote.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lattitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                        10);
            } else {
                locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                locationManager.removeUpdates(locationListener);
                if (location == null) {
                    Log.e("Maps", "Houston, we have a problem.");
                } else {
                    lattitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }



        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Intent userProfile = new Intent(getApplicationContext(), UserProfile.class);
                        userProfile.putExtra("age", user.getAge());
                        userProfile.putExtra("username", user.getUserName());
                        userProfile.putExtra("email", user.getEmail());
                        userProfile.putExtra("id", userid);
                        startActivity(userProfile);
                        //on data change listener rather than single event

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



        myParties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("parties").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Party p = dataSnapshot.getValue(Party.class);
                        if (p == null) {
                            Toast.makeText(getApplicationContext(), "Oops.. You do not have a party right now", Toast.LENGTH_SHORT).show();
                        } else if (!p.isParty()) {
                            Toast.makeText(getApplicationContext(), "Oops.. You do not have a party right now", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent myParties = new Intent(getApplicationContext(), PartyView.class);
                            myParties.putExtra("name", p.getName());
                            myParties.putExtra("UserLatt", lattitude);
                            myParties.putExtra("UserLong", longitude);
                            myParties.putExtra("id", p.getFireid());
                            startActivity(myParties);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        partyStarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateParty.class));
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewParties.class));
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });

        Promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent promoting = new Intent(getApplicationContext(), AlternateList.class);
                promoting.putExtra("Promotable", true);
                startActivity(promoting);
            }
        });
    }
}
