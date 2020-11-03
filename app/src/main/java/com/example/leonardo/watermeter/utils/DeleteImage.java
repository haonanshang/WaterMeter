package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.os.Environment;

import com.example.leonardo.watermeter.global.GlobalData;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Administrator on 2018/1/25 0025.
 */

public class DeleteImage {

    public static void Delete(Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AddFiles(AddPath(mContext));
            }
        }).start();

    }

    private static void AddFiles(ArrayList<String> pathall) {
        ArrayList<File> FileAll = new ArrayList<>();
        TreeSet<Integer> totalset = new TreeSet<>();
        for (String filepath : pathall) {
            File fileAll = new File(filepath);
            File[] files = fileAll.listFiles();
            if (fileAll.exists()) {
                if (files.length > 0) {
                    for (File file : files) {
                        FileAll.add(file);
                        String monthString = file.getName().split("_")[0];
                        System.out.println("--------字符串截取的字头为：" + monthString);
                        int monthInteger = Integer.valueOf(monthString);
                        System.out.println("--------整型截取的字头为：" + monthString);
                        totalset.add(monthInteger);
                    }
                }
                System.out.println("-------当前所有存在的数据为" + totalset.toString());
                System.out.println("-------当前所有存在的图片数量为：" + FileAll.size());
            }
        }
        int num = totalset.size();
        if (num > 3) {
            ArrayList<Integer> monthall = new ArrayList<>(totalset);
            System.out.println("------------所有数据为：" + monthall.toString());
            totalset.clear();
            System.out.println("---------1经过整理后存在的数据为：" + totalset.toString());
            for (int i = 0; i < (num - 3); i++) {
                totalset.add(monthall.get(i));
            }
            System.out.println("---------2经过整理后存在的数据为：" + totalset.toString());
            if (totalset.size() > 0) {
                DeleteImageFile(totalset, FileAll);
            }
        }
    }

    private static ArrayList<String> AddPath(Context mContext) {
        ArrayList<String> pathAll = new ArrayList<>();
        //String pathHead = Environment.getExternalStorageDirectory().toString() + File.separator;
        //System.out.println("DeleteImage::AddPath::pathHead::" + pathHead);
        pathAll.add(GlobalData.getFilePath(mContext, GlobalData.IVAWATER_PATH));
        pathAll.add(GlobalData.getFilePath(mContext, GlobalData.IVAWATERMETER_PATH));
        return pathAll;
    }

    private static void DeleteImageFile(TreeSet<Integer> set, ArrayList<File> FileAll) {
        for (File file : FileAll) {
            int monthInteger = Integer.valueOf(file.getName().split("_")[0]);
            for (Integer month : set) {
                if (month == monthInteger) {
                    System.out.println("------成功删除-----");
                    file.delete();
                }
            }
        }
    }
}
