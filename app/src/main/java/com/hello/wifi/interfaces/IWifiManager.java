package com.hello.wifi.interfaces;

import android.app.Activity;
import android.app.Application;

public interface IWifiManager {
    void init(Application application, IWifiLogListener iWifiLogListener);
    void openWifi();  //打开Wifi
    void openWifiSettingPage(Activity activity); //打开wifi设置页面
    void closeWifi(); //关闭wifi
    boolean isWifiEnabled(); //判断wifi是否可用

    void connect(String ssId,String pwd); //连接wifi
    void disConnect(String ssId); // 断开某个网络
}
