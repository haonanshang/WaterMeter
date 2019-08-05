package com.FireHydrant.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.FireHydrant.ui.FireHydrantDataShowActivity;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.global.GlobalData;
import com.example.leonardo.watermeter.ui.TaskShowActivity;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class FireHydrantDialog extends Dialog implements View.OnClickListener {
    private Button Addone, Addtwo, Addthree;
    private Button Addfour, Addfive, Addsix;
    private Button Addseven, Addeight, Addnine;
    private Button Addzero, Addsuccess, Adddefeate;
    private TextView ShowWaterCardNum;
    private String WaterCardNumtext = "";
    private FireHydrantDetailData mDetaiData;
    private FireHydrantDataShowActivity mActivity;

    public FireHydrantDialog(FireHydrantDataShowActivity activity, FireHydrantDetailData data) {
        super(activity, R.style.CustomDialog);
        this.mActivity = activity;
        this.mDetaiData = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manualkeyboard);
        InitViews();
        initListenners();
    }

    private void initListenners() {
        Addone.setOnClickListener(this);
        Addtwo.setOnClickListener(this);
        Addthree.setOnClickListener(this);
        Addfour.setOnClickListener(this);
        Addfive.setOnClickListener(this);
        Addsix.setOnClickListener(this);
        Addseven.setOnClickListener(this);
        Addeight.setOnClickListener(this);
        Addnine.setOnClickListener(this);
        Addzero.setOnClickListener(this);
        Adddefeate.setOnClickListener(this);
        Addsuccess.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    private void InitViews() {
        Addone = (Button) findViewById(R.id.manual_addone);
        Addtwo = (Button) findViewById(R.id.manual_addtwo);
        Addthree = (Button) findViewById(R.id.manual_addthree);
        Addfour = (Button) findViewById(R.id.manual_addfour);
        Addfive = (Button) findViewById(R.id.manual_addfive);
        Addsix = (Button) findViewById(R.id.manual_addsix);
        Addseven = (Button) findViewById(R.id.manual_addseven);
        Addeight = (Button) findViewById(R.id.manual_addeight);
        Addnine = (Button) findViewById(R.id.manual_addnine);
        Addzero = (Button) findViewById(R.id.manual_addzero);
        Adddefeate = (Button) findViewById(R.id.manual_delete);
        Addsuccess = (Button) findViewById(R.id.manual_success);
        ShowWaterCardNum = (TextView) findViewById(R.id.manual_ShowWaterCardNum);
        ShowWaterCardNum.setHint(mDetaiData.getLast_value());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manual_addone:
                WaterCardNumtext = WaterCardNumtext + "1";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addtwo:
                WaterCardNumtext = WaterCardNumtext + "2";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addthree:
                WaterCardNumtext = WaterCardNumtext + "3";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addfour:
                WaterCardNumtext = WaterCardNumtext + "4";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addfive:
                WaterCardNumtext = WaterCardNumtext + "5";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addsix:
                WaterCardNumtext = WaterCardNumtext + "6";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addseven:
                WaterCardNumtext = WaterCardNumtext + "7";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addeight:
                WaterCardNumtext = WaterCardNumtext + "8";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addnine:
                WaterCardNumtext = WaterCardNumtext + "9";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_addzero:
                WaterCardNumtext = WaterCardNumtext + "0";
                ShowWaterCardNum.setText(WaterCardNumtext);
                break;
            case R.id.manual_delete:
                int len = WaterCardNumtext.length();
                if (len > 0) {
                    WaterCardNumtext = WaterCardNumtext.substring(0, len - 1);
                    ShowWaterCardNum.setText(WaterCardNumtext);
                }
                break;
            case R.id.manual_success:
                String currentReadString = ShowWaterCardNum.getText().toString();
                String lastReadString = mDetaiData.getLast_value();
                String Result = "保存成功";
                if (currentReadString.length() != 0) {
                    int currentRead = Integer.valueOf(currentReadString);
                    int lastRead;
                    if (!lastReadString.equals("") && lastReadString != null) {
                        lastRead = Integer.valueOf(lastReadString.trim());
                    } else {
                        lastRead = 0;
                    }
                    int currentReadWaterSum = currentRead - lastRead;
                    if (currentReadWaterSum >= 0) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String imgTime = simpleDateFormat.format(new java.util.Date());
                        ContentValues values = new ContentValues();
                        values.put("check_date", imgTime);
                        values.put("current_value", currentReadString);
                        values.put("current_water_data", String.valueOf(currentReadWaterSum));
                        DataSupport.updateAll(FireHydrantDetailData.class, values, "fire_hydrant_id  = ?", mDetaiData.getFire_hydrant_id());
                        Toast.makeText(mActivity, Result, Toast.LENGTH_SHORT).show();
                        mActivity.initDatas();
                        mActivity.updateFireHydrantValue();
                        this.dismiss();
                    } else {
                        Toast.makeText(mActivity, "当前手抄读书需要大于等于上期读数", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mActivity, "当前手抄读数不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
