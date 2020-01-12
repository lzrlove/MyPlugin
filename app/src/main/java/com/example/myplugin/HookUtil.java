package com.example.myplugin;

import android.content.Intent;

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
            Object mInstance = mInstanceFiled.get(singleton);


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
                            return null;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
