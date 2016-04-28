package com.goxod.freedom.data.asyncdata;

import android.os.AsyncTask;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.view.dialog.InitDialog;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.data.db.DbFavor;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.Call;

/**
 * Created by Levey on 16/1/25.
 */
public class ClAsyncData implements IAsyncDataSource<List<PageBean>> {

    private int mPage;
    private boolean hasMore = true;
    private final int maxPage = 100;
    private static final String reg = "^(\\[[^\\[]*\\])*";
    private int fType;

    public ClAsyncData(int type) {
        super();
        this.fType = type;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<PageBean>> sender) throws Exception {
        return loadPage(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<PageBean>> sender) throws Exception {
        return loadPage(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }


    private RequestHandle loadPage(final ResponseSender<List<PageBean>> sender, final int page) throws Exception {
        if (fType == API.FRAGMENT_FAVORITE) {
            new AsyncTask<String, String, List<PageBean>>() {
                @Override
                protected List<PageBean> doInBackground(String... params) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    List<PageBean> list = new ArrayList<>();
                    List<DbFavor> db = DbUtil.loadFavorite();
                    for (int i = db.size() - 1; i >= 0; i--) {
                        PageBean pb = new PageBean();
                        pb.setTitle(db.get(i).getTitle());
                        pb.setAuthor(db.get(i).getAuthor());
                        pb.setUrl(db.get(i).getUrl());
                        pb.setReply(db.get(i).getType());
                        pb.setPostTime(db.get(i).getTime());
                        pb.setTypeF(API.FRAGMENT_FAVORITE);
                        list.add(pb);
                    }
                    return list;
                }

                @Override
                protected void onPostExecute(List<PageBean> list) {
                    super.onPostExecute(list);
                    mPage = page;
                    hasMore = false;
                    sender.sendData(list);
                }
            }.execute();
        } else {
            final String url = API.getBase() + "thread0806.php";
            OkHttpUtils
                    .get()
                    .url(url)
                    .addParams("fid", getFid(fType))
                    .addParams("search", "")
                    .addParams("page", String.valueOf(page))
                    .build()
                    .execute(new Callback<String>() {
                        @Override
                        public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                            return new String(response.body().bytes(), "gb2312");
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            InitDialog.popInit(MainActivity.activity,false);
                        }

                        @Override
                        public void onResponse(final String response) {

                            new AsyncTask<String, String, List<PageBean>>() {
                                @Override
                                protected List<PageBean> doInBackground(String... params) {
                                    List<PageBean> list = new ArrayList<>();
                                    Document doc = Jsoup.parse(response);
                                    Elements els = doc.select("td[style=text-align:left;padding-left:8px]");
                                    for (int i = 0; i < els.size(); i++) {
                                        PageBean pb = new PageBean();
                                        String pUrl = els.get(i).attr("onclick").replace("window.location='", "").replace("';", "");
                                        if (!Arrays.toString(Freedom.updateBean.getPost()).contains(pUrl)) {
                                            pb.setUrl(pUrl);
                                            String title = els.get(i).select("a[id]").text();
                                            String color = els.get(i).select("a[id]").select("font").attr("color");
                                            switch (color) {
                                                case "green":
                                                    pb.setColor(1);
                                                    break;
                                                case "orange":
                                                    pb.setColor(2);
                                                    break;
                                                case "blue":
                                                    pb.setColor(3);
                                                    break;
                                                default:
                                                    pb.setColor(0);
                                                    break;
                                            }
                                            if (fType == API.FRAGMENT_FLAG) {
                                                pb.setTitle(title.replaceAll(reg, "").replace("達蓋爾的旗幟", ""));
                                                pb.setType(title.matches("(.*)原创(.*)|(.*)原創(.*)|(.*)達蓋爾(.*)|(.*)先鋒團(.*)|(.*)达盖尔(.*)|(.*)先锋团(.*)|(.*)投稿(.*)") ? 1 : 0);
                                            } else {
                                                pb.setTitle(title);
                                            }
                                            String authorDate = els.get(i).select("span[class=f10 fl]").text();
                                            int rIndex = authorDate.length() - " 03-10".length();
                                            pb.setAuthor(authorDate.substring(0, rIndex).replace(" Top", ""));
                                            String postStr = authorDate.substring(rIndex, authorDate.length());
                                            if (postStr.contains("-marks")) {
                                                postStr = "置顶主题";
                                            }
                                            pb.setPostTime(postStr);
                                            pb.setReply(els.get(i).select("a[class=s6]").text().replace("回帖: ", ""));
                                            pb.setTypeF(fType);
                                            list.add(pb);
                                        }
                                    }
                                    mPage = page;
                                    hasMore = mPage <= maxPage;
                                    return list;
                                }

                                @Override
                                protected void onPostExecute(List<PageBean> pageBeans) {
                                    super.onPostExecute(pageBeans);
                                    sender.sendData(pageBeans);
                                }
                            }.execute();
                        }
                    });
        }
        return null;
    }

    private static String getFid(int type) {
        String fid;
        switch (type) {
            case API.FRAGMENT_FLAG:
                fid = "16";
                break;
            case API.FRAGMENT_AGE:
                fid = "8";
                break;
            case API.FRAGMENT_TECH:
                fid = "7";
                break;
            case API.FRAGMENT_FAVORITE:
                fid = "0";
                break;
            default:
                fid = "0";
                break;
        }
        return fid;
    }

}
