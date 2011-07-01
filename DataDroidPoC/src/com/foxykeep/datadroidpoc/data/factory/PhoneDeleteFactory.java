/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.factory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foxykeep.datadroidpoc.config.JSONTag;

public class PhoneDeleteFactory {

    public static long[] parseResult(final String wsResponse) throws JSONException {

        final JSONObject parser = new JSONObject(wsResponse);
        final JSONObject jsonRoot = parser.getJSONObject(JSONTag.CRUD_PHONE_DELETE_ELEM_PHONES);
        final JSONArray jsonPhoneArray = jsonRoot.getJSONArray(JSONTag.CRUD_PHONE_DELETE_ELEM_PHONE);
        final int size = jsonPhoneArray.length();

        final long[] deletedPhoneIdArray = new long[size];

        for (int i = 0; i < size; i++) {
            deletedPhoneIdArray[i] = jsonPhoneArray.getJSONObject(i).getLong(JSONTag.CRUD_PHONE_DELETE_ELEM_ID);
        }

        return deletedPhoneIdArray;
    }
}
