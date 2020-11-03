package com.example.leonardo.watermeter.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.global.GlobalData;
import com.example.leonardo.watermeter.ui.TaskShowActivity;
import com.itgoyo.logtofilelibrary.LogToFileUtils;


import org.litepal.LitePal;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018/3/22 0022.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class CustomDialog extends Dialog implements View.OnClickListener {
    private Button Addone, Addtwo, Addthree;
    private Button Addfour, Addfive, Addsix;
    private Button Addseven, Addeight, Addnine;
    private Button Addzero, Addsuccess, Adddefeate;
    private TextView ShowWaterCardNum;
    private String WaterCardNumtext = "";
    private DetailData mDetaiData;
    private TaskShowActivity mActivity;
    private int wbk = 0;

    public CustomDialog(TaskShowActivity activity, DetailData data, int n) {
        super(activity, R.style.CustomDialog);
        this.mActivity = activity;
        this.mDetaiData = data;
        wbk = n;
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
        if (wbk == 1 || wbk == 0) {
            ShowWaterCardNum.setHint(mDetaiData.getT_latest_index());
        }
        if (wbk == 2) {
            ShowWaterCardNum.setHint(mDetaiData.getNew_meter_value());
        }
        if (wbk == 3) {
            ShowWaterCardNum.setHint(mDetaiData.getT_meter_num().toString());
        }
        if (wbk == 4) {
            if (mDetaiData.getSealNo() != null) {
                ShowWaterCardNum.setHint(mDetaiData.getSealNo().toString());
            }
        }
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
                if (wbk == 0) {
                    try {
                        if (currentReadString.length() != 0) {
                            int currentRead = Integer.valueOf(currentReadString);
                            float currentReadWaterSum = Float.valueOf(WaterBudgetUtils.getCurrentWaterVloume(mDetaiData.getChange_meter_flag(), mDetaiData.getOld_meter_value(), mDetaiData.getNew_meter_value(), mDetaiData.getT_latest_index(), String.valueOf(currentRead)));
                            if (currentReadWaterSum >= 0) {
                                updateCurrentWaterReadNum(currentReadWaterSum, currentReadString);
                            } else {
                                if (SharedPreUtils.getReminderDialogStatus(mActivity)) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    builder.setTitle("提示，请确认以下信息");
                                    builder.setMessage("当前读数小于上期读数");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("确认", new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            updateCurrentWaterReadNum(currentReadWaterSum, currentReadString);
                                        }
                                    });
                                    builder.setNegativeButton("数据有误", new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                } else {
                                    Toast.makeText(mActivity, "当前手抄读数有误，请检查水表是否更换，若无更换，手抄读数要大于上期读数，若更换，请联系相关人员", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mActivity, "当前手抄读数不能为空", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        LogToFileUtils.write(e.toString());
                        if (SharedPreUtils.getReminderDialogStatus(mActivity)) {
                            CommanUtils.showReminderDialog(mActivity, "表册数据异常，请联系相关人员");
                        } else {
                            Toast.makeText(mActivity, "表册数据异常，请联系相关人员", Toast.LENGTH_SHORT).show();
                        }
                        this.dismiss();
                    }
                }
                if (wbk == 1) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String imgTime = simpleDateFormat.format(new java.util.Date());
                    ContentValues values = new ContentValues();
                    values.put("old_meter_value", currentReadString);
                    values.put("new_meter_value", String.valueOf(0));
                    values.put("change_meter_flag", "true");
                    values.put("t_cur_read_data", imgTime);
                    LitePal.updateAll(DetailData.class, values, "t_id = ?", mDetaiData.getT_id());
                    mActivity.changeTask(0);
                    this.dismiss();
                }
                if (wbk == 2) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String imgTime = simpleDateFormat.format(new java.util.Date());
                    ContentValues values = new ContentValues();
                    values.put("new_meter_value", String.valueOf(currentReadString));
                    values.put("t_cur_read_data", imgTime);
                    LitePal.updateAll(DetailData.class, values, "t_id = ?", mDetaiData.getT_id());
                    mActivity.changeTask(0);
                    this.dismiss();
                }
                if (wbk == 3) {
                    ContentValues values = new ContentValues();
                    values.put("t_meter_num", String.valueOf(currentReadString));
                    LitePal.updateAll(DetailData.class, values, "t_id = ?", mDetaiData.getT_id());
                    mActivity.changeTask(0);
                    this.dismiss();
                }
                if (wbk == 4) {
                    ContentValues values = new ContentValues();
                    values.put("sealNo", String.valueOf(currentReadString));
                    int i = LitePal.updateAll(DetailData.class, values, "t_id = ?", mDetaiData.getT_id());
                    mActivity.changeTask(0);
                    this.dismiss();
                }
                break;
        }
    }

    /**
     * @param currentReadWaterSum 当前使用用水量
     * @param currentReadString   当前表盘读数
     */
    public void updateCurrentWaterReadNum(float currentReadWaterSum, String currentReadString) {
        String messageHint = null;
        mActivity.getLocationInfo();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String imgTime = simpleDateFormat.format(new java.util.Date());
        ContentValues values = new ContentValues();
        values.put("t_cur_read_water_sum", String.valueOf(currentReadWaterSum));
        values.put("t_cur_meter_data", currentReadString);
        values.put("isChecked", "0");
        values.put("t_cur_read_data", imgTime);
        values.put("t_isManual", "0");
        if (mDetaiData.getAuto_detect_flag().equals("0")) {
            values.put("auto_detect_flag", "1");
            values.put("modify_detect_num", mDetaiData.getT_cur_meter_data());
        }
        LitePal.updateAll(DetailData.class, values, "t_id = ?", mDetaiData.getT_id());
        try {
            if (!QuantityCalculationUtils.WaterEstimat(mDetaiData.getT_average(), String.valueOf(currentReadWaterSum))) {
                messageHint = "本月用水量超过平均值";
            }
            if (SharedPreUtils.getReminderDialogStatus(mActivity)) {
                CommanUtils.showReminderDialog(mActivity, messageHint);
            } else {
                Toast.makeText(mActivity, messageHint, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            LogToFileUtils.write(e.toString());
        } finally {
            this.dismiss();
            mActivity.changeTask(0);
            if (GlobalData.setAutoJump) {
                mActivity.moveToMethod(9);
            }
        }
    }

}
