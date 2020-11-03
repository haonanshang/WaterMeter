package com.extended;

import android.os.AsyncTask;
import android.widget.Toast;

public class CancelUploadAsytask extends AsyncTask<Void, Void, Boolean> {
    private String month;
    private String statisTicalForms;
    private CancelUploadMessageActiviy mContext;
    private int postion;//记录移除数据的位置
    public CancelUploadAsytask(CancelUploadMessageActiviy context,int postion, String month, String statisTicalForms) {
        this.mContext = context;
        this.postion=postion;
        this.month = month;
        this.statisTicalForms = statisTicalForms;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MessageDialog.showDialog(mContext, "正在撤销上传", "勿进行任何操作，请耐心等待");
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return MesageUtils.cancelUploadState(month, statisTicalForms);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        MessageDialog.dismissDialog();
        if (aBoolean) {
            Toast.makeText(mContext, "撤销成功", Toast.LENGTH_SHORT).show();
            mContext.UpdateDatas(postion);
        } else {
            Toast.makeText(mContext, "撤销失败", Toast.LENGTH_SHORT).show();
        }
    }
}
