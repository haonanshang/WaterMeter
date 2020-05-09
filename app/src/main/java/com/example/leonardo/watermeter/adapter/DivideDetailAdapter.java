package com.example.leonardo.watermeter.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.DetailDivideData;

import org.litepal.LitePal;

import java.util.List;

public class DivideDetailAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<DetailDivideData> mDatas;

    public DivideDetailAdapter(Context mContext, List<DetailDivideData> mDatas) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        DetailDivideData detailDivideData = mDatas.get(i);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_detail_divide_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dividerNumber = convertView.findViewById(R.id.divideNumber);
            viewHolder.dividerOrder = convertView.findViewById(R.id.divideOrder);
            viewHolder.detailDoneCount = convertView.findViewById(R.id.detailDoneCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dividerNumber.setText(detailDivideData.getBch() + " - " + mDatas.get(i).getDivideNumber());
        viewHolder.dividerOrder.setText(detailDivideData.getStartIndex() + " - " + detailDivideData.getEndIndex());
        int detailDataDoneCount = LitePal.where("t_cbyf = ? and t_volume_num = ? and divideNumber = ? and isChecked = ? ", detailDivideData.getCbyf(), detailDivideData.getBch(), detailDivideData.getDivideNumber(), "0").count(DetailData.class);
        viewHolder.detailDoneCount.setText("已抄: " + detailDataDoneCount);
        return convertView;
    }

    private final class ViewHolder {
        TextView dividerNumber;
        TextView dividerOrder;
        TextView detailDoneCount;
    }
}
