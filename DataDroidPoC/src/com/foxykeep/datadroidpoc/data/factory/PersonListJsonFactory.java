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
import com.foxykeep.datadroidpoc.data.model.Person;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class PersonListJsonFactory {

    private static final String TAG = PersonListJsonFactory.class.getSimpleName();

    private PersonListJsonFactory() {
        // No public constructor
    }

    public static ArrayList<Person> parseResult(String wsResponse) throws DataException {
        ArrayList<Person> personList = new ArrayList<Person>();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONObject jsonRoot = parser.getJSONObject(JSONTag.PERSON_LIST_ELEM_PERSONS);
            JSONArray jsonPersonArray = jsonRoot
                    .getJSONArray(JSONTag.PERSON_LIST_ELEM_PERSON);
            int size = jsonPersonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonPerson = jsonPersonArray.getJSONObject(i);
                Person person = new Person();

                person.firstName = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_FIRST_NAME);
                person.lastName = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_LAST_NAME);
                person.email = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_EMAIL);
                person.city = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_CITY);
                person.postalCode = jsonPerson.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_POSTAL_CODE);
                person.age = jsonPerson.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_AGE);
                person.isWorking = jsonPerson.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_IS_WORKING) == 1;

                personList.add(person);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            throw new DataException(e);
        }

        return personList;
    }
}
