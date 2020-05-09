package com.example.leonardo.watermeter.utils;

import android.util.Log;

import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.DetailDivideData;

import org.litepal.LitePal;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LoadSearchDataRunnable implements Runnable {
    private String cbyf;
    private String bch;
    private ConcurrentLinkedDeque<List<DetailData>> detailDataListAll;
    private DetailDivideData detailDivideData;

    public LoadSearchDataRunnable(String cbyf, String bch, ConcurrentLinkedDeque<List<DetailData>> detailDataListAll, DetailDivideData detailDivideData) {
        this.cbyf = cbyf;
        this.bch = bch;
        this.detailDataListAll = detailDataListAll;
        this.detailDivideData = detailDivideData;
    }

    @Override
    public void run() {
        long t0 = System.currentTimeMillis();
        List<DetailData> tempDetailDataList = LitePal.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order", "t_meter_num", "divideNumber").where("t_cbyf = ? and t_volume_num = ? and divideNumber = ? ", cbyf, bch, detailDivideData.getDivideNumber()).find(DetailData.class);
        detailDataListAll.add(tempDetailDataList);
        long t1 = System.currentTimeMillis();
        //Log.i("waterMeter", this.getClass().getName() + " " + Thread.currentThread().getName() + " -> divideNumber: " + detailDivideData.getDivideNumber() + " startIndex: " + detailDivideData.getStartIndex() + "  endIndex: " + detailDivideData.getEndIndex());
        //Log.i("waterMeter", this.getClass().getName() + " " + Thread.currentThread().getName() + " -> cost time: " + (t1 - t0));
    }
}
