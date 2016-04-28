package com.goxod.freedom.data.db;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.bean.PageBean;
import com.goxod.freedom.bean.UpdateBean;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.view.activity.MainActivity;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Levey on 2016/2/25.
 */
public class DbUtil {

    public static final String DB_NAME = "freedom.db";
    public static final String DB_JOURNAL = "freedom.db-journal";

    public static void loadUpdateBean() {
        List<DBFilter> df = Freedom.liteDb.query(DBFilter.class);
        for (int i = 0; i < df.size(); i++) {
            if (i == 1) {
                Freedom.updateBean.setImage(df.get(0).getImage());
                Freedom.updateBean.setPost(df.get(0).getPost());
                return;
            }
        }
    }

    public static void saveUpdateBean(UpdateBean updateBean) {
        try {
            Freedom.liteDb.deleteAll(DBFilter.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBFilter dbFilter = new DBFilter();
        dbFilter.setCode(String.valueOf(updateBean.getCode()));
        dbFilter.setImage(updateBean.getImage());
        dbFilter.setPost(updateBean.getPost());
        Freedom.liteDb.save(dbFilter);
    }

    public static void saveNote(DbNote note) {
        Freedom.liteDb.save(note);
    }

    public static List<DbNote> loadNotes(int type) {
        List<DbNote> notes = new ArrayList<>();
        try {
            notes = Freedom.liteDb.query(
                    new QueryBuilder<>(DbNote.class).where(DbNote.TYPE + " LIKE ? ", new Integer[]{type}));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    public static DbNote loadOneNote(String url) {
        List<DbNote> notes;
        DbNote note = new DbNote();
        try {
            notes = Freedom.liteDb.query(
                    new QueryBuilder<>(DbNote.class).where(DbNote.URL + " LIKE ? ", new String[]{url}));
            note.setPage(notes.get(0).getPage());
        } catch (Exception e) {
            note.setPage(1);
        }
        return note;
    }

    public static void cleanNotes(int type) {
        if (type != -1) {
            Freedom.liteDb.delete(new WhereBuilder(DbNote.class)
                    .where(DbNote.TYPE + " LIKE ? ", new Integer[]{type}));
        } else {
            Freedom.liteDb.deleteAll(DbNote.class);
        }
    }

    public static void saveFavorite(boolean isUpdate, PageBean pb, String type, int pageId) {
        DbFavor c = new DbFavor();
        c.setTitle(pb.getTitle());
        c.setAuthor(pb.getAuthor() == null ? "樓主好人" : pb.getAuthor());
        c.setUrl(pb.getUrl());
        c.setTime(Tools.getDate());
        c.setType(type);
        c.setPageId(pageId);

        if (isUpdate) {
            Freedom.liteDb.delete(new WhereBuilder(DbFavor.class)
                    .where(DbFavor.URL + " LIKE ? ", new String[]{c.getUrl()}));
            Freedom.liteDb.save(c);
        } else {
            Freedom.liteDb.save(c);
        }
        MainActivity.mvcPageList.get(API.FRAGMENT_FAVORITE).refresh();
    }

    public static void delFavoriteItem(String url) {
        Freedom.liteDb.delete(new WhereBuilder(DbFavor.class)
                .where(DbFavor.URL + " LIKE ? ", new String[]{url}));
    }

    public static void delAllFavorite() {
        Freedom.liteDb.deleteAll(DbFavor.class);
    }

    public static List<DbFavor> loadFavorite() {
        List<DbFavor> db = new ArrayList<>();
        List cs = Freedom.liteDb.query(DbFavor.class);
        for (int i = 0; i < cs.size(); i++) {
            db.add((DbFavor) cs.get(i));
        }
        return db;
    }

    public static void delFavoriteDialog(final Context context, final String title, final String url) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.db_delete_title)
                .content(title)
                .positiveText(R.string.apply)
                .negativeText(R.string.cancel)
                .build();
        dialog.show();

        final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFavoriteItem(url);
                dialog.dismiss();
                MainActivity.mvcPageList.get(API.FRAGMENT_FAVORITE).refresh();
                Sys.toast(context, context.getString(R.string.db_delete_toast));
            }
        });

    }

    public static int getPageId(final PageBean pb) {
        int pageId = 0;
        boolean isEmpty = Freedom.liteDb.query(new QueryBuilder<>(DbFavor.class)
                .where(DbFavor.URL + " LIKE ? ", new String[]{pb.getUrl()})).isEmpty();
        if (!isEmpty) {
            pageId = Freedom.liteDb.query(new QueryBuilder<>(DbFavor.class)
                    .where(DbFavor.URL + " LIKE ? ", new String[]{pb.getUrl()})).get(0).getPageId();
        }
        return pageId;
    }

    public static void saveFavoriteDb(final Context context, final PageBean pb, String title, int type, final int pageId) {

        if (Freedom.liteDb != null) {

            String typeStr;
            switch (type) {
                case API.FRAGMENT_FLAG:
                    typeStr = context.getString(R.string.app_title_flag);
                    break;
                case API.FRAGMENT_AGE:
                    typeStr = context.getString(R.string.app_title_age);
                    break;
                case API.FRAGMENT_TECH:
                    typeStr = context.getString(R.string.app_title_tech);
                    break;
                default:
                    typeStr = "默認分類";
                    break;
            }

            final String fType = typeStr;


            boolean isEmpty = Freedom.liteDb.query(new QueryBuilder<>(DbFavor.class)
                    .where(DbFavor.URL + " LIKE ? ", new String[]{pb.getUrl()})).isEmpty();
            if (isEmpty) {
                final MaterialDialog dialog;
                if (pageId == 0) {
                    dialog = new MaterialDialog.Builder(context)
                            .title(R.string.db_title)
                            .content(title)
                            .positiveText(R.string.apply)
                            .negativeText(R.string.cancel)
                            .build();
                    dialog.show();
                } else {
                    dialog = new MaterialDialog.Builder(context)
                            .title(R.string.db_title)
                            .content(title)
                            .positiveText(R.string.apply)
                            .negativeText(R.string.cancel)
                            .neutralText("保存页码")
                            .build();
                    dialog.show();

                    final MDButton btn_here = dialog.getActionButton(DialogAction.NEUTRAL);
                    btn_here.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveFavorite(false, pb, fType, pageId);
                            dialog.dismiss();
                            Sys.toast(context, context.getString(R.string.db_page));
                        }
                    });
                }
                final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);
                btn_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveFavorite(false, pb, fType, 0);
                        dialog.dismiss();
                        Sys.toast(context, context.getString(R.string.db_toast));
                    }
                });
            } else {
                final MaterialDialog dialog;

                if (pageId == 0) {
                    dialog = new MaterialDialog.Builder(context)
                            .title(R.string.db_saved_title)
                            .content(pb.getTitle())
                            .positiveText(R.string.apply)
                            .build();
                    dialog.show();
                } else {
                    dialog = new MaterialDialog.Builder(context)
                            .title(R.string.db_saved_title)
                            .content(pb.getTitle())
                            .positiveText(R.string.apply)
                            .neutralText("保存页码")
                            .build();
                    dialog.show();
                    final MDButton btn_here = dialog.getActionButton(DialogAction.NEUTRAL);
                    btn_here.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveFavorite(true, pb, fType, pageId);
                            dialog.dismiss();
                            Sys.toast(context, context.getString(R.string.db_page));
                        }
                    });
                }
            }
        } else {
            Sys.toast(context, "您已禁用存储授权\n" +
                    " 无法使用收藏夹");
        }

    }

}
