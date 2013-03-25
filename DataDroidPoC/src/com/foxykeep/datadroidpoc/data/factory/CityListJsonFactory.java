/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.factory;

import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroidpoc.config.JSONTag;
import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class CityListJsonFactory {

    private static final String TAG = CityListJsonFactory.class.getSimpleName();

    private CityListJsonFactory() {
        // No public constructor
    }

    public static Bundle parseResult(String wsResponse) throws DataException {
        ArrayList<City> cityList = new ArrayList<City>();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONObject jsonRoot = parser.getJSONObject(JSONTag.CITY_LIST_ELEM_CITIES);
            JSONArray jsonPersonArray = jsonRoot.getJSONArray(JSONTag.CITY_LIST_ELEM_CITY);
            int size = jsonPersonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonPerson = jsonPersonArray.getJSONObject(i);
                City city = new City();

                city.name = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_NAME);
                city.postalCode = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_POSTAL_CODE);
                city.state = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_STATE);
                city.country = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_COUNTRY);

                cityList.add(city);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            throw new DataException(e);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PoCRequestFactory.BUNDLE_EXTRA_CITY_LIST, cityList);
        return bundle;
    }
}
