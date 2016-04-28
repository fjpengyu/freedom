package com.goxod.freedom.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.MzBean;
import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.data.db.DbUtil;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.util.listener.NoDoubleClickListener;
import com.goxod.freedom.view.dialog.AboutDialog;
import com.goxod.freedom.view.dialog.BackUpDialog;
import com.goxod.freedom.view.dialog.DialogUtils;
import com.goxod.freedom.view.dialog.MenuDialog;
import com.goxod.freedom.view.fragment.ClFragment;
import com.goxod.freedom.view.fragment.MzFragment;
import com.jaeger.library.StatusBarUtil;
import com.shizhefei.mvc.MVCHelper;
import com.victor.loading.rotate.RotateLoading;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * Created by Levey on 16/1/25.
 */

public class MainActivity extends AutoLayoutActivity implements FolderChooserDialog.FolderCallback, FileChooserDialog.FileCallback {


    private final static int SELECT_FOLDER = 69;
    private List<Fragment> mTabs = new ArrayList<>();
    public static final HashMap<Integer, MVCHelper<List<PageBean>>> mvcPageList = new HashMap<>();
    public static final HashMap<Integer, MVCHelper<List<MzBean>>> mvcMzList = new HashMap<>();
    public static final HashMap<Integer, ListView> lvList = new HashMap<>();
    private static TextView appTitle;
    public static TextView btnRefresh, btnNav;
    private static long mLastTime = 0;
    public static int itemId = 0;
    public static MainActivity activity;
    public static RotateLoading rl;
    private static int saveId;
    private static DrawerLayout drawer;
    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Freedom.getInstance().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            setTheme(R.style.AppTheme_FullScreen);
        }else{
            setTheme(R.style.AppTheme_NoActionBar);
        }
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setTranslucent(this, 0);
        }
        activity = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionRequest();
        }

        if (Freedom.config.isUpdate()) {
            AboutDialog.popAbout(activity, true);
        }
        if (Freedom.config.isAutoUpdate()) {
            DialogUtils.checkUpdate(activity, false);
        }
        Tools.filterDir(Freedom.config.getFolderPath());
        rl = (RotateLoading) findViewById(R.id.loading);
        mViewPager = (ViewPager) findViewById(R.id.page_viewpager);
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        RelativeLayout navHeader = (RelativeLayout) findViewById(R.id.nav_header);
        appTitle = (TextView) findViewById(R.id.app_title);
        btnRefresh = (TextView) findViewById(R.id.btn_refresh);
        btnNav = (TextView) findViewById(R.id.btn_nav);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout setting = (LinearLayout) findViewById(R.id.nav_setting);
        LinearLayout btnNavZp = (LinearLayout) findViewById(R.id.nav_mz);
        LinearLayout btnNavCl = (LinearLayout) findViewById(R.id.nav_cl);
        int tempId = Freedom.config.getFirstFragment();
        if (Freedom.config.getNavMode() == API.NAV_MODE_CL) {
            saveId = itemId = tempId;
        } else {
            saveId = itemId = tempId - API.FRAGMENT_MZ_ZP;
        }
        assert navHeader != null;
        navHeader.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                AboutDialog.popAbout(activity, false);
            }
        });
        btnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        assert btnNavCl != null;
        btnNavCl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                if (Freedom.config.getNavMode() != API.NAV_MODE_CL) {
                    changeMainView(API.NAV_MODE_CL, true);
                }
            }
        });
        assert btnNavZp != null;
        btnNavZp.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                if (Freedom.config.getNavMode() != API.NAV_MODE_MZ) {
                    changeMainView(API.NAV_MODE_MZ, true);
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRefresh.startAnimation(Tools.getAnim(getApplicationContext()));
                if (Freedom.config.getNavMode() == API.NAV_MODE_CL && itemId == API.FRAGMENT_FAVORITE) {
                    btnRefresh.clearAnimation();
                    BackUpDialog.popBackup(activity);
                } else {
                    listRefresh(itemId);
                }
            }
        });


        assert setting != null;
        setting.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        MenuDialog.popMenu(activity);
                    }
                }.execute();
            }
        });

        assert header != null;
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - mLastTime) > 1000) {
                    mLastTime = System.currentTimeMillis();
                } else {
                    listRefresh(itemId);
                }
            }
        });
        header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                        .title(appTitle.getText())
                        .content("是否删除当前页面阅读记录?")
                        .positiveText(R.string.apply)
                        .negativeText(R.string.cancel)
                        .build();
                dialog.show();

                MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
                btn_apply.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        dialog.dismiss();
                        if (Freedom.config.getNavMode() == API.NAV_MODE_CL) {
                            DbUtil.cleanNotes(itemId);
                            mvcPageList.get(itemId).getAdapter().notifyDataChanged(null, false);
                        } else {
                            DbUtil.cleanNotes(itemId + API.FRAGMENT_MZ_ZP);
                            mvcMzList.get(itemId + API.FRAGMENT_MZ_ZP).getAdapter().notifyDataChanged(null, false);
                        }
                        Sys.toast(getApplicationContext(), "阅读记录已清空");
                    }
                });
                return true;
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initView();
        } else {
            if (Freedom.config.isPermission()) {
                initView();
            }
        }
        mViewPager.setOffscreenPageLimit(4);
    }


    private void initView() {
        if(Freedom.updateBean == null){
            DbUtil.loadUpdateBean();
        }
        changeMainView(Freedom.config.getNavMode(), false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveId = itemId = position;
                if (Freedom.config.getNavMode() == API.NAV_MODE_CL) {
                    setTitleColor(activity, position);
                    if (mvcPageList.get(position).getAdapter().isEmpty()) {
                        mvcPageList.get(position).refresh();
                    }
                } else {
                    position = position + API.FRAGMENT_MZ_ZP;
                    setTitleColor(activity, position);
                    if (mvcMzList.get(position).getAdapter().isEmpty()) {
                        mvcMzList.get(position).refresh();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPermissionRequest() {
        PermissionGen.needPermission(activity, 100,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @PermissionSuccess(requestCode = 100)
    public void success() {
        if (!Freedom.config.isPermission()) {
            Freedom.config.setPermission(true);
            Config.saveSp(getApplicationContext(), Freedom.config);
        }
        Freedom.initDb(activity);
        DbUtil.saveUpdateBean(Freedom.updateBean);
        initView();
    }

    @PermissionFail(requestCode = 100)
    public void fail() {
        Sys.toastLong(activity, "         您已拒绝存储授权!\n" +
                "程序初始化失败,即将关闭!");
        new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                finish();
                System.exit(0);
            }
        }.execute();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return Sys.doubleClick(this, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private static void setTitleColor(Activity activity, int item) {
        switch (item) {
            case API.FRAGMENT_FLAG:
                appTitle.setText(R.string.app_title_flag);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_AGE:
                appTitle.setText(R.string.app_title_age);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_TECH:
                appTitle.setText(R.string.app_title_tech);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_FAVORITE:
                appTitle.setText(R.string.app_title_favorite);
                btnRefresh.setText(R.string.fa_cog);
                break;
            case API.FRAGMENT_MZ_ZP:
                appTitle.setText(R.string.app_title_mz_zp);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_MZ_JP:
                appTitle.setText(R.string.app_title_mz_jp);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_MZ_TW:
                appTitle.setText(R.string.app_title_mz_tw);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_MZ_QC:
                appTitle.setText(R.string.app_title_mz_qc);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            case API.FRAGMENT_MZ_XG:
                appTitle.setText(R.string.app_title_mz_xg);
                btnRefresh.setText(R.string.fa_refresh);
                break;
            default:
                appTitle.setText(R.string.app_title_flag);
                btnRefresh.setText(R.string.fa_refresh);
                break;
        }
    }

    public static void listRefresh(int itemId) {

        if (Freedom.config.getNavMode() == API.NAV_MODE_CL) {
            mvcPageList.get(itemId).refresh();
            lvList.get(itemId).smoothScrollToPositionFromTop(0, 0, 300);
        } else {
            mvcMzList.get(itemId + API.FRAGMENT_MZ_ZP).refresh();
            lvList.get(itemId + API.FRAGMENT_MZ_ZP).smoothScrollToPositionFromTop(0, 0, 300);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showFolderChooser() {
        openChooser(Type.FOLDER);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showFileChooser() {
        openChooser(Type.FILE);
    }

    private void openChooser(Type type) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FOLDER);
            return;
        }
        switch (type) {
            case FOLDER:
                new FolderChooserDialog.Builder(activity)
                        .chooseButton(R.string.choose_folder)
                        .initialPath(Freedom.config.getFolderPath())
                        .show();
                break;
            case FILE:
                new FileChooserDialog.Builder(activity)
                        .initialPath(Freedom.config.getFolderPath())
                        .show();
                break;
        }
    }

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {
    }

    @Override
    public void onFolderSelection(FolderChooserDialog dialog, File folder) {
        switch (itemId) {
            case API.LOCAL_BACKUP:
                try {
                    File fDb = Tools.backup(activity, folder.getAbsolutePath(), Tools.DbType.DB);
                    Tools.backup(activity, folder.getAbsolutePath(), Tools.DbType.DB_JOURNAL);
                    assert fDb != null;
                    Sys.toast(activity, "备份成功\n" + fDb.getParent());
                } catch (Exception e) {
                    e.printStackTrace();
                    Sys.toast(activity, "备份失败");
                }
                break;
            default:
                MenuDialog.setFolderPath(folder.getAbsolutePath());
                Freedom.config.setFolderPath(folder.getAbsolutePath());
                Config.saveSp(getApplicationContext(), Freedom.config);
                break;
        }
    }

    private enum Type {
        FOLDER,
        FILE
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        itemId = saveId;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void changeMainView(int type, boolean isInit) {
        mViewPager.removeAllViews();
        mViewPager.removeAllViewsInLayout();
        mTabs.clear();
        mvcPageList.clear();
        mvcMzList.clear();
        lvList.clear();
        switch (type) {
            case API.NAV_MODE_CL:
                Freedom.config.setNavMode(API.NAV_MODE_CL);
                if (isInit) {
                    Freedom.config.setFirstFragment(API.FRAGMENT_TECH);
                    setTitleColor(activity, API.FRAGMENT_TECH);
                    saveId = itemId = API.FRAGMENT_TECH;
                } else {
                    setTitleColor(activity, itemId);
                }
                mTabs.add(0, new ClFragment(API.FRAGMENT_FLAG));
                mTabs.add(1, new ClFragment(API.FRAGMENT_AGE));
                mTabs.add(2, new ClFragment(API.FRAGMENT_TECH));
                mTabs.add(3, new ClFragment(API.FRAGMENT_FAVORITE));
                break;
            default:
                Freedom.config.setNavMode(API.NAV_MODE_MZ);
                if (isInit) {
                    Freedom.config.setFirstFragment(API.FRAGMENT_MZ_ZP);
                    setTitleColor(activity, API.FRAGMENT_MZ_ZP);
                    saveId = itemId = 0;
                } else {
                    setTitleColor(activity, itemId + API.FRAGMENT_MZ_ZP);
                }
                mTabs.add(0, new MzFragment(API.FRAGMENT_MZ_ZP));
                mTabs.add(1, new MzFragment(API.FRAGMENT_MZ_JP));
                mTabs.add(2, new MzFragment(API.FRAGMENT_MZ_TW));
                mTabs.add(3, new MzFragment(API.FRAGMENT_MZ_QC));
                mTabs.add(4, new MzFragment(API.FRAGMENT_MZ_XG));
                break;
        }
        if (mAdapter == null) {
            mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public int getCount() {
                    return mTabs.size();
                }

                @Override
                public Fragment getItem(int arg0) {
                    return mTabs.get(arg0);
                }
            };
        }
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(itemId);
    }
}
