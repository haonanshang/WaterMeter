package com.example.leonardo.watermeter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.WaterYieldBean;

import java.util.List;

public class RecyWaterYieldAdapter extends RecyclerView.Adapter<RecyWaterYieldAdapter.VH> {
    private Context mContext;
    private List<WaterYieldBean> mDatas;

    public RecyWaterYieldAdapter(Context mContext, List<WaterYieldBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public static class VH extends RecyclerView.ViewHolder {
        public final TextView waterYield;

        public VH(View v) {
            super(v);
            waterYield = (TextView) v.findViewById(R.id.water_yield);
        }
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_item_water_yield, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.waterYield.setText(mDatas.get(position).getWaterYield());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
                showAllMessage(mDatas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 清空数据源
     */
    public void clearDatas() {
        mDatas.clear();
        this.notifyDataSetChanged();
    }

    /*
     *显示水量的具体信息
     */
    public void showAllMessage(WaterYieldBean waterYieldBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.show_water_yield_message, null);
        TextView used_value_tv = view.findViewById(R.id.used_value);
        used_value_tv.setText(waterYieldBean.getWaterYield());
        TextView used_data_tv = view.findViewById(R.id.used_date);
        used_data_tv.setText(waterYieldBean.getWaterDate());
        builder.setTitle("用水详情信息").setView(view);
        builder.show();
    }

}
