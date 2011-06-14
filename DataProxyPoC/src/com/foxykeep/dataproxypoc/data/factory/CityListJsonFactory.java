package com.foxykeep.dataproxypoc.data.factory;

import java.util.ArrayList;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foxykeep.dataproxypoc.config.JSONTag;
import com.foxykeep.dataproxypoc.data.model.City;
import com.foxykeep.dataproxypoc.data.requestmanager.PoCRequestManager;

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
            city.departementNumber = jsonPerson.getInt(JSONTag.CITY_LIST_ELEM_CITY_DEPARTEMENT_NUMBER);
            city.departementName = jsonPerson.getString(JSONTag.CITY_LIST_ELEM_CITY_DEPARTEMENT_NAME);

            cityList.add(city);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PoCRequestManager.RECEIVER_EXTRA_CITY_LIST, cityList);
        return bundle;
    }
}
