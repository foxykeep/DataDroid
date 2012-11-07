/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.worker;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection.Builder;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneDeleteFactory;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import java.util.HashMap;

public final class CrudSyncPhoneDeleteOperation implements Operation {

    public static final String PARAM_USER_ID = "com.foxykeep.datadroidpoc.extras.userId";
    public static final String PARAM_PHONE_ID_LIST = "com.foxykeep.datadroidpoc.extras.phoneIdList";

    public CrudSyncPhoneDeleteOperation() {}

    @Override
    public Bundle execute(Request request) throws ConnectionException, DataException,
            CustomException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_DELETE_PROPERTY_USER_UDID,
                request.getString(PARAM_USER_ID));
        params.put(WSConfig.WS_CRUD_PHONE_DELETE_PROPERTY_IDS,
                request.getString(PARAM_PHONE_ID_LIST));

        Builder builder = new Builder(WSConfig.WS_CRUD_PHONE_DELETE_URL);
        builder.setParameters(params);

        ConnectionResult result = builder.execute();

        long[] deletedPhoneIdArray = PhoneDeleteFactory.parseResult(result.body);

        Bundle bundle = new Bundle();
        bundle.putLongArray(PoCRequestFactory.BUNDLE_EXTRA_PHONE_DELETE_DATA,
                deletedPhoneIdArray);
        return bundle;
    }
}
