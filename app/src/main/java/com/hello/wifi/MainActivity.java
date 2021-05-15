package com.hello.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hello.wifi.tookit.IWifiConnectListener;
import com.hello.wifi.tookit.WifiManagerProxy;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }


    private void test() {
        WifiManagerProxy.get().init(getApplication());
        WifiManagerProxy.get().connect("xxx", "xxx", new IWifiConnectListener() {
            @Override
            public void onConnectStart() {
                Log.i("TAG", "onConnectStart: ");
            }

            @Override
            public void onConnectSuccess() {
                Log.i("TAG", "onConnectSuccess: ");
            }

            @Override
            public void onConnectFail(String errorMsg) {
                Log.i("TAG", "onConnectFail: " + errorMsg);
            }
        });
    }

}