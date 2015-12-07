package com.xiaoxiguo.fastfoodrecog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private DrawImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Procedure", "ImageActivity onCreate");
        setTitle("Food Image");
        setContentView(R.layout.activity_image);
        imageView = (DrawImageView) findViewById(R.id.food_image);

        // Get image
        Uri imageUri = getIntent().getParcelableExtra("imageUri");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            Log.v("Procedure", "Successfully load image " + imageUri.toString());
            imageView.setFoodImage(bitmap);
            bitmap.recycle();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading image!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_item:
                break;
            case R.id.confirm_item:
                try {
                    Bitmap fgImage = imageView.getGrabCuttedImage();
                    Intent infoIntent = new Intent(ImageActivity.this, LearningActivity.class);
//                    infoIntent.putExtra("fgImage", fgImage);
                    startActivity(infoIntent);
                } catch (FoodImage.EmptyContentException e) {
                    Toast.makeText(this, "Please grab-cut first!", Toast.LENGTH_LONG);
                }
                break;
        }
        return true;
    }
}

