package com.example.leonardo.watermeter.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.MyApplication;
import com.example.leonardo.watermeter.ui.TaskShowActivity;
import com.itgoyo.logtofilelibrary.LogToFileUtils;
import com.shuibiao.jni.JNIInterface;

import org.apache.axis.utils.StringUtils;

public class FaceThread extends AsyncTask<Void, Void, String> {
    private TaskShowActivity mContext;
    String imgPath;
    ProgressDialog progressDialog;
    long startTime = 0;
    private static String tag = "waterMeter";

    public FaceThread(TaskShowActivity context, String imgPath) {
        this.mContext = context;
        this.imgPath = imgPath;
        startTime = System.currentTimeMillis();
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("正在识别中...");
        progressDialog.setMessage("请耐心等待");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = null;
        try {
            int[] recogNumRes = new int[8];
            float[] numScore = new float[1];
            int[] numsLen = new int[1];
            JNIInterface jniInterface = MyApplication.getInstance().getJniInterface();
            int perocessResult = jniInterface.processImgPathNum(jniInterface.getMhandler(), imgPath, recogNumRes, numScore, numsLen);
            if (perocessResult == 2) {
                StringBuffer recogBuffer = new StringBuffer();
                for (int i = 0; i < numsLen[0]; i++) {
                    recogBuffer.append(recogNumRes[i]);
                }
                result = recogBuffer.toString();
            }
        } catch (Exception e) {
            LogToFileUtils.write(e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        Log.i(tag, "process result " + s);
        if (!StringUtils.isEmpty(s)) {
            mContext.updateMeterData(s);
        } else {
            Toast.makeText(mContext, "水表读数未识别，请重新拍照", Toast.LENGTH_SHORT).show();
        }
    }
}
