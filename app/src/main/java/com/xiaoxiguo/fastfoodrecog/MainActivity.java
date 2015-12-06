package com.xiaoxiguo.fastfoodrecog;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.core.Core;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
        Log.i("opencv", "Loaded opencv " + Core.VERSION);
    }

    /**
     * Created by Harris.
     * @param savedInstanceState
     */
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
//                Toast.makeText(MainActivity.this, "You clicked Restaurant", Toast.LENGTH_SHORT).show();
                Intent secIntent = new Intent(MainActivity.this, ChooseRestActivity.class);
                startActivity(secIntent);
            }
        });

        // Listen to connect to camera
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.hint_text, Toast.LENGTH_SHORT).show();
                // create imageIntent to take a photo
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(imageIntent, 0);
            }
        });
    }
}
