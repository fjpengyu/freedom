package com.goxod.freedom.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.util.List;
import com.goxod.freedom.fresco.FTool;

/**
 * Created by Levey on 16/2/17.
 */
public class ShareUtils {

    public static int QQ = 1;
    public static int WX = 2;

    public ShareUtils(Context context,String path,int type) {
        if(type==QQ){
            shareImage(context,path,new ShareItem("QQ","com.tencent.mobileqq.activity.JumpActivity","com.tencent.mobileqq"));
        }
        if(type==WX){
            shareImage(context,path,new ShareItem("微信","com.tencent.mm.ui.tools.ShareImgUI", "com.tencent.mm"));
        }
    }

    private void shareImage(Context context, String imageUrl,ShareItem share) {
        if (!share.packageName.isEmpty() && !isAvailable(context, share.packageName)) {
            Sys.toast(context,"请先安装 " + share.title);
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FTool.getCacheFile(imageUrl)));
        if(!share.packageName.isEmpty()) {
            shareIntent.setComponent(new ComponentName(share.packageName,share.activityName));
            context.startActivity(shareIntent);
        }
        else {
            context.startActivity(Intent.createChooser(shareIntent,Intent.ACTION_ALL_APPS));
        }

    }

    public boolean isAvailable(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        for (int i = 0; i < info.size(); i++) {
            if (info.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    private class ShareItem {
        String title,activityName,packageName;
        public ShareItem(String title,String activityName, String packageName) {
            this.title = title;
            this.activityName = activityName;
            this.packageName = packageName;
        }
    }
}
