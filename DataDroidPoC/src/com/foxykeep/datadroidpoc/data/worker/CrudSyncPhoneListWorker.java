/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
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

import android.os.Bundle;

import org.json.JSONException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneListFactory;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

public class CrudSyncPhoneListWorker {

    public static Bundle start(final String userId) throws IllegalStateException, IOException, URISyntaxException, RestClientException, JSONException {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_LIST_PROPERTY_USER_UDID, userId);

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(WSConfig.WS_CRUD_PHONE_LIST_URL,
                NetworkConnection.METHOD_GET, params);

        ArrayList<Phone> phoneList = PhoneListFactory.parseResult(wsResult.wsResponse);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PoCRequestManager.RECEIVER_EXTRA_PHONE_LIST, phoneList);
        return bundle;
    }
}
