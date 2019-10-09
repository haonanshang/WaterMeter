package com.example.leonardo.watermeter.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.UploadTaskBean;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class UploadTask extends AsyncTask<Void, Integer, Boolean> {
    public List<DetailData> uploadDataList;
    public MonthListViewActivity mcontext;
    public ProgressDialog uploadDialog;
    public int CurrentUploadIndex = 0;
    public int TotalUploadNum = 0;
    public boolean IsUpload = false;
    int uploadNum = 1;
    int publishProgressNum = 0;
    int currentUploadNum = 0;

    public UploadTask(List<DetailData> uploadDataList, MonthListViewActivity mcontext, int uploadNum) {
        this.uploadDataList = uploadDataList;
        this.mcontext = mcontext;
        TotalUploadNum = uploadDataList.size();
        this.uploadNum = uploadNum;
        IsUpload = true;
    }

    /*使用Soap上传信息*/
    public String uploadTask(String taskList, String imgBase64String, Context context) {
        //命名空间
        String nameSpace = "http://controller.wc.modules.vmrc.com/";
        //调用的方法名
        String methodName = "uploadTask";
        //EndPoint
        //String endPoint = "http://106.14.33.55:8080/services/webService?wsdl";
        String endPoint = "http://" + new SharedPreUtils().GetIp(context) + "/services/webService?wsdl";
        //SOAP Action
        String soapAction = "http://controller.wc.modules.vmrc.com/uploadTask";
        //指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);
        //创建HttpTransportSE对象，传递WebService服务器地址
        rpc.addProperty("taskList", taskList);
        rpc.addProperty("imgBase64String", imgBase64String);
        //生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = rpc;
        // 等价于envelope.bodyOut = rpc;
        //envelope.setOutputSoapObject(rpc);
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = false;
        HttpTransportSE transport = new HttpTransportSE(endPoint);
        transport.debug = true;
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();

        }
        // 获取返回的数据
        if (envelope.bodyIn instanceof SoapFault) {
            final SoapFault sf = (SoapFault) envelope.bodyIn;
            System.out.println("检测--soap调用的返回结果是：" + sf.faultstring);
            System.out.println("检测--soap调用的返回结果是[全集]：" + sf.toString());
            return sf.faultstring;
        } else {
            if (envelope.bodyIn != null) {
                //还不知道咋办
                SoapObject object = (SoapObject) envelope.bodyIn;
                String result = object.getProperty(0).toString();
                System.out.println("检测--第二种情况，结果是：" + result);
                return result;
            } else {
                return "";
            }
        }
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
        List<DetailData> uploadCurrentList = new ArrayList<>();
        for (int i = 0; i < uploadDataList.size(); i++) {
            if (IsUpload) {
                System.out.println("uploadCurrentList的size为：" + uploadCurrentList.size());
                System.out.println("开始上传第 " + i + " 条数据");
                uploadCurrentList.add(uploadDataList.get(i));
                if (uploadCurrentList.size() == uploadNum || i == TotalUploadNum - 1) {
                    currentUploadNum = uploadCurrentList.size();
                    publishProgressNum += currentUploadNum;
                    UploadTaskBean bean = new JsonDataUtils().GetJsonString(uploadCurrentList, mcontext);
                    String result = uploadTask(bean.getTaskList(), bean.getImgBase64String(), mcontext);
                    Log.e("zksy", "上传结果为:" + result);
                    if (result.equals("true")) {
                        for (DetailData currentData : uploadCurrentList) {
                            ContentValues values = new ContentValues();
                            values.put("isUpload", "0");
                            DataSupport.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
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
