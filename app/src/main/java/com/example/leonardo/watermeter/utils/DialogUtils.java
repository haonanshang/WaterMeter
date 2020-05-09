package com.example.leonardo.watermeter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DialogUtils {

    /**
     * 单选框
     *
     * @param context
     * @param choices      单选数据
     * @param checkedIteId 默认选项
     */
    public static void singleChoices(final Context context, String[] choices, int checkedIteId) {
        final int[] selectId = {checkedIteId};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, 3);
        builder.setTitle("设备选择");
        builder.setSingleChoiceItems(choices, checkedIteId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectId[0] = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreUtils.setDeviceType(context, selectId[0]);
                dialog.dismiss();
            }
        });
        builder.setCancelable(true)
                .create()
                .show();
    }
}
