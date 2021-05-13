package com.hello.wifi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hello.wifi.impl.WifiManagerProxy;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        WifiManagerProxy.get().init(getApplication(), new IWifiLogListener() {
//            @Override
//            public void onSuccess(String result) {
//                Log.i("MainActivity", "onSuccess: " + result);
//            }
//
//            @Override
//            public void onFail(String reason) {
//                Log.i("MainActivity", "onFail: " + reason);
//            }
//        });

       // WIFIConnectionManager.getInstance(this).connect("869455049330216", "12345678");
        //WifiManagerProxy.get().connect("869455049330216", "12345678");
//        try {
//            Thread.sleep(10000);
//            WifiManagerProxy.get().disConnect("869455049330216");
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        WifiUtils.withContext(getApplicationContext())
                .connectWith("869455049330216","12345678")
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void failed(ConnectionErrorCode errorCode) {

                    }
                })
                .start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        WifiManagerProxy.get().disConnect("869455049330216");

    }
}