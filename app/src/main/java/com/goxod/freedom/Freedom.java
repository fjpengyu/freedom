package com.goxod.freedom;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.bean.SpBean;
import com.goxod.freedom.bean.UpdateBean;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.fresco.Drawables;
import com.goxod.freedom.fresco.FTool;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.Tools;
import com.litesuits.orm.LiteOrm;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Levey on 16/1/27.
 */
public class Freedom extends Application {

    public static SpBean config;
    public static PageBean currentPageBean;
    public static LiteOrm liteDb;
    public static NotificationManager notifyManager;
    public static UpdateBean updateBean = new UpdateBean();
    public static String mzTitle = "empty";
    private static Freedom instance;
    private List<Activity> mList = new LinkedList<>();


    public synchronized static Freedom getInstance() {
        if (null == instance) {
            instance = new Freedom();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            CrashReport.initCrashReport(getApplicationContext(), "900023582", false);
        }catch (Exception e){
            e.fillInStackTrace();
        }
        config = Config.loadSp(getApplicationContext());
        Drawables.init(getResources());
        Fresco.initialize(getApplicationContext(), FTool.getConfig(getApplicationContext()));
        if(!Fresco.getImagePipelineFactory().getMainDiskStorageCache().isEnabled()){
            Fresco.initialize(getApplicationContext(), FTool.getConfig(getApplicationContext()));
        }
        Tools.getDbPath();
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public static void initDb(Context context){
        if (liteDb == null) {
            liteDb = LiteOrm.newSingleInstance(context, Tools.getDbPath() + DbUtil.DB_NAME);
        }
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
