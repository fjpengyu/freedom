package com.goxod.freedom.view.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.view.custom.PhotoView;
import com.goxod.freedom.view.dialog.ShareDialog;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Levey on 16/2/21.
 */

public class ImagePaperActivity extends AutoLayoutActivity implements FolderChooserDialog.FolderCallback{

    private final static int STORAGE_PERMISSION_RC = 69;
    public static ImagePaperActivity activity;
    private ViewPager mPager;
    private TextView current;
    private TextView total;
    private ArrayList<String> list;
    private static boolean isSingle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Freedom.getInstance().addActivity(this);
        setContentView(R.layout.activity_image_single);
        activity = this;
        isSingle = getIntent().getBooleanExtra(API.SINGLE_IMAGE_MODE, false);
        if(isSingle){
            list = new ArrayList<>();
            list.add(getIntent().getStringExtra(API.SINGLE_IMAGE_URL));
        }else{
            list = getIntent().getStringArrayListExtra(API.LIST);
        }
        int itemId = getIntent().getIntExtra(API.ITEM, 0);
        mPager = (ViewPager) findViewById(R.id.image_viewpager);
        current = (TextView) findViewById(R.id.image_current);
        current.setText(String.valueOf(itemId + 1));
        total = (TextView) findViewById(R.id.image_total);
        TextView line = (TextView) findViewById(R.id.image_line);
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                total.setText(String.valueOf(list.size()));
                return isSingle ? 1 : list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }


            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                PhotoView view = new PhotoView(activity);
                view.setImage(list.get(position));
                view.enable();
                view.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        ShareDialog.popShare(activity, list, list.get(position));
                        return true;
                    }
                });
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Fresco.getImagePipelineFactory().getImagePipeline().evictFromMemoryCache(Uri.parse(list.get(position)));
                container.removeView((View) object);
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPager.setCurrentItem(itemId);
        mPager.setOffscreenPageLimit(1);

        if(isSingle){
            total.setVisibility(View.INVISIBLE);
            current.setVisibility(View.INVISIBLE);
            assert line != null;
            line.setVisibility(View.INVISIBLE);
        }else{
            total.setVisibility(View.VISIBLE);
            current.setVisibility(View.VISIBLE);
            assert line != null;
            line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPager.destroyDrawingCache();
        Fresco.getImagePipelineFactory().getImagePipeline().clearMemoryCaches();
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
        Tools.saveImage(getApplicationContext(), folder.getAbsolutePath(), imgUrl, -1);
    }
}
