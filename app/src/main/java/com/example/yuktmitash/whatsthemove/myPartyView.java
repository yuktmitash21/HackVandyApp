package com.example.yuktmitash.whatsthemove;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class myPartyView extends AppCompatActivity {
    private TextView header;
    private TextView body;
    private TextView addy;
    private TextView sponsorMyParty;
    private TextView litnessOfParty;
    private TextView averageAgeofParty;
    private TextView promotionsOfParty;
    private TextView promotionalMessage;
    private ImageView imageView;
    private StorageReference myStorage;
    private String myPartyId;
    //on data change listener rather than single event


    private Button mapView;
    private Button home;
    private Button scan;

    FirebaseDatabase database;
    DatabaseReference reference;

    private String fireid;

    Party userParty;

    private String myPartyname;
    private String myPartysponsor;
    private String myPartyaddress;
    private boolean isMove;
    private String myPartylitness;
    private long myPartyaverageAge;
    private long myPartypromotions;


    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_party_view);

        header = findViewById(R.id.headerMyParty);
        body = findViewById(R.id.bodyMyParty);
        addy = findViewById(R.id.addressMyParty);
        sponsorMyParty = findViewById(R.id.sponsorMyParty);
        litnessOfParty = findViewById(R.id.LitnessMyParty);
        averageAgeofParty = findViewById(R.id.averageAgeMyParty);
        promotionsOfParty = findViewById(R.id.promotionsMyParty);
        promotionalMessage = findViewById(R.id.promotionalMessage);
        imageView = findViewById(R.id.myPartyImage);
        scan = findViewById(R.id.numPeople);

        home = findViewById(R.id.HomeFromMy);
        mapView = findViewById(R.id.mapViewFromMy);

        myStorage = FirebaseStorage.getInstance().getReference();

        final String userid = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        Log.d("USER", userid);

        header.setText("        " +username+"'s Party");
        header.setTypeface(null, Typeface.BOLD);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        fireid = userid;

        reference.child("parties").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                /*for (DataSnapshot dat: data) {
                    if (dat.getKey().equals(userid)) {
                        userParty = dat.getValue(Party.class);
                    }
                }*/

               userParty = dataSnapshot.getValue(Party.class);
                myPartyname = userParty.getName();
                Log.d("Party name", myPartyname);
                myPartysponsor = userParty.getSponsor();
                Log.d("sponsors", myPartysponsor);
                myPartyaddress = userParty.getAddress(); //
               // Log.d("address", myPartyaddress);
                myPartylitness = userParty.getLitness(); //
                Log.d("litness", myPartylitness);
                myPartyaverageAge = userParty.getAverageAge();//
                Log.d("age", ""+myPartyaverageAge);
                myPartypromotions = userParty.getPromotions();//
                Log.d("promotions", ""+ myPartypromotions);
                myPartyId = userParty.getFireid();

                String moveMessage;

                if (myPartypromotions >= 1) {
                    moveMessage = "Congratulations your party has " + myPartypromotions +" promotions and is considered a move!" +
                            " This means that anyone can view your party and get directions to it.";
                } else {
                    moveMessage = "We are sorry, but your party is not currently considered a move :(. " +
                            "You need " + (1 - myPartypromotions) + " more promotion(s) for your party to be considered " +
                            "a move";
                }

                body.setText(myPartyname);
                addy.setText(myPartyaddress);
                sponsorMyParty.setText("Sponsor: " + myPartysponsor);
                if (myPartysponsor == null) {
                    sponsorMyParty.setText("No current sponsor");
                }
                litnessOfParty.setText("Litness: " + myPartylitness);
                if (myPartyaverageAge != 0) {
                    averageAgeofParty.setText("Average Age: " + myPartyaverageAge);
                } else {
                    averageAgeofParty.setText("No current average age");
                }
                promotionsOfParty.setText("Promotions: " + myPartypromotions);
                promotionalMessage.setText(moveMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myStorage.child("photos").child(userid).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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





        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainScreen.class));

            }
        });

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(getApplicationContext(), PeopleCounter.class);
                //Log.d("This is the party id", myPartyId);
                picIntent.putExtra("id", myPartyId);
                startActivity(picIntent);
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CAPTURE) {
                Uri selectedImageUri = data.getData();
                StorageReference path = myStorage.child("Video").child(fireid);
                if (selectedImageUri != null) {
                    path.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(myPartyView.this, "Video Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(myPartyView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                /*selectedImageUri = handleImageUri(selectedImageUri);
                selectedPath = getRealPathFromURI(selectedImageUri);
                tvStatus.setText("Selected Path :: " + selectedPath);
                Log.i(TAG, " Path :: " + selectedPath);*/
                } else {
                    Toast.makeText(myPartyView.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
