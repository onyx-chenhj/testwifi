package com.hello.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



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

        String ssid = "102"; // 869455049330216
        String pwd = "4001001111";
//        WifiUtils.withContext(getApplicationContext())
//                .connectWith(ssid, pwd)
//                .onConnectionResult(new ConnectionSuccessListener() {
//                    @Override
//                    public void success() {
//                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void failed(ConnectionErrorCode errorCode) {
//                        Toast.makeText(MainActivity.this, "failed: = " + errorCode, Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .start();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}