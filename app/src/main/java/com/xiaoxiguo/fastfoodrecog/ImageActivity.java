package com.xiaoxiguo.fastfoodrecog;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    DrawImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Procedure", "ImageActivity onCreate");
        setTitle("Food Image");
        setContentView(R.layout.activity_image);
        imageView = (DrawImageView) findViewById(R.id.food_image);

        // Get image
        Uri imageUri = getIntent().getParcelableExtra("imageUri");
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            Log.v("Procedure", "Successfully load image " + imageUri.toString());
            imageView.setFoodImage(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Error loading image!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}

