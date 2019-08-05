package com.example.leonardo.watermeter.utils;


import com.example.leonardo.watermeter.entity.WaterYieldBean;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            int count = 0;
            for (String detail_data : waterYieldSet) {
                if (count == 12) {
                    break;
                } else {
                    waterYieldBeanList.add(new WaterYieldBean(waterYieldMap.get(detail_data), detail_data));
                }
            }
        } else if (waterYieldMap.size() > 0 && waterYieldMap.size() < 12) {
            Set<String> waterYieldSet = waterYieldMap.keySet();
            for (String detail_data : waterYieldSet) {
                waterYieldBeanList.add(new WaterYieldBean(waterYieldMap.get(detail_data), detail_data));
            }
            for (int i = waterYieldBeanList.size() - 1; i < 12; i++) {
                waterYieldBeanList.add(new WaterYieldBean(null, null));
            }
        } else {
            for (int i = 0; i < 12; i++) {
                waterYieldBeanList.add(new WaterYieldBean(null, null));
            }
        }
        return waterYieldBeanList;
    }
}
