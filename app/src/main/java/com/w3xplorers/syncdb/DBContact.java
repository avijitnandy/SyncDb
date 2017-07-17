package com.w3xplorers.syncdb;

/**
 * Created by DELL on 5/25/2017.
 */

public class DBContact {

    public static final int sync_status_ok = 0;
    public static final int sync_status_failed = 1;
    public static final String SERVER_URL = "http://192.168.198.2/sqlitesync-main/syncinfo.php";
    public static final String UI_UPDATE_BROADCAST = "com.w3xplorers.synctestmain.uiupdatebroadcast";

    public static final String DATABASE_NAME =  "sqlite_db";
    public static final String TABLE_NAME =  "users";
    public static final String NAME =  "name";
    public static final String STATUS =  "sync_status";
}
