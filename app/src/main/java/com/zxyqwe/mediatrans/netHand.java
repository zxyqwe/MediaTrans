package com.zxyqwe.mediatrans;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

class netHand extends Handler {
    static final int SEARCH_NET = 22;
    static final int UPLOAD = 23;
    private static final int SEARCH_NET_DONE = 24;
    private static final int UPLOAD_DONE = 25;
    private NetworkUtil nu = new NetworkUtil();

    @Override
    public void handleMessage(Message msg) {
        final MainActivity ma = (MainActivity) msg.obj;
        switch (msg.what) {
            case SEARCH_NET:
                ma.pd.show();
                new Thread() {
                    @Override
                    public void run() {
                        StringBuilder log = new StringBuilder();
                        boolean res = nu.searchNet(ma, log);
                        Message nmsg = new Message();
                        nmsg.what = SEARCH_NET_DONE;
                        nmsg.obj = ma;
                        Bundle bund = new Bundle();
                        bund.putString("log", log.toString());
                        bund.putBoolean("res", res);
                        nmsg.setData(bund);
                        ma.nh.sendMessage(nmsg);
                    }
                }.start();
                break;
            case SEARCH_NET_DONE:
                ma.pd.dismiss();
                Bundle bund = msg.getData();
                String log = bund.getString("log");
                boolean res = bund.getBoolean("res");
                ma.net_stat.setText(log);
                if (!res) return;
                ma.search_net.setEnabled(false);
                ma.upload.setEnabled(true);
                break;
            case UPLOAD:
                ma.pd.show();
                ma.picker.setEnabled(false);
                ma.upload.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        nu.upload(ma);
                        Message nmsg = new Message();
                        nmsg.what = UPLOAD_DONE;
                        nmsg.obj = ma;
                        ma.nh.sendMessage(nmsg);
                    }
                }.start();
                break;
            case UPLOAD_DONE:
                ma.pd.dismiss();
                ma.mSelected.clear();
                ma.renew();
                ma.upload.setEnabled(true);
                ma.picker.setEnabled(true);
                break;
            default:
                break;
        }

    }
}
