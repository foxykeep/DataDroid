/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.foxykeep.datadroidpoc.config.JSONTag;
import com.foxykeep.datadroidpoc.data.model.Phone;

public class PhoneAddEditFactory {

    public static Phone parseResult(final String wsResponse) throws JSONException {
        final JSONObject parser = new JSONObject(wsResponse);
        final JSONObject jsonPhone = parser.getJSONObject(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_PHONE);

        final Phone phone = new Phone();

        phone.serverId = jsonPhone.getLong(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_ID);
        phone.name = jsonPhone.getString(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_NAME);
        phone.manufacturer = jsonPhone.getString(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_MANUFACTURER);
        phone.androidVersion = jsonPhone.getString(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_ANDROID_VERSION);
        phone.screenSize = jsonPhone.getDouble(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_SCREEN_SIZE);
        phone.price = jsonPhone.getInt(JSONTag.CRUD_PHONE_ADD_EDIT_ELEM_PRICE);

        return phone;
    }

}
