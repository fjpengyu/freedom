package com.goxod.freedom.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goxod.freedom.R;
import com.goxod.freedom.data.db.DbNote;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Sys;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.bean.PageBean;

/**
 * Created by Levey on 16/1/25.
 */
public class ClItemAdapter extends BaseAdapter implements IDataAdapter<List<PageBean>> {


    private List<PageBean> list = new ArrayList<>();
    private Set<String> set = new HashSet<>();
    private LayoutInflater inflater;
    TextView title, author, reply, post;
    private boolean isCollection;
    private int fType;
    private Context context;
    List<DbNote> notes;
    ArrayList<String> nList;

    public ClItemAdapter(Context context, int type, boolean collection) {
        super();
        inflater = LayoutInflater.from(context);
        this.context = context;
        isCollection = collection;
        fType = type;
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
            convertView = inflater.inflate(R.layout.item_cl, null);
            title = (TextView) convertView.findViewById(R.id.title);
            reply = (TextView) convertView.findViewById(R.id.reply);
            author = (TextView) convertView.findViewById(R.id.author);
            post = (TextView) convertView.findViewById(R.id.post);
            convertView.setTag(R.id.reply, reply);
            convertView.setTag(R.id.author, author);
            convertView.setTag(R.id.post, post);
            convertView.setTag(R.id.title, title);

        } else {
            title = (TextView) convertView.getTag(R.id.title);
            reply = (TextView) convertView.getTag(R.id.reply);
            author = (TextView) convertView.getTag(R.id.author);
            post = (TextView) convertView.getTag(R.id.post);
        }


        if (list.get(position).getColor() == 1) {
            title.setTextColor(convertView.getResources().getColor(R.color.colorGreen));
        } else if (list.get(position).getColor() == 2) {
            title.setTextColor(convertView.getResources().getColor(R.color.colorOrange));
        } else if (list.get(position).getColor() == 3) {
            title.setTextColor(convertView.getResources().getColor(R.color.colorBlue));
        } else {
            title.setTextColor(convertView.getResources().getColor(R.color.colorLightBlue));
        }

        if (isCollection) {
            reply.setText(list.get(position).getReply());
        } else {
            String replyStr = convertView.getResources().getString(R.string.fa_comments) + " " + list.get(position).getReply();
            reply.setText(replyStr);
        }

        int pageId;
        if (fType == API.FRAGMENT_TECH) {
            pageId = DbUtil.loadOneNote(list.get(position).getUrl()).getPage();
        } else {
            pageId = DbUtil.getPageId(list.get(position));
        }
        if (pageId > 1) {
            String titleStr = "  已看 " + pageId + " 页";
            post.setText(titleStr);
        } else {
            post.setText(list.get(position).getPostTime());
        }
        title.setText(list.get(position).getTitle());
        author.setText(list.get(position).getAuthor());
        String url = list.get(position).getUrl();
        if (nList.contains(url)) {
            title.setTextColor(context.getResources().getColor(R.color.colorGrey));
        }
        return convertView;
    }

    @Override
    public void notifyDataChanged(List<PageBean> data, boolean isRefresh) {
        if (isRefresh) {
            MainActivity.btnRefresh.clearAnimation();
            set.clear();
            list.clear();
        }
        notes = DbUtil.loadNotes(fType);
        nList = getUrlList();
        if (data != null) {
            for (PageBean pb : data) {
                if(set.add(pb.getUrl())){
                    list.add(pb);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public List<PageBean> getData() {
        return list;
    }

    private ArrayList<String> getUrlList() {
        ArrayList<String> nList = new ArrayList<>();
        for (DbNote note : notes) {
            nList.add(note.getUrl());
        }
        return nList;
    }

}
