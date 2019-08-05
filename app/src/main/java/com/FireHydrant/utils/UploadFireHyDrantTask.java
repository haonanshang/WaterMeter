package com.FireHydrant.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.example.leonardo.watermeter.utils.JsonDataUtils;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 *消防栓上传
 */
public class UploadFireHyDrantTask extends AsyncTask<Void, Integer, Boolean> {
    public List<FireHydrantDetailData> uploadDataList;
    public MonthListViewActivity mcontext;
    public ProgressDialog uploadDialog;
    public int CurrentUploadIndex = 0;
    public int TotalUploadNum = 0;
    public boolean IsUpload = false;
    int uploadNum = 1;
    int publishProgressNum = 0;
    int currentUploadNum = 0;

    public UploadFireHyDrantTask(List<FireHydrantDetailData> uploadDataList, MonthListViewActivity mcontext, int uploadNum) {
        this.uploadDataList = uploadDataList;
        this.mcontext = mcontext;
        TotalUploadNum = uploadDataList.size();
        this.uploadNum = uploadNum;
        IsUpload = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        uploadDialog = new ProgressDialog(mcontext);
        uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadDialog.setTitle("上传进度");
        uploadDialog.setMessage("上传进行中，请勿进行其他操作！");
        uploadDialog.setCancelable(false);
        //取消当前上传进度
        uploadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    CancelUpload(false);
                }
                return false;
            }
        });
        uploadDialog.setMax(TotalUploadNum);
        uploadDialog.setProgress(0);
        uploadDialog.show();
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        System.out.println("上传的数据总数为：" + uploadDataList.size());
        List<FireHydrantDetailData> uploadCurrentList = new ArrayList<>();
        for (int i = 0; i < uploadDataList.size(); i++) {
            if (IsUpload) {
                System.out.println("uploadCurrentList的size为：" + uploadCurrentList.size());
                System.out.println("开始上传第 " + i + " 条数据");
                uploadCurrentList.add(uploadDataList.get(i));
                if (uploadCurrentList.size() == uploadNum || i == TotalUploadNum - 1) {
                    currentUploadNum = uploadCurrentList.size();
                    publishProgressNum += currentUploadNum;
                    boolean flag = uploadTask(new JsonDataUtils().GetFireHydrantJsonString(uploadCurrentList));
                    System.out.println("上传任务的结果为：" + flag);
                    if (flag) {
                        for (FireHydrantDetailData currentData : uploadCurrentList) {
                            ContentValues values = new ContentValues();
                            values.put("isUpload", "0");
                            DataSupport.updateAll(FireHydrantDetailData.class, values, "fire_hydrant_id= ?", currentData.getFire_hydrant_id());
                            CurrentUploadIndex += 1;
                        }
                    }
                    uploadCurrentList.clear();
                    publishProgress(publishProgressNum);
                }
            } else {
                System.out.println("上传中断，当前上传进度为：" + CurrentUploadIndex);
                break;
            }
        }
        return IsUpload;
    }

    /*
     *上传json数组
     */
    private boolean uploadTask(String Json) {
        String result = "";
        BufferedReader reader = null;
        boolean flag = false;
        try {
            URL url = new URL("http://" + new SharedPreUtils().GetIp(mcontext) + "/services/phone/uploadFireHydrantTask");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            //"application/x-www-form-urlencoded"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
            conn.setRequestProperty("accept", "application/json");
            // 往服务器里面发送数据
            if (Json != null && !TextUtils.isEmpty(Json)) {
                byte[] writebytes = Json.getBytes();
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
            }
            System.out.println("responseCode:" + conn.getResponseCode());
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
                JSONObject jsonObject = new JSONObject(result);
                flag = jsonObject.getBoolean("flag");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        System.out.println("----当前进度条为：" + values[0]);
        uploadDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        System.out.println("上传结束");
        System.out.println("CurrentUploadIndex的下标为：" + CurrentUploadIndex);
        if (aVoid) {
            if (CurrentUploadIndex == TotalUploadNum) {
                Toast.makeText(mcontext, "上传数据成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mcontext, "上传数据部分失败，请检查表册信息", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mcontext, "上传数据中断", Toast.LENGTH_SHORT).show();
        }
        publishProgressNum = 0;
        currentUploadNum = 0;
        CurrentUploadIndex = 0;
        TotalUploadNum = 0;
        uploadDialog.dismiss();
        mcontext.TaskResult();
    }

    public void CancelUpload(boolean iscancel) {
        IsUpload = iscancel;
    }
}
