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
import com.hello.wifi.interfaces.IWifiLogListener;

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
    private WifiConnector mConnector = new WifiConnector();
    private IWifiLogListener mListener;


    private void checkInit() {
        if (manager == null || mListener == null) {
            throw new IllegalArgumentException("You must call init()  before other methods!");
        }
    }


    @Override
    public void init(Application application, IWifiLogListener iWifiLogListener) {
        if (application == null) {
            throw new IllegalArgumentException("Application cant be null!");
        }
        if (iWifiLogListener == null) {
            throw new IllegalArgumentException("IWifiLogListener cant be null!");
        }
        if (manager == null) {
            manager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
            mConnector.init(manager, iWifiLogListener);
        }
        if (mListener == null) {
            mListener = iWifiLogListener;
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
        mConnector.connect(ssId, pwd, WifiConnector.WifiCipherType.WIFICIPHER_WPA);
    }

    @Override
    public void disConnect(String ssId) {
        checkInit();
        if (TextUtils.isEmpty(ssId)) {
            mListener.onFail("disConnect fail = 未指定对应的SSID!");
            return;
        }
        ssId = "\"" + ssId + "\"";
        WifiInfo wifiInfo = manager.getConnectionInfo();
        if (wifiInfo != null && !TextUtils.isEmpty(ssId)) {
            int netId = wifiInfo.getNetworkId();
            manager.removeNetwork(netId);
        } else {
            mListener.onFail("disConnect fail = wifi异常  或者 此时就没有连接上对应的SSID ！");
        }
    }

}
