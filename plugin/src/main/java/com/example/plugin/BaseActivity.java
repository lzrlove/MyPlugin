package com.example.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import java.lang.reflect.Field;

/**
 * Create by lizongrun
 * Create Date 2020/1/15 10:21
 */
public class BaseActivity extends Activity {

    public Context mContext;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Resources resource = Loadutil.getResource(getApplication());
//        mContext = new ContextThemeWrapper(getBaseContext(), 0);
//        Class<? extends Context> clazz = mContext.getClass();
//        try {
//            Field mResourceFiled = clazz.getDeclaredField("mResources");
//            mResourceFiled.setAccessible(true);
//            mResourceFiled.set(mContext,resource);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public Resources getResources() {
        if (getApplication() != null && getApplication().getResources() != null){
            return getApplication().getResources();
        }
        return super.getResources();
    }
}
