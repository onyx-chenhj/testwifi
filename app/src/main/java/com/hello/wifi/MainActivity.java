package com.hello.wifi;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hello.wifi.demo1.WifiAutoConnectManager;
import com.hello.wifi.tookit.IWifiConnectListener;
import com.hello.wifi.tookit.WifiManagerProxy;


public class MainActivity extends AppCompatActivity {

    private WifiAutoConnectManager mWifiAutoConnectManager;
    private BroadcastReceiver mWifiConnectBroadcastReceiver;
    private IntentFilter mWifiConnectIntentFilter;


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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiConnectBroadcastReceiver);
    }

    private void test() {
        WifiManagerProxy.get().init(getApplication());
        WifiManagerProxy.get().connect("1023", "4001001111", new IWifiConnectListener() {
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