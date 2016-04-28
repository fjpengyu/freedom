package com.goxod.freedom.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goxod.freedom.R;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by Levey on 2016/3/6.
 */
public class StringItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] list;
    private int layoutId,tvId;

    public StringItemAdapter(Context context, int type){
        super();
        inflater = LayoutInflater.from(context);
        int arrayId;
        switch (type){
            case 1:
                arrayId = R.array.list_mode;
                layoutId = R.layout.item_menu_spinner;
                tvId = R.id.spinner_item;
                break;
            case 2:
                arrayId = R.array.first_fragment;
                layoutId = R.layout.item_menu_spinner;
                tvId = R.id.spinner_item;
                break;
            case 3:
                arrayId = R.array.change_log;
                layoutId = R.layout.item_change_log;
                tvId = R.id.change_item;
                break;
            default:
                arrayId = 0;
                break;
        }
        list = context.getResources().getStringArray(arrayId);
    }
    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(layoutId, parent,false);
            holder.tv = (TextView) convertView.findViewById(tvId);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
            AutoUtils.autoTextSize(holder.tv);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(list[position]);
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }
}
