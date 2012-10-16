/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.factory;

import android.os.Bundle;

import com.foxykeep.datadroidpoc.config.JSONTag;
import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CityListJsonFactory {

    public static Bundle parseResult(final String wsResponse) throws JSONException {
        final ArrayList<City> cityList = new ArrayList<City>();

        final JSONObject parser = new JSONObject(wsResponse);
        final JSONObject jsonRoot = parser.getJSONObject(JSONTag.CITY_LIST_ELEM_CITIES);
        final JSONArray jsonPersonArray = jsonRoot.getJSONArray(JSONTag.CITY_LIST_ELEM_CITY);
        final int size = jsonPersonArray.length();
        for (int i = 0; i < size; i++) {
            final JSONObject jsonPerson = jsonPersonArray.getJSONObject(i);
            final City city = new City();

            city.name = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_NAME);
            city.postalCode = jsonPerson.getInt(JSONTag.CITY_LIST_ELEM_CITY_POSTAL_CODE);
            city.countyNumber = jsonPerson.getInt(JSONTag.CITY_LIST_ELEM_CITY_COUNTY_NUMBER);
            city.countyName = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_COUNTY_NAME);

            cityList.add(city);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PoCRequestManager.RECEIVER_EXTRA_CITY_LIST, cityList);
        return bundle;
    }
}
