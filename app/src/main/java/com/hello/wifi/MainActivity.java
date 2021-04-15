package com.hello.wifi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hello.wifi.interfaces.IWifiManagerListener;
import com.hello.wifi.impl.WifiManagerProxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiManagerProxy.get().init(getApplication(), new IWifiManagerListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String reason) {

            }
        });
        WifiManagerProxy.get().connect("","");
        try {
            Thread.sleep(10000);
            WifiManagerProxy.get().disConnect("869455049330216");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}