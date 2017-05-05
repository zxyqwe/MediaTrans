package com.zxyqwe.mediatrans;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    private static final int port = 5555;

    private int getWiFiIP(Context context) {
        WifiManager wifiManager = (WifiManager) context
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

            String sendStr = TAG;
            byte[] sendBuf;
            sendBuf = sendStr.getBytes();
            log.append("Send IP: ").append(int2ip3(ip)).append("255").append("\r\n");
            InetAddress addr = InetAddress.getByName(int2ip3(ip) + "255");
            DatagramPacket sendPacket
                    = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
            client.send(sendPacket);
            client.receive(sendPacket);
            InetAddress ad = sendPacket.getAddress();
            String sip = ad.getHostAddress();
            String acceptStr = new String(sendPacket.getData());
            log.append("Server IP: ").append(sip).append("\r\n");
            return acceptStr.equals(TAG);
        } catch (Exception e) {
            e.printStackTrace();
            log.append(e.toString());
        }
        return false;
    }

    void upload(MainActivity ma) {
        ContentResolver resolver = ma.getContentResolver();
        InputStream is;
        for (Uri m :
                ma.mSelected) {
            try {
                is = resolver.openInputStream(m);
                if (is == null) continue;
                int temp;
                StringBuilder sb = new StringBuilder();
                while ((temp = is.read()) != -1) {    //当没有读取完时，继续读取
                    sb.append((byte) temp);
                }
                is.close();
                Log.d(TAG, "" + sb.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
