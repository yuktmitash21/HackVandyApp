package com.example.yuktmitash.whatsthemove;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class realTimeFaceDetection extends AppCompatActivity {
    private CameraView cameraView;
    private GraphicOverlay graphicOverlay;
    private Button detecter;

   ProgressDialog waitingDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_face_detection);

        cameraView = findViewById(R.id.camera_view);
        graphicOverlay = findViewById(R.id.helper_detect);
        detecter = findViewById(R.id.detection_button);
        waitingDialog = new ProgressDialog(realTimeFaceDetection.this);
        waitingDialog.setMessage("Loading camera...");
        //waitingDialog.show();

        detecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
               waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                runFaceDetector(bitmap);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void runFaceDetector(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions optons = new FirebaseVisionFaceDetectorOptions.Builder().
                setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE).
                setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS).
                setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS).
                enableTracking().build();
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(optons);
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                processFaces(firebaseVisionFaces);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(realTimeFaceDetection.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void processFaces(List<FirebaseVisionFace> firebaseVisionFaces) {
        int count = firebaseVisionFaces.size();
        for (FirebaseVisionFace face: firebaseVisionFaces) {
            Rect bounds = face.getBoundingBox();
            Box box = new Box(realTimeFaceDetection.this, bounds);
          //  RectOverlay rect = new RectOverlay(graphicOverlay, bounds);
           // graphicOverlay.add(rect);
            addContentView(box, new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));

        }
        waitingDialog.dismiss();
        Toast.makeText(realTimeFaceDetection.this, String.format("Detected %d faces in room", count), Toast.LENGTH_SHORT).show();

    }

    public class Box extends View {
        private Paint paint = new Paint();
        private Rect rect;
        Box(Context context, Rect rect) {
            super(context);
            this.rect = rect;
        }

        @Override
        protected void onDraw(Canvas canvas) { // Override the onDraw() Method
            super.onDraw(canvas);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);

            //center
            int x0 = rect.left;
            int y0 = rect.top;
            int dx = rect.right;
            int dy = rect.bottom;
            //draw guide box
            canvas.drawRect(x0, y0, dx, dy, paint);
        }
    }
}
