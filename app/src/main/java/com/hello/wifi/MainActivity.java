package com.hello.wifi;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.hello.wifi.interfaces.IWifiLogListener;
import com.hello.wifi.impl.WifiManagerProxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiManagerProxy.get().init(getApplication(), new IWifiLogListener() {
            @Override
            public void onSuccess(String result) {
                Log.i("MainActivity", "onSuccess: "+result);
            }

            @Override
            public void onFail(String reason) {
                Log.i("MainActivity", "onFail: " + reason);
            }
        });
        WifiManagerProxy.get().connect("869455049330216", "12345678");
//        try {
//            Thread.sleep(10000);
//            WifiManagerProxy.get().disConnect("869455049330216");
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}