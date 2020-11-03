package com.example.leonardo.watermeter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import com.example.leonardo.watermeter.global.GlobalData;

/**
 * 通用型工具类
 */
public class CommanUtils {
    /**
     * 提示dialog
     *
     * @param context
     * @param msg
     */
    public static void showReminderDialog(Context context, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = null;
        builder.setTitle("提示，请确认以下信息");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            dialogInterface.dismiss();
                        }
                    });
                }
            }
        });
        builder.create().show();
    }

    /**
     * 判断是否是特别的品牌
     *
     * @return
     */
    public static boolean isUniqueBrand() {
        for (String brand : GlobalData.UniQueBrandArray) {
            if (brand.equals(GlobalData.Brand.trim())) {
                return true;
            }
        }
        return false;
    }
}
