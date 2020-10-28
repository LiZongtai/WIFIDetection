package com.example.wifidetection;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.wifidetection.util.WifiUtil.scanStart;
import static com.example.wifidetection.util.WifiUtil.scanWifiInfo;

public class DetectActivity extends AppCompatActivity {

    Timer timer = new Timer();
    private static final String TAG = "DetectActivity wifi: ";
    WifiBroadcastReceiver wifiReceiver = new WifiBroadcastReceiver();

    private ListView showListView;
    private List<ScanResult> wifiList = new ArrayList<>();
    private ArrayList<String> showList =new ArrayList<>();
    ArrayAdapter adapter=null;
    StringBuffer sb=new StringBuffer();

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 每1s重新检索WIFI
                    scanStart(DetectActivity .this);
                    break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        setButton();
//        showTv = (TextView) findViewById(R.id.showID);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showList);
        showListView=(ListView)findViewById(R.id.showListView);
        showListView.setAdapter(adapter);

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
        reFreshUi();
        setViewPage();
        start();
    }
    public void setButton(){
        Button submitBtn=(Button)findViewById(R.id.submitBtn);
        Button refrashBtn=(Button)findViewById(R.id.refrashBtn);
        Button backBtn=(Button)findViewById(R.id.backBtn);
        Button connectedBtn=(Button)findViewById(R.id.connectedBtn);

        submitBtn.setEnabled(false);
//        refrashBtn.setEnabled(false);
//        backBtn.setEnabled(false);
        connectedBtn.setEnabled(false);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetectActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        refrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanStart(DetectActivity .this);
                reFreshUi();
            }
        });
    }

    //获取wifi 列表
    public void getWifiList() {
        wifiList.clear();
        wifiList=scanWifiInfo(this);
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
    }

    public void reFreshUi() {
        showList.clear();
        setTextView();
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("LongLogTag")
    public void setTextView() {
        getWifiList();
        int num=wifiList.size();
        for(int i=0;i<num;i++){
            sb.append("\n"+
                    "设备名："+wifiList.get(i).SSID +"\n "+
                    "BSSID(MAC)：" + wifiList.get(i).BSSID +"\n "+
                    "信号强度(dBm)："+wifiList.get(i).level+"\n "+
                    "信号质量："+WifiManager.calculateSignalLevel(wifiList.get(i).level,4)
                    +"\n");
//            TextView tv= new TextView(this);
//            tv.setText(sb.toString());
//            Log.i(TAG,sb.toString());
            showList.add(sb.toString());
            sb.setLength(0);
        }
    }

    public void setViewPage() {
//        adapter = new ArrayAdapter<ScanResult>(this, android.R.layout.simple_list_item_1, wifiList);
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

    @SuppressLint("LongLogTag")
    public void start() {
        Log.i(TAG,"start");
        timer.schedule(timerTask,0,1000);//0为延迟时间，1000为间隔时间,(单位：毫秒)
        registerWifiReceiver();
    }

    private void stop() {
        timer.cancel();
        timerTask = null;
        timer = null;
        this.unregisterReceiver(wifiReceiver);
    }

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    };

    private void registerWifiReceiver() {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态广播,是否连接了一个有效路由
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        this.registerReceiver(wifiReceiver, filter);
    }
    //监听wifi状态
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
//                Log.i(TAG, "网络变化了");
                // 当网络变化时更新WIFI列表显示
                reFreshUi();
            }
        }
    }

}
