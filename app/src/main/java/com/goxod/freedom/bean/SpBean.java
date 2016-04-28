package com.goxod.freedom.bean;

/**
 * Created by Levey on 2016/2/13.
 */
public class SpBean {

    private boolean isFirstRun;
    private boolean isOfficial;
    private String customServer;
    private int listMode;
    private String folderPath;
    private int firstFragment;
    private boolean isAutoUpdate;
    private boolean isUpdate;
    private int navMode;
    private boolean permission;

    public boolean isFirstRun() {
        return isFirstRun;
    }

    public void setIsFirstRun(boolean isFirstRun) {
        this.isFirstRun = isFirstRun;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setIsOfficial(boolean isOfficial) {
        this.isOfficial = isOfficial;
    }

    public String getCustomServer() {
        return customServer;
    }

    public void setCustomServer(String customServer) {
        this.customServer = customServer;
    }

    public int getListMode() {
        return listMode;
    }

    public void setListMode(int listMode) {
        this.listMode = listMode;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getFirstFragment() {
        return firstFragment;
    }

    public void setFirstFragment(int firstFragment) {
        this.firstFragment = firstFragment;
    }

    public boolean isAutoUpdate() {
        return isAutoUpdate;
    }

    public void setIsAutoUpdate(boolean isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public int getNavMode() {
        return navMode;
    }

    public void setNavMode(int navMode) {
        this.navMode = navMode;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
}
