package com.netease.cloudmusic.hostapp;

import android.util.Log;

import dalvik.system.DexClassLoader;

/**
 * Created by hzliyongan on 2018/3/1.
 */

public class PluginDexClassLoader extends DexClassLoader {
    public static final String TAG = "PluginDexClassLoader";
    private ClassLoader mHostClassLoader;

    public PluginDexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        mHostClassLoader = parent;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> pc;
        if (name.contains("Fragment")) {
        }
        Log.i(TAG, "loadClass : " + name);
        try {
            pc = super.loadClass(name, resolve);
            if (pc != null) {
                return pc;
            } else {
                Log.i(TAG, "super.loadClass null: " + name);
            }
        } catch (ClassNotFoundException e) {
            pc = mHostClassLoader.loadClass(name);
            if (pc != null) {
                return pc;
            } else {
                Log.i(TAG, "mHostClassLoader.loadClass null: " + name);
            }
        }
        return null;
    }

    public Class<?> loadPluginClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
}
