package com.hello.wifi.impl;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hello.wifi.interfaces.IWifiLogListener;

import java.util.List;

class WifiConnector {

    private IWifiLogListener logListener;
    private WifiManager wifiManager;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (logListener != null) {
                switch (msg.what) {
                    case 0:
                        logListener.onSuccess("WifiConnector connect success = " + msg.obj);
                        break;
                    case -1:
                        logListener.onFail("WifiConnector connect fail = " + msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    };


    public void init(WifiManager wifiManager, IWifiLogListener iWifiLogListener) {
        if (wifiManager == null) {
            throw new IllegalArgumentException("WifiConnector wifiManager cant be null!");
        }
        if (iWifiLogListener == null) {
            throw new IllegalArgumentException("WifiConnector iWifiLogListener cant be null!");
        }
        this.wifiManager = wifiManager;
        this.logListener = iWifiLogListener;
    }


    private void checkInit() {
        if (logListener == null || wifiManager == null) {
            throw new IllegalArgumentException("You must call init()  before other methods!");
        }
    }


    /**
     * 子线程要向UI发送连接失败的消息
     *
     * @param info 消息
     */
    public void sendErrorMsg(String info) {
        if (mHandler != null) {
            Message msg = new Message();
            msg.obj = info;
            msg.what = -1;
            mHandler.sendMessage(msg);// 向Handler发送消息
        }
    }


    /**
     * 子线程向UI主线程发送连接成功的消息
     *
     * @param info
     */
    public void sendSuccessMsg(String info) {
        if (mHandler != null) {
            Message msg = new Message();
            msg.obj = info;
            msg.what = 0;
            mHandler.sendMessage(msg);// 向Handler发送消息
        }
    }

    //WIFICIPHER_WEP是WEP ，WIFICIPHER_WPA是WPA，WIFICIPHER_NOPASS没有密码
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }


    // 提供一个外部接口，传入要连接的无线网
    public void connect(String ssid, String password, WifiCipherType type) {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        checkInit();
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


    public WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // config.SSID = SSID;
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {// wep
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {// wpa
            config.preSharedKey = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    // 打开wifi功能
    private boolean openWifi() {
        checkInit();
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    class ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private WifiCipherType type;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        @Override
        public void run() {
            checkInit();
            try {
                // 如果之前没打开wifi,就去打开  确保wifi开关开了
                openWifi();
                Thread.sleep(200);
                // 开启wifi功能需要一段时间,每隔100ms检测一次，WIFI可用了就继续下面的操作
                while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个100毫秒检测……
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        sendErrorMsg("ConnectRunnable InterruptedException =" + ie.getMessage());
                    }
                }

                //禁掉所有wifi
                for (WifiConfiguration c : wifiManager.getConfiguredNetworks()) {
                    wifiManager.disableNetwork(c.networkId);
                }

                WifiConfiguration wifiConfig = createWifiInfo("102", "4001001111", WifiCipherType.WIFICIPHER_WPA);
                if (wifiConfig == null) {
                    Log.d("wifidemo", "wifiConfig is null!");
                    return;
                }
                Log.d("wifidemo", wifiConfig.SSID);

                int netID = wifiManager.addNetwork(wifiConfig);
                boolean enabled = wifiManager.enableNetwork(netID, true);
                sendSuccessMsg("连接成功! enabled = " + enabled);
            } catch (Exception e) {
                sendErrorMsg(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }
}
