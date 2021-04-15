package com.hello.wifi.interfaces;

public interface IWifiManagerListener {
    void onSuccess();

    void onFail(String reason);
}
