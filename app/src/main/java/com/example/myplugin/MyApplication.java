package com.example.myplugin;

import android.app.Application;
import android.content.res.Resources;

/**
 * Create by lizongrun
 * Create Date 2020/1/9 14:24
 */
public class MyApplication extends Application {

    private Resources mResources;

    @Override
    public void onCreate() {
        super.onCreate();
        LoaderUtil.load(this);
        mResources = LoaderUtil.loadResource(this);
        HookUtil.hookAMS();
        HookUtil.hookHandler();
    }

    @Override
    public Resources getResources() {

        return mResources == null ? super.getResources() : mResources;
    }
}
