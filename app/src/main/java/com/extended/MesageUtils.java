package com.extended;

import android.content.ContentValues;

import com.example.leonardo.watermeter.entity.DetailData;

import org.litepal.crud.DataSupport;

import java.net.DatagramSocket;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class MesageUtils {
    /*
     * 更改已经上传表册的上传状态
     * @param month 月份
     * @param  statisTicalForms 表册号
     */
    public static boolean cancelUploadState(String month, String statisTicalForms) {
        boolean result = false;
        ContentValues values = new ContentValues();
        values.put("isUpload", "1");
        DataSupport.updateAll(DetailData.class, values, " t_cbyf = ? and t_volume_num = ?", month, statisTicalForms);
        List<DetailData> datas = DataSupport.where(" t_cbyf = ? and t_volume_num = ? and isUpload= ?", month, statisTicalForms, "0").find(DetailData.class);
        if (datas.size() > 0) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    /*
     *根据月份 表册号 查找已经拍照但是未上传的数据
     */
    public static List<DetailData> getDetaiDataListByFields(String month, String statisTicalForms) {
        return DataSupport.where(" t_cbyf = ? and t_volume_num = ? and isUpload= ? and isChecked=?", month, statisTicalForms, "1", "0").find(DetailData.class);
    }

    /*
     *定制排序 允许重复
     */
    public static TreeSet getDescendingOrderTreeSet() {
        TreeSet<Integer> descendingOrderSet = new TreeSet<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                // TODO Auto-generated method stub
                // 降序排列
                if (o1 > o2) {
                    return -1;
                } else if (o1 == o2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return descendingOrderSet;
    }

}
