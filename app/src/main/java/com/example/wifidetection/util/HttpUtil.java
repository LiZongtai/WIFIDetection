package com.example.wifidetection.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    public static final String TAG = "MainActivity";
    private static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    public static void postJson(final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //申明给服务端传递一个json串
                    //创建一个OkHttpClient对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                    RequestBody requestBody = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url("http:/81.68.171.100:8110/api/wifi/detect")
                            .post(requestBody)
                            .build();
                    Response response=okHttpClient.newCall(request).execute();
                    String responseData=response.body().string();
                    Log.i("response data ----",responseData);
                }catch (Exception e){
                    Log.i("response errror ----",""+e);
                }
            }
        }).start();
    }
}
