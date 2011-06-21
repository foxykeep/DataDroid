package com.foxykeep.datadroidpoc;

import greendroid.app.GDApplication;

import com.foxykeep.datadroidpoc.ui.HomeActivity;

public class DataDroidApplication extends GDApplication {

    @Override
    public Class<?> getHomeActivityClass() {
        return HomeActivity.class;
    }
}
