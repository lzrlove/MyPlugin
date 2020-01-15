package com.example.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("lzr", "onCreate: 我是插件的Activity111");
        String intent = getIntent().getStringExtra("lzr");
        Log.e("lzr","传值："+intent);
        setContentView(R.layout.activity_main);
//        View inflate = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
//        setContentView(inflate);
    }

}
