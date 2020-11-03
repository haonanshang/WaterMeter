package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.widget.Toast;

import com.FireHydrant.entity.FireHydrantBchData;
import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.entity.BchData;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.DetailDivideData;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 2017/5/4.
 */

public class DBHelper {
    /**
     * 将表册号存储到数据库当中
     * 区别是普通水司还是消防栓水司
     *
     * @param bcList
     * @param cbyf
     * @param context
     */
    public static void saveBCToDB(String[] bcList, String cbyf, Context context) {
        if (!SharedPreUtils.getDataType(context)) {
            List<BchData> bchDataList = new ArrayList<BchData>();
            for (int i = 0; i < bcList.length; i++) {
                BchData bchData = new BchData();
                bchData.setCbyf(cbyf);
                bchData.setBch(bcList[i]);
                bchData.setIsDetectByPhone("1");
                bchDataList.add(bchData);
            }
            if (!bchDataList.isEmpty()) {
                LitePal.saveAll(bchDataList);
            } else {
                Toast.makeText(context, "存储失败！", Toast.LENGTH_SHORT).show();
            }
        } else {
            List<FireHydrantBchData> bchDataList = new ArrayList<>();
            for (int i = 0; i < bcList.length; i++) {
                FireHydrantBchData bchData = new FireHydrantBchData();
                bchData.setCbyf(cbyf);
                bchData.setBch(bcList[i]);
                bchDataList.add(bchData);
            }
            if (!bchDataList.isEmpty()) {
                LitePal.saveAll(bchDataList);
            } else {
                Toast.makeText(context, "存储失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 删除已经下载的数据
     * 区分普通数据和消防栓类型数据
     *
     * @param cbyf
     * @param bch
     */
    public static void deleteDownLoadData(Context mContext, String cbyf, String bch) {
        boolean isFireHydrant = SharedPreUtils.getDataType(mContext);
        if (isFireHydrant) {
            LitePal.deleteAll(FireHydrantDetailData.class, "detail_date= ? and booklet_no = ? ", cbyf, bch);
        } else {
            LitePal.deleteAll(DetailData.class, "t_cbyf = ? and t_volume_num = ? ", cbyf, bch);
            LitePal.deleteAll(BchData.class, "cbyf = ? and bch = ?", cbyf, bch);
            LitePal.deleteAll(DetailDivideData.class, "cbyf = ? and bch = ?", cbyf, bch);
        }
    }

    /**
     * 检查本地是否有下载数据
     * 区分普通数据和消防栓数据
     *
     * @param cbyf
     * @param bch
     * @return
     */
    public static boolean checkDownloadData(Context mContext, String cbyf, String bch) {
        int count = 0;
        boolean isFireHydrant = SharedPreUtils.getDataType(mContext);
        if (isFireHydrant) {
            count = LitePal.where("cbyf = ? and bch = ? ", cbyf, bch).count(FireHydrantBchData.class);
        } else {
            count = LitePal.where("cbyf = ? and bch = ? ", cbyf, bch).count(BchData.class);
        }
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新分类数据
     */
    public static void updateDivideData(String cbyf, String bch) {
        List<DetailDivideData> mDatas = LitePal.where("cbyf = ? and bch = ?", cbyf, bch).find(DetailDivideData.class);
        for (int i = 0; i < mDatas.size(); i++) {
            DetailDivideData detailDivideData = mDatas.get(i);
            String startIndex = LitePal.where("t_cbyf = ? and t_volume_num = ? and divideNumber = ? ", detailDivideData.getCbyf(), detailDivideData.getBch(), detailDivideData.getDivideNumber()).findFirst(DetailData.class).getT_volume_order();
            String endIndex = LitePal.where("t_cbyf = ? and t_volume_num = ? and divideNumber = ? ", detailDivideData.getCbyf(), detailDivideData.getBch(), detailDivideData.getDivideNumber()).findLast(DetailData.class).getT_volume_order();
            detailDivideData.setStartIndex(startIndex);
            detailDivideData.setEndIndex(endIndex);
            detailDivideData.updateAll("cbyf= ? and bch = ? and divideNumber = ? ", detailDivideData.getCbyf(), detailDivideData.getBch(), detailDivideData.getDivideNumber());
        }
    }
}
