package com.hello.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hello.wifi.demo1.WifiAutoConnectManager;
import com.hello.wifi.impl.WifiManagerProxy;
import com.hello.wifi.interfaces.IWifiLogListener;


public class MainActivity extends AppCompatActivity {

    private WifiAutoConnectManager mWifiAutoConnectManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        mWifiAutoConnectManager = WifiAutoConnectManager.newInstance(wifiManager);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startLink();
//            }
//        }).start();

        test();
    }

    private void test() {
        WifiManagerProxy.get().init(getApplication(), new IWifiLogListener() {
            @Override
            public void onSuccess(String success) {

            }

            @Override
            public void onFail(String reason) {

            }
        });
        WifiManagerProxy.get().connect("102", "4001001111");
    }

}