package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.utils.SharedPreUtils;

public class IPActivity extends Activity {
    EditText currentipEt;
    TextView pretermitipTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu_ip);
        initViews();
        initDatas();
    }

    private void initDatas() {
        pretermitipTV.setText((new SharedPreUtils().GetIp(IPActivity.this)));
        currentipEt.setHint((new SharedPreUtils().GetIp(IPActivity.this)));
    }

    private void initViews() {
        currentipEt = (EditText) findViewById(R.id.currentIP);
        pretermitipTV = (TextView) findViewById(R.id.pretermitipTV);
    }

    public void IPsetting(View view) {
        if (!currentipEt.getText().toString().equals("") && currentipEt.getText() != null) {
            new SharedPreUtils().SetIp(currentipEt.getText().toString(), IPActivity.this);
            IPActivity.this.finish();
        } else {
            Toast.makeText(IPActivity.this, "修改的IP值不能为空", Toast.LENGTH_SHORT).show();
        }

    }
}
