package com.example.leonardo.watermeter.utils;

import android.os.AsyncTask;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.FireHydrant.utils.UploadFireHyDrantTask;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UploadTaskNum extends AsyncTask<Void, Void, Integer> {
    public List<DetailData> uploadDataList;
    public List<FireHydrantDetailData> fireHydrantUploadDataList;
    public MonthListViewActivity mcontext;
    private boolean isFireHydrant;//区分消防栓还是普通水司
    String urlPath = "http://106.14.33.55:8080/services/phone/getBatchUploadQuantity";

    public UploadTaskNum(List uploadDataList, MonthListViewActivity mcontext, boolean isFireHydrant) {
        this.isFireHydrant = isFireHydrant;
        if (isFireHydrant) {
            this.fireHydrantUploadDataList = uploadDataList;
        } else {
            this.uploadDataList = uploadDataList;
        }
        this.mcontext = mcontext;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            URL url = new URL(urlPath);
            String responseString = "";
            int responseCode = 0;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                //请求成功 获得返回的流
                System.out.println("返回内容：" + responseString);
                InputStream is = connection.getInputStream();
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                responseString = buf.readLine();
                buf.close();
                is.close();
                System.out.println("返回内容：" + responseString);
                return Integer.valueOf(responseString);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer num) {
        super.onPostExecute(num);
        System.out.println("uplaodTaskNum的result为：" + num);
        if (isFireHydrant) {
            new UploadFireHyDrantTask(fireHydrantUploadDataList, mcontext, 1).execute();
        } else {
            new UploadTask(uploadDataList, mcontext, num).execute();
        }
    }
}
