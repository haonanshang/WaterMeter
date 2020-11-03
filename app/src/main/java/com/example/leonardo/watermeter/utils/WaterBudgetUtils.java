package com.example.leonardo.watermeter.utils;


import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.leonardo.watermeter.entity.WaterYieldBean;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 水量计算工具
 */
public class WaterBudgetUtils {
    /**
     * 获取当前用水量
     *
     * @param change_meter_flag 换表标志 是否换表 true代表换表 false 代表未换表
     * @param old_meter_value   老表读数
     * @param new_meter_value   新表读数
     * @param t_latest_index    上期读数
     * @param t_cur_meter_data  本期读数
     */
    public static String getCurrentWaterVloume(String change_meter_flag, String old_meter_value, String new_meter_value, String t_latest_index, String t_cur_meter_data) {
        if (change_meter_flag.equals("true")) {
            //TODO 换表
            float oldMeterValue = 0, newMeterValue = 0, lastIndex = 0, curMeterData = 0;
            if (!StringUtils.isEmpty(old_meter_value)) {
                oldMeterValue = Float.valueOf(old_meter_value);
            }
            if (!StringUtils.isEmpty(new_meter_value)) {
                newMeterValue = Float.valueOf(new_meter_value);
            }
            if (!StringUtils.isEmpty(t_latest_index)) {
                lastIndex = Float.valueOf(t_latest_index);
            }
            if (!StringUtils.isEmpty(t_cur_meter_data)) {
                curMeterData = Float.valueOf(t_cur_meter_data);
            }
            return String.valueOf((oldMeterValue - lastIndex) + (curMeterData - newMeterValue));

        } else {
            //TODO 未换表
            float lastIndex = 0, curMeterData = 0;
            if (!StringUtils.isEmpty(t_latest_index)) {
                lastIndex = Float.valueOf(t_latest_index);
            }
            if (!StringUtils.isEmpty(t_cur_meter_data)) {
                curMeterData = Float.valueOf(t_cur_meter_data);
            }
            return String.valueOf((curMeterData - lastIndex));
        }
    }

    /**
     * 获取用水量bean 默认是12个 超过12个截取  不足 补足
     *
     * @param detail_date_list 当前所有已超月份列表 ，分割
     * @param used_value_list  已超月份水量的使用情况  对应月份列表
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<WaterYieldBean> getWaterYieldList(String detail_date_list, String used_value_list) {
        Map<String, String> waterYieldMap = new LinkedHashMap<String, String>();
        if (!StringUtils.isEmpty(detail_date_list) && !StringUtils.isEmpty(used_value_list)) {
            String[] detailDataArray = detail_date_list.split(",");
            String[] usedValueArray = used_value_list.split(",");
            for (int i = 0; i < detailDataArray.length; i++) {
                String detailData = detailDataArray[i];
                String usedValue = null;
                try {
                    usedValue = usedValueArray[i];
                } catch (Exception e) {

                }
                waterYieldMap.put(detailData, usedValue);
            }
        }
        List<WaterYieldBean> waterYieldBeanList = new ArrayList<>();
        if (waterYieldMap.size() >= 12) {
            Set<String> waterYieldSet = waterYieldMap.keySet();
            // 倒序截取 最近12个月的数据
            List<String> detailDataList = waterYieldSet.stream().sorted(Comparator.reverseOrder()).limit(12).collect(Collectors.toList());
            for (String detailData : detailDataList) {
                waterYieldBeanList.add(new WaterYieldBean(waterYieldMap.get(detailData), detailData));
            }
        } else if (waterYieldMap.size() > 0 && waterYieldMap.size() < 12) {
            Set<String> waterYieldSet = waterYieldMap.keySet();
            //TODO 倒序展示，后面数据默认是正序显示，因此加到头
            for (int i = waterYieldMap.size(); i < 12; i++) {
                waterYieldBeanList.add(new WaterYieldBean(null, null));
            }
            for (String detail_data : waterYieldSet) {
                waterYieldBeanList.add(new WaterYieldBean(waterYieldMap.get(detail_data), detail_data));
            }
        } else {
            for (int i = 0; i < 12; i++) {
                waterYieldBeanList.add(new WaterYieldBean(null, null));
            }
        }
        return waterYieldBeanList;
    }
}
