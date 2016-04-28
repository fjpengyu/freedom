package com.goxod.freedom.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.fresco.FTool;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Notify;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.view.activity.MainActivity;

/**
 * Created by Levey on 16/2/18.
 */
public class SaveAllDialog {

    private static DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
    private static int pro = 0;
    private static int max = 0;

    public static void popAlert(final Context context, final ArrayList<String> list) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .content(R.string.save_all)
                .positiveText(R.string.apply)
                .negativeText(R.string.cancel)
                .build();
        dialog.show();
        final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sys.toast(context, "开始保存整组图片");
                dialog.dismiss();
                File f;
                if (MainActivity.itemId <= API.FRAGMENT_FAVORITE) {
                    try {
                        f = new File(Freedom.config.getFolderPath() + "/images/" + Freedom.currentPageBean.getTitle());
                    } catch (Exception e) {
                        e.printStackTrace();
                        f = new File(Freedom.config.getFolderPath() + "/images/" + System.currentTimeMillis());
                    }
                } else {
                    f = new File(Freedom.config.getFolderPath() + "/images/" + Freedom.mzTitle);
                    Freedom.mzTitle = "empty";
                }
                if (Tools.isDirExists(f.getParent())) {
                    if (!f.exists()) {
                        if (f.mkdir()) {
                            saveImages(context, f.getAbsolutePath(), list);
                        }
                    } else {
                        saveImages(context, f.getAbsolutePath(), list);
                    }
                }
            }
        });
        if (mDraweeHolder == null) {
            mDraweeHolder = DraweeHolder.create(FTool.getHierarchy(context,false), context);
        }
    }

    private static void saveImages(Context context, String path, ArrayList<String> list) {
        mDraweeHolder.onAttach();

        for (int j = 0; j < list.size(); j++) {
            String url = list.get(j);
            if (url.contains(".gif")) {
                list.remove(j);
            }
        }
        max = pro = list.size();
        int type = (int) System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            String url = list.get(i);
            saveImage(context, type, path, url, i);
        }
    }

    private static void saveImage(final Context context, final int type, final String path, final String url, final int position) {
        if (pro == max) {
            Notify.image(context, type, path, max, max - pro);
        }
        if (Fresco.getImagePipelineFactory().getMainDiskStorageCache().hasKey(FTool.getCacheKey(url))) {
            try {
                Tools.save(path, url, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pro--;
            Notify.image(context, type, path, max, max - pro);
        } else {
            DraweeHolder<GenericDraweeHierarchy> holder = new DraweeHolder<>(mDraweeHolder.getHierarchy());
            final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                    .setAutoRotateEnabled(true)
                    .build();
            final ImagePipeline imagePipeline = Fresco.getImagePipeline();
            final DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
            final AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(mDraweeHolder.getController())
                    .setImageRequest(imageRequest)
                    .setAutoPlayAnimations(true)
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            CloseableReference<CloseableImage> imageCloseableReference = dataSource.getResult();
                            try {
                                if (imageCloseableReference != null) {
                                    final CloseableImage image = imageCloseableReference.get();
                                    if (image != null && image instanceof CloseableStaticBitmap) {
                                        CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                        final Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                        if (bitmap != null) {
                                            try {
                                                Tools.save(path, url, position);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    if (image != null) {
                                        image.close();
                                    }
                                }
                            } finally {
                                CloseableReference.closeSafely(imageCloseableReference);
                                dataSource.close();
                            }
                            pro--;
                            Notify.image(context, type, path, max, max - pro);
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                            super.onFailure(id, throwable);
                            pro--;
                            Notify.image(context, type, path, max, max - pro);
                        }
                    })
                    .build();
            holder.setController(controller);
            holder.onAttach();
        }
    }
}
