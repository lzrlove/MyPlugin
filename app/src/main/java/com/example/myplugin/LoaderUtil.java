package com.example.myplugin;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Create by lizongrun
 * Create Date 2020/1/9 14:24
 */
public class LoaderUtil {
    private final static String apkPath = "/sdcard/plugin-debug.apk";
    public static void load(Context context){
        try {
            // private final DexPathList pathList;
            Class<?> baseDexClazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = baseDexClazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);

            //private Element[] dexElements;
            Class<?> dexClazz = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexClazz.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            //创建插件的类加载器，通过反射获取插件的dexElements
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath,
                    context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
            Object pluginPathList = pathListField.get(dexClassLoader);
            Object[] pluginElements = (Object[]) dexElementsField.get(pluginPathList);

            //获取宿主的类加载器，通过反射获取宿主的dexElements
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Object hostPathList = pathListField.get(pathClassLoader);
            Object[] hostElements = (Object[]) dexElementsField.get(hostPathList);

            //进行合并
            Object[] dexElements = (Object[]) Array.newInstance(pluginElements.getClass().getComponentType(), hostElements.length + pluginElements.length);
            System.arraycopy(hostElements,0,dexElements,0,hostElements.length);
            System.arraycopy(pluginElements,0,dexElements,hostElements.length,pluginElements.length);

            //宿主的dexElements 替换为新的dexElements
            dexElementsField.set(hostPathList,dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
