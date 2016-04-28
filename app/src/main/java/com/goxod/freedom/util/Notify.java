package com.goxod.freedom.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;

import java.io.File;

/**
 * Created by vange-levey on 16/3/3.
 */
public class Notify {

    public static void image(Context context, int type, String mText, int mMax, int mProgress) {
        boolean holding = !(mMax == mProgress);
        int max, progress;
        if (mMax == mProgress) {
            max = progress = 0;
        } else {
            max = mMax;
            progress = mProgress;
        }
        String title, text, ticker;
        if (holding) {
            title = "下载中...    " + mProgress + "/" + mMax;
            text = mText.substring(mText.lastIndexOf("/") + 1, mText.length());
            ticker = "开始下载";
        } else {
            title = mText.substring(mText.lastIndexOf("/") + 1, mText.length());
            text = "下载完成    " + mMax + "P";
            ticker = title + "   下载完成";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(holding)
                .setProgress(max, progress, false)
                .setSmallIcon(R.mipmap.ic_launcher);
        if (!holding) {
            Uri uri = Uri.fromFile(new File(Freedom.config.getFolderPath() + "/images/"));
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setData(uri);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);
        }
        Freedom.notifyManager.notify(type, mBuilder.build());
    }

    public static void video(Context context, int type, String mText, int mMax, int mProgress) {
        boolean holding = !(mMax == mProgress);
        int max, progress;
        if (mMax == mProgress) {
            max = progress = 0;
        } else {
            max = mMax;
            progress = mProgress;
        }
        String title, text, ticker;
        float fPro = Tools.getDec((float)mProgress/100f);
        if (holding) {
            title = "视频下载中...    " + fPro + " %";
            text = mText.substring(mText.lastIndexOf("/") + 1, mText.length());
            ticker = "开始下载";
        } else {
            title = mText.substring(mText.lastIndexOf("/") + 1, mText.length());
            String fileSize = Tools.getSizeStr(Tools.sizeOf(new File(Tools.getVideoDir() + mText)));
            text = "下载完成    " + fileSize;
            ticker = title + "   下载完成";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(holding)
                .setProgress(max, progress, false)
                .setSmallIcon(R.mipmap.ic_launcher);
        if (!holding) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File file = new File(Tools.getVideoDir()+mText);
            intent.setDataAndType(Uri.fromFile(file), "video/*");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);
        }
        Freedom.notifyManager.notify(type, mBuilder.build());
    }

}
