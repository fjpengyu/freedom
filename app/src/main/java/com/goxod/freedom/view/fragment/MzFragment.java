package com.goxod.freedom.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.MzBean;
import com.goxod.freedom.data.adapter.MzItemAdapter;
import com.goxod.freedom.data.asyncdata.MzAsyncData;
import com.goxod.freedom.data.db.DbNote;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.request.RequestUtil;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.view.activity.ImagePaperActivity;
import com.goxod.freedom.view.activity.MainActivity;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by Levey on 16/1/25.
 */

@SuppressLint("ValidFragment")
public class MzFragment extends Fragment {

    private MVCHelper<List<MzBean>> mvcHelper;
    private int fType;

    public MzFragment(int fType) {
        super();
        this.fType = fType;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mz, null);
        PtrClassicFrameLayout mPtrFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.list_view_frame);
        mvcHelper = new MVCUltraHelper<>(mPtrFrameLayout);
        mvcHelper.setDataSource(new MzAsyncData(fType));
        mvcHelper.setAdapter(new MzItemAdapter(getContext(),fType));
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        MainActivity.mvcMzList.put(fType, mvcHelper);
        MainActivity.lvList.put(fType, listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.itemId = fType;
                MainActivity.rl.start();
                final String url;
                if (fType == API.FRAGMENT_MZ_ZP) {
                    Intent intent = new Intent(getContext(), ImagePaperActivity.class);
                    Bundle bundle1 = new Bundle();
                    url = mvcHelper.getAdapter().getData().get(position).getImage();
                    bundle1.putString(API.SINGLE_IMAGE_URL, url);
                    bundle1.putBoolean(API.SINGLE_IMAGE_MODE, true);
                    intent.putExtras(bundle1);
                    MainActivity.rl.stop();
                    startActivity(intent);
                } else {
                    Freedom.mzTitle = mvcHelper.getAdapter().getData().get(position).getTitle();
                    url = mvcHelper.getAdapter().getData().get(position).getUrl();
                    RequestUtil.openMzImages(getContext(), url);
                }

                new AsyncTask<Integer,Integer,Integer>(){
                    @Override
                    protected Integer doInBackground(Integer... params) {
                        try {
                            Thread.sleep(666);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        DbNote note = new DbNote();
                        note.setUrl(url);
                        note.setType(fType);
                        note.setPage(1);
                        DbUtil.saveNote(note);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        mvcHelper.getAdapter().notifyDataChanged(null,false);
                    }
                }.execute();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Sys.toast(getContext(), " 千呼万唤始出来\n犹抱琵琶半遮面");
                return true;
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(Freedom.config.getFirstFragment() == fType && MainActivity.mvcMzList.get(fType).getAdapter().isEmpty()){
            MainActivity.mvcMzList.get(fType).refresh();
        }
    }
}
