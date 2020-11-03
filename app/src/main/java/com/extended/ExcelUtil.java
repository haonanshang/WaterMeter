package com.extended;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.example.leonardo.watermeter.entity.DetailData;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    /*
     * 将数据写入本地excel
     * @param month  月份
     * @param statisTicalForms 表册号
     * @param mDatas 数据列表
     */
    public static boolean writeExcel(Context context, String month, String statisTicalForms, List<DetailData> mDatas) {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WaterMeter/Excel";
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        String fileName = rootPath + File.separator + month + "-" + statisTicalForms + ".xls";
        Workbook mExcelWorkbook = new HSSFWorkbook();
        //创建execl中的一个表
        Sheet sheet = mExcelWorkbook.createSheet();
        mExcelWorkbook.setSheetName(0, "表册数据");
        //创建标题栏1
        CreatExcelTitle(sheet);
        //将内容写入excel
        WriteDatasToExcel(sheet, mDatas, context);
        try {
            writeFile(mExcelWorkbook, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (new File(fileName).exists()) {
            return true;
        } else {
            return false;
        }
    }

    /*
     *将数据写入指定的excel
     */
    private static void WriteDatasToExcel(Sheet sheet, List<DetailData> mDatas, Context mContext) {
        for (int i = 0; i < mDatas.size(); i++) {
            DetailData detailData = mDatas.get(i);
            Row row = sheet.createRow(sheet.getLastRowNum() + (i + 1));
            row.setHeightInPoints(30);
            row.createCell(0).setCellValue(detailData.getOrg_code());
            row.createCell(1).setCellValue(detailData.getT_phone_imei());
            row.createCell(2).setCellValue(detailData.getT_id());
            row.createCell(3).setCellValue(detailData.getT_card_num());
            row.createCell(4).setCellValue(detailData.getT_normal_detect());
            row.createCell(5).setCellValue(detailData.getT_jblx());
            row.createCell(6).setCellValue(detailData.getT_cur_read_water_sum());
            row.createCell(7).setCellValue(detailData.getT_cur_meter_data());
            row.createCell(8).setCellValue(detailData.getT_x());
            row.createCell(9).setCellValue(detailData.getT_y());
            row.createCell(10).setCellValue(detailData.getT_cur_read_data());
            row.createCell(11).setCellValue(detailData.getT_cbyf());
            row.createCell(12).setCellValue(detailData.getT_eid());
            row.createCell(13).setCellValue(detailData.getT_eid_bz());
            row.createCell(14).setCellValue(detailData.getT_new_phonenumber());
            row.createCell(15).setCellValue(detailData.getT_bz());
            row.createCell(16).setCellValue(detailData.getT_isManual());
            row.createCell(17).setCellValue(detailData.getAuto_detect_flag());
            row.createCell(18).setCellValue(detailData.getModify_detect_num());
            try {
                row.createCell(19).setCellValue(mContext.getPackageManager().getPackageInfo(mContext.getApplicationContext().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *创建标题栏
     */
    private static void CreatExcelTitle(Sheet sheet) {
        Row titleRow1 = sheet.createRow(0);
        // 设置标题栏高度
        titleRow1.setHeightInPoints(20);
        titleRow1.createCell(0).setCellValue("水司编号");
        titleRow1.createCell(1).setCellValue("imei号");
        titleRow1.createCell(2).setCellValue("任务id");
        titleRow1.createCell(3).setCellValue("卡号");
        titleRow1.createCell(4).setCellValue("是否正常识别");
        titleRow1.createCell(5).setCellValue("水表状态");
        titleRow1.createCell(6).setCellValue("当期用水量");
        titleRow1.createCell(7).setCellValue("表盘数");
        titleRow1.createCell(8).setCellValue("经度");
        titleRow1.createCell(9).setCellValue("纬度");
        titleRow1.createCell(10).setCellValue("抄表日期");
        titleRow1.createCell(11).setCellValue("表册日期");
        titleRow1.createCell(12).setCellValue("水箱位置");
        titleRow1.createCell(13).setCellValue("水表位置");
        titleRow1.createCell(14).setCellValue("新手机号");
        titleRow1.createCell(15).setCellValue("备注");
        titleRow1.createCell(16).setCellValue("是否手抄");
        titleRow1.createCell(17).setCellValue("自动识别标志");
        titleRow1.createCell(18).setCellValue("自动识别值修改");
        titleRow1.createCell(19).setCellValue("版本号");
    }

    /**
     * 将Excle表格写入文件中
     *
     * @param workbook
     * @param fileName
     */
    private static void writeFile(Workbook workbook, String fileName) throws IOException {
        File excelFile = new File(fileName);
        if (excelFile.exists()) {
            excelFile.delete();
        }
        excelFile.createNewFile();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(excelFile);
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}




