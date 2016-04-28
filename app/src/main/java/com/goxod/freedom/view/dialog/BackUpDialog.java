package com.goxod.freedom.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.R;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Sys;

/**
 * Created by Levey on 16/2/25.
 */
public class BackUpDialog {
    public static void popBackup(final Context context){

        TextView btnLocalBackup,btnLocalRecovery,btnRemoteBackup,btnRemoteRecovery;

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.menu_favorite)
                .customView(R.layout.dialog_favorite, true)
                .positiveText(R.string.apply)
                .neutralText(R.string.txt_clean_favorite)
                .build();
        dialog.show();


        final MDButton cleanAll = dialog.getActionButton(DialogAction.NEUTRAL);
        cleanAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.popConfirm(context,null,DialogUtils.CLEAN_FAVORITE);
            }
        });

        btnLocalBackup = (TextView) dialog.findViewById(R.id.btn_local_backup);
        btnLocalRecovery = (TextView) dialog.findViewById(R.id.btn_local_recovery);
        btnRemoteBackup = (TextView) dialog.findViewById(R.id.btn_remote_backup);
        btnRemoteRecovery = (TextView) dialog.findViewById(R.id.btn_remote_recovery);
        btnLocalBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.itemId = API.LOCAL_BACKUP;
                MainActivity.activity.showFolderChooser();
            }
        });

        btnLocalRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.itemId = API.LOCAL_RECOVERY;
                Sys.toast(context, "功能开发中");
            }
        });

        btnRemoteBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.itemId = API.REMOTE_BACKUP;
                Sys.toast(context,"功能开发中");
            }
        });

        btnRemoteRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.itemId = API.REMOTE_RECOVERY;
                Sys.toast(context,"功能开发中");
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MainActivity.itemId = API.FRAGMENT_FAVORITE;
            }
        });
    }
}
