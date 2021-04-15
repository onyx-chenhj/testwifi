package com.hello.wifi.interfaces;

public interface IWifiLogListener {
    void onSuccess(String success);

    void onFail(String reason);
}
