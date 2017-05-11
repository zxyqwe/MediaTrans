package com.zxyqwe.mediatrans;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 23;
    Button search_net;
    Button picker;
    Button upload;
    TextView media_stat;
    TextView net_stat;
    TextView upload_stat;
    List<Uri> mSelected = new ArrayList<>();
    netHand nh = new netHand();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_net = (Button) findViewById(R.id.search_net);
        picker = (Button) findViewById(R.id.picker);
        upload = (Button) findViewById(R.id.upload);
        media_stat = (TextView) findViewById(R.id.media_stat);
        net_stat = (TextView) findViewById(R.id.net_stat);
        upload_stat = (TextView) findViewById(R.id.upload_stat);

        upload.setEnabled(false);
        search_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = netHand.SEARCH_NET;
                msg.obj = MainActivity.this;
                nh.sendMessage(msg);
            }
        });
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(Integer.MAX_VALUE)
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = netHand.UPLOAD;
                msg.obj = MainActivity.this;
                nh.sendMessage(msg);
            }
        });
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("请稍等");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
    }

    public void renew() {
        ContentResolver resolver = this.getContentResolver();
        StringBuilder res = new StringBuilder();
        String path;
        for (Uri m :
                mSelected) {
            path = PhotoMetadataUtils.getPath(resolver, m);
            res.append(path).append(' ').append(IOUtil.exchangeFileSize(path)).append("\r\n");
        }
        media_stat.setText(res.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d(TAG, "mSelected: " + mSelected);
            renew();
        }
    }
}
