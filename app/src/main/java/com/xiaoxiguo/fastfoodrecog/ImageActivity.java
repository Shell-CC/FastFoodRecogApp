package com.xiaoxiguo.fastfoodrecog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

import java.io.IOException;

public class ImageActivity extends Activity implements View.OnTouchListener {

    private String imagepath;
    FoodImage foodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Food Image");
        setContentView(R.layout.activity_image);
        ImageView imageView = (ImageView) findViewById(R.id.food_image);

        // Get image
        imagepath = getIntent().getStringExtra("filepath");
        Log.v("Procedure", "Reading image from " + imagepath);
        foodImage = new FoodImage();
        try {
            foodImage.read(imagepath);
            Bitmap bitmap = foodImage.toBitmap();
            Toast.makeText(this, "Press top left and bottom right of the foreground image", Toast.LENGTH_LONG).show();
            imageView.setImageBitmap(bitmap);
//            predictFoodId();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_LONG).show();
        }

    }




    private int predictFoodId() {
//        Mat backgroundMask = foodImage.extractBackgroundMask();
        Mat features = foodImage.extractFeatures(FeatureDetector.FAST, DescriptorExtractor.ORB);
        Log.v("Procedure", "Extracting features: " + features.size());
        try {
//            Image imageWithMask = foodImage.getImageWithMask();
            Image imageWithFeats = foodImage.getImageWithFeatures();
//            imageWithMask.write(imagepath.substring(0, imagepath.length() - 4) + "BACK.png");
            imageWithFeats.write(imagepath.substring(0, imagepath.length() - 4) + "FEAT.png");
        } catch (FoodImage.EmptyContentException e1) {
            Toast.makeText(this, "No features saved", Toast.LENGTH_LONG).show();
        } catch (IOException e2) {
            Toast.makeText(this, "Error writing", Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
