package com.zxyqwe.mediatrans;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    private static final int port = 5555;
    private OkHttpClient ok;
    public String server_ip = "";

    NetworkUtil() {
        ok = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.SECONDS).readTimeout(1, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.SECONDS).pingInterval(1, TimeUnit.SECONDS).build();
    }

    private int getWiFiIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getIpAddress();
    }

    private static String int2ip4(int ipInt) {
        return "" + (ipInt & 0xFF) + '.' + ((ipInt >> 8) & 0xFF)
                + '.' + ((ipInt >> 16) & 0xFF) + '.' + ((ipInt >> 24) & 0xFF);
    }

    private static String int2ip3(int ipInt) {
        return "" + (ipInt & 0xFF) + '.' + ((ipInt >> 8) & 0xFF)
                + '.' + ((ipInt >> 16) & 0xFF) + '.';
    }

    boolean searchNet(Context context, StringBuilder log) {
        int ip = getWiFiIP(context);
        log.append("Local IP: ").append(int2ip4(ip)).append("\r\n");
        try {
            DatagramSocket client = new DatagramSocket();

            byte[] sendBuf;
            sendBuf = TAG.getBytes();
            log.append("Send IP: ").append(int2ip3(ip)).append("255").append("\r\n");
            InetAddress addr = InetAddress.getByName(int2ip3(ip) + "255");
            DatagramPacket sendPacket
                    = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
            client.send(sendPacket);
            client.setSoTimeout(1000);
            client.receive(sendPacket);
            InetAddress ad = sendPacket.getAddress();
            String sip = ad.getHostAddress();
            server_ip = sip;
            String acceptStr = new String(sendPacket.getData());
            log.append("Server IP: ").append(sip).append("\r\n");
            return acceptStr.equals(TAG);
        } catch (Exception e) {
            e.printStackTrace();
            log.append(e.toString());
        }
        return false;
    }

    void upload(MainActivity ma, StringBuilder log) {
        ContentResolver resolver = ma.getContentResolver();
        String path;
        File f;
        for (Uri m :
                ma.mSelected) {
            path = PhotoMetadataUtils.getPath(resolver, m);
            f = new File(path);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), f);
            RequestBody requestBody = new MultipartBody.Builder().addFormDataPart("filename", f.getName(), fileBody).build();
            Request requestPostFile = new Request.Builder()
                    .url("http://" + ma.nh.nu.server_ip + ":8000/upload")
                    .post(requestBody)
                    .build();
            try {
                Response response = ok.newCall(requestPostFile).execute();
                log.append(response.body().string()).append("\r\n");
            } catch (IOException e) {
                e.printStackTrace();
                log.append(e.toString());
            }

        }

    }
}
