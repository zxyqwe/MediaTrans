package com.zxyqwe.mediatrans;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 23;
    Button search_net;
    Button picker;
    Button upload;
    TextView media_stat;
    List<Uri> mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_net = (Button) findViewById(R.id.search_net);
        picker = (Button) findViewById(R.id.picker);
        upload = (Button) findViewById(R.id.upload);
        media_stat = (TextView) findViewById(R.id.media_stat);

        upload.setEnabled(false);
        search_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_net.setEnabled(false);
                upload.setEnabled(true);
            }
        });
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(MainActivity.this)
                        .choose(MimeType.allOf())
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
                picker.setEnabled(false);
                upload.setEnabled(false);

                mSelected.clear();
                renew();
                upload.setEnabled(true);
                picker.setEnabled(true);
            }
        });
    }

    private void renew() {
        ContentResolver resolver = this.getContentResolver();
        StringBuilder res = new StringBuilder();
        String path;
        File f;
        for (Uri m :
                mSelected) {
            path = PhotoMetadataUtils.getPath(resolver, m);
            f = new File(path);
            res.append(path).append(' ').append(exchange(f.length())).append("\r\n");
        }
        media_stat.setText(res.toString());
    }

    private String exchange(long i) {
        DecimalFormat df = new DecimalFormat("#.##");
        char[] f = {' ', 'K', 'M', 'G'};
        double di = (double) i;
        int j = 0;
        while (di > 1024) {
            j++;
            di /= 1024;
        }
        return df.format(di) + f[j] + 'B';
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
