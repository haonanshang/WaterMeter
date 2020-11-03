package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.leonardo.watermeter.R;
import com.tencent.bugly.beta.Beta;



public class VersionActivity extends Activity  {
    TextView currentversionTV;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu_version);
        initViews();
        initDatas();
    }

    private void initDatas() {
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(this.getPackageName(), 0);
            currentversionTV.setText("WaterMeter " + pi.versionName );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        currentversionTV = (TextView) findViewById(R.id.currentversion);
    }

    public void versionUpdate(View view) {
        Beta.checkUpgrade(true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
