package com.extended;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.leonardo.watermeter.ui.MonthListViewActivity;

public class CustomYearDialog {
    private MonthListViewActivity activity;
    public String[] years = {"2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027"};
    public AlertDialog dialog;
    public String updataYearString = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CustomYearDialog(MonthListViewActivity activity) {
        this.activity = activity;
    }

    /*
     *展示dialog
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Holo_Light_Dialog);
        builder.setTitle("选择对应年份");
        builder.setSingleChoiceItems(years, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updataYearString = years[i];
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (updataYearString != null) {
                    new SharedPreferencesUtils(activity).updateYearString(updataYearString);
                    dialog.dismiss();
                    activity.updateData();
                } else {
                    Toast.makeText(activity, "未选择任何年份，请选择年份", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /*
     *dialog消失
     */
    public void dissmissDialog() {
        dialog.dismiss();
    }


}
