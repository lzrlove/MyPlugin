package com.example.myplugin;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class HookUtil {
    private static final String TARGET_INTENT = "target_intent";

    public static void hookAMS() {
        try {
            //大于26的版本
            Object singleton = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Class<?> clazz = Class.forName("android.app.ActivityManager");
                Field singletonFiled = clazz.getDeclaredField("IActivityManagerSingleton");
                singletonFiled.setAccessible(true);
                singleton = singletonFiled.get(null);
            } else {
                Class<?> clazz = Class.forName("android.app.ActivityManagerNative");
                Field singletonFiled = clazz.getDeclaredField("gDefault");
                singletonFiled.setAccessible(true);
                singletonFiled.get(null);
            }


            Class<?> singtonClass = Class.forName("android.util.Singleton");
            Field mInstanceFiled = singtonClass.getDeclaredField("mInstance");
            mInstanceFiled.setAccessible(true);
            final Object mInstance = mInstanceFiled.get(singleton);


            //要代理的对象
            Class<?> iActivityClass = Class.forName("android.app.IActivityManager");

            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                            if ("startActivity".equals(method.getName())) {
                                int index = 0;
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] instanceof Intent) {
                                        index = i;
                                        break;
                                    }
                                }
                                Intent intent = (Intent) args[index];
                                Intent proxyIntent = new Intent();
                                proxyIntent.setClassName("com.example.myplugin",
                                        "com.example.myplugin.ProxyActivity");
                                proxyIntent.putExtra(TARGET_INTENT, intent);
                                args[index] = proxyIntent;
                            }
                            return method.invoke(mInstance, args);
                        }
                    });
            mInstanceFiled.set(singleton, proxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadFiled = clazz.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadFiled.setAccessible(true);
            Object activityThread = sCurrentActivityThreadFiled.get(null);

            Field mHFiled = clazz.getDeclaredField("mH");
            mHFiled.setAccessible(true);
            Object mH = mHFiled.get(activityThread);

            //new 一个callback对象去截取handleMessage流程
            Class<?> handlerClass = Class.forName("android.os.Handler");
            Field mCallbackFiled = handlerClass.getDeclaredField("mCallback");
            mCallbackFiled.setAccessible(true);
            mCallbackFiled.set(mH, new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message message) {
                    switch (message.what) {
                        case 100:
                            try {
                                Field intentFiled = message.obj.getClass().getDeclaredField("intent");
                                intentFiled.setAccessible(true);
                                //拿到代理的intent
                                Intent proxyIntent = (Intent) intentFiled.get(message.obj);
                                //拿到之前保存在代理里面的插件的intent
                                Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                //替换回来
                                proxyIntent.setComponent(intent.getComponent());
//                                intentFiled.set(message.obj, intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 159:
                            try {
                                Class<?> clazz = Class.forName("android.app.servertransaction.ClientTransaction");
                                Field mActivityCallbacksFiled = clazz.getDeclaredField("mActivityCallbacks");
                                mActivityCallbacksFiled.setAccessible(true);
                                List activityCallbacks = (List) mActivityCallbacksFiled.get(message.obj);
                                for (int i = 0; i < activityCallbacks.size(); i++) {
                                    if (activityCallbacks.get(i).getClass().getName().equals("android.app.servertransaction.LaunchActivityItem")){
                                        Object activityItem = activityCallbacks.get(i);
                                        Field mIntentFiled = activityItem.getClass().getDeclaredField("mIntent");
                                        mIntentFiled.setAccessible(true);
                                        Intent proxyIntent = (Intent) mIntentFiled.get(activityItem);
                                        Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                        if (intent != null){
                                            mIntentFiled.set(activityItem,intent);
                                        }
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
