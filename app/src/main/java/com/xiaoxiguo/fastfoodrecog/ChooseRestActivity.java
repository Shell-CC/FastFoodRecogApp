package com.xiaoxiguo.fastfoodrecog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChooseRestActivity extends AppCompatActivity {

    private RadioGroup restList;

    private Uri fileUri;
    private int restId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("Activity", "ChooseRestActivity onCreate");
        super.onCreate(savedInstanceState);
        setTitle(R.string.choose_rest_title);
        setContentView(R.layout.activity_choose_rest);

        // Get image uri and pass to the next activity
        fileUri = getIntent().getParcelableExtra("imageUri");
        Log.v("Procedure", "Get image uri: " + fileUri.toString());

        restList = (RadioGroup) findViewById(R.id.restList);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restId == -1) {
                    Toast.makeText(ChooseRestActivity.this, "Please choose a restaurant first", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("Procedure", "Confirmed choosing restaurant " + restId);

                    // start the activity to process image.
                    Intent imageIntent = new Intent(ChooseRestActivity.this, ImageActivity.class);
                    imageIntent.putExtra("restId", restId);
                    imageIntent.putExtra("imageUri", fileUri);

                    startActivity(imageIntent);
                }
            }
        });
    }

    public void onRadioButtonClicked(View v) {
        switch (restList.getCheckedRadioButtonId()) {
            case R.id.rest1:
                restId = 1;
                break;
            case R.id.rest2:
                restId = 2;
                break;
            case R.id.rest3:
                restId = 3;
                break;
            case R.id.rest4:
                restId = 4;
                break;
            case R.id.rest5:
                restId = 5;
                break;
            case R.id.rest6:
                restId = 6;
                break;
            case R.id.rest7:
                restId = 7;
                break;
            case R.id.rest8:
                restId = 8;
                break;
            case R.id.rest9:
                restId = 9;
                break;
            case R.id.rest10:
                restId = 10;
                break;
            case R.id.rest11:
                restId = 11;
                break;
            default:
                Log.e("ChooseRest", "Wrong radio button ID ");
        }
    }


}
