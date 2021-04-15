package com.hello.wifi.api;

public interface IWifiManagerListener {
    void onSuccess();

    void onFail(String reason);
}
