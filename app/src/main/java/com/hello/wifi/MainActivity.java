package com.hello.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hello.wifi.demo1.WifiAutoConnectManager;
import com.hello.wifi.tookit.IWifiConnectListener;
import com.hello.wifi.tookit.WifiManagerProxy;


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
        WifiManagerProxy.get().init(getApplication());
        WifiManagerProxy.get().connect("102", "4001001111", new IWifiConnectListener() {
            @Override
            public void onConnectStart() {

            }

            @Override
            public void onConnectEnd() {

            }

            @Override
            public void onConnectLoading() {

            }

            @Override
            public void onConnectSuccess() {

            }

            @Override
            public void onConnectFail(String errorMsg) {

            }
        });
    }

}