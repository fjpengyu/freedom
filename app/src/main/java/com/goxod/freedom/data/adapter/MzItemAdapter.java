package com.goxod.freedom.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.MzBean;
import com.goxod.freedom.data.db.DbNote;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.fresco.FTool;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.view.activity.MainActivity;
import com.shizhefei.mvc.IDataAdapter;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Levey on 16/1/25.
 */
public class MzItemAdapter extends BaseAdapter implements IDataAdapter<List<MzBean>> {


    private List<MzBean> list = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private int fType;
    SimpleDraweeView image;
    TextView title,time_look;
    List<DbNote> notes;
    ArrayList<String> nList;

    public MzItemAdapter(Context context,int fType) {
        super();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.fType = fType;
        notes = DbUtil.loadNotes(fType);
        nList = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return list.size();
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mz, null);
            image = (SimpleDraweeView) convertView.findViewById(R.id.image_item);
            title = (TextView) convertView.findViewById(R.id.item_txt);
            time_look = (TextView) convertView.findViewById(R.id.item_time_look);
            image.setHierarchy(FTool.getHierarchy(context,true));
            convertView.setTag(R.id.item_txt, title);
            convertView.setTag(R.id.image_item, image);
            convertView.setTag(R.id.item_time_look, time_look);
        } else {
            title = (TextView) convertView.getTag(R.id.item_txt);
            image = (SimpleDraweeView) convertView.getTag(R.id.image_item);
            time_look = (TextView) convertView.getTag(R.id.item_time_look);
        }
        image.setController(FTool.getController(list.get(position).getImage(), true));
        title.setText("  " + list.get(position).getTitle()+"  ");

        String pUrl;
        if(fType == API.FRAGMENT_MZ_ZP){
            pUrl = list.get(position).getImage();
        }else {
            pUrl = list.get(position).getUrl();
        }
        if(nList.contains(pUrl)){
            title.setTextColor(context.getResources().getColor(R.color.read));
        }else{
            title.setTextColor(context.getResources().getColor(R.color.withe));
        }
        if(list.get(position).getTime() != null && list.get(position).getLook() != null) {
            String timeLookStr = " " + convertView.getResources().getString(R.string.fa_time) + " " + list.get(position).getTime()
                    + "  " + convertView.getResources().getString(R.string.fa_look) + " " + list.get(position).getLook() + " ";
            time_look.setText(timeLookStr);
        }else{
            time_look.setText("");
        }
        return convertView;
    }

    @Override
    public void notifyDataChanged(List<MzBean> data, boolean isRefresh) {
        if (isRefresh) {
            MainActivity.btnRefresh.clearAnimation();
            list.clear();
        }
        notes = DbUtil.loadNotes(fType);
        nList = getUrlList();
        if(data != null){
        list.addAll(data);
        }
        notifyDataSetChanged();
    }


    @Override
    public List<MzBean> getData() {
        return list;
    }

    private ArrayList<String> getUrlList(){
        ArrayList<String> nList = new ArrayList<>();
        for (DbNote note : notes) {
            nList.add(note.getUrl());
        }
        return nList;
    }
}
