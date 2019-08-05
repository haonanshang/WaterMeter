package com.extended;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.FireHydrant.entity.FireHydrantBchData;
import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;
import com.example.leonardo.watermeter.utils.DBHelper;

import org.litepal.crud.DataSupport;

import java.security.spec.PSSParameterSpec;

public class MessageDialog {
    public static ProgressDialog progressDialog;

    /*
     * dialog显示
     * @param context  上下文
     * @param title 标题
     * @param message
     */
    public static void showDialog(Context context, String title, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    /*
     *dialog消失
     */

    public static void dismissDialog() {
        progressDialog.dismiss();
    }

}
