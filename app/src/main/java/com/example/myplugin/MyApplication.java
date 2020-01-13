package com.example.myplugin;

import android.app.Application;

/**
 * Create by lizongrun
 * Create Date 2020/1/9 14:24
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LoaderUtil.load(this);
        HookUtil.hookAMS();
        HookUtil.hookHandler();
    }
}
