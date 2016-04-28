package com.goxod.freedom.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.fresco.FTool;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.ShareUtils;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.view.activity.ImagePaperActivity;
import com.goxod.freedom.view.activity.ImageListActivity;
import com.goxod.freedom.view.activity.MainActivity;

/**
 * Created by Levey on 16/2/5.
 */
public class ShareDialog {

    public static MaterialDialog dialog;
    public static List<PageBean> list;

    public static void popShare(final Context context, final ArrayList<String> list, final String imgUrl){
        final SimpleDraweeView ivShare;
        TextView tvQQ,tvWX,tvSave,tvDownload;
        if(MainActivity.itemId<= API.FRAGMENT_FAVORITE){
            dialog = new MaterialDialog.Builder(context)
                    .title(R.string.share_title)
                    .customView(R.layout.dialog_share, true)
                    .neutralText(R.string.favorite)
                    .negativeText(R.string.cancel)
                    .build();
            dialog.show();
            final MDButton favorite = dialog.getActionButton(DialogAction.NEUTRAL);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbUtil.saveFavoriteDb(
                            context,
                            Freedom.currentPageBean,
                            Freedom.currentPageBean.getTitle(),
                            Freedom.currentPageBean.getTypeF(),
                            0);
                }
            });
        }else {
            dialog = new MaterialDialog.Builder(context)
                    .title(R.string.share_title)
                    .customView(R.layout.dialog_share, true)
                    .negativeText(R.string.cancel)
                    .build();
            dialog.show();
        }
        ivShare = (SimpleDraweeView) dialog.findViewById(R.id.share_image);
        tvQQ = (TextView) dialog.findViewById(R.id.txt_qq);
        tvWX = (TextView) dialog.findViewById(R.id.txt_wx);
        tvSave = (TextView) dialog.findViewById(R.id.txt_save);
        tvDownload = (TextView) dialog.findViewById(R.id.txt_download);
        ivShare.setHierarchy(FTool.getHierarchy(context,false));
        ivShare.setController(FTool.getController(imgUrl, true));
        tvQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareUtils(context, imgUrl, ShareUtils.QQ);
            }
        });
        tvWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareUtils(context,imgUrl,ShareUtils.WX);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Freedom.config.isFirstRun()){
                    try{
                        ImageListActivity.activity.showFolderChooser(imgUrl);
                        saveSp(context);
                    }catch (Exception e){
                        try{
                            ImagePaperActivity.activity.showFolderChooser(imgUrl);
                            saveSp(context);
                        }catch (Exception e1){
                            Sys.toast(context,"系统错误!");
                        }
                    }
                }else{
                    Tools.saveImage(context, Freedom.config.getFolderPath(), imgUrl,-1);
                    saveSp(context);
                }

            }
        });

        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 1) {
                    SaveAllDialog.popAlert(context, list);
                } else {
                    Tools.saveImage(context, Freedom.config.getFolderPath(), imgUrl, -1);
                }
            }
        });


    }

    private static void saveSp(Context context){
        Freedom.config.setIsFirstRun(false);
        Freedom.config.setFolderPath(Freedom.config.getFolderPath());
        Config.saveSp(context, Freedom.config);
    }


}
