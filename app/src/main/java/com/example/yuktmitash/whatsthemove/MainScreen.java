package com.example.yuktmitash.whatsthemove;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.NonNull;
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
                final Intent myParties = new Intent(getApplicationContext(), myPartyView.class);
                myParties.putExtra("userId", firebaseUser.getUid());
                myParties.putExtra("username", username);
                reference.child("parties").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Party.class) == null) {
                            Toast.makeText(getApplicationContext(), "Oops.. You do not have a party right now", Toast.LENGTH_SHORT).show();
                        } else {
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
