/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.factory;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foxykeep.datadroidpoc.config.JSONTag;
import com.foxykeep.datadroidpoc.data.model.Person;

public class PersonListJsonFactory {

    public static ArrayList<Person> parseResult(final String wsResponse) throws JSONException {
        final ArrayList<Person> personList = new ArrayList<Person>();

        final JSONObject parser = new JSONObject(wsResponse);
        final JSONObject jsonRoot = parser.getJSONObject(JSONTag.PERSON_LIST_ELEM_PERSONS);
        final JSONArray jsonPersonArray = jsonRoot.getJSONArray(JSONTag.PERSON_LIST_ELEM_PERSON);
        final int size = jsonPersonArray.length();
        for (int i = 0; i < size; i++) {
            final JSONObject jsonPerson = jsonPersonArray.getJSONObject(i);
            final Person person = new Person();

            person.firstName = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_FIRST_NAME);
            person.lastName = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_LAST_NAME);
            person.email = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_EMAIL);
            person.city = jsonPerson.getString(JSONTag.PERSON_LIST_ELEM_PERSON_CITY);
            person.postalCode = jsonPerson.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_POSTAL_CODE);
            person.age = jsonPerson.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_AGE);
            person.isWorking = jsonPerson.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_IS_WORKING) == 1;

            personList.add(person);
        }

        return personList;
    }
}
