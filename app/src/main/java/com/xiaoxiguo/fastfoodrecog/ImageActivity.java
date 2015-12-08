package com.xiaoxiguo.fastfoodrecog;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;

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
                imageView.setDrawable();
                break;
            case R.id.confirm_item:
                imageView.setNotDrawble();
                AssetManager manager = getAssets();
                // try to load dictionary
                Dictionary dictionary;
                try {
                    for (String file : manager.list("Dictionary/")) {
                        Log.d("Assets", "Dictionary found: " + file);
                    }
                    Reader reader = new InputStreamReader(manager.open("Dictionary/rest1.txt"));
                    dictionary = Dictionary.load(reader);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load dictionary", Toast.LENGTH_LONG).show();
                    break;
                }

                // try to load classifier
                Classifier classifier = new Classifier();
                try {
                    String path = copyToCache(manager.open("Classifier/svm1.xml"));
                    classifier.load(path);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load classifier", Toast.LENGTH_LONG).show();
                    break;
                }

                // predict
                int label = imageView.learning(dictionary, classifier);
                Toast.makeText(this, "Food ID: " + label, Toast.LENGTH_LONG).show();

                final DatabaseReader db = new DatabaseReader(this);
                try {
                    db.openDatabase();
                } catch (SQLException e) {
                    Log.e("Databse", "error opening");
                }

                String[] result = db.readDb(1, label);
                Toast.makeText(this, "Food name: " + result[0], Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Calorie: " + result[1], Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    private String copyToCache(InputStream in) throws IOException{
        Log.d("loadDictionary", "Success to open assets");
        File outFile = new File(getCacheDir(), "classifier");
        OutputStream out = new FileOutputStream(outFile);
        Log.e("loadDictionary", "Success to find an internal cache: " + outFile.getPath());
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        Log.d("loadDictionary", "Success to copy file");
        in.close();
        Log.d("loadDictionary", "Success to close input");
        out.close();
        Log.d("loadDictionary", "Success to close output");
        return outFile.getPath();
    }

}

