package com.goxod.freedom.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.fresco.FTool;
import com.goxod.freedom.view.dialog.ShareDialog;

/**
 * Created by Levey on 16/1/25.
 */
public class Tools {

    private static final char WINDOWS_SEPARATOR = '\\';
    private static final char SYSTEM_SEPARATOR = File.separatorChar;
    public static final long KB = 1024L;
    public static final long MB = 1024L * 1024L;
    public static final long GB = 1024L * 1024L * 1024L;

    public enum DbType {
        DB,
        DB_JOURNAL
    }

    public static String md5Encode(String inStr) {
        MessageDigest md5;
        byte[] byteArray;
        try {
            md5 = MessageDigest.getInstance("MD5");

            byteArray = inStr.getBytes("UTF-8");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase().substring(10, 25);
    }

    public static String getDbPath() {
        String path =  getCacheDir() + "/database/";
        if (isDirExists(path)) {
            return path;
        }
        return path;
    }

    public static String getVideoDir() {
        String path =  Freedom.config.getFolderPath() + "/videos/";
        if (isDirExists(path)) {
            return path;
        }
        return path;
    }

    public static String getCacheDir() {

        String path = Environment.getExternalStorageDirectory() + "/Freedom/";
        if (isDirExists(path)) {
            return path;
        }
        return path;
    }

    public static String getDownloadDir() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS;
    }


    public static boolean isDirExists(String path) {
        boolean isExists = false;
        try {
            File dcmDir = new File(path);
            if (!dcmDir.exists()) {
                if (dcmDir.mkdirs()) {
                    isExists = true;
                }
            } else {
                isExists = true;
            }
        } catch (Exception e) {
            isExists = false;
        }
        return isExists;
    }


    public static Animation getAnim(Context context) {
        return AnimationUtils.loadAnimation(context,
                R.anim.refresh_anim);
    }


    public static String getFileSize(File d) {
        float size = (float) sizeOfDirectory(d);
        return getSizeStr(size);
    }

    public static String getDirSize(String dir){
        return getSizeStr(sizeOfDirectory(new File(dir)));
    }

    public static String getSizeStr(float size) {
        if (size <= KB) {
            return getDec(size) + " B";
        } else if (size > KB && size <= MB) {
            return getDec(size / KB) + " KB";
        } else if (size > MB && size < GB) {
            return getDec(size / MB) + " MB";
        } else {
            return getDec(size / GB) + " GB";
        }
    }

    public static float getDec(float size) {
        return (float) (Math.round(size * 100)) / 100;
    }

    public static long sizeOfDirectory(File directory) {
        if (!checkDirectory(directory)) {
            return 0L;
        }
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }
        long size = 0;

        for (final File file : files) {
            try {
                if (!isSymlink(file)) {
                    size += sizeOf(file);
                    if (size < 0) {
                        break;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return size;
    }

    public static long sizeOf(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            return sizeOfDirectory(file);
        } else {
            return file.length();
        }
    }

    private static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }

    private static boolean checkDirectory(File directory) {
        return directory.exists() && directory.isDirectory();

    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        if (isSystemWindows()) {
            return false;
        }
        File fileInCanonicalDir;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }
        return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
    }


    public static void saveImage(final Context context, final String folder, final String url, final int position) {

        try {
            File f = save(folder, url, position);
            Sys.toast(context, "图片保存成功!\n" + f.getAbsolutePath());
            ShareDialog.dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Sys.toast(context, "图片保存失败!");
        }

    }

    public static boolean filterDir(String dir){
        File nomedia = new File(dir + "/.nomedia" );
        if (!nomedia.exists()) {
            try {
                if (nomedia.createNewFile()) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static File save(String folder, String url, int position) throws Exception {
        final File file = FTool.getCacheFile(url);
        final String fileName;
        final String suffix = url.endsWith(".gif") ? ".gif" : ".jpg";
        if (position < 0) {
            fileName = "" + Tools.md5Encode(file.getName().replace(".cnt","")) + suffix;
        } else {
            fileName = "" + (position + 1) + suffix;
        }
        InputStream is = new FileInputStream(file.getAbsolutePath());
        OutputStream os = new FileOutputStream(folder + "/" + fileName);
        byte bt[] = new byte[1024];
        int c;
        while ((c = is.read(bt)) > 0) {
            os.write(bt, 0, c);
        }
        is.close();
        os.close();
        return new File(folder + "/" + fileName);
    }

    public static File backup(Context context, String folder, DbType dbType) throws Exception {
        String dbPtah;
        switch (dbType) {
            case DB:
                dbPtah = Tools.getDbPath() + DbUtil.DB_NAME;
                break;
            default:
                dbPtah = Tools.getDbPath() + DbUtil.DB_JOURNAL;
                break;
        }

        File db = new File(dbPtah);
        String folderDir = folder + "/freedom_backup_" + getBackUpTime() + "/";
        if (Tools.isDirExists(folderDir)) {
            String path = folderDir + db.getName();
            InputStream is = new FileInputStream(db.getAbsolutePath());
            OutputStream os = new FileOutputStream(path);
            byte bt[] = new byte[1024];
            int c;
            while ((c = is.read(bt)) > 0) {
                os.write(bt, 0, c);
            }
            is.close();
            os.close();
            File f = new File(path);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(f);
            intent.setData(uri);
            context.sendBroadcast(intent);
            return f;
        }
        return null;
    }

    public static File recovery(Context context, String folder, int dbType) throws Exception {
        String dbPtah;
        if (dbType == 1) {
            dbPtah = folder + DbUtil.DB_NAME;
        } else {
            dbPtah = folder + DbUtil.DB_JOURNAL;
        }
        File db = new File(dbPtah);
        String folderDir = Tools.getDbPath();
        if (Tools.isDirExists(folderDir)) {
            String path = folderDir + db.getName();
            InputStream is = new FileInputStream(db.getAbsolutePath());
            OutputStream os = new FileOutputStream(path);
            byte bt[] = new byte[1024];
            int c;
            while ((c = is.read(bt)) > 0) {
                os.write(bt, 0, c);
            }
            is.close();
            os.close();
            File f = new File(path);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(f);
            intent.setData(uri);
            context.sendBroadcast(intent);
            return f;
        }
        return null;
    }
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String getBackUpTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

}
