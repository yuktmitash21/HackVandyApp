package com.example.yuktmitash.whatsthemove;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.util.List;

public class PeopleCounter extends AppCompatActivity {
    private Button updater;
    private Button takePic;
    private Button home;
    private ImageView imageView;
    private boolean scanned = false;
    private int numFaces;
    private DatabaseReference ref;
    private FirebaseVisionLabelDetectorOptions options;
    private ProgressDialog progressDialog;
    private TextView textView;

    private static final int CAMERA_REQUEST_CODE = 1;
    //on data change listener rather than single event


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_counter);
        home = findViewById(R.id.Homefromdetection1);
        takePic = findViewById(R.id.picTaker1);
        updater = findViewById(R.id.UpdateParty1);
        imageView = findViewById(R.id.peoplepic1);
        textView = findViewById(R.id.peoplenumber1);
        progressDialog = new ProgressDialog(PeopleCounter.this);

       options = new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //temporary
                startActivity(new Intent(getApplicationContext(), realTimeFaceDetection.class));
            }
        });

        updater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!scanned) {
                    Toast.makeText(PeopleCounter.this,"Please perform a scan before updating party!", Toast.LENGTH_SHORT).show();
                } else {
                    ref = FirebaseDatabase.getInstance().getReference();
                    String id = getIntent().getStringExtra("id");
                    ref.child("parties").child(id).child("people").setValue(numFaces);
                    Toast.makeText(PeopleCounter.this, "Party Updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, CAMERA_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            if (bitmap != null) {
                progressDialog.dismiss();
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                        .getVisionLabelDetector(options);
                Task<List<FirebaseVisionLabel>> result = detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
                        StringBuilder x = new StringBuilder("" + firebaseVisionLabels.size() + "\n");
                        for (FirebaseVisionLabel label : firebaseVisionLabels) {
                            x.append("\n" + label.getLabel());
                            Log.d("IMPORTANT LABEL", label.getLabel());
                        }
                        textView.setText(x);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PeopleCounter.this, "Something went wrong...", Toast.LENGTH_SHORT).show();

                    }
                });



            }



        }
    }
}
