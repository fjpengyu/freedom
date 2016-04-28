package com.goxod.freedom.request;


import com.goxod.freedom.Freedom;

/**
 * Created by Levey on 16/1/26.
 */
public class API {

    public static final int FRAGMENT_FLAG = 0;
    public static final int FRAGMENT_AGE = 1;
    public static final int FRAGMENT_TECH = 2;
    public static final int FRAGMENT_FAVORITE = 3;
    public static final int FRAGMENT_MZ_ZP = 4;
    public static final int FRAGMENT_MZ_JP = 5;
    public static final int FRAGMENT_MZ_TW = 6;
    public static final int FRAGMENT_MZ_QC = 7;
    public static final int FRAGMENT_MZ_XG = 8;
    public static final int LOCAL_BACKUP = 101;
    public static final int LOCAL_RECOVERY = 102;
    public static final int REMOTE_BACKUP = 103;
    public static final int REMOTE_RECOVERY = 104;
    public static final int CARD_PAPER_MODE = 0;
    public static final int ONE_COLUMN_MODE = 1;
    public static final int NAV_MODE_CL = 1;
    public static final int NAV_MODE_MZ = 2;


    public static final String BASE_CUSTOM = "www.t66y.com";
    public static final String BASE_OFFICIAL = "www.t66y.com";
    public static final String LIST = "images_list";
    public static final String ITEM = "current_item";
    public static final String TITLE = "post_title";
    public static final String PAGE_ID = "page_id";
    public static final String PAGE_URL = "comments_url";
    public static final String VIDEO_URL = "video_url";
    public static final String IS_VIDEO = "is_video";
    public static final String SINGLE_IMAGE_URL = "single_image";
    public static final String SINGLE_IMAGE_MODE = "single_image_mode";
    public static final String UA_TITLE = "user-agent";
    public static final String UA_CONTENT = "Mozilla/5.0 (Linux; Android 4.4.4; Nexus 5 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.114 Mobile Safari/537.36";

    public static String getBase(){
            return toUrl(Freedom.config.isOfficial() ? BASE_OFFICIAL : Freedom.config.getCustomServer());
    }

    public static String getImageUrl(String url){
        if(BASE_CUSTOM.contains(Freedom.config.getCustomServer())){
            return (Freedom.config.isOfficial() ? "" : "") + url;
        }else{
            return url;
        }
    }

    public static String toUrl(String url){
        if(url.startsWith("https://") || url.startsWith("http://")){
            return url.endsWith("/") ? url :  url + "/";
        }else {
            return "http://" + (url.endsWith("/") ? url : url + "/");
        }
    }

    public static String getMobUrl(String url){
        if(url.contains("htm_data")){
            url = url.replace("htm_data","htm_mob");
        }
        return url;
    }

    public static String getStr(int navType){
        String typeStr;
        switch (navType){
            case API.FRAGMENT_MZ_ZP:
                typeStr = "mm";
                break;
            case API.FRAGMENT_MZ_JP:
                typeStr = "japan";
                break;
            case API.FRAGMENT_MZ_TW:
                typeStr = "taiwan";
                break;
            case API.FRAGMENT_MZ_QC:
                typeStr = "mm";
                break;
            case API.FRAGMENT_MZ_XG:
                typeStr = "xinggan";
                break;
            default:
                typeStr = "mm";
                break;
        }
        return typeStr;
    }
}
