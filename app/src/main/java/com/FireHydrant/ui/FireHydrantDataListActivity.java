package com.FireHydrant.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.R;

import org.litepal.crud.DataSupport;

public class FireHydrantDataListActivity extends Activity implements View.OnClickListener {
    private LinearLayout fireHydrantDataOne, fireHydrantDataTwo;
    private TextView fireHydrantImageOne, fireHydrantImageTwo;
    private String fire_hydrant_id;
    private FireHydrantDetailData currentData;
    int imageIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_firehydrantdatalist);
        Bundle bundle = this.getIntent().getExtras();
        fire_hydrant_id = bundle.getString("fire_hydrant_id");
        initViews();
        initListenner();
    }

    /*
     *初始化界面和数据
     */
    private void initData() {
        currentData = DataSupport.where("fire_hydrant_id= ?", fire_hydrant_id).findFirst(FireHydrantDetailData.class);
        Log.i("FireHydrant", "currentData.getIsUpload:" + currentData.getIsUpload());
        Log.i("FireHydrant", "currentData.getIsChecked:" + currentData.getIsChecked());
        if (currentData.getIsUpload().equals("1")) {
            if (currentData.getIsChecked().equals("1")) {
                Log.i("FireHydrant", "currentData.getImagePathOne:" + currentData.getImagePathOne());
                if (currentData.getImagePathOne() != null && !currentData.getImagePathOne().equals("")) {
                    Log.i("FireHydrant", "imageOne已拍摄");
                    fireHydrantImageOne.setText("已拍摄");
                    fireHydrantImageOne.setTextColor(Color.parseColor("#0000ff"));
                } else {
                    Log.i("FireHydrant", "imageOne未抄");
                    fireHydrantImageOne.setText("未抄");
                    fireHydrantImageOne.setTextColor(Color.parseColor("#A3A3A3"));
                }
                Log.i("FireHydrant", "currentData.getImagePathTwo:" + currentData.getImagePathTwo());
                if (currentData.getImagePathTwo() != null && !currentData.getImagePathTwo().equals("") && currentData.getCurrent_value() != null && !currentData.getCurrent_value().equals("")) {
                    fireHydrantImageTwo.setText("已拍摄");
                    fireHydrantImageTwo.setTextColor(Color.parseColor("#0000ff"));
                } else {
                    fireHydrantImageTwo.setText("未抄");
                    fireHydrantImageTwo.setTextColor(Color.parseColor("#A3A3A3"));
                }
                Log.i("FireHydrant", "currentData.getCurrent_value:" + currentData.getCurrent_value());

            } else {
                fireHydrantImageOne.setText("已拍摄");
                fireHydrantImageOne.setTextColor(Color.parseColor("#0000ff"));
                fireHydrantImageTwo.setText("已拍摄");
                fireHydrantImageTwo.setTextColor(Color.parseColor("#0000ff"));
            }
        } else {
            fireHydrantImageOne.setText("已上传");
            fireHydrantImageOne.setTextColor(Color.parseColor("#ff0000"));
            fireHydrantImageTwo.setText("已上传");
            fireHydrantImageTwo.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    /*
    设置监听
     */
    private void initListenner() {
        fireHydrantDataOne.setOnClickListener(this);
        fireHydrantDataTwo.setOnClickListener(this);
    }

    /*
    / 初始化参数
     */
    private void initViews() {
        fireHydrantDataOne = (LinearLayout) findViewById(R.id.fireHydrantDataOne);
        fireHydrantDataTwo = (LinearLayout) findViewById(R.id.fireHydrantDataTwo);
        fireHydrantImageOne = (TextView) findViewById(R.id.fireHydrantImageOne);
        fireHydrantImageTwo = (TextView) findViewById(R.id.fireHydrantImageTwo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fireHydrantDataOne:
                imageIndex = 1;
                break;
            case R.id.fireHydrantDataTwo:
                imageIndex = 2;
                break;

        }
        if (currentData.getIsUpload().equals("0")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(FireHydrantDataListActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("该客户本月数据已经上传，可进行以下操作");
            dialog.setNegativeButton("修改数据", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ContentValues values = new ContentValues();
                    values.put("isUpload", "1");
                    DataSupport.updateAll(FireHydrantDetailData.class, values, " fire_hydrant_id= ?", fire_hydrant_id);
                    Intent intent = new Intent(FireHydrantDataListActivity.this, FireHydrantDataShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fire_hydrant_id", fire_hydrant_id);
                    bundle.putInt("fire_hydrant_imageindex", imageIndex);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            dialog.setPositiveButton("浏览数据", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(FireHydrantDataListActivity.this, FireHydrantDataShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fire_hydrant_id", fire_hydrant_id);
                    bundle.putInt("fire_hydrant_imageindex", imageIndex);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent(FireHydrantDataListActivity.this, FireHydrantDataShowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("fire_hydrant_id", fire_hydrant_id);
            bundle.putInt("fire_hydrant_imageindex", imageIndex);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("FireHydrant", "onresume");
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("FireHydrant", "onrestart");
    }
}
