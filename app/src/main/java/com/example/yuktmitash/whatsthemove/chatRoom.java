package com.example.yuktmitash.whatsthemove;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class chatRoom extends AppCompatActivity {
    private DatabaseReference reference;

    private Button button;
    private TextView textView;
    private EditText editText;
    private TextView myTextView;
    private Button home;


    private FirebaseUser firebaseUser;
    private User user;


    private String partyId;
    private String partyName;

    private long numberOfMessages;
    private String usern;
    private String mess;


    private StringBuilder x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        button = findViewById(R.id.chatRoomButton);
        textView = findViewById(R.id.Messages);
        editText = findViewById(R.id.messageToSend);
        home = findViewById(R.id.HomeFromChat);
       // myTextView = findViewById(R.id.partyNameChat);

        partyName = getIntent().getStringExtra("partyName");
        partyId = getIntent().getStringExtra("partyid");
        reference = FirebaseDatabase.getInstance().getReference();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
            }
        });



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = firebaseUser.getUid();
        reference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("messages").child("instances").child(partyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    reference.child("messages").child("instances").child(partyId).setValue(0);
                    numberOfMessages = 0;

                } else {
                    numberOfMessages =(long) dataSnapshot.getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //on data change listener rather than single event

                reference.child("messages").child(partyId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        x = new StringBuilder("");
                        for (DataSnapshot d : data) {
                            HashMap<String, String> myMap= (HashMap<String, String>) d.getValue();
                            String line;
                            if (myMap.size() == 1) {
                                String username = (String) myMap.keySet().toArray()[0];
                                String message = (String) myMap.values().toArray()[0];
                                line = username + ": " + message;
                            } else {
                                Object[] arr = (Object[]) myMap.keySet().toArray();
                                Object[] arr2 = (Object[]) myMap.values().toArray();
                                String username1 = (String) arr[0];
                                String username2 = (String) arr[1];
                                String message1 = (String) arr2[0];
                                String message2 = (String) arr2[1];
                                line = username1 + ": " + message1 + "\n\n" + username2 + ": " + message2;
                            }
                            x.append(line +"\n\n");
                        }
                        textView.setText(x.toString());
                        final ScrollView scroll = findViewById(R.id.scrollView1);
                        scroll.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        },1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }



    public void sendMessage(View view) {
        //final int instances;
        boolean inRange = getIntent().getBooleanExtra("inRange", true);
        if (!inRange) {
            Toast.makeText(getApplicationContext(), "Oops... You must be at the party to chat", Toast.LENGTH_SHORT).show();
        } else {
            reference.child("messages").child(user.getUserName()).child(partyId).child("instances").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long newInstances;
                    if (dataSnapshot.getValue() != null) {
                        long instances = (long) dataSnapshot.getValue();
                        newInstances = instances + 1;
                    } else {
                        newInstances = 1;
                    }
                    if (!editText.getText().toString().equals("")) {
                      //  ScrollView scroll = findViewById(R.id.scrollView1);
                       // scroll.fullScroll(View.FOCUS_DOWN);
                        String id = user.getUserName();
                        numberOfMessages = numberOfMessages + 1;
                        reference.child("messages").child("instances").child(partyId).setValue(numberOfMessages);
                        reference.child("messages").child(partyId).child(numberOfMessages + "").child(id).setValue(editText.getText().toString());
                        editText.setText("");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
