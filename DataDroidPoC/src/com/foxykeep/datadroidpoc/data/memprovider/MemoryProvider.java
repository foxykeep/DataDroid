/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.memprovider;

import java.util.ArrayList;

import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.model.Phone;

public class MemoryProvider {

    private static MemoryProvider sInstance;

    public static MemoryProvider getInstance() {
        if (sInstance == null) {
            sInstance = new MemoryProvider();
        }
        return sInstance;
    }

    public static void resetInstance() {
        sInstance = null;
    }

    private MemoryProvider() {

    }

    // CityList WS
    public ArrayList<City> cityList;

    // SyncPhoneList WS
    public ArrayList<Phone> syncPhoneList;

    // SyncPhoneDelete WS
    public long[] syncPhoneDeleteData;

    // SyncPhoneAdd WS
    public Phone syncPhoneAddData;

    // SyncPhoneEdit WS
    public Phone syncPhoneEditData;
}
