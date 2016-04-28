package com.goxod.freedom.data.asyncdata;

import android.os.AsyncTask;

import com.goxod.freedom.bean.MzBean;
import com.goxod.freedom.request.API;
import com.goxod.freedom.view.activity.MainActivity;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * Created by Levey on 16/1/25.
 */
public class MzAsyncData implements IAsyncDataSource<List<MzBean>> {

    private int mPage;
    private boolean hasMore = true;
    private int fType;

    public MzAsyncData(int fType) {
        super();
        this.fType = fType;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<MzBean>> sender) throws Exception {
        mPage = 0;
        return loadPage(sender,1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<MzBean>> sender) throws Exception {
        return loadPage(sender, mPage + (fType == API.FRAGMENT_MZ_ZP ? -1 : 1));
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }


    private RequestHandle loadPage(final ResponseSender<List<MzBean>> sender, final int page) throws Exception {
        String url;
        if(fType == API.FRAGMENT_MZ_ZP) {
            if (mPage == 0) {
                url = "http://www.mzitu.com/share";
            } else {
                url = "http://www.mzitu.com/share/comment-page-" + page;
            }
        }else{
            if(page == 1){
                url = "http://www.mzitu.com/"+ API.getStr(fType);
            }else{
                url = "http://www.mzitu.com/"+ API.getStr(fType)+"/page/" + page;
            }
        }
        OkHttpUtils
                .get()
                .url(url)
                .addHeader(API.UA_TITLE, API.UA_CONTENT)
                .build()
                .connTimeOut(2000)
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                        return new String(response.body().bytes(), "utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        if(page==1) {
                            MainActivity.listRefresh(fType - API.FRAGMENT_MZ_ZP);
                        }
                    }

                    @Override
                    public void onResponse(final String response) {
                        final List<MzBean> list = new ArrayList<>();
                        new AsyncTask<String, String, List<MzBean>>() {
                            @Override
                            protected List<MzBean> doInBackground(String... params) {
                                Document doc = Jsoup.parse(response);
                                if(fType == API.FRAGMENT_MZ_ZP) {
                                    Elements img = doc.select("ul[id=comments]").select("img[src]");
                                    Elements title = doc.select("ul[id=comments]").select("a[href]");
                                    for (int i = 0; i < img.size(); i++) {
                                        MzBean mz = new MzBean();
                                        mz.setImage(img.get(i).attr("src"));
                                        mz.setTitle(title.get(i).text().replace("at ", ""));
                                        list.add(mz);
                                    }
                                    Elements fyList = doc.select("div[class=prev-next share-fenye]").select("a[href]");
                                    String fyStr, nextStr;
                                    if (fyList.size() == 2) {
                                        fyStr = fyList.get(1).attr("href");
                                        nextStr = fyList.get(1).text();
                                    } else if (fyList.size() > 0) {
                                        fyStr = fyList.get(0).attr("href");
                                        nextStr = fyList.get(0).text();
                                    } else {
                                        fyStr = "empty";
                                        nextStr = "empty";
                                    }
                                    if (!fyStr.equals("empty")) {
                                        fyStr = fyStr.substring(fyStr.lastIndexOf("-") + 1, fyStr.lastIndexOf("#"));
                                        mPage = Integer.parseInt(fyStr) + 1;
                                    }else{
                                        mPage = page;
                                    }
                                    hasMore = !nextStr.contains("empty") && nextStr.contains("下一页");
                                }else {
                                    Elements items  = doc.select("div[class=place-padding]");
                                    for (int j = 0; j < items.size(); j++) {
                                        MzBean mz = new MzBean();
                                        mz.setUrl(items.get(j).select("figure").select("a[href]").attr("href"));
                                        mz.setTitle(items.get(j).select("figure").select("a[href]").attr("title"));
                                        mz.setImage(items.get(j).select("figure").select("img").attr("data-original"));
                                        String time = items.get(j).select("span[class=time]").get(0).text();
                                        try {
                                            time = time.substring(time.lastIndexOf("(")+1, time.lastIndexOf(")"));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        mz.setTime(time);
                                        mz.setLook(items.get(j).select("span[class=time]").get(1).text().replace("次浏览", ""));
                                        list.add(mz);
                                    }
                                    mPage = page;
                                    hasMore = !doc.select("span[class=prev-next-page]").text().contains("没有了");
                                }
                                if(page == 1) {
                                    try {
                                        Thread.sleep(250);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return list;
                            }
                            @Override
                            protected void onPostExecute(List<MzBean> mzBeans) {
                                super.onPostExecute(mzBeans);
                                sender.sendData(list);
                            }
                        }.execute();
                    }
                });
        return null;
    }
}
