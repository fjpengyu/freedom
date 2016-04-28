package com.goxod.freedom.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.data.adapter.PageItemAdapter;
import com.goxod.freedom.data.db.DbNote;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Notify;
import com.goxod.freedom.util.PostHtml;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.util.listener.NoDoubleClickListener;
import com.jaeger.library.StatusBarUtil;
import com.victor.loading.rotate.RotateLoading;
import com.zhy.autolayout.AutoLayoutActivity;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;
import com.goxod.freedom.okhttp.callback.FileCallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class TechActivity extends AutoLayoutActivity {

    private TechActivity activity;
    private TextView pages;
    private WebView webView;
    private static int alertNum = 0;
    private MaterialDialog dialog5;
    private static long mLastTime = 0;
    private RotateLoading rl;
    private String url;
    private int currentPage = 1;
    private int maxPage = 1;
    private List<String> postList;
    private TextView title;
    private String titleStr, authorStr;
    private RelativeLayout footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Freedom.getInstance().addActivity(this);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            setTheme(R.style.AppTheme_FullScreen);
        }else{
            setTheme(R.style.AppTheme_NoActionBar);
        }
        setContentView(R.layout.activity_tech);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            StatusBarUtil.setTranslucent(this,0);
        }
        activity = this;
        postList = new ArrayList<>();
        rl = (RotateLoading) findViewById(R.id.loading);
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        footer = (RelativeLayout) findViewById(R.id.footer);
        title = (TextView) findViewById(R.id.app_title);
        TextView previous = (TextView) findViewById(R.id.previous);
        TextView next = (TextView) findViewById(R.id.next);
        TextView favorite = (TextView) findViewById(R.id.favorite);
        pages = (TextView) findViewById(R.id.pages);
        webView = (WebView) findViewById(R.id.web_view);
        url = getIntent().getStringExtra(API.PAGE_URL);
        currentPage = getIntent().getIntExtra(API.PAGE_ID, 1);
        for (int i = 0; i < currentPage; i++) {
            postList.add(i, "empty");
        }
        WebSettings settings = webView.getSettings();
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setDefaultFixedFontSize(16);
        settings.setDefaultFontSize(16);
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        try{
        titleStr = Freedom.currentPageBean.getTitle();
        authorStr = Freedom.currentPageBean.getAuthor();
        }catch (Exception e){
            e.printStackTrace();
        }
        title.setText(getIntent().getStringExtra(API.TITLE));
        assert header != null;
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - mLastTime) > 1000) {
                    mLastTime = System.currentTimeMillis();
                } else {
                    alertNum = 0;
                    Sys.toast(activity, "已启用弹窗");
                }
            }
        });

        assert favorite != null;
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PageBean pb = new PageBean();
                    pb.setUrl(url);
                    pb.setTitle(titleStr);
                    pb.setAuthor(authorStr);
                    pb.setPostTime(Freedom.currentPageBean.getPostTime());
                    pb.setReply(Freedom.currentPageBean.getReply());
                    pb.setColor(Freedom.currentPageBean.getColor());
                    pb.setType(Freedom.currentPageBean.getType());
                    pb.setTypeF(Freedom.currentPageBean.getTypeF());
                    DbUtil.saveFavoriteDb(
                            activity,
                            pb,
                            titleStr,
                            pb.getTypeF(),
                            currentPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String sUrl = API.getBase() + url;
                ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText(sUrl, sUrl));
                Sys.toast(getApplicationContext(), "复制成功\n" + sUrl);
                return true;
            }
        });

        assert previous != null;
        previous.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {

                if (currentPage < 2) {
                    Sys.toast(getApplicationContext(), "已是第一页");
                } else {
                    try {
                        if (!postList.get(currentPage - 2).contains("empty")) {
                            webView.loadDataWithBaseURL(null, postList.get(currentPage - 2), "text/html", "utf-8", null);
                            String tStr = (currentPage - 1) + "/" + maxPage;
                            pages.setText(tStr);
                        } else {
                            loadHtml(currentPage - 1);
                        }
                    } catch (Exception e) {
                        loadHtml(currentPage - 1);
                    }
                    currentPage = currentPage - 1;
                }
            }
        });

        assert next != null;
        next.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (maxPage > currentPage) {
                    try {
                        if (!postList.get(currentPage).contains("empty")) {
                            webView.loadDataWithBaseURL(null, postList.get(currentPage), "text/html", "utf-8", null);
                            String tStr = (currentPage + 1) + "/" + maxPage;
                            pages.setText(tStr);
                        } else {
                            loadHtml(currentPage + 1);
                        }
                    } catch (Exception e) {
                        loadHtml(currentPage + 1);
                    }
                    currentPage = currentPage + 1;
                } else {
                    Sys.toast(getApplicationContext(), "已是最后页");
                }
            }
        });
        webView.setWebViewClient(new FWebView());
        webView.getParent().requestDisallowInterceptTouchEvent(true);
        loadHtml(currentPage);

        pages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                        .title("选择页码")
                        .customView(R.layout.dialog_page_selector, true)
                        .negativeText(R.string.cancel)
                        .build();
                dialog.show();
                ListView pageList = (ListView) dialog.findViewById(R.id.page_list);
                PageItemAdapter adapter = new PageItemAdapter(activity, maxPage);
                pageList.setAdapter(adapter);
                pageList.setSelection(currentPage - 1);
                pageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        postList.clear();
                        for (int i = 0; i < maxPage; i++) {
                            postList.add(i, "empty");
                        }
                        currentPage = position + 1;
                        dialog.dismiss();
                        loadHtml(currentPage);
                    }
                });

            }
        });
    }


    private void loadHtml(final int page) {

        String mUrl;
        if (page == 1) {
            mUrl = url;
        } else {
            String mId = url.substring(url.lastIndexOf("/") + 1, url.length() - ".html".length());
            mUrl = "read.php?tid=" + mId + "&page=" + page;
        }
        rl.start();
        OkHttpUtils
                .get()
                .url(API.getBase() + mUrl)
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response) throws Exception {
                        return new String(response.body().bytes(), "utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        rl.stop();
                        title.setText("页面加载错误");
                        Sys.toast(getApplicationContext(), "页面加载错误");
                    }

                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<String, String, String>() {

                            @Override
                            protected String doInBackground(String... params) {

                                Document doc = Jsoup.parse(response);
                                if (currentPage == 1) {
                                    titleStr = doc.select("title").text().replace("草榴社區", "").trim();
                                    try {
                                        authorStr = doc.select("font[face=Gulim]").get(0).text();
                                    } catch (Exception e) {
                                        authorStr = "楼主好人";
                                    }
                                }
                                Elements main = doc.select("div[id=main]");
                                String lastPageStr = main.select("a[id=last]").attr("href");
                                if (lastPageStr.length() > 1) {
                                    try {
                                        maxPage = Integer.parseInt(lastPageStr.substring(lastPageStr.lastIndexOf("page=") + "page=".length(), lastPageStr.length()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                Elements blk = main.select("blockquote");
                                for (int i = 0; i < blk.size(); i++) {
                                    Element blkElt = blk.get(i);
                                    if (blkElt.text().contains("magnet:?")
                                            || blkElt.text().contains("ed2k://")
                                            || blkElt.text().contains("thunder://")) {
                                        String txt = blkElt.text();
                                        String pre = "<center><a href=\"http://www.viidii.info/?" + txt + "\">";
                                        String app = "↑ 复制链接 ↑</a></center><br>";
                                        txt = pre + app;
                                        main.select("blockquote").get(i).after(txt);
                                    }
                                }

                                Elements video = main.select("video[controls=controls]");
                                for (int i = 0; i < video.size(); i++) {
                                    String v = video.get(i).attr("src");
                                    String pre = "<center><a href=\"http://www.viidii.info/?download_video=" + v + "\">";
                                    String cen = "下载视频</a>    |    ";
                                    String app = "<a href=\"http://www.viidii.info/?play_video=" + v + "\">" +
                                            "全屏播放</a>" +
                                            "</center><br>";
                                    String txt = pre + cen + app;
                                    video.get(i).after(txt);
                                }


                                main.select("div[id=header]").remove();
                                main.select("div[class=tips black]").remove();
                                main.select("center[class=gray]").remove();
                                main.select("div[class=t]").remove();
                                main.select("script[language=JavaScript]").remove();
                                main.select("textarea[style=display:none]").remove();
                                Elements str = main.select("a[title=回復此樓并短信通知]");
                                main.select("a[title=回復此樓并短信通知]").remove();
                                main.select("span[class=fr]").remove();
                                main.select("a[onclick=scroll(0,0)]").remove();
                                for (int i = 0; i < str.size(); i++) {
                                    String apStr = "<span class='fr'>" + str.get(i).text().replace("回", "") + "</span>";
                                    main.select("div[class=tipad]").get(i).append(apStr);
                                }
                                return PostHtml.getHtml(main.html());
                            }

                            @Override
                            protected void onPostExecute(String str) {
                                super.onPostExecute(str);
                                title.setText(titleStr);
                                webView.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
                                if (maxPage == 1) {
                                    maxPage = currentPage;
                                }
                                String tStr = currentPage + "/" + maxPage;
                                pages.setText(tStr);
                                try {
                                    postList.remove(currentPage - 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    postList.add(currentPage - 1, str);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                rl.stop();
                                footer.setBackgroundResource(R.color.postBg);
                                footer.setVisibility(View.VISIBLE);
                            }
                        }.execute();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        DbNote note = new DbNote();
        note.setUrl(url);
        note.setType(API.FRAGMENT_TECH);
        note.setPage(currentPage);
        DbUtil.saveNote(note);
        MainActivity.mvcPageList.get(API.FRAGMENT_TECH).getAdapter().notifyDataChanged(null,false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }


    private class FWebView extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            super.shouldOverrideUrlLoading(view, url);
            webView.onPause();

            int type;
            boolean isVideo = false;
            String uUrl;
            try {

                if (url.contains("http://www.viidii.info/?action=image&url=&src=")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?action=image&url=&src=", ""), "utf-8").
                            replace("%20", " ");
                    type = 1;
                } else if (url.contains("http://www.viidii.info/?action=image&url=")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?action=image&url=", ""), "utf-8").
                            replace("%20", " ");
                    type = 1;
                } else if (url.contains("http://www.viidii.info/?magnet:?")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?", ""), "utf-8").replace("&z", "").replaceAll("______", ".");
                    type = 2;
                } else if (url.contains("http://www.viidii.info/?ed2k://")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?", ""), "utf-8").replace("&z", "").replaceAll("______", ".");
                    type = 2;
                } else if (url.contains("http://www.viidii.info/?thunder://")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?", ""), "utf-8").replace("&z", "").replaceAll("______", ".");
                    type = 2;
                } else if (url.contains("http://www.viidii.info/?download_video=")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?download_video=", ""), "utf-8");
                    type = 6;
                } else if (url.contains("http://www.viidii.info/?play_video=")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?play_video=", ""), "utf-8");
                    isVideo = true;
                    type = 3;
                } else if (url.contains("http://www.viidii.info/?http://")
                        && (url.contains("youku")
                        || url.contains("bigetu"))) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?", ""), "utf-8").
                            replaceAll("______", ".");
                    type = 3;
                } else if (url.contains("htm_data") || url.contains("htm_mob")) {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?http://", ""), "utf-8").
                            replaceAll("______", ".");
                    if (url.contains("htm_data")) {
                        uUrl = API.getMobUrl(uUrl);
                    }
                    uUrl = uUrl.substring(uUrl.indexOf("/htm_mob/"), uUrl.indexOf(".html")) + ".html";
                    type = 4;
                } else {
                    uUrl = URLDecoder.decode(url.
                            replace("http://www.viidii.info/?", ""), "utf-8").
                            replaceAll("______", ".").replace("&z", "");
                    type = 5;
                    alertNum++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                uUrl = "";
                type = 0;
            }

            switch (type) {
                case 1:
                    if (uUrl.contains("src=")) {
                        uUrl = uUrl.substring(uUrl.indexOf("src=") + "src=".length(), uUrl.length());
                    }
                    Intent intent1;
                    intent1 = new Intent(getApplicationContext(), ImagePaperActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(API.SINGLE_IMAGE_URL, uUrl);
                    bundle1.putBoolean(API.SINGLE_IMAGE_MODE, true);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);

                    break;
                case 2:
                    ClipboardManager clipboard =
                            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText(uUrl, uUrl));
                    Sys.toast(getApplicationContext(), "复制成功\n" + uUrl);
                    break;
                case 3:
                    final MaterialDialog dialog3 = new MaterialDialog.Builder(activity)
                            .title("是否全屏播放视频?")
                            .content(uUrl)
                            .positiveText(R.string.apply)
                            .negativeText(R.string.cancel)
                            .build();
                    dialog3.show();
                    final MDButton btn_apply3 = dialog3.getActionButton(DialogAction.POSITIVE);
                    final String finalUUrl3 = uUrl;
                    final boolean finalIsVideo = isVideo;
                    btn_apply3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog3.dismiss();
                            Intent intent3;
                            intent3 = new Intent(getApplicationContext(), VideoActivity.class);
                            Bundle bundle3 = new Bundle();
                            bundle3.putString(API.VIDEO_URL, finalUUrl3);
                            bundle3.putBoolean(API.IS_VIDEO, finalIsVideo);
                            intent3.putExtras(bundle3);
                            startActivity(intent3);
                        }
                    });

                    break;
                case 4:
                    Intent intent = new Intent(getApplicationContext(), TechActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(API.TITLE, "页面加载中...");
                    bundle.putString(API.PAGE_URL, uUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case 5:
                    if (alertNum <= 1) {
                        dialog5 = new MaterialDialog.Builder(activity)
                                .title("是否打开外部链接?")
                                .content(uUrl)
                                .positiveText(R.string.apply)
                                .negativeText(R.string.cancel)
                                .neutralText(R.string.btn_copy)
                                .cancelable(false)
                                .build();
                        dialog5.show();
                        final MDButton btn_apply5 = dialog5.getActionButton(DialogAction.POSITIVE);
                        final String finalUUrl5 = uUrl;
                        btn_apply5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog5.dismiss();
                                Uri uri = Uri.parse(finalUUrl5);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                alertNum = 0;
                            }
                        });
                        final MDButton btn_cancel5 = dialog5.getActionButton(DialogAction.NEGATIVE);
                        btn_cancel5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog5.dismiss();
                                alertNum = 0;
                            }
                        });

                        final MDButton btn_copy = dialog5.getActionButton(DialogAction.NEUTRAL);
                        final String finalUUrl = uUrl;
                        btn_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog5.dismiss();
                                alertNum = 0;
                                ClipboardManager clipboard =
                                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                clipboard.setPrimaryClip(ClipData.newPlainText(finalUUrl, finalUUrl));
                                Sys.toast(getApplicationContext(), "复制成功\n" + finalUUrl);
                            }
                        });
                    } else if (alertNum == 2) {
                        dialog5.dismiss();
                        final MaterialDialog dialog52 = new MaterialDialog.Builder(activity)
                                .title("是否禁止该页面弹窗?")
                                .content("禁止后可双击标题栏重新启用弹窗")
                                .positiveText(R.string.ban_alert)
                                .negativeText(R.string.cancel)
                                .cancelable(false)
                                .build();
                        dialog52.show();
                        final MDButton btn_apply52 = dialog52.getActionButton(DialogAction.POSITIVE);
                        btn_apply52.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog52.dismiss();
                                alertNum = 10;
                            }
                        });
                        final MDButton btn_cancel52 = dialog52.getActionButton(DialogAction.NEGATIVE);
                        btn_cancel52.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog52.dismiss();
                                alertNum = 0;
                            }
                        });
                    }
                    break;
                case 6:
                    final MaterialDialog dialogDlVideo = new MaterialDialog.Builder(activity)
                            .title("是否下载该视频?")
                            .content(uUrl)
                            .positiveText(R.string.apply)
                            .negativeText(R.string.cancel)
                            .build();
                    dialogDlVideo.show();
                    final MDButton btn_DlVideo = dialogDlVideo.getActionButton(DialogAction.POSITIVE);

                    final String videoUrl = uUrl;
                    btn_DlVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogDlVideo.dismiss();
                            Sys.toast(getApplicationContext(), "开始下载...");
                            final String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
                            final int time = (int) System.currentTimeMillis();
                            OkHttpUtils
                                    .get()
                                    .url(videoUrl)
                                    .tag(videoUrl)
                                    .build()
                                    .connTimeOut(20000)
                                    .readTimeOut(20000)
                                    .writeTimeOut(20000)
                                    .execute(new FileCallBack(Tools.getVideoDir(), fileName) {

                                        @Override
                                        public void inProgress(float progress, long total) {
                                            int pro = (int) (progress * 10000);
                                            if (pro % 10 == 0) {
                                                Notify.video(activity, time, fileName, 10000, pro);
                                            }
                                        }

                                        @Override
                                        public File parseNetworkResponse(Response response) throws Exception {
                                            return super.parseNetworkResponse(response);
                                        }


                                        @Override
                                        public void onError(Call call, Exception e) {
                                            Sys.toast(activity, "下载失败\n" + fileName);
                                            Freedom.notifyManager.cancel(time);
                                        }

                                        @Override
                                        public void onResponse(File response) {
                                        }

                                        @Override
                                        public void onAfter() {
                                            super.onAfter();
                                        }
                                    });

                        }
                    });
                    break;
                default:
                    Sys.toast(activity, "资源打开错误");
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        alertNum = 0;
        currentPage = 1;
        maxPage = 1;
    }
}
