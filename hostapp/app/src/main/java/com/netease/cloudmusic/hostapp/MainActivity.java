package com.netease.cloudmusic.hostapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用adb push xxx.apk /sdcard/DL/lyric.apk先将模板apk传到手机里
        String apkPath = Environment.getExternalStorageDirectory() + "/DL/plugin.apk";
        File file = new File(apkPath);
        apkPath = file.getAbsolutePath();
        ResourcesManager.createResources(this, apkPath);
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        Fragment fragment = getFragmentFromApk(createDexClassLoader(MainActivity.this, apkPath), packageInfo.packageName + ".PluginFragment");
        if (fragment == null) {
            Toast.makeText(this, "can not load plugin fragment", Toast.LENGTH_LONG).show();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commitAllowingStateLoss();
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

    public static DexClassLoader createDexClassLoader(Context context, String apkPath) {
        File dexOutputDir = context.getDir(DEX_OUTPUT_DIR, Context.MODE_PRIVATE);
        return new PluginDexClassLoader(apkPath, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
    }
}
