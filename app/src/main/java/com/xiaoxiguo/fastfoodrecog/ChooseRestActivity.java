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
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChooseRestActivity extends AppCompatActivity {

    private RadioGroup restList;
    private int restId = -1;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.choose_rest_title);
        setContentView(R.layout.activity_choose_rest);

        restList = (RadioGroup) findViewById(R.id.restList);
        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restId == -1) {
                    Toast.makeText(ChooseRestActivity.this, "Please select one restaurant!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("Procedure", "Confirmed choosing restaurant " + restId);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // create a file to save the image
                    try {
                        fileUri = Uri.fromFile(createImageFile(MEDIA_TYPE_IMAGE));
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        // start the image capture Intent
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    } catch (IOException e) {
                        Toast.makeText(ChooseRestActivity.this, "No space to save image.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onRadioButtonClicked(View v) {
        restId = restList.getCheckedRadioButtonId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent imageIntent = new Intent(ChooseRestActivity.this, ImageActivity.class);
                imageIntent.putExtra("imageUri", fileUri);
                startActivity(imageIntent);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Why am I here", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create a File for saving an image
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
        Log.v("Procedure", "Image will be saved to " + mediaStorageDir.getPath());

        // Create a file for image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            String filename = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png";
            mediaFile = new File(filename);
        } else {
            throw new IOException("Failed to create image file");
        }
        Log.v("Procedure", "Image will be saved as " + mediaFile.getName());
        return mediaFile;
    }
}
