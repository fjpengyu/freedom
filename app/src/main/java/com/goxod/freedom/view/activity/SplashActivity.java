package com.goxod.freedom.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.view.dialog.InitDialog;
import com.zhy.autolayout.AutoLayoutActivity;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

public class SplashActivity extends AutoLayoutActivity {

    private static int tryTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Freedom.getInstance().addActivity(this);
        setContentView(R.layout.activity_splash);
        final SplashActivity activity = this;
        String[] face = activity.getResources().getStringArray(R.array.splash_face);
        TextView faceTxt = (TextView) findViewById(R.id.txt_init);
        assert faceTxt != null;
        faceTxt.setText(face[(int)(Math.random() * face.length)]);
        initHttp(activity,0);
        try {
            final String url = activity.getApplicationContext().getResources().getString(R.string.version_url);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.getInstance().cancelTag(url);
                }
            }, 2000);
            Config.initFilter(getApplicationContext(),url);
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }

    public static void initHttp(final Activity activity,int times){
        tryTime = times;
        if(tryTime < 3) {
            final String url = API.getBase() + "mobile.php?ismobile=yes";
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.getInstance().cancelTag(url);
                }
            }, 2000);
            OkHttpUtils
                    .get()
                    .url(url)
                    .tag(url)
                    .build()
                    .connTimeOut(2000)
                    .execute(new Callback<String>() {
                        @Override
                        public String parseNetworkResponse(Response response) throws Exception {
                            return new String(response.body().bytes(),"utf-8");
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            if (tryTime>= 2) {
                                tryTime = 3;
                                InitDialog.popInit(activity, true);
                            } else {
                                initHttp(activity,tryTime);
                            }
                            tryTime++;
                        }

                        @Override
                        public void onAfter() {
                            super.onAfter();
                        }

                        @Override
                        public void onResponse(String response) {
                            Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });
        }
    }


}
