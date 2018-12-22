package com.example.yuktmitash.whatsthemove;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Maps extends FragmentActivity implements OnMapReadyCallback {
    private double lattitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;
    //on data change listener rather than single event

    private Button mapsHome;

    private GoogleMap mMap;

    private FirebaseAuth mauth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
    private Firebase mRootRef;

    private float counter = 0;

    private StorageReference myStorage;


    FirebaseDatabase database;
    DatabaseReference reference;
    List<Party> parties = new ArrayList<>();
    String usernumber;
    private int myDistance;

    private static final int MINIMUM_PROMOTIONS = 1;
    private static final int SCALE_WIDTH = 10;
    private static final int SCALE_HEIGHT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myStorage = FirebaseStorage.getInstance().getReference();
        mapsHome = findViewById(R.id.mapsHome);
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
            if (ActivityCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    mMap = googleMap;
                    //mMap.setMapType(GoogleMap);

                    //add a lattitude and longitude to current user
                    mauth = FirebaseAuth.getInstance();
                    firebaseUser = mauth.getCurrentUser();
                    usernumber = firebaseUser.getUid();
                    database = FirebaseDatabase.getInstance();
                    userReference = database.getReference();
                    userReference.child("users").child(usernumber).child("lattitude").setValue(lattitude);
                    userReference.child("users").child(usernumber).child("longitude").setValue(longitude);




                    //  Add a marker in Sydney and move the camera
                    LatLng sydney = new LatLng(lattitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("You are here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));
                    // mMap.addMarker(new MarkerOptions().position(new LatLng(30, 80)).title("You are not here"));

                }
            }
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("parties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot dat : data) {
                    final Party party = dat.getValue(Party.class);
                    party.setFireid(dat.getKey());
                    reference.child("parties").child(party.getFireid()).setValue(party);
                    parties.add(party);
                    final DataSnapshot dataSnapshot1 = dat;

                    //delete party if necessary
                    reference.child("dates").child(party.getFireid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Date date = Calendar.getInstance().getTime();
                            CustomDate customDate = dataSnapshot.getValue(CustomDate.class);
                            if (customDate != null) {
                                if (date.getDay() - customDate.getDay() > 1) {
                                    dataSnapshot1.getRef().removeValue();
                                    reference.child("messages").child(party.getFireid()).removeValue();
                                    reference.child("promoted").child(party.getFireid()).removeValue();
                                    myStorage.child("Video").child(party.getFireid()).delete();
                                    myStorage.child("photos").child(party.getFireid()).delete();
                                } else if (date.getDay() - customDate.getDay() == 1 && customDate.getHours() > 11) {
                                    dataSnapshot1.getRef().removeValue();
                                    reference.child("messages").child(party.getFireid()).removeValue();
                                    reference.child("promoted").child(party.getFireid()).removeValue();
                                    myStorage.child("Video").child(party.getFireid()).delete();
                                    myStorage.child("photos").child(party.getFireid()).delete();
                                } else if (date.getDay() - customDate.getDay() <= 1 && date.getHours() -
                                        customDate.getHours() >= 12) {
                                    dataSnapshot1.getRef().removeValue();
                                    reference.child("messages").child(party.getFireid()).removeValue();
                                    reference.child("promoted").child(party.getFireid()).removeValue();
                                    myStorage.child("Video").child(party.getFireid()).delete();
                                    myStorage.child("photos").child(party.getFireid()).delete();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("NO Date", databaseError.getMessage());

                        }
                    });

                    //Calculalate distance
                    float[] arr = new float[10];
                    Location.distanceBetween(lattitude, longitude, party.getLattitude(), party.getLongitude(), arr);
                    float distanceBetween = arr[0];
                    final float trueDistance = distanceBetween * (float) 0.000621371;
                    myDistance = (int) trueDistance;

                    //get picture
                    Bitmap bm;
                    myStorage.child("photos").child(party.getFireid()).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int height = (displayMetrics.heightPixels / SCALE_HEIGHT) + (int) party.getPromotions();
                            int width = (displayMetrics.widthPixels / SCALE_WIDTH) + (int) party.getPromotions();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(bm, width, height, false);




                        //add marker with title
                            if (party.getPromotions() >= MINIMUM_PROMOTIONS) {
                                LatLng partyLocation = new LatLng(party.getLattitude(), party.getLongitude());
                                Marker marker = mMap.addMarker(new MarkerOptions().position(partyLocation).title(party.getName() + " Distance: " + (int)trueDistance + " miles"));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                marker.setTag(party.getFireid());
                            }

                        }
                    });
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String name = (String) marker.getTag();
                for (Party p: parties) {
                    if (p.getFireid().equals(name)) {
                        Intent partyView = new Intent(getApplicationContext(), PartyView.class);
                        partyView.putExtra("average age", p.getAverageAge());
                        partyView.putExtra("promotions", p.getPromotions());
                        partyView.putExtra("address", p.getAddress());
                        partyView.putExtra("name", p.getName());
                        partyView.putExtra("sponsor", p.getSponsor());
                        partyView.putExtra("litness", p.getLitness());
                        partyView.putExtra("PartyLatt", p.getLattitude());
                        partyView.putExtra("PartyLong", p.getLongitude());
                        partyView.putExtra("UserLatt", lattitude);
                        partyView.putExtra("UserLong", longitude);
                        partyView.putExtra("Distance", myDistance);
                        partyView.putExtra("id", p.getFireid());
                        startActivity(partyView);
                    }
                }
                return false;


            }
        });

        mapsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
            }
        });


    }



}


