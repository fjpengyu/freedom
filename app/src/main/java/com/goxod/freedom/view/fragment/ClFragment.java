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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.data.adapter.ClItemAdapter;
import com.goxod.freedom.data.asyncdata.ClAsyncData;
import com.goxod.freedom.data.db.DbNote;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.request.RequestUtil;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.listener.ItemNoDoubleClickListener;
import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.view.activity.TechActivity;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Levey on 16/1/25.
 */

@SuppressLint("ValidFragment")
public class ClFragment extends Fragment {

    private MVCHelper<List<PageBean>> mvcHelper;
    private int fType;

    public ClFragment(int type) {
        super();
        this.fType = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cl, null);
        PtrClassicFrameLayout mPtrFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.list_view_frame);
        mvcHelper = new MVCUltraHelper<>(mPtrFrameLayout);
        mvcHelper.setDataSource(new ClAsyncData(fType));
        mvcHelper.setAdapter(new ClItemAdapter(getContext(), fType, fType == API.FRAGMENT_FAVORITE));
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        MainActivity.mvcPageList.put(fType,mvcHelper);
        MainActivity.lvList.put(fType, listView);
        listView.setOnItemClickListener(new ItemNoDoubleClickListener() {
            @Override
            public void onItemNoDoubleClick(AdapterView<?> parent, View view, final int position, long id) {
                Freedom.currentPageBean = MainActivity.mvcPageList.get(fType).getAdapter().getData().get(position);
                final String url = mvcHelper.getAdapter().getData().get(position).getUrl();
                if(url.contains("read.php?")){
                    MainActivity.rl.start();
                    OkHttpUtils
                            .get()
                            .url(API.getBase() + url)
                            .build()
                            .execute(new Callback<String>() {
                                @Override
                                public String parseNetworkResponse(Response response) throws Exception {
                                    return new String(response.body().bytes(),"utf-8");
                                }

                                @Override
                                public void onError(Call call, Exception e) {
                                    MainActivity.rl.stop();
                                    Sys.toast(getActivity().getApplicationContext(),"跳转失败,请重试...");
                                }

                                @Override
                                public void onResponse(String response) {
                                    MainActivity.rl.stop();
                                    Document doc = Jsoup.parse(response);
                                    String url = doc.select("center").select("a").get(1).attr("href");
                                    url = url.replace(API.getBase(),"");
                                    openView(position,url);
                                }
                            });
                }else{
                    openView(position,url);
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (fType == API.FRAGMENT_FAVORITE) {
                    DbUtil.delFavoriteDialog(getContext(),
                            MainActivity.mvcPageList.get(fType).getAdapter().getData().get(position).getTitle(),
                            MainActivity.mvcPageList.get(fType).getAdapter().getData().get(position).getUrl());
                } else {
                    DbUtil.saveFavoriteDb(
                            getContext(),
                            MainActivity.mvcPageList.get(fType).getAdapter().getData().get(position),
                            MainActivity.mvcPageList.get(fType).getAdapter().getData().get(position).getTitle(),
                            fType,
                            0);
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Freedom.config.getFirstFragment() == fType && MainActivity.mvcPageList.get(fType).getAdapter().isEmpty()) {
            MainActivity.mvcPageList.get(fType).refresh();
        }
    }

    private void openTech(int type, String title, String url, int pageId) {
        if (type == API.FRAGMENT_TECH) {
            Intent intent = new Intent(getContext(), TechActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(API.TITLE, title);
            bundle.putString(API.PAGE_URL, url);
            bundle.putInt(API.PAGE_ID, pageId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void openView(final int position,final String url){
        if (fType == API.FRAGMENT_FLAG || fType == API.FRAGMENT_AGE) {
            MainActivity.rl.start();
            Freedom.currentPageBean.setTypeF(fType);
            RequestUtil.openClImages(getContext(), url);
        }
        if (fType == API.FRAGMENT_TECH) {
            Freedom.currentPageBean.setTypeF(API.FRAGMENT_TECH);
            int page = DbUtil.loadOneNote(url).getPage();
            openTech(API.FRAGMENT_TECH, mvcHelper.getAdapter().getData().get(position).getTitle(), url, page);
        }

        if (fType == API.FRAGMENT_FAVORITE) {
            final int type;
            String reply = mvcHelper.getAdapter().getData().get(position).getReply();
            if (reply.equals(getContext().getString(R.string.app_title_flag))) {
                type = API.FRAGMENT_FLAG;
            } else if (reply.equals(getContext().getString(R.string.app_title_age))) {
                type = API.FRAGMENT_AGE;
            } else if (reply.equals(getContext().getString(R.string.app_title_tech))) {
                type = API.FRAGMENT_TECH;
            } else {
                type = API.FRAGMENT_FLAG;
            }
            Freedom.currentPageBean.setTypeF(type);
            final int pageId = DbUtil.getPageId(Freedom.currentPageBean);
            if (pageId > 1) {
                final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title(mvcHelper.getAdapter().getData().get(position).getTitle())
                        .content("已经看到  " + pageId + "  页,是否继续阅读?")
                        .positiveText(R.string.apply)
                        .negativeText(R.string.cancel)
                        .build();
                dialog.show();


                final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
                btn_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        openTech(type, mvcHelper.getAdapter().getData().get(position).getTitle(), url, pageId);
                    }
                });

                final MDButton btn_cancel = dialog.getActionButton(DialogAction.NEGATIVE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        openTech(type, mvcHelper.getAdapter().getData().get(position).getTitle(), url, 1);
                    }
                });
            } else {
                if (type == API.FRAGMENT_TECH) {
                    openTech(type, mvcHelper.getAdapter().getData().get(position).getTitle(), url, 1);
                } else {
                    MainActivity.rl.start();
                    RequestUtil.openClImages(getContext(), url);
                }
            }
        }

        if(fType != API.FRAGMENT_TECH) {
            new AsyncTask<Integer, Integer, Integer>() {
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
                    mvcHelper.getAdapter().notifyDataChanged(null, false);
                }
            }.execute();
        }
    }

}
