package com.extended;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.leonardo.watermeter.entity.DetailData;

import java.util.List;

public class DownExcelAsytask extends AsyncTask<Void, Void, Boolean> {
    private String month;
    private String statisTicalForms;
    private Context mContext;

    public DownExcelAsytask(Context context, String month, String statisTicalForms) {
        this.mContext = context;
        this.month = month;
        this.statisTicalForms = statisTicalForms;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MessageDialog.showDialog(mContext, "正在导出数据到本地", "勿进行任何操作，请耐心等待");
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        List<DetailData> mDatas = MesageUtils.getDetaiDataListByFields(month, statisTicalForms);
        return ExcelUtil.writeExcel(mContext, month, statisTicalForms, mDatas);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        MessageDialog.dismissDialog();
        if (aBoolean) {
            Toast.makeText(mContext, "导出数据成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "导出数据失败", Toast.LENGTH_SHORT).show();
        }
    }
}
