package com.foxykeep.datadroidpoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;

import org.json.JSONException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneDeleteFactory;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

public class CrudSyncPhoneDeleteWorker {

    public static Bundle start(final Context context, final String userId, final String phoneIdList)
            throws IllegalStateException, IOException, URISyntaxException, RestClientException, JSONException {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_DELETE_PROPERTY_USER_ID, userId);
        params.put(WSConfig.WS_CRUD_PHONE_DELETE_PROPERTY_IDS, phoneIdList);

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                WSConfig.WS_CRUD_PHONE_DELETE_URL, NetworkConnection.METHOD_GET, params);

        long[] deletedPhoneIdArray = PhoneDeleteFactory.parseResult(wsResult.wsResponse);

        Bundle bundle = new Bundle();
        bundle.putLongArray(PoCRequestManager.RECEIVER_EXTRA_PHONE_DELETE_DATA, deletedPhoneIdArray);
        return bundle;
    }
}
