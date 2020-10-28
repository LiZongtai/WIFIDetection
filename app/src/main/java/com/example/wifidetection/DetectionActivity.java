package com.example.wifidetection;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetectionActivity extends AppCompatActivity {

    private ListView showListView;
    private BaseAdapter adapter;
    private static final String TAG = "DetectionActivity---wifi";
//    private TextView showTv;
    StringBuffer sb=new StringBuffer();
    private ArrayList<ScanResult> wifiList = new ArrayList<>();
    private ArrayList<String> showList =new ArrayList<>();

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    reFreshUi();
                    break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
//        showTv = (TextView) findViewById(R.id.showID);
        showListView=(ListView)findViewById(R.id.showListView);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }

    //获取wifi 列表
    @SuppressLint("LongLogTag")
    public ArrayList<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //判断wifi 是否开启
        if (wifiManager.isWifiEnabled()) {
            Log.e(TAG, " wifi 打开");
            List<ScanResult> scanWifiList = wifiManager.getScanResults();
            ArrayList<ScanResult> wifiList = new ArrayList<>();
            if (scanWifiList != null && scanWifiList.size() > 0) {
                for (int i = 0; i < scanWifiList.size(); i++) {
                    ScanResult scanResult = scanWifiList.get(i);
                    wifiList.add(scanResult);
                }
                return wifiList;
            } else {
                Log.e(TAG, "非常遗憾搜索到wifi");
            }
        } else {
            Log.e(TAG, " wifi 关闭");
        }

        return null;
    }

    public void reFreshUi() {
        setTextView();
        setViewPager();
    }
    @SuppressLint("LongLogTag")
    public void setTextView() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiList=getWifiList();
        Collections.sort(wifiList, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult o1, ScanResult o2) {
                int c1=o1.level;
                int c2=o2.level;
                if(c1<c2){
                    return 1;
                }
                else if(c1==c2){
                    return  0;
                }
                else{
                    return -1;
                }
            }
        });
        int num=wifiList.size();
        for(int i=0;i<num;i++){
            sb.append("\n设备名："+wifiList.get(i).SSID +"\n "+
                    "BSSID(MAC)：" + wifiList.get(i).BSSID +"\n "+
                    "信号强度(dB)："+wifiList.get(i).level+"\n "+
                    "信号质量："+wifiManager.calculateSignalLevel(wifiList.get(i).level,4)+"\n");
//            TextView tv= new TextView(this);
//            tv.setText(sb.toString());
            Log.i(TAG,sb.toString());
            showList.add(sb.toString());
            sb.setLength(0);
        }
    }

    public void setViewPager() {
//        adapter = new ArrayAdapter<ScanResult>(this, android.R.layout.simple_list_item_1, wifiList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showList);
        showListView.setAdapter(adapter);
        showListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0) {
                    // !=0:有下拉动作
                    if ((firstVisibleItem + visibleItemCount) > totalItemCount - 2) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void start(View v) {

    }

    public void stop(View v) {

    }
}
