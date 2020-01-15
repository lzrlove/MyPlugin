package com.example.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

/**
 * Create by lizongrun
 * Create Date 2020/1/15 10:17
 */
public class Loadutil {
    private final static String apkPath = "/sdcard/1/plugin-debug.apk";
    private static Resources sResources;

    public static Resources getResource(Context context){
        if (sResources == null){
            sResources = loadResource(context);
        }
        return sResources;
    }
    private static Resources loadResource(Context context){
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager,apkPath);
            Resources resources = context.getResources();
            return new Resources(assetManager,resources.getDisplayMetrics(),resources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
