package com.example.yuktmitash.whatsthemove;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class PartyView extends AppCompatActivity {
    private static final int NECESSARY_PROMOTIONS = 1;


    ImageView imageView;
    TextView distance;

    StorageReference storage;

    int oldRating;

    private Button promote1;
    Button getDirections;
    Button cancel;
    private Button chatRoom1;

    FirebaseDatabase database;
    DatabaseReference reference;
    private Party party;

    private double partyLatt;
    private double partyLong;

    String partyName;


    String id;
    private float trueDistance;
    private boolean alreadyPromoted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_view);

        distance = findViewById(R.id.Distance);

        promote1 = findViewById(R.id.Promote);
        cancel = findViewById(R.id.Cancel);
        getDirections = findViewById(R.id.directions);
        imageView = findViewById(R.id.imageView3);

        storage = FirebaseStorage.getInstance().getReference();
        chatRoom1 = findViewById(R.id.chattingRoom);




       final String name = getIntent().getStringExtra("name");
        final double userLatt = getIntent().getDoubleExtra("UserLatt", 0.0);
        final double userLong = getIntent().getDoubleExtra("UserLong", 0.0);
        id = getIntent().getStringExtra("id");




        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("parties").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                party = dataSnapshot.getValue(Party.class);
                float[] arr = new float[10];
                Location.distanceBetween(userLatt, userLong, party.getLattitude(), party.getLongitude(), arr );
                float distanceBetween = arr[0];
                trueDistance = distanceBetween * (float) 0.000621371;
                //THIS IS HARD CODING FOR HACKATHON PURPOSES!!!!!!!!!!!!!!!!!
                if (trueDistance >= 1000) {
                    trueDistance = 1;
                }
                String ages = "Average Age: " + party.getAverageAge();
                String numProtions = "Promotions: " + party.getPromotions();
                boolean isMove = party.getPromotions() >= NECESSARY_PROMOTIONS;

                partyLatt = party.getLattitude();
                partyLong = party.getLongitude();

                partyName = party.getName();

                  String extra = "This party needs " + (NECESSARY_PROMOTIONS - party.getPromotions()) + " more promotion(s)" +
                            " before it can be considered a move!";
                  if (!isMove) {
                      numProtions = extra;
                  }
                  String avgAge;
                  if (party.getAverageAge() == 0) {
                      avgAge = "No current average age";
                  } else {
                      avgAge = "Average Age: "+party.getAverageAge();
                  }
                  String people;
                  if (party.getPeople() == 0) {
                      people = "Host has not performed a people scan. You can let them know in the chatroom!";
                  } else {
                      people = "Number of people: " + (int) (party.getPeople());
                  }
                  //String address = party.getAddress().replace("\n", "");
                String x = party.getName() + "\n\n" + "Sponsered by: " + party.getSponsor() + "\n\n" +"Address: " + party.getAddress() +"\n\n"+numProtions
                        + "\n\n"+ "Litness: " + party.getLitness() + "\n\n" + avgAge + "\n\n" + people + "\n\n" +"Distance: " + trueDistance+ " miles";
                if (party.getPromotions() < NECESSARY_PROMOTIONS) {
                    extra = "";
                    x = x + extra;
                }
                
                distance.setText(x);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



      /*  String ages = "Average Age: " + averageAge;
        String numProtions = "Promotions: " + promotionsnum;
        String x = name + "\n\n\n" +"Sponsered by: " + sponsor + "\n\n\n" +"Address: " +address+"\n\n\n"+ages+"\n\n\n"+numProtions
                + "\n\n\n"+ "Litness: " + litness + "\n\n\n" +"Distance: " + finaldist+ " miles";

        //distance.setText(x);*/


        promote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("promoted").child(id).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                            for (DataSnapshot d : data) {
                                String tempId = (String) d.getValue();
                                if (tempId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    alreadyPromoted = true;
                                }
                            }

                        }

                        if (alreadyPromoted) {
                            Toast.makeText(PartyView.this, "Oops! You can only promote a party once :(", Toast.LENGTH_SHORT).show();
                        } else if (party.getFireid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Toast.makeText(PartyView.this, "You cannot promote your own party :(", Toast.LENGTH_SHORT).show();
                        } else if ((int)trueDistance >= 10) {
                            Toast.makeText(PartyView.this, "You are too far from this party to promote it!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            database = FirebaseDatabase.getInstance();
                            reference = database.getReference();
                            reference.child("parties").child(id).child("promotions").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long pro = (Long) dataSnapshot.getValue();
                                    //reference.child("parties").child(id).child("promotions").setValue(pro + 1);
                                    Intent promote33 = new Intent(getApplicationContext(), PromoteParty.class);
                                    promote33.putExtra("partyId", id);
                                    promote33.putExtra("promotions", pro);
                                    promote33.putExtra("partyName", name);
                                    promote33.putExtra("latt", userLatt);
                                    promote33.putExtra("long", userLong);
                                    startActivity(promote33);
                                    // Toast.makeText(PartyView.this, "The party has been promoted. Thanks for your input!",
                                    //       Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(PartyView.this, "Oops. Something went wrong",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
            }
        });

        storage.child("photos").child(id).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                imageView.setMinimumHeight(dm.heightPixels);
                imageView.setMinimumWidth(dm.widthPixels);
                imageView.setImageBitmap(bm);
            }
        });

        getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(PartyView.this);
                builder.setMessage("Open Google Maps?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String latitude = "" + partyLatt;
                                String longitude = "" + partyLong;
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");

                                try{
                                    if (mapIntent.resolveActivity(PartyView.this.getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                }catch (NullPointerException e){
                                    Log.e("MAPERROR", "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                    Toast.makeText(PartyView.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();

            }
        });

        chatRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)trueDistance <= 10) {
                    Intent chat = new Intent(getApplicationContext(), chatRoom.class);
                    chat.putExtra("partyid", id);
                    Log.d("TAG", party.getName());
                    chat.putExtra("partyName", partyName);
                    startActivity(chat);
                } else {
                    Toast.makeText(PartyView.this, "You are too far from this party to enter the chatRoom!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
