/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.memprovider;

import java.util.ArrayList;

import com.foxykeep.dataproxypoc.data.model.City;

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
}
