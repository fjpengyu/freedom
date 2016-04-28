package com.goxod.freedom.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.goxod.freedom.R;
import com.goxod.freedom.fresco.FTool;
import com.goxod.freedom.request.API;
import com.goxod.freedom.view.activity.ImagePaperActivity;
import com.goxod.freedom.view.dialog.ShareDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Levey on 16/2/16.
 */
public class ImageItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> list;
    private Context context;
    Map<String,Integer> heightMap = new HashMap<>();
    Map<String,Integer> widthMap = new HashMap<>();
    private int w,h;
    public ImageItemAdapter(Context context, ArrayList<String> items, int column) {
        this.list = items;
        this.context = context;
        switch (column){
            case 1:w = h = 600;break;
            case 2:w = h = 300;break;
            case 3:w = h = 200;break;
            default:w = h = 300;break;
        }
    }

    public void setData(ArrayList<String> datas) {
        list = datas;
    }

    public void addDatas(ArrayList<String> datas) {
        list.addAll(datas);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ViewHolder vh = (ViewHolder) holder;
        final int pos = holder.getAdapterPosition();
        final  String url = list.get(pos);
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                if (qualityInfo.isOfGoodEnoughQuality()) {
                    int heightTarget = (int) getTargetHeight(imageInfo.getWidth(), imageInfo.getHeight(), vh.itemView, url);
                    if (heightTarget <= 0) return;
                    heightMap.put(url, heightTarget);
                    updateItemHeight(heightTarget, vh.itemView);
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
            } 
        };
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(w, h))
                .setImageType(ImageRequest.ImageType.SMALL)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller =  Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setControllerListener(controllerListener)
                .build();
        vh.sdv.setController(controller);
        vh.sdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePaperActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(API.LIST, list);
                bundle.putInt(API.ITEM, pos);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        String txt = (pos + 1) + " / " + getItemCount();
        vh.txt.setText(txt);
        vh.sdv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShareDialog.popShare(context, list, list.get(pos));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdv;
        TextView txt;
        public ViewHolder(View view) {
            super(view);
            sdv = (SimpleDraweeView) view.findViewById(R.id.image_item);
            txt = (TextView) view.findViewById(R.id.item_txt);
            sdv.setHierarchy(FTool.getHierarchy(context,false));
        }
    }


    private float getTargetHeight(float width,float height,View view, String url){
        View child = view.findViewById(R.id.image_item);
        float widthTarget;
        if (widthMap.containsKey(url)){
            widthTarget = widthMap.get(url);
        }
        else {
            widthTarget = child.getMeasuredWidth();
            if (widthTarget>0){
                widthMap.put(url, (int) widthTarget);
            }
        }
        return height * (widthTarget /width);
    }

    private void updateItemHeight(int height, View view) {
        CardView cardView = (CardView) view.findViewById(R.id.card_view);
        View child = view.findViewById(R.id.image_item);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) child.getLayoutParams();
        layoutParams.height = height;
        cardView.updateViewLayout(child,layoutParams);
    }
}
