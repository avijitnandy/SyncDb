package com.w3xplorers.syncdb;

/**
 * Created by DELL on 5/25/2017.
 */

public class Contacts {

    private String name;
    private int sync_status;

    Contacts(String name,int sync_status){

        this.setName(name);
        this.setSync_status(sync_status);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}
