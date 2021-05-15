package com.hello.wifi.tookit;

public interface IWifiConnectListener {
    //开始连接
    void onConnectStart();

    //连接结束
    void onConnectEnd();

    // 连接中
    void onConnectLoading();

    // 连接成功
    void onConnectSuccess();

    //连接失败
    void onConnectFail(String errorMsg);

}
