package com.objecteye.author;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

import com.MyApplication;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;
import com.syteco.android.hardwareinfo.deploy.AuthBody;
import com.syteco.android.hardwareinfo.deploy.DeploymentTool;
import com.syteco.android.hardwareinfo.utils.JsonUtils;
import com.syteco.android.hardwareinfo.utils.LogToFile;
import com.syteco.android.hardwareinfo.utils.SPUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class AuthorCustomUtils {

    static ProgressDialog dialog = null;

    /**
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 检查文件和授权码是否存在
     *
     * @return
     */
    public static boolean CheckLicence() {
        String authCode = "B23CFC7E7B42D204D7AF285D62BA0534A6AC577A6424C4233ABE06BFDC312778A";
        String result = DeploymentTool.analyzeVerificationCode(authCode);
        final AuthBody authBody1 = (AuthBody) JsonUtils.GSON.fromJson(result, AuthBody.class);
        String licPath = "/mnt/sdcard/syteco/" + authBody1.productSN + "/licence.bin";
        String onLineAuthCode = SPUtil.getString("set_product_sn_code" + authBody1.productSN, "-1");
        if (LogToFile.fileExsit(licPath) && onLineAuthCode.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示授权进度
     *
     * @param context
     */
    public static void showAuthorProcess(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("提示");
        dialog.setMessage("正在授权中，请耐心等待...");
        dialog.show();
    }

    /**
     * 关闭授权进度
     */
    public static void dissmisAuthorProcess() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 提示dialog
     *
     * @param context
     * @param msg
     */
    public static void showReminderDialog(Context context, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("授权失败");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });
        builder.create().show();
    }

}