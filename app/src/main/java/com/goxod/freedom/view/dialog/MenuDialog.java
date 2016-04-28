package com.goxod.freedom.view.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import com.goxod.freedom.Freedom;
import com.goxod.freedom.view.activity.MainActivity;
import com.goxod.freedom.R;
import com.goxod.freedom.data.adapter.StringItemAdapter;
import com.goxod.freedom.request.API;
import com.goxod.freedom.util.Config;
import com.goxod.freedom.util.Sys;
import com.goxod.freedom.util.Tools;
import com.goxod.freedom.view.custom.AutoSpinner;

/**
 * Created by Levey on 16/2/5.
 */
public class MenuDialog {

    private static TextView tvFolderPath, tvCacheSize, tvAutoUpdate;
    private static EditText etServer;

    public static void popMenu(final Activity context) {
        Switch swServer, swUpdate;
        final AutoSpinner spListMode, spFirstFragment;
        final TextView btnFolder, btnCache, tvIsOfficial, btnServerSetting;
        final RelativeLayout about;

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.menu_options)
                .customView(R.layout.dialog_menu, true)
                .positiveText(R.string.apply)
                .neutralText(R.string.cancel)
                .cancelable(false)
                .build();
        dialog.show();

        final MDButton btn_cancel = dialog.getActionButton(DialogAction.NEUTRAL);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final MDButton btn_apply = dialog.getActionButton(DialogAction.POSITIVE);

        etServer = (EditText) dialog.findViewById(R.id.et_server);
        swServer = (Switch) dialog.findViewById(R.id.sw_server);
        spListMode = (AutoSpinner) dialog.findViewById(R.id.sp_list);
        spFirstFragment = (AutoSpinner) dialog.findViewById(R.id.sp_first);
        tvFolderPath = (TextView) dialog.findViewById(R.id.tv_folder_path);
        btnFolder = (TextView) dialog.findViewById(R.id.btn_folder);
        tvCacheSize = (TextView) dialog.findViewById(R.id.tv_cache_size);
        btnCache = (TextView) dialog.findViewById(R.id.btn_cache);
        tvIsOfficial = (TextView) dialog.findViewById(R.id.tv_is_official);
        about = (RelativeLayout) dialog.findViewById(R.id.about);
        tvAutoUpdate = (TextView) dialog.findViewById(R.id.tv_is_auto_update);
        swUpdate = (Switch) dialog.findViewById(R.id.sw_update);
        btnServerSetting = (TextView) dialog.findViewById(R.id.btn_server_test);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialog.popAbout(context, false);
            }
        });
        swServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Freedom.config.setIsOfficial(isChecked);
                if (Freedom.config.isOfficial()) {
                    etServer.setText(API.BASE_OFFICIAL);
                    btnServerSetting.setEnabled(false);
                    btnServerSetting.setVisibility(View.GONE);
                    tvIsOfficial.setText(R.string.tv_official);
                } else {
                    etServer.setText(Freedom.config.getCustomServer());
                    btnServerSetting.setVisibility(View.VISIBLE);
                    btnServerSetting.setEnabled(true);
                    tvIsOfficial.setText(R.string.tv_custom);
                }
                Config.saveSp(context, Freedom.config);
            }
        });

        btnServerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = InitDialog.popInit(context, false);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        etServer.setText(Freedom.config.getCustomServer());
                    }
                });
            }
        });

        swUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Freedom.config.setIsAutoUpdate(isChecked);
                if (Freedom.config.isAutoUpdate()) {
                    tvAutoUpdate.setText(R.string.auto_update);
                } else {
                    tvAutoUpdate.setText(R.string.manual_update);
                }
                Config.saveSp(context, Freedom.config);
            }
        });

        spListMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Freedom.config.setListMode(position);
                Config.saveSp(context, Freedom.config);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spListMode.setAdapter(new StringItemAdapter(context,1));
        spFirstFragment.setAdapter(new StringItemAdapter(context,2));
        spFirstFragment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case API.FRAGMENT_FLAG:
                    case API.FRAGMENT_AGE:
                    case API.FRAGMENT_TECH:
                    case API.FRAGMENT_FAVORITE:
                        Freedom.config.setNavMode(API.NAV_MODE_CL);
                        break;
                    case API.FRAGMENT_MZ_ZP:
                    case API.FRAGMENT_MZ_JP:
                    case API.FRAGMENT_MZ_TW:
                    case API.FRAGMENT_MZ_QC:
                    case API.FRAGMENT_MZ_XG:
                        Freedom.config.setNavMode(API.NAV_MODE_MZ);
                        break;
                    default:
                        break;
                }
                Freedom.config.setFirstFragment(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activity.showFolderChooser();
            }
        });

        btnCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.popConfirm(context, null, DialogUtils.CLEAN_CACHE);
            }
        });
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Freedom.config.setCustomServer(etServer.getText().toString().equals(API.BASE_OFFICIAL) ? Freedom.config.getCustomServer() : etServer.getText().toString());
                Config.saveSp(context, Freedom.config);
                Sys.toast(context, "选项保存成功!");
                dialog.dismiss();
            }
        });

        swServer.setChecked(Freedom.config.isOfficial());
        etServer.setText(Freedom.config.isOfficial() ? API.BASE_OFFICIAL : Freedom.config.getCustomServer());
        etServer.setEnabled(false);
        spListMode.setSelection(Freedom.config.getListMode());
        spFirstFragment.setSelection(Freedom.config.getFirstFragment());
        tvFolderPath.setText(Freedom.config.getFolderPath());
        btnServerSetting.setVisibility(Freedom.config.isOfficial() ? View.GONE : View.VISIBLE);
        swUpdate.setChecked(Freedom.config.isAutoUpdate());
        setCacheSize();
    }

    public static void setFolderPath(String path) {
        tvFolderPath.setText(path);
    }

    public static void setCacheSize() {
        String size = Tools.getDirSize(Tools.getCacheDir() + "cache");
        tvCacheSize.setText(size);
    }
}
