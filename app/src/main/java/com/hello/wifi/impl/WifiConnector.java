package com.hello.wifi.impl;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hello.wifi.interfaces.IWifiLogListener;

import java.util.ArrayList;
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

    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {
        checkInit();
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
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
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
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

                //根据传入的ssid pwd type创建 wifi config
                WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);

                if (wifiConfig == null) {
                    sendErrorMsg("wifiConfig is null!");
                    return;
                }

                // 重新扫描下最新的wifi信息，防止wifi信息已经变了但是 getScanResults还是使用旧的wifi信息
                wifiManager.startScan();
                // 线程休眠0.5秒  等待最新的扫描结果出来
                Thread.sleep(500);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResults == null) {
                    sendErrorMsg("没有扫描到可用的网络!");
                    return;
                }

                List<String> ssIdList = new ArrayList<>();
                for (int i = 0; i < scanResults.size(); i++) {
                    ScanResult scanResult = scanResults.get(i);
                    ssIdList.add(scanResult.SSID);
                }

                if(!ssIdList.contains(ssid)){
                    sendErrorMsg("不存在指定SSID的 网络!");
                    return;
                }

                WifiConfiguration tempConfig = isExsits(ssid);
                //之前如果存在的网络, 那就删除掉重新连接一次 ,确保最新的ssid 和 pws
                if (tempConfig != null) {
                    wifiManager.removeNetwork(tempConfig.networkId);
                }

                int netID = wifiManager.addNetwork(wifiConfig);
                boolean enabled = wifiManager.enableNetwork(netID, true);
                boolean connected = wifiManager.reconnect();
                sendSuccessMsg("连接成功! enabled = " + enabled + " connected  =" + connected);
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
