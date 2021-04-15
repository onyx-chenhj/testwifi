package com.hello.wifi.api;

import android.app.Activity;
import android.app.Application;
import android.net.wifi.ScanResult;

import java.util.List;

public interface IWifiManager {
    void init(Application application, IWifiManagerListener iWifiManagerListener);
    void openWifi();  //打开Wifi
    void openWifiSettingPage(Activity activity); //打开wifi设置页面
    void closeWifi(); //关闭wifi
    boolean isWifiEnabled(); //判断wifi是否可用

    List<ScanResult> getScanResults(); //获取Wifi扫描列表
}
