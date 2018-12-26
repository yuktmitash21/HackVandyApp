package com.example.yuktmitash.whatsthemove;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.HashMap;

public class AlternateChat extends AppCompatActivity {
    private Button button;
    private EditText editText;
    private String partyId;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private FirebaseUser firebaseUser;
    private User user;
    private ListView listView;
    private ArrayList<String> messages;
    private ArrayList<Bitmap> bitmaps;
    private CustomAdapter customAdapter;



    private String partyName;
    private ViewHolder viewHolder;

    private long numberOfMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_chat);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        partyName = getIntent().getStringExtra("partyName");
        partyId = getIntent().getStringExtra("partyid");
        editText = findViewById(R.id.SendingMessage);
        listView = findViewById(R.id.dynamic);
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);



      viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView) listView.findViewById(R.id.imageView5);
        viewHolder.textView = (TextView) listView.findViewById(R.id.textView3);
       listView.setTag(viewHolder);


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
       reference.child("messages").child("instances").child(partyId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        reference.child("messages").child(partyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                if (messages == null && bitmaps == null) {
                    messages = new ArrayList<>();
                    bitmaps = new ArrayList<>();
                }
                for (DataSnapshot d : data) {
                    HashMap<String, String> myMap= (HashMap<String, String>) d.getValue();
                    String username = (String) myMap.keySet().toArray()[0];
                    String message = (String) myMap.values().toArray()[0];
                    //gives a username with the message
                    String line = username+ ": " + message;
                    messages.add(line);
                    //x.append(line +"\n");

                    storageReference.child("users").child(username).getBytes( 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            if (bytes != null) {
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                DisplayMetrics dm = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(dm);

                                bitmaps.add(bm);
                            }
                        }
                    });


                }
             //   textView.setText(x.toString());
                customAdapter.notifyDataSetChanged();
                //listView.setSelection(listView.getMaxScrollAmount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void sendMessage(View view) {
        //final int instances;

                String id = user.getUserName();
                numberOfMessages = numberOfMessages + 1;
                reference.child("messages").child("instances").child(partyId).setValue(numberOfMessages);
                reference.child("messages").child(partyId).child(numberOfMessages + "").child(id).setValue(editText.getText().toString());
                editText.setText("");

        reference.child("messages").child(partyId).child("" + numberOfMessages).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (messages == null && bitmaps == null) {
                    messages = new ArrayList<>();
                    bitmaps = new ArrayList<>();
                }
                customAdapter.notifyDataSetChanged();
                HashMap<String, String> myMap= (HashMap<String, String>) dataSnapshot.getValue();
                String username = (String) myMap.keySet().toArray()[0];
                String mess = (String) myMap.values().toArray()[0];

                String line = username+ ": " + mess;
                messages.add(line);

                storageReference.child("users").child(username).getBytes( 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        if (bytes != null) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(dm);

                            bitmaps.add(bm);
                        }
                    }
                });

                customAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setSelection(listView.getMaxScrollAmount());
            }


            class CustomAdapter extends BaseAdapter {


                @Override
                public int getCount() {
                    if (messages == null) {
                        return -1;
                    } else {
                        return messages.size();
                    }
                }

                @Override
                public Object getItem(int i) {
                    return null;
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    view = getLayoutInflater().inflate(R.layout.custom_layout, null);

                    ImageView imageView = view.findViewById(R.id.imageView5);
                    TextView textView = view.findViewById(R.id.textView3);


                    if (bitmaps != null && bitmaps.size() > i) {
                        imageView.setImageBitmap(bitmaps.get(i));
                        textView.setText(messages.get(i));
                    }
                    return view;

                }
            }

            static class ViewHolder {
        private ImageView imageView;
        private TextView textView;

            }


}
