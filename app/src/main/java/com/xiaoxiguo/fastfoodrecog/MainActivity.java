package com.xiaoxiguo.fastfoodrecog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static {
        // load opencv library
        System.loadLibrary("opencv_java");
        Log.v("opencv", "Loaded opencv " + Core.VERSION);
    }

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 101;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate");

        // Listen to connect to choose restaurant
        Button restButton = (Button) findViewById(R.id.choose_rest_button);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imageIntent, "Choose A Food Image"),
                        CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        // Listen to connect to camera
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create imageIntent to take a photo
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // create a file to save the image
                try {
                    fileUri = Uri.fromFile(createImageFile(MEDIA_TYPE_IMAGE));
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // start the image capture Intent
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "No SD card to save image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // create database.
        Log.d("database", "creating...");
        final DatabaseReader db = new DatabaseReader(MainActivity.this);
        try {
            db.createDatabase();
        } catch (IOException e) {
            Log.e("database", "error creating");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent restIntent = new Intent(MainActivity.this, ChooseRestActivity.class);
                restIntent.putExtra("imageUri", fileUri);
                startActivity(restIntent);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fileUri = data.getData();
                Log.d("Image", "Choose image: " + fileUri.toString());
                Intent restIntent = new Intent(MainActivity.this, ChooseRestActivity.class);
                restIntent.putExtra("imageUri", fileUri);
                startActivity(restIntent);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                Toast.makeText(this, "Image choose cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Image choose failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Created by Harris.
     *
     * Create a file in SD card to save image file.
     * @param type Type of the file.
     * @return The file created.
     * @throws IOException If creating file failed.
     */
    private File createImageFile(int type) throws IOException {
        // Create a dir to save files it not exists.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "FastFoodRecognizer");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                throw new IOException("Failed to create directory");
            }
        }
        Log.v("Image", "Image will be saved to " + mediaStorageDir.getPath());

        // Create a file for image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            String filename = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png";
            mediaFile = new File(filename);
        } else {
            throw new IOException("Failed to create image file");
        }
        Log.v("Image", "Image will be saved as " + mediaFile.getName());
        return mediaFile;
    }
}
