package com.example.yuktmitash.whatsthemove;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListOfMoves extends AppCompatActivity {
    public static final int NECESSARY_PROMOTIONS = 1;

    private ListView listView;
    private ArrayList<Party> partyArrayList;



    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String userid;

    private Set<String> keyset;


    private LocationListener locationListener;
    private LocationManager locationManager;
    private double longitude;
    private double lattitude;

    private SimpleAdapter simpleAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_moves);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userid = firebaseUser.getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        partyArrayList = new ArrayList<>();






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
            if (ActivityCompat.checkSelfPermission(ListOfMoves.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListOfMoves.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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



        listView = findViewById(R.id.myListView);

        final HashMap<String, String> myData = new HashMap<>();
       /* myData.put("Havana", "Needs 1 more promotion. You are in range!");
        myData.put("Cuba", "You are not in range");
        myData.put("Havanaa", "Needs 1 more promotion. You are in range!");
        myData.put("Miama", "You are not in range");
        myData.put("Francisco", "Needs 1 more promotion. You are in range!");
        myData.put("Europe", "You are not in range");
        myData.put("Eduardo", "Needs 1 more promotion. You are in range!");
        myData.put("European", "You are not in range");
        myData.put("American", "Needs 1 more promotion. You are in range!");
        myData.put("Yeet", "You are not in range");
        myData.put("Yuh", "Needs 1 more promotion. You are in range!");
        myData.put("Yukt", "You are not in range");
        myData.put("Why", "Needs 1 more promotion. You are in range!");
        myData.put("Tonight", "You are not in range");*/
       final List<HashMap<String, String>> myList = new ArrayList<>();
       simpleAdapter = new SimpleAdapter(this, myList, R.layout.list_item,
                new String[]{"First Line", "Second Line"}, new int[]{R.id.mainLayoutText, R.id.subLayoutText});



        reference.child("parties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    Party p = d.getValue(Party.class);
                    float[] arr = new float[10];
                    Location.distanceBetween(lattitude, longitude, p.getLattitude(), p.getLongitude(), arr) ;
                    float distanceBetween = arr[0];
                    float trueDistance = distanceBetween * (float) 0.000621371;
                    String first = "Needs " +(NECESSARY_PROMOTIONS - p.getPromotions()) + " more promotion(s)" +
                            " to become a move. " ;
                    String last = "You are in range.";
                    if (trueDistance >= 10) {
                        last = "You are not in range.";
                    }
                    if (p.getPromotions() >= NECESSARY_PROMOTIONS) {
                        first = "This party is a move! " ;
                    }
                    String leading = first + " " + last;
                    if (myData.containsKey(p.getName())) {
                        String myPartyName = p.getName();
                        int index = 1;
                        while (true) {
                            myPartyName = myPartyName + "-" + index;
                            if(!myData.containsKey(myPartyName)) {
                                break;
                            }
                            index++;

                        }
                        p.setName(myPartyName);
                        reference.child("parties").child(userid).child("name").setValue(myPartyName);
                    }
                    partyArrayList.add(p);
                    boolean checker;
                        myData.put(p.getName(), leading);


                }

                keyset = myData.keySet();
                Iterator it = myData.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap<String, String> resultsMap = new HashMap<>();
                    Map.Entry pair = (Map.Entry) it.next();
                    resultsMap.put("First Line", pair.getKey().toString());
                    resultsMap.put("Second Line", pair.getValue().toString());
                    myList.add(resultsMap);
                }

                listView.setAdapter(simpleAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              //  Party p = partyArrayList.get((i + 1) % partyArrayList.size());
              //  int index = i -1;
              //  if (index == -1) {
                Log.d("INDEX", partyArrayList.get(i).getName());
                    int index = partyArrayList.size() - (i + 1);
               // }
                Party p = null;
                Object[] partyNames = keyset.toArray();
                for (Party x: partyArrayList) {
                    if (x.getName().equals(partyNames[i])) {
                        p = x;
                    }
                }
                String fireid = p.getFireid();
                String name = p.getName();
                Intent promoteIntent = new Intent(getApplicationContext(), PartyView.class);
                promoteIntent.putExtra("name", name);
                promoteIntent.putExtra("id", fireid);



                promoteIntent.putExtra("UserLatt", lattitude);
                promoteIntent.putExtra("UserLong", longitude);
                startActivity(promoteIntent);










            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.searchForParties);
        //Log.d("TAG", item.getActionView().toString());
        SearchView searchView = (SearchView) item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                simpleAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);


    }
}
