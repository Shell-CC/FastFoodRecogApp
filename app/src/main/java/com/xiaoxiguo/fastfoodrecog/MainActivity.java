package com.xiaoxiguo.fastfoodrecog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.core.Core;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("opencv", "Load opencv " + Core.VERSION);
    }
}
