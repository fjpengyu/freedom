package com.goxod.freedom.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.goxod.freedom.util.Tools;
import com.squareup.okhttp.OkHttpClient;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;

/**
 * Created by Levey on 16/3/1.
 */
public class FTool {

    public static ImagePipelineConfig getConfig(Context context) {

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(Tools.getCacheDir()))
                .setBaseDirectoryName("cache")
                .setMaxCacheSize(2 * Tools.GB)
                .setVersion(1024)
                .build();

        return OkHttpImagePipelineConfigFactory.newBuilder(context, new OkHttpClient())
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setMainDiskCacheConfig(diskCacheConfig)
                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .setDecodeMemoryFileEnabled(true)
                .build();
    }


    public static DraweeController getController(String url, boolean isList) {
        int w;
        int h;
        if (isList) {
            w = h = AutoUtils.getPercentHeightSize(500);
        } else {
            w = h = AutoUtils.getPercentHeightSize(1920);
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(w, h))
                .setAutoRotateEnabled(true)
                .build();
        return Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(url))
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
    }

    public static GenericDraweeHierarchy getHierarchy(Context context,boolean isMz) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        return builder
                .setProgressBarImage(new FProgressBar())
                .setActualImageScaleType(isMz ? ScalingUtils.ScaleType.CENTER_CROP : ScalingUtils.ScaleType.FIT_CENTER)
                .setPlaceholderImage(Drawables.sPlaceholderDrawable)
                .setFailureImage(Drawables.sErrorDrawable)
                .build();
    }

    public static File getCacheFile(String url) {
        ImageRequest imageRequest = ImageRequest.fromUri(Uri.parse(url));
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
        BinaryResource resource = Fresco.getImagePipelineFactory().getMainDiskStorageCache().getResource(cacheKey);
        return ((FileBinaryResource) resource).getFile();
    }


    public static CacheKey getCacheKey(String url){
        ImageRequest imageRequest = ImageRequest.fromUri(Uri.parse(url));
        return DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
    }

}
