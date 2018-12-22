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
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Arrays;
import java.util.Locale;

public class AlternateList extends AppCompatActivity {
    private ArrayList<Bitmap> bitmaps;
    private ArrayList<String> names;
    private ArrayList<String> messages;
    private ListView listView;
    private static final int NECESSARY_PROMOTIONS = 1;
    private Spinner spinner;

    private ArrayList<String> holder = new ArrayList<>();
    private ArrayList<String> holder2 = new ArrayList<>();
    private ArrayList<String> holder3 = new ArrayList<>();


    private ArrayList<Party> partyArrayList;
    private Object[] array;



    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String userid;
    private StorageReference storageReference;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private double longitude;
    private double lattitude;

    private CustomAdapter MycustomAdapter;
    private ArrayList<String> IdsOfPictures;

    private static final double MINIMUM_DISTANCE = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_list);
        listView = findViewById(R.id.dynamic2);
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();

        bitmaps = new ArrayList<>();
        names = new ArrayList<>();
        messages = new ArrayList<>();
        IdsOfPictures = new ArrayList<>();
        spinner = findViewById(R.id.spinnerOfSorting);
        String[] sorting = new String[]{"Sort by Distance", "Sort by Promotions", "Sort by Ratings", "Sort by Number of People"};
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(AlternateList.this, android.R.layout.simple_spinner_item, sorting);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (array != null) {
                    if (spinner.getSelectedItem().equals("Sort by Promotions")) {
                        for (Object p : array) {
                            Party pp = (Party) (p);
                            pp.setSortBy("promotions");
                        }
                    } else if (spinner.getSelectedItem().equals("Sort by Distance")) {
                        for (Object p : array) {
                            Party pp = (Party) (p);
                            pp.setSortBy("distance");
                        }
                    } else if (spinner.getSelectedItem().equals("Sort by Number of People")) {
                        for (Object p : array) {
                            Party pp = (Party) (p);
                            pp.setSortBy("people");
                        }
                    } else {
                            for (Object p : array) {
                                Party pp = (Party) (p);
                                pp.setSortBy("rating");
                            }
                    }

                   Arrays.sort(array);
                    Party logger = (Party) array[0];
                    //Log.d("SortedARRAY", logger.getName());
                   // bitmaps = new ArrayList<Bitmap>();
                    messages = new ArrayList<String>();
                    names = new ArrayList<String>();
                    IdsOfPictures = new ArrayList<>();



                    for (Object pp : array) {
                        final Party p = (Party) pp;
                        names.add(p.getName());
                        Log.d("SIXENAMES", "" + names.size());
                        if (p.getDistance() <= MINIMUM_DISTANCE) {
                            messages.add("Promotions: " + p.getPromotions() + " You are in range!");
                        } else {
                            messages.add("Promotions: " + p.getPromotions() + " You are not in range :(");
                        }
                        IdsOfPictures.add(p.getFireid());
                        MycustomAdapter.notifyDataSetChanged();
                       /* storageReference.child("photos").child(p.getFireid()).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                if (bytes != null) {

                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    DisplayMetrics dm = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 300, 300, false);

                                    bitmaps.add(smallMarker);
                                    MycustomAdapter.notifyDataSetChanged();


                                    // MycustomAdapter.notifyDataSetChanged();
                                    Log.d("SIZEBIT", "" + bitmaps.size());
                                }


                            }
                        });*/



                       // Log.d("SIZEMESSAGES", "" + messages.size());


                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //on data change listener rather than single event

            }
        });








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
            if (ActivityCompat.checkSelfPermission(AlternateList.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AlternateList.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
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

        reference.child("parties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                partyArrayList = new ArrayList<Party>();
                for (DataSnapshot d : data) {

                    Party p = d.getValue(Party.class);

                    float[] arr = new float[10];
                    Location.distanceBetween(lattitude, longitude, p.getLattitude(), p.getLongitude(), arr);
                    float distanceBetween = arr[0];
                    float trueDistance = distanceBetween * (float) 0.000621371;


                    p.setDistance(trueDistance);
                    boolean promoteOnly = getIntent().getBooleanExtra("Promotable", false);
                    if (!promoteOnly || p.getDistance() < MINIMUM_DISTANCE) {
                        partyArrayList.add(p);
                    }
                   p.setSortBy("distance");
                }
                array = partyArrayList.toArray();
                Log.d("SIXE", "" + partyArrayList.size());
                Arrays.sort(array);
                for (Object pp: array) {
                    final Party p = (Party) pp;
                    names.add(p.getName());
                    Log.d("SIXENAMES", "" + names.size());
                    if ((double)p.getDistance() <= MINIMUM_DISTANCE) {
                        messages.add("Promotions: " + p.getPromotions() + " You are in range!");
                    } else {
                        messages.add("Promotions: " + p.getPromotions() + " You are not in range :(");
                    }
                    IdsOfPictures.add(p.getFireid());
                    MycustomAdapter.notifyDataSetChanged();
                    /*storageReference.child("photos").child(p.getFireid()).getBytes( 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            if (bytes != null) {

                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                DisplayMetrics dm = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(dm);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 300, 300, false);

                                bitmaps.add(smallMarker);
                                    MycustomAdapter.notifyDataSetChanged();

                                // MycustomAdapter.notifyDataSetChanged();
                                Log.d("SIZEBIT", "" + bitmaps.size());

                            }
                        }*/
                   // });




                    //Log.d("SIZEMESSAGES", "" + messages.size());





                }
                Log.d("SIZEBITAFTER", "" + bitmaps.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        MycustomAdapter = new CustomAdapter();
        listView.setAdapter(MycustomAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = names.get(i);
                Party mine = null;
                for (Party p: partyArrayList) {
                    if (p.getName().equals(name)) {
                        mine = p;

                    }
                }
                String fireid = mine.getFireid();
                Intent promoteIntent = new Intent(getApplicationContext(), PartyView.class);
                promoteIntent.putExtra("name", name);
                promoteIntent.putExtra("id", fireid);



                promoteIntent.putExtra("UserLatt", lattitude);
                promoteIntent.putExtra("UserLong", longitude);
                startActivity(promoteIntent);
            }
        });










    }

    class CustomAdapter extends BaseAdapter {


        @Override
        public int getCount() {

                return messages.size();
        }

        @Override
        public Object getItem(int i) {
            //return array[i];
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {




            Log.d("SIZEBITAFTER", "" + bitmaps.size());
            Log.d("Othersizeafter", "" + names.size());

            view = getLayoutInflater().inflate(R.layout.othercustom_layout, null);

            final ImageView imageView = view.findViewById(R.id.imageView6);
            TextView textView = view.findViewById(R.id.textView4);
            TextView otherTextView = view.findViewById(R.id.textView5);


            //if (bitmaps.size() != 0 && bitmaps != null) {




                //imageView.setImageBitmap(bitmaps.get(i - 1));
                if (holder!= null && holder.size() <= i) {
           // if (bitmaps.size() == names.size()) {
                textView.setText(names.get(i));
                otherTextView.setText(messages.get(i));
           //     imageView.setImageBitmap(bitmaps.get(i));
                storageReference.child("photos").child(IdsOfPictures.get(i)).getBytes( 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        if (bytes != null) {

                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(dm);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 300, 300, false);
                            imageView.setImageBitmap(smallMarker);

                           /* bitmaps.add(smallMarker);
                            MycustomAdapter.notifyDataSetChanged();*/

                            // MycustomAdapter.notifyDataSetChanged();
                            Log.d("SIZEBIT", "" + bitmaps.size());

                        }
                    }
                });
           }
                else {

                    textView.setText(holder.get(i));
                    otherTextView.setText(holder3.get(i));
                    storageReference.child("photos").child(holder2.get(i)).getBytes( 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            if (bytes != null) {

                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                DisplayMetrics dm = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(dm);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bm, 300, 300, false);
                                imageView.setImageBitmap(smallMarker);

                           /* bitmaps.add(smallMarker);
                            MycustomAdapter.notifyDataSetChanged();*/

                                // MycustomAdapter.notifyDataSetChanged();
                                Log.d("SIZEBIT", "" + bitmaps.size());

                            }
                        }
                    });
                }
           // }


            return view;

        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            holder.clear();
            holder2.clear();
            holder3.clear();

            if (charText.length() == 0) {
                holder.clear();
                holder2.clear();
                holder3.clear();
            } else {
                for (int i = 0; i < names.size(); i++) {
                    String wp = names.get(i);
                    if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                        holder.add(wp);
                        holder2.add(IdsOfPictures.get(i));
                        holder3.add(messages.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        }
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
                MycustomAdapter.filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);


    }


}
