package com.hello.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hello.wifi.tookit.IWifiConnectListener;
import com.hello.wifi.tookit.WifiManagerProxy;


public class MainActivity extends AppCompatActivity {

    //wifi ssid and password
    private static final String ssId = "SchoolLeaderAP";
    private static final String pwd = "test1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.btn_auto_connect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }


    private void test() {
        WifiManagerProxy.get().init(getApplication());
        WifiManagerProxy.get().connect(ssId, pwd, new IWifiConnectListener() {
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