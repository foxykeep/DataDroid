/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.worker;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneDeleteFactory;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class CrudSyncPhoneDeleteWorker {

    public static Bundle start(final String userId, final String phoneIdList)
            throws IllegalStateException, IOException, URISyntaxException,
            RestClientException, JSONException {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_DELETE_PROPERTY_USER_UDID, userId);
        params.put(WSConfig.WS_CRUD_PHONE_DELETE_PROPERTY_IDS, phoneIdList);

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                WSConfig.WS_CRUD_PHONE_DELETE_URL,
                NetworkConnection.METHOD_GET, params);

        long[] deletedPhoneIdArray = PhoneDeleteFactory.parseResult(wsResult.body);

        Bundle bundle = new Bundle();
        bundle.putLongArray(PoCRequestManager.RECEIVER_EXTRA_PHONE_DELETE_DATA, deletedPhoneIdArray);
        return bundle;
    }
}
