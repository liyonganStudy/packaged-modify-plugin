package com.netease.cloudmusic.hostapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private PluginDexClassLoader mPluginDexClassLoader;
    private String apkPath = new File(Environment.getExternalStorageDirectory() + "/DL/plugin.apk").getAbsolutePath();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用adb push xxx.apk /sdcard/DL/lyric.apk先将模板apk传到手机里

        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        Fragment fragment = getFragmentFromApk(getPluginDexClassLoader(), packageInfo.packageName + ".PluginFragment");
        if (fragment == null) {
            Toast.makeText(this, "can not load plugin fragment", Toast.LENGTH_LONG).show();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        ResourcesManager.createResources(this, apkPath);
//        ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).cloneInContext(this).setFactory(new LayoutInflater.Factory() {
//            @Override
//            public View onCreateView(String name, Context context, AttributeSet attrs) {
//                try {
//                    Class<?> cl = getPluginDexClassLoader().loadPluginClass(name);
//                    Constructor<?> constructor = cl.getConstructor(Context.class, AttributeSet.class);
//                    return ((View) constructor.newInstance(context, attrs));
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                    return null;
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        });
    }

    private Fragment getFragmentFromApk(DexClassLoader classLoader, String fragmentFullName) {
        try {
            Class<?> remoteClass = classLoader.loadClass(fragmentFullName);
            Constructor<?> localConstructor = remoteClass.getConstructor();
            return (Fragment) localConstructor.newInstance();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    private static final String DEX_OUTPUT_DIR = "dex";

    private PluginDexClassLoader getPluginDexClassLoader() {
        if (mPluginDexClassLoader == null) {
            mPluginDexClassLoader = createDexClassLoader(MainActivity.this, apkPath);
        }
        return mPluginDexClassLoader;
    }

    public static PluginDexClassLoader createDexClassLoader(Context context, String apkPath) {
        File dexOutputDir = context.getDir(DEX_OUTPUT_DIR, Context.MODE_PRIVATE);
        return new PluginDexClassLoader(apkPath, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
    }
}
