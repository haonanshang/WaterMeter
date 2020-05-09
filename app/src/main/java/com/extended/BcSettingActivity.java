package com.extended;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.ui.IPActivity;
import com.example.leonardo.watermeter.utils.SharedPreUtils;

import org.apache.commons.lang3.StringUtils;

public class BcSettingActivity extends Activity {
    private EditText bcNumberEt;
    private Button bcNumberBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.extend_bc_setting);
        initViews();
        initDatas();
        initListenner();
    }

    private void initListenner() {
        bcNumberBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(bcNumberEt.getText().toString())) {
                    try {
                        int divideNumber = Integer.valueOf(bcNumberEt.getText().toString().trim());
                        new SharedPreUtils().SetBcNumber(divideNumber, BcSettingActivity.this);
                        BcSettingActivity.this.finish();
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                        Toast.makeText(BcSettingActivity.this, "表册数量只能设置为整数", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BcSettingActivity.this, "表册数量不能设置为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initDatas() {
        int divideNumber = new SharedPreUtils().GetBcNumber(BcSettingActivity.this);
        bcNumberEt.setHint(String.valueOf(divideNumber));
    }

    private void initViews() {
        bcNumberEt = findViewById(R.id.bcNumberEt);
        bcNumberBt = findViewById(R.id.bcSetting_save);
    }

}
