package com.extended;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.utils.SharedPreUtils;

/**
 * 其他设置
 */
public class OtherSettingActivity extends Activity {
    private Switch show_dialog_Switch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_setting_activity);
        show_dialog_Switch = findViewById(R.id.show_dialog_switch);
        show_dialog_Switch.setShowText(true);
        if (SharedPreUtils.getReminderDialogStatus(this)) {
            show_dialog_Switch.setChecked(true);
        } else {
            show_dialog_Switch.setChecked(false);
        }
        show_dialog_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isShowDialog) {
                if (isShowDialog) {
                    SharedPreUtils.setReminderDialogStatus(OtherSettingActivity.this, true);
                    show_dialog_Switch.setTextOn("开");
                } else {
                    SharedPreUtils.setReminderDialogStatus(OtherSettingActivity.this, false);
                    show_dialog_Switch.setTextOff("关");
                }
            }
        });
    }
}
