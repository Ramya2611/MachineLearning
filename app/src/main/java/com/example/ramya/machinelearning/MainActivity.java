package com.example.ramya.machinelearning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    private final int TEXT_RECO_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void textReco(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,TEXT_RECO_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==TEXT_RECO_REQ_CODE){
            if (resultCode==RESULT_OK){
                Bitmap photo = (Bitmap)data.getExtras().get("data");
                textRecognisation(photo);
            }else if (resultCode==RESULT_CANCELED){
                Toast.makeText(this,"Operation cancelled by the User",Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this,"Failed to capture Image",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void textRecognisation(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        for (FirebaseVisionText.Block block: result.getBlocks()){
                            Rect boundingBox = block.getBoundingBox();
                            Point[] cornerPoints = block.getCornerPoints();
                            String text = block.getText();
                            //Toast.makeText(MainActivity.this,""+text,Toast.LENGTH_LONG).show();
                            Log.d(TAG,"text"+text);
                            Toast.makeText(MainActivity.this,""+text,Toast.LENGTH_LONG).show();


                            for (FirebaseVisionText.Line line: block.getLines()){
                                for (FirebaseVisionText.Element element: line.getElements()){
                                   // Toast.makeText(MainActivity.this,"Element:"+element.getText(),Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                        // Task completed successfully
                        // ...
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Failed to recognize Image",Toast.LENGTH_SHORT).show();

                                // Task failed with an exception
                                // ...
                            }
                        });


    }
}
