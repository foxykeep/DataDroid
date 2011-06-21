/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneListFactory;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PhoneDao;

public class CrudPhoneListWorker {

    public static void start(final Context context, final String userId) throws IllegalStateException, IOException,
            URISyntaxException, RestClientException, JSONException {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_LIST_PROPERTY_USER_ID, userId);

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(WSConfig.WS_CITY_LIST_URL,
                NetworkConnection.METHOD_GET, params);

        ArrayList<Phone> phoneList = PhoneListFactory.parseResult(wsResult.wsResponse);

        // Clear the table
        context.getContentResolver().delete(PhoneDao.CONTENT_URI, null, null);

        // Adds the persons in the database
        final int phoneListSize = phoneList.size();
        if (phoneList != null && phoneListSize > 0) {
            ContentValues[] valuesArray = new ContentValues[phoneListSize];
            for (int i = 0; i < phoneListSize; i++) {
                valuesArray[i] = PhoneDao.getContentValues(phoneList.get(i));
            }
            context.getContentResolver().bulkInsert(PhoneDao.CONTENT_URI, valuesArray);
        }
    }
}
