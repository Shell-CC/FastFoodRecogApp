package com.xiaoxiguo.fastfoodrecog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    //public Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate execute");

        // connect to second activity
        Button button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "You clicked Restaurant", Toast.LENGTH_SHORT).show();
                Intent secIntent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(secIntent);
            }
        });

        // connect to camera
        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "You clicked Camera", Toast.LENGTH_SHORT).show();

                // create imageIntent to take a photo
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // create image directory to save photo
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "/MyImages");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }
                File image = new File(imagesFolder, "FoodImage.png");
                Uri fileUri = Uri.fromFile(image);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(imageIntent, 0);

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
