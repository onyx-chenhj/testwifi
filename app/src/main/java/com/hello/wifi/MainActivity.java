package com.hello.wifi;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.hello.wifi.api.IWifiManagerListener;
import com.hello.wifi.impl.WifiManagerProxy;

import java.util.List;

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
        List<ScanResult> scanResults = WifiManagerProxy.get().getScanResults();
        Log.i("TAG", "onCreate: size = "+scanResults.size());
    }
}