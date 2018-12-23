package com.example.yuktmitash.whatsthemove;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.client.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CreateParty extends AppCompatActivity {
    private EditText name;
    private EditText sponsor;
    private Spinner spinner;
    private Button makeParty;
    private TextView failed;
    private Button addPic;
    private ImageView imageView;

    public boolean locationSet = false;
    private ProgressDialog progressDialog2;


    private int rating;
    //private String usernumber;
    //on data change listener rather than single event
    private RequestQueue requestQueue;


    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private Firebase mRootRef;
    private DatabaseReference dateRef;
    private Uri downloadURI;


    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button locButtton;
    private Button cancel;

    private double lattitude;
    private double longitude;
    private String myAddress;
    private String usernumber;

    private Location loc;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private boolean checkForImage = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);



        //widgets
        name = findViewById(R.id.nameOfParty);
        //spinner = findViewById(R.id.litness);
        makeParty = findViewById(R.id.makePartyButton);
        sponsor = findViewById(R.id.sponsor);
        failed = findViewById(R.id.failed2);
        locButtton = findViewById(R.id.LocationButton);
        cancel = findViewById(R.id.CancelMakeParty);
        addPic = findViewById(R.id.addPic);
        imageView = findViewById(R.id.imageView2);

        progressDialog = new ProgressDialog(CreateParty.this);
        progressDialog2 = new ProgressDialog(CreateParty.this);


        //firebase stuff
        storageReference = FirebaseStorage.getInstance().getReference();

        //firebaseUser = FirebaseUser
       // ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mRootRef = new Firebase("https://whatsthemove-42660.firebaseio.com/parties");
        dateRef = FirebaseDatabase.getInstance().getReference();
      //  userfb = new Firebase("https://whatsthemove-42660.firebaseio.com/users/numParties");
        usernumber = firebaseUser.getUid();

       // users_num = new Firebase("https://whatsthemove-42660.firebaseio.com/users/"+usernumber);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, CAMERA_REQUEST_CODE);
            }
        });





       /* String[] stringarr = new String[] {"Litness", "Worse than listening to nickelback",
                "Better than Nothing", "Pretty decent", "Best Party of the month", "Rager"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(CreateParty.this, android.R.layout.simple_spinner_item, stringarr);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);*/

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

                configure();
                // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ///     if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ///       requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                //               10);
                //        return;
                //     } else {
                //      configure();
                //      }


                //locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);


                makeParty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storeInfo();

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), MainScreen.class));
                    }
                });


            }

        public void storeInfo() {

        if (name.getText().toString().equals("")) {
            Toast.makeText(CreateParty.this, "Please enter a name", Toast.LENGTH_SHORT).show();
        } else if (sponsor.getText().toString().equals("")) {
            Toast.makeText(CreateParty.this, "Please enter a sponsor", Toast.LENGTH_SHORT).show();
        } else if (myAddress.equals("")) {
            Toast.makeText(CreateParty.this, "Please set your location" , Toast.LENGTH_SHORT).show();
        } else if (!checkForImage) {
            Toast.makeText(CreateParty.this, "Please upload an image", Toast.LENGTH_SHORT).show();
        } else {

            //adding party to db by user id
            usernumber = firebaseUser.getUid();
            Party party = new Party(0, longitude, lattitude, 0, false,
                    "Not yet rated!", name.getText().toString(), sponsor.getText().toString(),
                    myAddress, 0, 0);
            party.setFireid(usernumber);

            Firebase partay = mRootRef.child(usernumber);
            partay.setValue(party);
            Date date = new Date();
            CustomDate customDate = new CustomDate(date.getMonth(), date.getDay(), date.getYear(),
                    date.getHours(), date.getMinutes());
            dateRef.child("dates").child(usernumber).setValue(customDate);


            Toast.makeText(CreateParty.this, "Party Created!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainScreen.class));
        }

        }

        private void configure() {
            locButtton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog2.setMessage("Setting Location...");
                    progressDialog2.show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(CreateParty.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateParty.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                                    10);
                        } else {
                            locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                            Location location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                            locationManager.removeUpdates(locationListener);
                            if (location == null) {
                                locButtton.setText("Its null" + longitude);
                            } else {
                                lattitude = location.getLatitude();
                                longitude = location.getLongitude();
                                //locButtton.setText("" + longitude);
                                locationSet = true;
                                requestQueue = Volley.newRequestQueue(CreateParty.this);
                                JsonObjectRequest jsonObjectRequest = new
                                        JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?" +
                                        "latlng=" + lattitude + "," + longitude + "&key=AIzaSyDjA3PnTSWYV0D4xkMeTm7OaKAcS4iKYnQ",
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                                    locButtton.setText("" + address);
                                                    myAddress = address;
                                                    progressDialog2.dismiss();
                                                    Toast.makeText(CreateParty.this, "Location Set", Toast.LENGTH_LONG).show();
                                                } catch (JSONException e) {
                                                    Log.d("Create Party", e.getLocalizedMessage());
                                                    locButtton.setText("Location Set!");
                                                    progressDialog2.dismiss();
                                                    Toast.makeText(CreateParty.this, "Location Set", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                requestQueue.add(jsonObjectRequest);
                                //Toast.makeText(CreateParty.this, "Location Set", Toast.LENGTH_LONG).show();
                                //IN MASTER

                            }
                        }
                    }

                }
            });

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //    progressDialog2.setMessage("Setting Location...");
                    configure();
                   // progressDialog2.dismiss();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            Bundle extras = data.getExtras();
            final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();




            final StorageReference filepath = storageReference.child("photos").child(usernumber);

            filepath.putBytes(dataBAOS).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateParty.this, "Image Uploaded!", Toast.LENGTH_LONG).show();
                    addPic.setVisibility(View.INVISIBLE);
                    imageView.setImageBitmap(bitmap);
                    checkForImage = true;


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateParty.this, "Ooops.. Something went wrong", Toast.LENGTH_LONG).show();
                    checkForImage = false;

                }
            });



        }
    }
}

