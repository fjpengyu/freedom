package com.goxod.freedom.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.okhttp.OkHttpUtils;


/**
 * Created by Levey on 2015/9/24.
 */
public class Sys {

    public static final boolean DEBUG = true;
    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;


    public static void out(String str) {
        if (DEBUG) {
            System.out.println(str);
        }
    }

    public static void log(String str) {
        if (DEBUG) {
            Log.d("Sys", "log: " + str);
        }
    }

    public static void toast(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void toastLong(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_LONG) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    private static long exitTime = 0;

    public static boolean doubleClick(Activity activity, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Sys.toast(activity.getApplicationContext(),
                        activity.getString(R.string.double_click_tips)
                                + activity.getString(R.string.app_name));
                exitTime = System.currentTimeMillis();
            } else {
                OkHttpUtils.getInstance().getCookieStore().removeAll();
                Freedom.notifyManager.cancelAll();
                Freedom.getInstance().exit();
            }
            return true;
        } else {
            return false;
        }
    }
}
