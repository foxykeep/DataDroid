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
import com.foxykeep.datadroidpoc.data.model.Phone;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public final class PhoneAddEditFactory {

    private static final String TAG = PhoneAddEditFactory.class.getSimpleName();

    private PhoneAddEditFactory() {
        // No public constructor
    }

    public static Phone parseResult(String wsResponse) throws DataException {
        Phone phone = new Phone();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONObject jsonPhone = parser
                    .getJSONObject(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_PHONE);

            phone.serverId = jsonPhone.getLong(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_ID);
            phone.name = jsonPhone.getString(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_NAME);
            phone.manufacturer = jsonPhone.getString(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_MANUFACTURER);
            phone.androidVersion = jsonPhone
                    .getString(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_ANDROID_VERSION);
            phone.screenSize = jsonPhone.getDouble(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_SCREEN_SIZE);
            phone.price = jsonPhone.getInt(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_PRICE);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            throw new DataException(e);
        }

        return phone;
    }

}
