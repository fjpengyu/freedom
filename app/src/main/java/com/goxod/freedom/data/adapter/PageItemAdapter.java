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
public class PageItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int count;

    public PageItemAdapter(Context context, int count){
        super();
        inflater = LayoutInflater.from(context);
        this.count = count;
    }
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_page_selector, parent,false);
            holder.tv = (TextView) convertView.findViewById(R.id.page_item);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
            AutoUtils.autoTextSize(holder.tv);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText("第  " + (position+1) +  "  页");
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }
}
