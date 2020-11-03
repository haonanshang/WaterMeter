package com.extended;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.leonardo.watermeter.R;

import java.util.List;

public class CancelUploadMessageAdapter extends BaseAdapter {
    private List<MessageBean> datas;
    private CancelUploadMessageActiviy mContext;

    public CancelUploadMessageAdapter(CancelUploadMessageActiviy mContext, List<MessageBean> datas) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cancelupload_listview, viewGroup, false);
            holder = new ViewHolder();
            holder.monthTextView = (TextView) convertView.findViewById(R.id.uploadMonth);
            holder.bchTextView = (TextView) convertView.findViewById(R.id.uploadStatisTicalForms);
            holder.cancelBt = (Button) convertView.findViewById(R.id.cancelUpload);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.monthTextView.setText(datas.get(i).getMonth());
        holder.bchTextView.setText(datas.get(i).getStatisTicalForms());
        holder.cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 撤销上传
                CancelUploadAsytask cancelUploadAsytask = new CancelUploadAsytask(mContext, i, datas.get(i).getMonth(), datas.get(i).getStatisTicalForms());
                cancelUploadAsytask.execute();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView monthTextView;
        TextView bchTextView;
        Button cancelBt;
    }
}
