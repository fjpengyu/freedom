package com.goxod.freedom.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.bean.SpBean;
import com.goxod.freedom.bean.UpdateBean;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;

import okhttp3.Call;

/**
 * Created by Levey on 16/2/5.
 */
public class Config {

    private static final String IS_FIRST_RUN = "isFirstRun";
    private static final String IS_OFFICIAL = "isOfficial";
    private static final String CUSTOM_SERVER = "customServer";
    private static final String LIST_MODE = "listMode";
    private static final String FOLDER_PATH = "folderPath";
    private static final String FIRST_FRAGMENT = "firstFragment";
    private static final String IS_AUTO_UPDATE = "isAutoUpdate";
    private static final String IS_UPDATE = "isUpdate";
    private static final String NAV_MODE = "nav_mode";
    private static final String PERMISSION = "have_permission";

    public static void saveSp(Context context, SpBean spBean){
        SharedPreferences settings = context.getSharedPreferences("fog_sp", 0);
        settings.edit()
                .putBoolean(IS_FIRST_RUN, spBean.isFirstRun())
                .putBoolean(IS_OFFICIAL, spBean.isOfficial())
                .putString(CUSTOM_SERVER, spBean.getCustomServer())
                .putInt(LIST_MODE,spBean.getListMode())
                .putString(FOLDER_PATH,spBean.getFolderPath())
                .putInt(FIRST_FRAGMENT,spBean.getFirstFragment())
                .putBoolean(IS_AUTO_UPDATE,spBean.isAutoUpdate())
                .putBoolean(IS_UPDATE,spBean.isUpdate())
                .putInt(NAV_MODE,spBean.getNavMode())
                .putBoolean(PERMISSION,spBean.isPermission())
                .apply();
    }

    public static SpBean loadSp(Context context){
        SpBean spBean = new SpBean();
        SharedPreferences sp = context.getSharedPreferences("fog_sp", 0);
        spBean.setIsFirstRun(sp.getBoolean(IS_FIRST_RUN, true));
        spBean.setIsOfficial(sp.getBoolean(IS_OFFICIAL, true));
        spBean.setCustomServer(sp.getString(CUSTOM_SERVER, API.BASE_CUSTOM));
        spBean.setListMode(sp.getInt(LIST_MODE, API.ONE_COLUMN_MODE));
        String fPath = sp.getString(FOLDER_PATH, Tools.getCacheDir());
        spBean.setFolderPath(fPath.toLowerCase().contains("null") ? Tools.getCacheDir() : fPath);
        spBean.setFirstFragment(sp.getInt(FIRST_FRAGMENT, API.FRAGMENT_MZ_ZP));
        spBean.setIsAutoUpdate(sp.getBoolean(IS_AUTO_UPDATE, true));
        spBean.setIsUpdate(sp.getBoolean(IS_UPDATE, true));
        spBean.setNavMode(sp.getInt(NAV_MODE, API.NAV_MODE_MZ));
        spBean.setPermission(sp.getBoolean(PERMISSION,false));
        return spBean;
    }

    public static void initFilter(final Context context, String url){
        OkHttpUtils
                .post()
                .url(url)
                .tag(url)
                .build()
                .connTimeOut(2000)
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                        return new String(response.body().bytes(), "utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            UpdateBean updateBean = JSON.parseObject(response, UpdateBean.class);
                            Freedom.updateBean.setImage(updateBean.getImage());
                            Freedom.updateBean.setPost(updateBean.getPost());
                            if (Freedom.config.isPermission() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                Freedom.initDb(context.getApplicationContext());
                                DbUtil.saveUpdateBean(updateBean);
                            }
                        }catch (Exception e){
                            e.fillInStackTrace();
                        }
                    }
                });
    }
}
