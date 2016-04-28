package com.goxod.freedom.bean;

/**
 * Created by Levey on 2016/2/21.
 */
public class UpdateBean {
    private int code;
    private String version;
    private String url;
    private String[] image;
    private String[] post;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getImage() {
        return image;
    }

    public void setImage(String[] image) {
        this.image = image;
    }

    public String[] getPost() {
        return post;
    }

    public void setPost(String[] post) {
        this.post = post;
    }
}
