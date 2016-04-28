package com.goxod.freedom.data.db;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by Levey on 2016/3/24.
 */


@Table("notes")
public class DbNote {

    public static final String TYPE = "type";
    public static final String URL = "url";

    @PrimaryKey(AssignType.BY_MYSELF)
    @NotNull
    private String url;

    @NotNull
    private int type;

    @NotNull
    private int page;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
