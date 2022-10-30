package com.gpsenable;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Post {
    public static final MediaType JSON
            = MediaType.parse("application/json;charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    //TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    //@SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();

    public static void main(String[] args) throws IOException {
        Post example = new Post();
        String json = "{'imei':'" + "deviceId" + "'}";
        String response = example.post("http://ec2-13-125-225-22.ap-northeast-2.compute.amazonaws.com:8080/user/login", json);
        System.out.println(response);
    }
}
