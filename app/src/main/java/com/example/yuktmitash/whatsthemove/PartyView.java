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
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.HashMap;

public class PartyView extends AppCompatActivity {
    private static final int NECESSARY_PROMOTIONS = 1;


    ImageView imageView;
    TextView distance;
    private PlayerView player;
    private SimpleExoPlayer simpleExoPlayer;

    StorageReference storage;

    int oldRating;

    private Button promote1;
    private VideoView videoView;
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

    private static final double MINIMUM_DISTANCE = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_view);

        distance = findViewById(R.id.Distance);
        player = findViewById(R.id.playerVideoView);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        player.setPlayer(simpleExoPlayer);
        player.setVisibility(View.INVISIBLE);

        promote1 = findViewById(R.id.Promote);
        cancel = findViewById(R.id.Cancel);
        getDirections = findViewById(R.id.directions);
        imageView = findViewById(R.id.imageView3);

        storage = FirebaseStorage.getInstance().getReference();
        chatRoom1 = findViewById(R.id.chattingRoom);
       // videoView = findViewById(R.id.videoView);
       // videoView.setVisibility(View.INVISIBLE);




       final String name = getIntent().getStringExtra("name");
        final double userLatt = getIntent().getDoubleExtra("UserLatt", 0.0);
        final double userLong = getIntent().getDoubleExtra("UserLong", 0.0);
        id = getIntent().getStringExtra("id");




        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("parties").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                party = dataSnapshot.getValue(Party.class);
                if (party == null) {
                    Toast.makeText(PartyView.this, "Sorry... Party just ended", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainScreen.class));
                }
                float[] arr = new float[10];
                Location.distanceBetween(userLatt, userLong, party.getLattitude(), party.getLongitude(), arr );
                float distanceBetween = arr[0];
                trueDistance = distanceBetween * (float) 0.000621371;

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

                  String ratio;
                  if (party.getMaleCount() == 0 || party.getFemaleCount() == 0) {
                      ratio ="Female-to-Male ratio: No ratio yet";
                  } else {
                      double ratio_dub = (double)party.getFemaleCount() / (double) party.getMaleCount();
                      ratio = "Female-to-Male ratio: " + party.getFemaleCount() + ":" + party.getMaleCount();
                  }
                  String people;
                  if (party.getPeople() == 0) {
                      people = "\n\n";
                  } else {
                      people = "\n\n" + "Number of people: " + (int) (party.getPeople()) + "\n\n";
                  }
                  //String address = party.getAddress().replace("\n", "");
                String x = party.getName() + "\n\n" + "Sponsor: " + party.getSponsor() + "\n\n" +"Address: " + party.getAddress() +"\n\n"+numProtions
                        + "\n\n"+ "Litness: " + party.getLitness() + "\n\n" + avgAge + people +"Distance: " + trueDistance+ " miles" + "\n\n"+ratio;
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
                        } else if ((double)trueDistance >= MINIMUM_DISTANCE) {
                            Toast.makeText(PartyView.this, "You are too far from this party to promote it!",
                                    Toast.LENGTH_SHORT).show();
                        }  else if (!party.isParty()) {
                            Toast.makeText(PartyView.this, "Sorry ... This party was recently deleted",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainScreen.class));
                        } else {
                            database = FirebaseDatabase.getInstance();
                            reference = database.getReference();
                            reference.child("parties").child(id).child("promotions").addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (party.isParty()) {
                    Intent comment = new Intent(getApplicationContext(), Comments.class);
                    comment.putExtra("name", party.getName());
                    comment.putExtra("partyId", id);
                    startActivity(comment);
                } else if (party.getFireid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(PartyView.this, "Sorry ... Your party has been deleted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PartyView.this, "Sorry ... This party has been deleted",
                            Toast.LENGTH_SHORT).show();
                }
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PartyView.this, "Sorry... Party just ended", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainScreen.class));

            }
        });

        /*storage.child("Video").child(id).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

            }
        })*/


        final DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "WhatsTheMove??"));
        storage.child("Video").child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageView.setVisibility(View.INVISIBLE);
                player.setVisibility(View.VISIBLE);

                ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                simpleExoPlayer.prepare(extractorMediaSource);
                simpleExoPlayer.setPlayWhenReady(true);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
                if ((double)trueDistance <= MINIMUM_DISTANCE && party.isParty()) {
                    Intent chat = new Intent(getApplicationContext(), chatRoom.class);
                    chat.putExtra("partyid", id);
                    chat.putExtra("partyName", partyName);
                    chat.putExtra("inRange", true);
                    startActivity(chat);
                } else if ((double)trueDistance > MINIMUM_DISTANCE) {
                    Intent chat = new Intent(getApplicationContext(), chatRoom.class);
                    chat.putExtra("partyid", id);
                    chat.putExtra("partyName", partyName);
                    chat.putExtra("inRange", false);
                    startActivity(chat);
                } else if (!party.isParty()) {
                    Toast.makeText(PartyView.this, "Sorry ... This party was recently deleted",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainScreen.class));
                }
            }
        });


    }

    /*@Override
    protected void onStop() {
        super.onStop();

        player.setPlayer(null);
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }*/
}
