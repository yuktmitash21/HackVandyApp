package com.example.yuktmitash.whatsthemove;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.FaceDetector;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;


public class NumberOfPeople extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private TextView textView;
    private ProgressDialog progressDialog;
    private com.google.android.gms.vision.face.FaceDetector detector;
    private int numFaces;
    private Button home;
    private Button update;
    private Bitmap bitmap;

    private DatabaseReference ref;
    private String id;

    private boolean scanned = false;


    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_of_people);

        progressDialog = new ProgressDialog(NumberOfPeople.this);
        imageView = findViewById(R.id.peoplepic);
        button = findViewById(R.id.picTaker);
        textView = findViewById(R.id.peoplenumber);
        home = findViewById(R.id.Homefromdetection);
        update = findViewById(R.id.UpdateParty);


        detector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        button.setOnClickListener(new View.OnClickListener() {
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!scanned) {
                    Toast.makeText(NumberOfPeople.this, "Please perform scan first", Toast.LENGTH_SHORT).show();
                } else {
                    //progressDialog.setMessage("Updating...");
                    id = getIntent().getStringExtra("id");
                    ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("parties").child(id).child("people").setValue(numFaces);
                    //progressDialog.dismiss();
                    Toast.makeText(NumberOfPeople.this, "Number of people updated!", Toast.LENGTH_SHORT).show();

                }
            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();


            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            detector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                    .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
                    .build();

            if (detector.isOperational() && bitmap != null) {
                Bitmap editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                        .getHeight(), bitmap.getConfig());
                float scale = getResources().getDisplayMetrics().density;
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.rgb(255, 61, 61));
                paint.setTextSize((int) (14 * scale));
                paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3f);
                Canvas canvas = new Canvas(editedBitmap);
                canvas.drawBitmap(bitmap, 0, 0, paint);
                Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
                SparseArray<Face> faces = detector.detect(frame);
                numFaces = faces.size();
                textView.setText("Number of Faces detected: " + numFaces);
                scanned = true;


                progressDialog.dismiss();







        }
    }
}
}
