package com.goxod.freedom.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.goxod.freedom.Freedom;
import com.goxod.freedom.R;
import com.goxod.freedom.data.adapter.StringItemAdapter;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.Sys;

/**
 * Created by Levey on 2016/2/17.
 */
public class AboutDialog {

    public static void popAbout(final Context context,boolean isUpdate){
        final MaterialDialog dialog;

        if(Freedom.config.isUpdate() && isUpdate){
            dialog = new MaterialDialog.Builder(context)
                    .title(R.string.change_log_title)
                    .customView(R.layout.dialog_about,true)
                    .negativeText(R.string.apply)
                    .neutralText(R.string.no_alert)
                    .cancelable(false)
                    .build();
            dialog.show();
            final MDButton btn = dialog.getActionButton(DialogAction.NEUTRAL);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Freedom.config.setIsUpdate(false);
                    Config.saveSp(context, Freedom.config);
                }
            });
        }else{
            dialog = new MaterialDialog.Builder(context)
                    .title(R.string.change_log_title)
                    .customView(R.layout.dialog_about,true)
                    .negativeText(R.string.apply)
                    .build();
            dialog.show();
        }

        ListView changeLog = (ListView) dialog.findViewById(R.id.change_log_list);
        TextView update = (TextView) dialog.findViewById(R.id.update);
        changeLog.setAdapter(new StringItemAdapter(context,3));


        if(Freedom.config.isUpdate() && isUpdate) {
            String cv = context.getString(R.string.current_version);
            String name = context.getString(R.string.version_name);
            String cvName = cv + name;
            update.setText(cvName);
        }else{
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sys.toast(context, context.getString(R.string.update_check));
                    DialogUtils.checkUpdate(context,true);
                }
            });
        }
    }
}
