package com.example.wifidetection;

import android.annotation.SuppressLint;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class TimerWithHandlerActivity extends DetectionActivity {
    Timer timer = new Timer();
    private static final String TAG = "DetectionActivity---wifi";

    @SuppressLint("LongLogTag")
    @Override
    public void start(View v) {
        Log.i(TAG,"start");
        timer.schedule(timerTask,0,5000);//0为延迟时间，1000为间隔时间,(单位：毫秒)
    }

    @Override
    public void stop(View v) {
        stop();
    }

    private void stop() {
        timer.cancel();
        timerTask = null;
        timer = null;
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
            message.what = 0;
            mHandler.sendMessage(message);
        }
    };
}
