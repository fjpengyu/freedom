package com.goxod.freedom.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.listener.NoDoubleClickListener;
import com.goxod.freedom.view.activity.SplashActivity;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Levey on 16/3/16.
 */
public class InitDialog {

    private static long lastClick = 0;
    public static MaterialDialog popInit(final Activity activity, final boolean isInit){
        final MaterialDialog dialog;
        if(isInit) {
            dialog = new MaterialDialog.Builder(activity)
                    .title(R.string.init_title)
                    .customView(R.layout.dialog_init, true)
                    .cancelable(false)
                    .positiveText(R.string.apply)
                    .negativeText(R.string.init_get_host)
                    .neutralText(R.string.splash_exit)
                    .show();
            final MDButton btn_exit = dialog.getActionButton(DialogAction.NEUTRAL);
            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Freedom.getInstance().exit();
                }
            });
        }else{
            dialog = new MaterialDialog.Builder(activity)
                    .title(R.string.init_title)
                    .customView(R.layout.dialog_init, true)
                    .cancelable(false)
                    .positiveText(R.string.apply)
                    .negativeText(R.string.init_get_host)
                    .neutralText(R.string.cancel)
                    .show();
            final MDButton btn_exit = dialog.getActionButton(DialogAction.NEUTRAL);
            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
        final MDButton btn_get = dialog.getActionButton(DialogAction.NEGATIVE);

        final CheckBox cb_c = (CheckBox) dialog.findViewById(R.id.init_cb_c);
        final CheckBox cb_o = (CheckBox) dialog.findViewById(R.id.init_cb_o);
        final CheckBox cb_1 = (CheckBox) dialog.findViewById(R.id.init_cb_1);
        final CheckBox cb_2 = (CheckBox) dialog.findViewById(R.id.init_cb_2);
        final EditText tv_c = (EditText) dialog.findViewById(R.id.init_tv_c);
        final TextView tv_o = (TextView) dialog.findViewById(R.id.init_tv_o);
        final TextView tv_1 = (TextView) dialog.findViewById(R.id.init_tv_1);
        final TextView tv_2 = (TextView) dialog.findViewById(R.id.init_tv_2);
        final TextView btn_test = (TextView) dialog.findViewById(R.id.init_btn_c);

        tv_c.setText(Freedom.config.getCustomServer());
        cb_c.setEnabled(false);
        cb_c.setChecked(false);
        cb_o.setChecked(false);
        cb_1.setChecked(false);
        cb_2.setChecked(false);

        cb_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_o.setChecked(false);
                    cb_1.setChecked(false);
                    cb_2.setChecked(false);
                    tv_c.setEnabled(true);
                }
            }
        });

        cb_c.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(tv_c.getText().toString().length() != 0  &&
                        !tv_c.getText().toString().contains(activity.getString(R.string.init_error))
                        && !tv_c.getText().toString().contains(activity.getString(R.string.init_connect))){
                    cb_c.setChecked(true);
                    cb_o.setChecked(false);
                    cb_1.setChecked(false);
                    cb_2.setChecked(false);
                    tv_c.setEnabled(true);
                }else{
                    Sys.toast(activity,"请先获取域名");
                    cb_c.setChecked(false);
                }
            }
        });
        cb_o.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(tv_o.getText().toString().length() != 0  &&
                        !tv_o.getText().toString().contains(activity.getString(R.string.init_error))
                        && !tv_o.getText().toString().contains(activity.getString(R.string.init_connect))) {
                    cb_c.setChecked(false);
                    cb_o.setChecked(true);
                    cb_1.setChecked(false);
                    cb_2.setChecked(false);
                }else{
                    Sys.toast(activity,"请先获取域名");
                    cb_o.setChecked(false);
                }
            }
        });
        cb_1.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(tv_1.getText().toString().length() != 0  &&
                        !tv_1.getText().toString().contains(activity.getString(R.string.init_error))
                        && !tv_1.getText().toString().contains(activity.getString(R.string.init_connect))) {
                    cb_c.setChecked(false);
                    cb_o.setChecked(false);
                    cb_1.setChecked(true);
                    cb_2.setChecked(false);
                }else{
                    Sys.toast(activity,"请先获取域名");
                    cb_1.setChecked(false);
                }
            }
        });
        cb_2.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(tv_2.getText().toString().length() != 0  &&
                        !tv_2.getText().toString().contains(activity.getString(R.string.init_error))
                        && !tv_2.getText().toString().contains(activity.getString(R.string.init_connect))) {
                    cb_c.setChecked(false);
                    cb_o.setChecked(false);
                    cb_1.setChecked(false);
                    cb_2.setChecked(true);
                }else{
                    Sys.toast(activity,"请先获取域名");
                    cb_2.setChecked(false);
                }
            }
        });


        btn_apply.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(cb_c.isChecked()){
                    Freedom.config.setCustomServer(tv_c.getText().toString().toLowerCase().trim());
                }
                if(cb_o.isChecked()){
                    Freedom.config.setCustomServer(tv_o.getText().toString().toLowerCase().trim());
                }
                if(cb_1.isChecked()){
                    Freedom.config.setCustomServer(tv_1.getText().toString().toLowerCase().trim());
                }
                if(cb_2.isChecked()){
                    Freedom.config.setCustomServer(tv_2.getText().toString().toLowerCase().trim());
                }

                if(cb_c.isChecked() || cb_o.isChecked() || cb_1.isChecked() || cb_2.isChecked()){
                    Config.initFilter(activity.getApplicationContext(),activity.getApplicationContext().getResources().getString(R.string.version_url));
                    Freedom.config.setIsOfficial(false);
                    Config.saveSp(activity, Freedom.config);
                    Freedom.config = Config.loadSp(activity);
                    if(isInit) {
                        SplashActivity.initHttp(activity,0);
                    }
                    dialog.dismiss();
                }else{
                    Sys.toast(activity,"请先选择域名");
                }
            }
        });

        btn_get.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {

                if(System.currentTimeMillis() - lastClick >= 15 * 1000){
                    lastClick = System.currentTimeMillis();
                    Sys.toast(activity,"正在获取...");
                    tv_o.setText("获取中...");
                    tv_1.setText("获取中...");
                    tv_2.setText("获取中...");
                    getHost(activity.getApplicationContext(),tv_o, tv_1, tv_2);
                }else{
                    long cTime = 15 * 1000- (System.currentTimeMillis() - lastClick);
                    Sys.toast(activity,"请 "+ (cTime/1000) + " 秒后再试...");
                }

            }
        });
        btn_test.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String host = tv_c.getText().toString().toLowerCase().trim();
                if (host.length() > 0) {
                    testCustomHost(activity, cb_c, tv_c, host);
                } else {
                    Sys.toast(activity, "请输入自定义域名");
                }
            }
        });

        return dialog;
    }

    private static void getHost(final Context context, final TextView ...tv){
        OkHttpUtils
                .post()
                .url("http://get.xunfs.com/app/listapp.php")
                .addHeader("accept", "*/*")
                .addHeader("connection","Keep-Alive")
                .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addParams("a", "get")
                .addParams("system", "android")
                .addParams("v", "1")
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response) throws Exception {
                        return new String(response.body().bytes(), "utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        for (TextView aTv : tv) {
                            aTv.setText(R.string.init_error);
                        }
                        Sys.toastLong(context,"请检查网络是否打开!");
                    }

                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);
                        Elements es = doc.select("a[href]");
                        for (int i = 0; i < es.size(); i++) {
                            tv[i].setText(R.string.init_connect);
                            String host = es.get(i).attr("href");
                            host = host.substring("http://".length(),host.lastIndexOf("/"));
                            testHost(tv[i],host);
                        }

                    }
                });
    }

    public static void testHost(final TextView tv, final String host){
        OkHttpUtils
                .get()
                .url(API.toUrl(host) + "mobile.php?ismobile=yes")
                .build()
                .connTimeOut(2000)
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response) throws Exception {
                        return new String(response.body().bytes(),"utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        tv.setText(R.string.init_error);
                    }

                    @Override
                    public void onResponse(String response) {
                        tv.setText(host);
                    }
                });
    }

    public static void testCustomHost(final Context context, final CheckBox cb, final EditText et ,final String host){
        Sys.toast(context,"正在测试...");
        OkHttpUtils
                .get()
                .url(API.toUrl(host) + "mobile.php?ismobile=yes")
                .build()
                .connTimeOut(2000)
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response) throws Exception {
                        return new String(response.body().bytes(),"utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Sys.toast(context,"连接错误!");
                        cb.setEnabled(false);
                        et.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response) {
                        Sys.toast(context, "自定义域名可用!");
                        cb.setEnabled(true);
                        cb.setChecked(true);
                        et.setEnabled(false);
                    }
                });
    }

}
