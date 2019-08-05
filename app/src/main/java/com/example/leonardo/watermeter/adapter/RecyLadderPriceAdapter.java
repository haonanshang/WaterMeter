package com.example.leonardo.watermeter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueToothPrinter.NumberToCH;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.UsedLadderPriceBean;
import com.example.leonardo.watermeter.entity.WaterYieldBean;

import java.util.List;

public class RecyLadderPriceAdapter extends RecyclerView.Adapter<RecyLadderPriceAdapter.VH> {
    private Context mContext;
    private List<UsedLadderPriceBean> mDatas;

    public RecyLadderPriceAdapter(Context mContext, List<UsedLadderPriceBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public static class VH extends RecyclerView.ViewHolder {
        public final TextView ladderStep;
        public final TextView ladderPrice;

        public VH(View v) {
            super(v);
            ladderStep = (TextView) v.findViewById(R.id.ladder_step);
            ladderPrice = v.findViewById(R.id.ladder_price);
        }
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_ladder_price_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.ladderStep.setText(NumberToCH.numberToCH(mDatas.get(position).getLadderStep()) + "档: ");
        holder.ladderPrice.setText(mDatas.get(position).getLadderPrice() + "元");
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}
