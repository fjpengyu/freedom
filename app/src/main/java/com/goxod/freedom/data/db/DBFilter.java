package com.goxod.freedom.data.db;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by Levey on 2016/4/2.
 */

@Table("filter")
public class DBFilter {


    @PrimaryKey(AssignType.BY_MYSELF)
    @NotNull
    private String code;

    @NotNull
    private String[] image;

    @NotNull
    private String[] post;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
