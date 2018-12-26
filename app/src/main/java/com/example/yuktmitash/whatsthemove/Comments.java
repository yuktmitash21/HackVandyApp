package com.example.yuktmitash.whatsthemove;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class Comments extends AppCompatActivity {
    private TextView comments;
    private ImageView imageView;
    private PlayerView player;
    private SimpleExoPlayer simpleExoPlayer;
    private Button addComments;
    private Button home;
    private Button addVideo;
    private Button faceScan;
    private Button deleteParty;
    private Button changePic;

    private DatabaseReference databaseReference;

    private String partyId;
    private String name;

    private StorageReference reference;
    private ProgressDialog progressDialog;


    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
   private DefaultDataSourceFactory dataSourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comments = findViewById(R.id.textView6);
        imageView = findViewById(R.id.imageView7);
        addComments = findViewById(R.id.addComments);
        home = findViewById(R.id.homeFromView1);
        addVideo = findViewById(R.id.videoAdder);
        faceScan = findViewById(R.id.commentScanner);
        deleteParty = findViewById(R.id.deleter);
        changePic = findViewById(R.id.picChanger);

        progressDialog = new ProgressDialog(Comments.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        name = getIntent().getStringExtra("name");







        player = findViewById(R.id.commentVideo);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        player.setPlayer(simpleExoPlayer);
        player.setVisibility(View.INVISIBLE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        partyId = getIntent().getStringExtra("partyId");
        if (!(userId.equals(partyId))) {
            addComments.setVisibility(View.INVISIBLE);
            addVideo.setVisibility(View.INVISIBLE);
            faceScan.setVisibility(View.INVISIBLE);
            deleteParty.setVisibility(View.INVISIBLE);
            changePic.setVisibility(View.INVISIBLE);
        }

        comments.setText("The host has not yet added comments");
        addComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comIntent = new Intent(getApplicationContext(), TextBox.class);
                comIntent.putExtra("id", partyId);
                comIntent.putExtra("name", name);
                startActivity(comIntent);
            }
        });

        databaseReference.child("comments").child(partyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comm = dataSnapshot.getValue(String.class);
                if (comm != null && !comm.equals("")) {
                    comments.setText(comm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        reference = FirebaseStorage.getInstance().getReference();
        reference.child("photos").child(partyId).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                Toast.makeText(Comments.this, "Sorry... Party just ended", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainScreen.class));

            }
        });

        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "WhatsTheMove??"));
        reference.child("Video").child(partyId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, CAMERA_REQUEST_CODE);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainScreen.class));
            }
        });

        //add Alert Dialog
        deleteParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Comments.this);
                alert.setTitle(name);
                alert.setTitle("Are you sure you want to delete your party?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.child("dates").child(partyId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                databaseReference.child("dates").child(partyId).removeValue();
                                databaseReference.child("parties").child(partyId).child("party").setValue(false);
                                databaseReference.child("messages").child(partyId).removeValue();
                                databaseReference.child("promoted").child(partyId).removeValue();
                                reference.child("Video").child(partyId).delete();
                                progressDialog.dismiss();
                                Toast.makeText(Comments.this, "Party deleted!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainScreen.class));

                                //myStorage.child("photos").child(party.getFireid()).delete();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("NO Date", databaseError.getMessage());
                                Toast.makeText(Comments.this, "Oops.. Something went wrong!", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alert.create().show();


            }
        });

        faceScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanning = new Intent(getApplicationContext(), realTimeFaceDetection.class);
                scanning.putExtra("id", partyId);
                startActivity(scanning);
            }
        });



    }

  /*  @Override
    protected void onStop() {
        super.onStop();

        player.setPlayer(null);
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CAPTURE) {
                progressDialog.setMessage("Uploading Video...");
                progressDialog.show();
                final Uri selectedImageUri = data.getData();
                StorageReference path = reference.child("Video").child(partyId);
                if (selectedImageUri != null) {
                    path.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            imageView.setVisibility(View.INVISIBLE);
                            player.setVisibility(View.VISIBLE);


                            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(selectedImageUri);
                            simpleExoPlayer.prepare(extractorMediaSource);
                            simpleExoPlayer.setPlayWhenReady(true);
                            Toast.makeText(Comments.this, "Video Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Comments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Comments.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                progressDialog.setMessage("Uploading Picture...");
                progressDialog.show();

                final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();

                final StorageReference ref = reference.child("photos").child(partyId);
                ref.putBytes(dataBAOS).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Comments.this, "Image Uploaded!.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Comments.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
                        Log.d("IMAGE UPLOAD", e.getMessage());

                    }
                });

            }
        }
    }


}
