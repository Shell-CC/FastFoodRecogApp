package com.xiaoxiguo.fastfoodrecog;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class LearningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Food Info");
        setContentView(R.layout.activity_learning);

        // Get bitmap from intent;
//        Bitmap fgImage = (Bitmap) getIntent().getParcelableExtra("fgImage");
//        ImageView imageView = (ImageView) findViewById(R.id.fg_image);
//        imageView.setImageBitmap(fgImage);
    }
}
