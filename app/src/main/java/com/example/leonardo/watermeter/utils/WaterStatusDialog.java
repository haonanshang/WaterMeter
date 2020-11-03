package com.example.leonardo.watermeter.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.example.leonardo.watermeter.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaterStatusDialog {

    Activity mContext;
    NumberPicker mFirstPicker;
    NumberPicker mSecondPicker;
    List<FirstModel> mDataList;
    protected String[] mFirstDatas;
    protected Map<String, Object> mSecondDatasMap = new HashMap<>();
    StatusPickedListener mListener;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public WaterStatusDialog(Activity mContext, StatusPickedListener listener, List<FirstModel> datas) {
        this.mContext = mContext;
        this.mListener = listener;
        mDataList = datas;
        initDatas();
    }

    /**
     * 展示选择器
     *
     * @return
     */
    public void showPickDialog() {
        FrameLayout locatePickLayout = (FrameLayout) mContext.getLayoutInflater().inflate(R.layout.dialog_pick_locate, null);
        mFirstPicker = locatePickLayout.findViewById(R.id.first_picker);
        mSecondPicker = locatePickLayout.findViewById(R.id.second_picker);
        initPickDialog();
        new AlertDialog.Builder(mContext).setTitle("请选择水表的状态").setView(locatePickLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirstModel firstModel = mDataList.get(mFirstPicker.getValue());
                System.out.println("firstModel|" + firstModel.toString());
                if (firstModel.getChild() != null && firstModel.getChild().size() > 0) {
                    SecondModel secondModel = firstModel.getChild().get(mSecondPicker.getValue());
                    mListener.onStatusPicked(firstModel, secondModel);
                } else {
                    mListener.onStatusPicked(firstModel, null);
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    /**
     * 初始化dialog
     */
    protected void initPickDialog() {
        mFirstPicker.setWrapSelectorWheel(false);
        mFirstPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                mSecondPicker.setDisplayedValues(null);
                setSecondLevelPicker((String[]) mSecondDatasMap.get(mFirstDatas[newVal]));
                mSecondPicker.setWrapSelectorWheel(false);
            }
        });
        mSecondPicker.setWrapSelectorWheel(false);
        mSecondPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setFirstLevelPicker();
        setSecondLevelPicker((String[]) mSecondDatasMap.get(mFirstDatas[0]));
    }

    /**
     * 一级选择列表
     */
    private void setFirstLevelPicker() {
        if (mFirstPicker != null) {
            mFirstPicker.setMinValue(0);
            mFirstPicker.setMaxValue(mFirstDatas.length - 1);
            mFirstPicker.setDisplayedValues(mFirstDatas);
        }
    }

    /**
     * 二级选择列表
     *
     * @param secondChoose
     */
    private void setSecondLevelPicker(String[] secondChoose) {
        if (null != mSecondPicker) {
            if (secondChoose != null && secondChoose.length > 0) {
                mSecondPicker.setMinValue(0);
                mSecondPicker.setMaxValue(secondChoose.length - 1);
                mSecondPicker.setDisplayedValues(secondChoose);
            } else {
                mSecondPicker.setMinValue(0);
                mSecondPicker.setMaxValue(0);
                mSecondPicker.setDisplayedValues(new String[]{"无"});
            }
        }
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void initDatas() {
        mFirstDatas = new String[mDataList.size()];
        for (int i = 0; i < mDataList.size(); i++) {
            mFirstDatas[i] = mDataList.get(i).getName();
            List<SecondModel> sencodList = mDataList.get(i).getChild();
            if (sencodList != null && sencodList.size() > 0) {
                String[] secondStatus = new String[sencodList.size()];
                for (int j = 0; j < sencodList.size(); j++) {
                    secondStatus[j] = sencodList.get(j).getName();
                }
                mSecondDatasMap.put(mDataList.get(i).getName(), secondStatus);
            } else {
                mSecondDatasMap.put(mDataList.get(i).getName(), null);
            }

        }
        showPickDialog();
    }

    public interface StatusPickedListener {
        void onStatusPicked(FirstModel firstModel, SecondModel secondModel);
    }
}
