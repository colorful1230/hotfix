package com.test.hotfix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by zhaolin on 2017/12/15.
 */

public class HotfixUtils {

    private static final String TAG = "HotfixUtils";

    public static void checkPatch(Context context, String patchPath) {
        File patchFile = new File(patchPath);
        if (patchFile == null || !patchFile.exists()) {
            return;
        }

        mergePathList(context, patchPath);
    }

    private static void mergePathList(Context context, String dexPath) {
        File optPath = context.getDir("dex", Context.MODE_PRIVATE);

        ClassLoader parent = context.getClassLoader();
        if (parent == null) {
            return;
        }

        DexClassLoader dexClassLoader = new DexClassLoader(dexPath,
                optPath.getAbsolutePath(), null, parent);

        try {
            //获取外部dex中的pathList
            Class<?> baseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");

            Object dexPathList = getField(dexClassLoader, baseDexClassLoader, "pathList");
            Object dexElements = getField(dexPathList, dexPathList.getClass(), "dexElements");

            //获取本地apk中的pathList
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Object pathPathList = getField(pathClassLoader, baseDexClassLoader, "pathList");
            Object pathElements = getField(pathPathList, pathPathList.getClass(), "dexElements");

            //合并pathList, 将修复bug的classLoader放在最前面
            Object merge = mergeDex(dexElements, pathElements);

            //将合并后的pathList设置回去
            Object pathList = getField(pathClassLoader, baseDexClassLoader, "pathList");
            setField(pathList, pathList.getClass(), "dexElements", merge);

            Log.d(TAG, "mergePathList: finish merge");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static Object getField(Object obj, Class<?> clazz, String field) throws NoSuchFieldException, IllegalAccessException {
        Field f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(obj);
    }

    private static void setField(Object obj, Class<?> clazz, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private static Object mergeDex(Object dexElements, Object pathElements) {
        int dexLength = Array.getLength(dexElements);
        int pathLength = Array.getLength(pathElements);
        Object merge = Array.newInstance(dexElements.getClass().getComponentType(), dexLength + pathLength);
        System.arraycopy(dexElements, 0, merge, 0, dexLength);
        System.arraycopy(pathElements, 0, merge, dexLength, pathLength);
        return merge;
    }
}
