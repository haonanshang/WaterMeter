package com.example.leonardo.watermeter.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.leonardo.watermeter.download.DownloadDialog;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;

import org.apache.axis.utils.StringUtils;
import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Leonardo on 2017/4/26.
 */

public class DownloadData {
    DownloadData.GetBCdata getBCdata;
    DownloadData.GetTaskData getTaskData;
    String TAG = "waterMeter";

    public DownloadData() {
    }

    public void GetBCdata(String imei, String cbyf, MonthListViewActivity context) {
        getBCdata = new DownloadData.GetBCdata(imei, cbyf, context);
        getBCdata.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetTaskData(MonthListViewActivity mContext, String imei, String cbyf, String[] bcList) {
        getTaskData = new DownloadData.GetTaskData(mContext, imei, cbyf, bcList);
        getTaskData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class GetBCdata extends AsyncTask<Void, Void, String> {
        String imei = "";
        String cbyf = "";
        String urlPath;
        ProgressDialog progressDialog;
        MonthListViewActivity context;

        public GetBCdata(String imei, String cbyf, MonthListViewActivity context) {
            this.imei = imei;
            this.cbyf = cbyf;
            this.context = context;
            if (SharedPreUtils.getDataType(context)) {
                urlPath = "http://" + new SharedPreUtils().GetIp(context) + "/services/phone/getFireHydrantBookletNO?" + "imei=" + imei + "&cbyf=" + cbyf;//消防栓下载表册的方法名
            } else {
                urlPath = "http://" + new SharedPreUtils().GetIp(context) + "/services/phone/getUserBookletNO?" + "imei=" + imei + "&cbyf=" + cbyf;//默认普通水司的方法名
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("获取表册列表中");
            progressDialog.setMessage("正在获取表册列表中，请勿进行其他操作");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {
            String responseString = null;
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(urlPath).build();
                okhttp3.Response response = null;
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseString = response.body().string();
                    System.out.println("联网-表册号返回内容：" + responseString);
                } else {
                    Log.i(TAG, "okHttp is request error");
                }
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                responseString = "netError";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!StringUtils.isEmpty(s)) {
                if (!s.equals("netError")) {
                    final String[] bcList = s.split(",");
                    DownloadDialog downloadDialog1 = new DownloadDialog(context, cbyf, bcList);
                    downloadDialog1.show();
                    downloadDialog1.setCancelable(true);
                } else {
                    Toast.makeText(context, "当前网络异常", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "表册号列表数据为空", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private class GetTaskData extends AsyncTask<Void, Integer, String> {
        String imei = "";
        String cbyf = "";
        String[] bcList;
        String urlPath;
        MonthListViewActivity mContext;
        ProgressDialog progressDialog;
        int downloadTotal = 0;

        public GetTaskData(MonthListViewActivity context, String imei, String cbyf, String[] bcList) {
            this.mContext = context;
            this.imei = imei;
            this.cbyf = cbyf;
            this.bcList = bcList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("下载表册数据");
            progressDialog.setMessage("下载进行中，请不要进行其他操作！");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < bcList.length; i++) {
                try {
                    boolean isFireHydrant = SharedPreUtils.getDataType(mContext);
                    if (isFireHydrant) {
                        urlPath = "http://" + new SharedPreUtils().GetIp(mContext) + "/services/phone/getFireHydrantReadingTask?imei=" + imei + "&cbyf=" + cbyf + "&bch=" + bcList[i];
                    } else {
                        urlPath = "http://" + new SharedPreUtils().GetIp(mContext) + "/services/phone/getMeterReadingTask?imei=" + imei + "&cbyf=" + cbyf + "&bch=" + bcList[i];
                    }
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(urlPath).build();
                    okhttp3.Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        InputStream inputStream = response.body().byteStream();
                        List detailDataList = null;
                        if (isFireHydrant) {
                            detailDataList = ParseXml.getFireHydrantDetailData(inputStream);
                        } else {
                            //获取阶梯水价
                            String ladderJson = getLadderPrice();
                            JSONObject.parseObject(ladderJson);
                            JSONObject jsonObject = (JSONObject) JSONObject.parseObject(ladderJson);
                            String resultCode = jsonObject.getString("result");
                            String ladder_price = null;
                            if (resultCode.equals("0")) {
                                ladder_price = jsonObject.getJSONArray("ladder_price_config").toString();
                            }
                            detailDataList = ParseXml.getDetailData(inputStream, cbyf, ladder_price);
                        }
                        if (detailDataList.size() > 0) {
                            Log.i(TAG, "download bch is " + bcList[i]);
                            if (DBHelper.checkDownloadData(mContext, cbyf, bcList[i])) {
                                DBHelper.deleteDownLoadData(mContext, cbyf, bcList[i]);
                            }
                            DataSupport.saveAll(detailDataList);//将解析后的数据存入数据库
                            DBHelper.saveBCToDB(new String[]{bcList[i]}, cbyf, mContext);
                            downloadTotal++;
                        }
                    } else {
                        Log.i(TAG, "okHttp is request error");
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * 获取阶梯水价json数据
         *
         * @return
         */
        public String getLadderPrice() {
            //String ladderPricePath = "http://" + new SharedPreUtils().GetIp(mContext) + "/services/phone/getLadderPriceConfig?imei=867227039028569";
            String ladderPricePath = "http://" + new SharedPreUtils().GetIp(mContext) + "/services/phone/getLadderPriceConfig?imei=" + imei;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(ladderPricePath).build();
            InputStream inputStream = null;
            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    inputStream = response.body().byteStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        bos.write(buffer, 0, length);
                    }
                    result = bos.toString(StandardCharsets.UTF_8.name());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (downloadTotal != bcList.length) {
                Toast.makeText(mContext, "下载部分数据失败，请检查数据是否完整", Toast.LENGTH_SHORT).show();
            }
            mContext.updateData(); //更新下载数据状态
            progressDialog.dismiss();
        }
    }

}
