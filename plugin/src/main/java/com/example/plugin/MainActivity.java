package com.example.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("lzr", "onCreate: 我是插件的Activity");
        String intent = getIntent().getStringExtra("lzr");
        Log.e("lzr","传值："+intent);
    }
}
