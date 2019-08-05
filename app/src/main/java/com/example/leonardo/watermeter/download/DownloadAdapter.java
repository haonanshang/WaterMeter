package com.example.leonardo.watermeter.download;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends BaseAdapter {
    private List<DownloadModel> data;
    private Context context;
    private DownloadDialog.AllCheckListener allCheckListener;
    private String mCbyf;
    private DownloadDialog mDownloadDialog;

    public DownloadAdapter(List<DownloadModel> data, DownloadDialog downloadDialog, Context context, String cbyf, DownloadDialog.AllCheckListener allCheckListener) {
        this.data = data;
        this.mDownloadDialog = downloadDialog;
        this.context = context;
        this.mCbyf = cbyf;
        this.allCheckListener = allCheckListener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoder hd;
        if (view == null) {
            hd = new ViewHoder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.download_checkbox_item, null);
            hd.bchTv = view.findViewById(R.id.bch);
            hd.bchStateTv = view.findViewById(R.id.bch_state);
            hd.checkBox = (CheckBox) view.findViewById(R.id.ckb);
            view.setTag(hd);
        }
        DownloadModel mModel = data.get(i);
        hd = (ViewHoder) view.getTag();
        hd.bchTv.setText(mModel.getSt());
        if (DataSupport.where("t_cbyf = ? and t_volume_num = ?", mCbyf, mModel.getSt()).find(DetailData.class).size() > 0) {
            hd.bchStateTv.setText("已下载");
            hd.bchStateTv.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            hd.bchStateTv.setText("未下载");
            hd.bchStateTv.setTextColor(context.getResources().getColor(R.color.black));
        }
        final ViewHoder hdFinal = hd;
        hd.checkBox.setChecked(mModel.isIscheck());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = hdFinal.checkBox;
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    data.get(i).setIscheck(false);
                } else {
                    checkBox.setChecked(true);
                    data.get(i).setIscheck(true);
                }
                //监听每个item，若所有checkbox都为选中状态则更改main的全选checkbox状态
                for (DownloadModel model : data) {
                    if (!model.isIscheck()) {
                        allCheckListener.onCheckedChanged(false);
                        return;
                    }
                }
                allCheckListener.onCheckedChanged(true);
            }
        });
        return view;
    }

    /**
     * @return 所有被选择的item
     */
    public List<DownloadModel> getAllModelOnCheck() {
        List<DownloadModel> allModelData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isIscheck()) {
                allModelData.add(data.get(i));
            }
        }
        return allModelData;
    }

    class ViewHoder {
        TextView bchTv;
        TextView bchStateTv;
        CheckBox checkBox;
    }

}
