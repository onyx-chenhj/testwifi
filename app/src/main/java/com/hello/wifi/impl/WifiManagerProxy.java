package com.hello.wifi.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.hello.wifi.interfaces.IWifiManager;
import com.hello.wifi.interfaces.IWifiManagerListener;
import com.hello.wifi.utils.WifiConnector;

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


    private void checkInit() {
        if (manager == null || mListener == null) {
            throw new IllegalArgumentException("You must call init()  before other methods!");
        }
    }


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
    public void connect(String ssId, String pwd) {
        checkInit();
        WifiConnector connector = new WifiConnector(manager);
        connector.connect("869455049330216", "12345678", WifiConnector.WifiCipherType.WIFICIPHER_WPA);
    }

    @Override
    public void disConnect(String ssId) {
        checkInit();
        ssId = "\"" + ssId + "\"";
        WifiInfo wifiInfo = manager.getConnectionInfo();
        if (!TextUtils.isEmpty(ssId) && TextUtils.equals(ssId, wifiInfo.getSSID())) {
            int netId = wifiInfo.getNetworkId();
            manager.disableNetwork(netId);
        }
    }

}
