package com.goxod.freedom.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.alibaba.fastjson.JSON;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.UpdateBean;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.callback.Callback;
import com.goxod.freedom.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by Levey on 2016/2/17.
 */
public class DialogUtils {

    public final static int CLEAN_CACHE = 1;
    public final static int CONFIRM_UPDATE = 2;
    public final static int CANCEL_UPDATE = 3;
    public final static int CLEAN_FAVORITE = 4;
    private static String path;

    public static void popConfirm(final Context context, final UpdateBean updateBean,final int popType){
        String title,content,toast,cleanAll = "";
        switch (popType){
            case CLEAN_CACHE:
                title = getStr(context,R.string.clean_cache_title);
                content = getStr(context,R.string.clean_cache_content);
                toast = getStr(context,R.string.clean_cache_toast);
                cleanAll = context.getString(R.string.clean_cache_only_read);
                break;
            case CONFIRM_UPDATE:
                title = getStr(context,R.string.confirm_update_title) + "  " + updateBean.getVersion();
                content = getStr(context,R.string.confirm_update_content);
                toast = getStr(context,R.string.confirm_update_toast);
                break;
            case CANCEL_UPDATE:
                title = getStr(context,R.string.cancel_update_title);
                content = getStr(context,R.string.cancel_update_content);
                toast = getStr(context,R.string.cancel_update_toast);
                break;
            case CLEAN_FAVORITE:
                title = getStr(context,R.string.clean_favorite_title);
                content = getStr(context,R.string.clean_favorite_content);
                toast = getStr(context,R.string.clean_favorite_toast);
                break;
            default:
                title = getStr(context, R.string.apply_title);
                content = getStr(context,R.string.apply_content);
                toast = getStr(context,R.string.apply_toast);
                break;
        }

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(R.string.apply)
                .negativeText(R.string.cancel)
                .neutralText(cleanAll)
                .build();
        dialog.show();

        if(popType == CLEAN_CACHE) {
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
            final MDButton btn_clean_read = dialog.getActionButton(DialogAction.NEUTRAL);
            btn_clean_read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    cleanNotes();
                    Sys.toast(context,"阅读记录已清空");
                }
            });
        }else{
            dialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);
        }

        final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
        final String finalToast = toast;
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (popType){
                    case CLEAN_CACHE:
                        cleanNotes();
                        Fresco.getImagePipelineFactory().getImagePipeline().clearDiskCaches();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                MenuDialog.setCacheSize();
                            }
                        }, 1000);
                        break;
                    case CONFIRM_UPDATE:
                        final boolean[] isCancel = {false};
                        path = "freedom_" + updateBean.getVersion() + ".apk";
                        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .title(updateBean.getVersion() + " 更新中...")
                                .progress(false,100,true)
                                .negativeText("取消")
                                .cancelable(false)
                                .show();
                        final MDButton btn_cancel = dialog.getActionButton(DialogAction.NEGATIVE);

                        OkHttpUtils
                                .get()
                                .url(updateBean.getUrl())
                                .tag(path)
                                .build()
                                .execute(new FileCallBack(Tools.getDownloadDir(), path) {

                                    @Override
                                    public void inProgress(float progress, long total) {
                                        dialog.setProgress((int) progress);
                                        dialog.incrementProgress((int) (dialog.getMaxProgress() * progress));
                                    }

                                    @Override
                                    public File parseNetworkResponse(okhttp3.Response response) throws Exception {
                                        dialog.setMaxProgress((int) response.body().contentLength());
                                        return super.parseNetworkResponse(response);
                                    }

                                    @Override
                                    public void onError(Call call, Exception e) {
                                        dialog.dismiss();
                                        e.printStackTrace();
                                        Sys.toast(context, "下载错误!");
                                    }

                                    @Override
                                    public void onResponse(File file) {
                                        dialog.dismiss();
                                        Freedom.config.setIsUpdate(true);
                                        Config.saveSp(context, Freedom.config);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setDataAndType(Uri.fromFile(file),
                                                "application/vnd.android.package-archive");
                                        context.startActivity(intent);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }

                                    @Override
                                    public void onAfter() {
                                        super.onAfter();
                                        if (isCancel[0]) {
                                            Sys.toast(context, context.getString(R.string.cancel_update_toast));
                                        }
                                    }
                                });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isCancel[0] = true;
                                DialogUtils.popConfirm(context, null, DialogUtils.CANCEL_UPDATE);
                            }
                        });

                        break;
                    case CANCEL_UPDATE:
                        OkHttpUtils.getInstance().cancelTag(path);
                        break;
                    case CLEAN_FAVORITE:
                        DbUtil.delAllFavorite();
                        MainActivity.mvcPageList.get(API.FRAGMENT_FAVORITE).refresh();
                        break;
                    default:break;
                }
                Sys.toast(context, finalToast);
            }
        });
    }

    private static String getStr(Context context, int id){
        return context.getResources().getString(id);
    }

    public static void checkUpdate(final Context context,final boolean isManual){

        OkHttpUtils
                .get()
                .url(context.getResources().getString(R.string.version_url))
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(okhttp3.Response response) throws Exception {
                        return new String(response.body().bytes(), "utf-8");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        if(isManual){Sys.toast(context, context.getString(R.string.update_check_error));}
                    }

                    @Override
                    public void onResponse(String response) {
                        UpdateBean updateBean = JSON.parseObject(response, UpdateBean.class);
                        int updateVersion = updateBean.getCode();
                        int currentVersion = 0;
                        try {
                            PackageManager manager = context.getPackageManager();
                            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                            currentVersion = info.versionCode;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (updateVersion > currentVersion) {
                            DialogUtils.popConfirm(context, updateBean, DialogUtils.CONFIRM_UPDATE);
                        }else{
                            if(isManual){Sys.toast(context, context.getString(R.string.check_update_toast));}
                        }

                    }
                });

    }

    private static void cleanNotes(){
        DbUtil.cleanNotes(-1);
        for (int i = 0; i < MainActivity.mvcPageList.size(); i++) {
            MainActivity.mvcPageList.get(i).getAdapter().notifyDataChanged(null,false);
        }
        for (int j = 0; j < MainActivity.mvcMzList.size(); j++) {
            MainActivity.mvcMzList.get(j + API.FRAGMENT_MZ_ZP).getAdapter().notifyDataChanged(null,false);
        }
    }
}
