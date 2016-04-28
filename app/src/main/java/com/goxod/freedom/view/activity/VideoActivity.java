package com.goxod.freedom.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.PostHtml;
import com.victor.loading.rotate.RotateLoading;
import com.zhy.autolayout.AutoLayoutActivity;

public class VideoActivity extends AutoLayoutActivity {

    private VideoActivity activity;
    private WebView webView;
    private RotateLoading rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Freedom.getInstance().addActivity(this);
        setContentView(R.layout.activity_vedio);
        activity = this;
        String videoUrl = getIntent().getStringExtra(API.VIDEO_URL);
        boolean isVideo = getIntent().getBooleanExtra(API.IS_VIDEO,false);
        webView = (WebView) findViewById(R.id.web_view);
        rl = (RotateLoading) findViewById(R.id.loading);
        rl.start();
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
        if(isVideo){
            String main = "<div class=\"playvideo\"><video src='"+ videoUrl+ "' controls='controls' width='100%' height='100%' >不支持 video 标签。</video></div>";
            webView.loadDataWithBaseURL(null, PostHtml.getHtml(main), "text/html", "utf-8", null);
        }else {
            webView.loadUrl(videoUrl);
        }
        webView.setWebViewClient(new FogWebView());
    }

    private class FogWebView extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            rl.stop();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                final boolean[] close = {false};
                final MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("是否关闭视频?")
                        .positiveText(R.string.apply)
                        .negativeText(R.string.cancel)
                        .neutralText("横竖屏切换")
                        .build();
                dialog.show();
                final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
                btn_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        close[0] = true;
                        dialog.dismiss();
                        activity.finish();
                    }
                });

                final MDButton btn_change = dialog.getActionButton(DialogAction.NEUTRAL);
                btn_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }else if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                }
                });
                return close[0];
        }
        return super.onKeyDown(keyCode, event);
    }
}
