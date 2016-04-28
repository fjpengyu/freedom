package com.goxod.freedom.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.view.activity.ImageListActivity;
import com.goxod.freedom.view.activity.ImagePaperActivity;
import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;

/**
 * Created by Levey on 16/3/21.
 */
public class RequestUtil {
    public static void openMzImages(final Context context, String url){
        OkHttpUtils
                .get()
                .url(url)
                .addHeader(API.UA_TITLE, API.UA_CONTENT)
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                        return new String(response.body().bytes(), "utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        MainActivity.rl.stop();
                        Sys.toast(context,"加载错误");
                    }

                    @Override
                    public void onResponse(String response) {
                        ArrayList<String> list = new ArrayList<>();
                        Document doc = Jsoup.parse(response);
                        Elements item = doc.select("figure");
                        String pageStr = doc.select("span[class=prev-next-page]").text();
                        String imgUrl = item.select("img").attr("src");
                        pageStr = pageStr.substring(pageStr.lastIndexOf("/") + 1, pageStr.lastIndexOf("页"));
                        String suffix = imgUrl.substring(imgUrl.lastIndexOf("."), imgUrl.length());
                        String baseUrl = imgUrl.substring(0, imgUrl.length() - (pageStr.length() + suffix.length()));
                        int page = Integer.parseInt(pageStr);
                        for (int i = 0; i < page; i++) {
                            String id;
                            if (i < 9) {
                                id = "0" + (i + 1);
                            } else {
                                id = String.valueOf(i + 1);
                            }
                            String url = baseUrl + id + suffix;
                            list.add(url);
                        }
                        if (!list.isEmpty()) {
                            Intent intent;
                            if (Freedom.config.getListMode() != API.CARD_PAPER_MODE) {
                                intent = new Intent(context, ImageListActivity.class);
                            } else {
                                intent = new Intent(context, ImagePaperActivity.class);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList(API.LIST, list);
                            bundle.putInt(API.ITEM, 0);
                            intent.putExtras(bundle);
                            MainActivity.rl.stop();
                            context.startActivity(intent);
                        } else {
                            MainActivity.rl.stop();
                            Sys.toast(context, "本帖未发现图片");
                        }

                    }
                });
    }

    public static void openClImages(final Context context, String url){
        OkHttpUtils
                .get()
                .url(API.getBase() + url)
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                        return new String(response.body().bytes(), "gb2312");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        MainActivity.rl.stop();
                        Sys.toast(context,"加载错误");
                    }

                    @Override
                    public void onResponse(String response) {
                        ArrayList<String> list = new ArrayList<>();
                        Document doc = Jsoup.parse(response);
                        Elements h3 = doc.select("input[src]");
                        for (int i = 0; i < h3.size(); i++) {
                            final String url = API.getImageUrl(h3.get(i).attr("src"));
                            if(!list.contains(url) && !Arrays.toString(Freedom.updateBean.getImage()).contains(url)){
                                list.add(url);
                            }
                        }

                        if(!list.isEmpty()){
                            Intent intent;
                            if (Freedom.config.getListMode() != API.CARD_PAPER_MODE) {
                                intent = new Intent(context, ImageListActivity.class);
                            } else {
                                intent = new Intent(context, ImagePaperActivity.class);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList(API.LIST, list);
                            bundle.putInt(API.ITEM, 0);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }else{
                            Sys.toast(context,"本帖未发现图片");
                        }
                        MainActivity.rl.stop();
                    }
                });

    }
}
