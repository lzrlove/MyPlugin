package com.example.myplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //哎呦不错哟
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getLoader();
//                DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/test.dex",
////                        MainActivity.this.getCacheDir().getAbsolutePath(), null, MainActivity.this.getClassLoader());
////                try {
////                    Class<?> clazz = dexClassLoader.loadClass("com.example.plugin.Test");
////                    Method method = clazz.getMethod("print");
////                    method.invoke(null);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
                try {
                    Class<?> clazz = Class.forName("com.example.plugin.Test");
                    Method method = clazz.getMethod("print");
                    method.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.plugin",
                        "com.example.plugin.MainActivity"));
                intent.putExtra("lzr","123");
                startActivity(intent);
            }
        });
    }

    private void getLoader(){
        ClassLoader classLoader = getClassLoader();
        while(classLoader != null){
            Log.e("lzr","loader:   "+classLoader);
            classLoader = classLoader.getParent();
        }
        Log.e("lzr","Activity:   "+ Activity.class.getClassLoader());
    }

}
