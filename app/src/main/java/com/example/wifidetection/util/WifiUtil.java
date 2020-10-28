package com.example.wifidetection.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class WifiUtil {
    /**
     * 扫描之前需要刷新附近的Wifi列表，这需要使用startScan方法
     */
    public static void scanStart(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
    }

    /**
     * 扫描附近wifi
     */
    public static List<ScanResult> scanWifiInfo(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> mWifiList = new ArrayList<>();
        mWifiList.clear();
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiList = mWifiManager.getScanResults();
//        Log.i(TAG, "mWifiList size  :" + mWifiList.size());
        return mWifiList;
    }
}
