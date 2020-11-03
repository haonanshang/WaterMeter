package com.example.leonardo.watermeter.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 尚浩楠 on 2018/5/23.
 */

public class ExtendsFieldActivity extends Activity {
    ListView listView;
    Switch mswitch;
    String tid;
    DetailData currentData;

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        Bundle bundle = this.getIntent().getExtras();
        tid = bundle.getString("t_id");
        initDatas();
        LinearLayout linearlayout = new LinearLayout(this);
        linearlayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout mlinearlayout = new LinearLayout(this);
        mlinearlayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mlinearlayout.setOrientation(LinearLayout.VERTICAL);
        mswitch = new Switch(this);
        mswitch.setTextOff("是");
        mswitch.setTextOn("否");
        mswitch.setText("是否为正常水表");
        mswitch.setSwitchPadding(20);
        mswitch.setTextSize(20);
        mswitch.setShowText(true);
        mswitch.setThumbResource(R.drawable.thumb);
        mswitch.setTrackResource(R.drawable.track);
        LinearLayout.LayoutParams mlayoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mlayoutparams.leftMargin = 30;
        mlayoutparams.topMargin = 20;
        mlayoutparams.rightMargin = 30;
        mlinearlayout.addView(mswitch, mlayoutparams);
        linearlayout.addView(mlinearlayout);
        listView = new ListView(this);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData()));
        linearlayout.addView(listView, mlayoutparams);
        setContentView(linearlayout);
        initSwitchListenners();
        ChangeSwitchState();
    }

    /**
     * 改变开关状态
     */
    private void ChangeSwitchState() {
        if (currentData.getT_normal_detect().equals("true")) {
            mswitch.setChecked(false);
        } else {
            mswitch.setChecked(true);
        }
    }

    /**
     * 初始化
     */
    private void initDatas() {
        currentData = LitePal.where("t_id = ?", tid).findFirst(DetailData.class);

    }

    /**
     * 监听
     */
    private void initSwitchListenners() {
        mswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ContentValues values = new ContentValues();
                    values.put("t_normal_detect", "false");
                    LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                } else {
                    ContentValues values = new ContentValues();
                    values.put("t_normal_detect", "true");
                    LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                }
            }
        });
    }

    /**
     * 获取扩展字段参数
     *
     * @return
     */
    private List<String> getData() {
        List<String> data = new ArrayList<>();
        JSONObject hostObject = null;
        try {
            hostObject = new JSONObject(currentData.getExtend_field_string());
            Iterator<String> sIterator = hostObject.keys();
            while (sIterator.hasNext()) {
                // 获得key
                String key = sIterator.next();
                // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
                String value = hostObject.getString(key);
                data.add(key + ":" + value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}
