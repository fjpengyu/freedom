package com.goxod.freedom.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.data.adapter.ImageItemAdapter;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Tools;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Levey on 16/2/21.
 */

public class ImageListActivity extends AutoLayoutActivity implements FolderChooserDialog.FolderCallback{

    private final static int STORAGE_PERMISSION_RC = 69;
    public static ImageListActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Freedom.getInstance().addActivity(this);
        setContentView(R.layout.activity_image_list);
        activity = this;
        ArrayList<String> list = getIntent().getStringArrayListExtra(API.LIST);
        int itemId = getIntent().getIntExtra(API.ITEM, 0);
        boolean isTest = getIntent().getBooleanExtra("isTest", false);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle_list);
        int mColumnCount = isTest ? getIntent().getIntExtra("Column",1) : Freedom.config.getListMode() ;
        ImageItemAdapter adapter = new ImageItemAdapter(activity, list,mColumnCount);
        if (API.ONE_COLUMN_MODE == mColumnCount) {
            assert rv != null;
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } else {
            assert rv != null;
            rv.setLayoutManager(new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL));
        }
        rv.setAdapter(adapter);
        rv.setHasFixedSize(false);

        rv.setItemAnimator(new DefaultItemAnimator());
        rv.scrollToPosition(itemId);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fresco.getImagePipelineFactory().getImagePipeline().clearMemoryCaches();
        finish();
    }


    private static String imgUrl;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showFolderChooser(String url) {
        imgUrl = url;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
            return;
        }
        new FolderChooserDialog.Builder(activity)
                .chooseButton(R.string.choose_folder)
                .initialPath(Freedom.config.getFolderPath())
                .show();
    }

    @Override
    public void onFolderSelection(FolderChooserDialog dialog, File folder) {
        Tools.saveImage(getApplicationContext(), folder.getAbsolutePath(), imgUrl,-1);
    }

}
