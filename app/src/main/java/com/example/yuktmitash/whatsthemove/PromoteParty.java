package com.example.yuktmitash.whatsthemove;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PromoteParty extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;

    private TextView title;
    private Spinner rater;
    private Button cancel;
    private Button promote;
    private TextView failed;


    private long currentAge;
    private long currentRating;
    private long dbRating;
    private String newLitnessFactor;
    private long newPromotions;

    private long newAge;
    private long newRating;
    private long userAge;


    private FirebaseUser firebaseUser;
    private FirebaseAuth mauth;

    private static Party myParty;
    private User myUser;

    private ImageView myImageView;
    private StorageReference myStorage;

    private double userLatt;
    private double userLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote_party);

        title = findViewById(R.id.partyNamePromote);
        rater = findViewById(R.id.spinner69);
        cancel = findViewById(R.id.cancelpromote);
        promote = findViewById(R.id.promotepromote);
        failed = findViewById(R.id.failed52);
        myImageView = findViewById(R.id.myImageView);
        myStorage = FirebaseStorage.getInstance().getReference();


        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        final String id = getIntent().getStringExtra("partyId");
        final Long promotions = getIntent().getLongExtra("promotions", 0) + 1;
        final String name = getIntent().getStringExtra("partyName");

        userLatt = getIntent().getDoubleExtra("latt", 0);
        userLong = getIntent().getDoubleExtra("long", 0);



        title.setText(name);

        String[] stringarr = new String[] {"Litness", "Worse than listening to nickelback",
                "Better than Nothing", "Pretty decent", "Best Party of the month", "Rager"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(PromoteParty.this, android.R.layout.simple_spinner_item, stringarr);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rater.setAdapter(adapter1);


        reference.child("parties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot dat: data) {
                    Party p = dat.getValue(Party.class);
                    if (p.getFireid().equals(id)) {
                        myParty = p;
                    }
                }

            }







            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myStorage.child("photos").child(id).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                myImageView.setMinimumHeight(dm.heightPixels);
                myImageView.setMinimumWidth(dm.widthPixels);
                myImageView.setImageBitmap(bm);
            }
        });


        //get user id
        mauth = FirebaseAuth.getInstance();
        firebaseUser = mauth.getCurrentUser();
        final String currentUserId = firebaseUser.getUid();


        //get user object
        reference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot dat: data) {
                    Log.d("keys", dat.getKey());
                    if (dat.getKey().equals(currentUserId)) {
                        myUser = dat.getValue(User.class);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent maps = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(maps);
            }
        });

        promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {










                if (rater.getSelectedItem().toString().equals("Litness")) {
                    failed.setText("Please select a level of Litness");
                } else {

                    if (rater.getSelectedItem().toString().equals("Worse than listening to nickelback")) {
                        currentRating = 2;
                    } else if (rater.getSelectedItem().toString().equals("Better than Nothing")) {
                        currentRating = 4;
                    } else if (rater.getSelectedItem().toString().equals("Pretty decent")) {
                        currentRating = 6;
                    } else if (rater.getSelectedItem().toString().equals("Best Party of the month")) {
                        currentRating = 8;
                    } else {
                        currentRating = 10;
                    }

                    //pull necessary data
                    newPromotions = myParty.getPromotions() + 1;
                    userAge = myUser.getAge();
                    dbRating = myParty.getRating();
                    currentAge = myParty.getAverageAge();




                    reference.child("parties").child(id).child("promotions").setValue(newPromotions);
                    if (newPromotions == 1) {
                        newAge = userAge;
                    } else {
                        newAge = (((promotions - 1) * currentAge) + userAge) / promotions;
                    }

                    //reference.child("parties").child(id).child("averageAge").setValue(newAge);
                    newRating = ((dbRating * (promotions - 1)) + currentRating) / promotions;
                    //reference.child("parties").child(id).child("rating").setValue(newRating);
                    if (newRating <= 2) {
                        newLitnessFactor = "Worse than listening to nickelback";
                    } else if (newRating <= 4) {
                        newLitnessFactor = "Better than Nothing";
                    } else if (newRating <= 6) {
                        newLitnessFactor = "Pretty decent";
                    } else if (newRating <= 8) {
                        newLitnessFactor = "Best Party of the month";
                    } else {
                        newLitnessFactor = "Rager";

                    }
                    //reference.child("parties").child(id).child("litness").setValue(newLitnessFactor);

                    myParty.setPromotions(newPromotions);
                    myParty.setAverageAge( newAge);
                    myParty.setRating(newRating);
                    myParty.setLitness(newLitnessFactor);
                    
                    Log.d("NewAge", "" + newAge);


                    PartyManager partyManager = new PartyManager(myParty, reference);
                    partyManager.pushToFirebase();

                    //reference.child("promoted").child(id).child("numPromotions").setValue(newPromotions);
                    reference.child("promoted").child(id).child("users").child("" + newPromotions).setValue(firebaseUser.getUid());



                   Toast.makeText(PromoteParty.this, "The party has been promoted and" +
                                   " the ratings and average age have been updated. Thanks for your input!",
                         Toast.LENGTH_LONG).show();
                    Intent viewingParty = new Intent(getApplicationContext(), PartyView.class);
                    viewingParty.putExtra("name", name);
                    viewingParty.putExtra("UserLatt", userLatt);
                    viewingParty.putExtra("UserLong", userLong);
                    viewingParty.putExtra("id", id);
                    startActivity(viewingParty);
                    //master branch
                    //startActivity();

                   // startActivity(new Intent(getApplicationContext(), Maps.class));




                }
            }
        });




    }
}
