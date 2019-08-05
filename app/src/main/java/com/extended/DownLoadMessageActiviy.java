package com.extended;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.utils.SharedPreUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class DownLoadMessageActiviy extends Activity {
    private TextView mTextView;
    private ListView mListView;
    private List<MessageBean> mDatas;
    private DownLoadMessageAdapter downLoadMessageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);
        //先判断是否是消防栓
        boolean isFireHydrant =SharedPreUtils.getDataType(this);
        if (!isFireHydrant) {
            initView();
            initDatas();
            initAdapter();
        } else {
            Toast.makeText(this, "未对消防栓表册提供此功能", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *加载adapter
     */
    private void initAdapter() {
        downLoadMessageAdapter = new DownLoadMessageAdapter(this, mDatas);
        mListView.setAdapter(downLoadMessageAdapter);
    }

    /*
     *初始化资源
     */
    private void initDatas() {
        mDatas = new ArrayList<>();
        List<DetailData> monthList = DataSupport.select("t_cbyf").where("isUpload=? and isChecked =? ", "1", "0").find(DetailData.class);
        if (monthList.size() > 0) {
            //月份排序  降序排列
            TreeSet<Integer> monthSet = new TreeSet<>();
            for (DetailData detailData : monthList) {
                Log.e("zksy", "月份为：" + Integer.valueOf(detailData.getT_cbyf()));
                monthSet.add(Integer.valueOf(detailData.getT_cbyf()));
            }
            Log.e("zksy", "月份数量为：" + monthSet.size());
            //查找已拍照但是未上传的表册
            for (Integer month : monthSet) {
                List<DetailData> statisTicalFormsList = DataSupport.select("t_volume_num").where("t_cbyf= ? and isUpload= ? and isChecked = ?", String.valueOf(month), "1", "0").find(DetailData.class);
                TreeSet<String> statisTicalFormsSet = new TreeSet<>();
                for (DetailData detailData : statisTicalFormsList) {
                    statisTicalFormsSet.add(detailData.getT_volume_num());
                }
                Log.e("zksy", "表册号数量为：" + statisTicalFormsSet.size());
                for (String statisTicalForms : statisTicalFormsSet) {
                    mDatas.add(new MessageBean(String.valueOf(month), String.valueOf(statisTicalForms)));
                }
            }
        }
        Log.e("zksy", "已拍照但未上传的全部表册数量为：" + mDatas.size());
        if (mDatas.size() == 0) {
            Toast.makeText(this, "导出列表数据为空", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *初始化控件
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);
        mTextView = (TextView) findViewById(R.id.extendTitle);
        mTextView.setText("导出列表");
    }

}
