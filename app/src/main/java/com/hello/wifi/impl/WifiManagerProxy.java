package com.hello.wifi.impl;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.hello.wifi.api.IWifiManager;
import com.hello.wifi.api.IWifiManagerListener;

import java.util.ArrayList;
import java.util.List;

public class WifiManagerProxy implements IWifiManager {

    private static class SingletonHolder {
        private static final WifiManagerProxy INSTANCE = new WifiManagerProxy();
    }

    private WifiManagerProxy() {
    }

    public static WifiManagerProxy get() {
        return SingletonHolder.INSTANCE;
    }


    private WifiManager manager;
    private Context mContext;
    private IWifiManagerListener mListener;


    @Override
    public void init(Application application, IWifiManagerListener iWifiManagerListener) {
        if (application == null) {
            throw new IllegalArgumentException("Application cant be null!");
        }
        if (iWifiManagerListener == null) {
            throw new IllegalArgumentException("IWifiManagerListener cant be null!");
        }
        if (manager == null) {
            mContext = application;
            manager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        }
        if (mListener == null) {
            mListener = iWifiManagerListener;
        }
    }

    @Override
    public void openWifi() {
        checkInit();
        if (!isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }
    }

    @Override
    public void openWifiSettingPage(Activity activity) {
        checkInit();
        if (activity == null) {
            mListener.onFail("openWifiSettingPage but activity == null!");
            return;
        }
        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void closeWifi() {
        checkInit();
        if (isWifiEnabled()) {
            manager.setWifiEnabled(false);
        }
    }


    @Override
    public boolean isWifiEnabled() {
        checkInit();
        return manager.isWifiEnabled();
    }

    @Override
    public List<ScanResult> getScanResults() {
        checkInit();
        boolean hasLocationPermission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasLocationPermission) {
            return manager.getScanResults();
        } else {
//            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
//            ActivityCompat.re(mContext, permissions, 0);
            Toast.makeText(mContext, "没有定位权限无法获取!", Toast.LENGTH_LONG).show();
            mListener.onFail("没有获取定位权限，无法查看Wifi列表!");
            return new ArrayList<>();
        }
    }

    private void checkInit() {
        if (manager == null || mListener == null) {
            throw new IllegalArgumentException("You must call init()  before other methods!");
        }

    }
}
