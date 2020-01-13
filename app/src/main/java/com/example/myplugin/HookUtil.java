package com.example.myplugin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookUtil {
    private static final String TARGET_INTENT = "target_intent";
    public static void hookAMS() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityManager");
            Field singletonFiled = clazz.getDeclaredField("IActivityManagerSingleton");
            singletonFiled.setAccessible(true);
            Object singleton = singletonFiled.get(null);

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

                            if ("startActivity".equals(method.getName())){
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
                                proxyIntent.putExtra(TARGET_INTENT,intent);
                                args[index] = proxyIntent;
                            }
                            return method.invoke(mInstance,args);
                        }
                    });
            mInstanceFiled.set(singleton,proxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler(){

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
                    switch (message.what){
                        case 100:
                            try {
                                Field intentFiled  = message.obj.getClass().getDeclaredField("intent");
                                intentFiled.setAccessible(true);
                                //拿到代理的intent
                                Intent proxyIntent = (Intent) intentFiled.get(message.obj);
                                //拿到之前保存在代理里面的插件的intent
                                Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                //替换回来
                                proxyIntent.setComponent(intent.getComponent());
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
